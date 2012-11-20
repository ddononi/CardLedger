package kr.co.cardledger;

import java.util.ArrayList;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 *	카드사별 내역 조회
 */
public class CompanyListActivity extends MyActivity {
	private ArrayList<CardData> list;
	private ListAdapter la;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list_layout);

        //  list = getList();
        final ListView lv = (ListView)findViewById(R.id.list);
        final TextView countTv = (TextView)findViewById(R.id.list_count);
        // 카드사를 선택할 스피너 설정
        Spinner spinner = (Spinner)findViewById(R.id.company_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.card_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // 카드사를 선택시 해당 선택 카드사 내역 보여주기
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View v,
					final int pos, final long arg3) {

				String card = (String)parent.getItemAtPosition(pos);
				list = getList(card);
				if(list != null){
					la = new ListAdapter(CompanyListActivity.this, list, R.layout.list_item_layout);
					lv.setAdapter(la);
					countTv.setText("사용 내역 건수 : " + list.size());
				}else{
					Toast.makeText(CompanyListActivity.this, "사용내역이 없습니다.", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

        // 아이템 선택시 세부내역 다이얼로그를 보여준다.
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View arg1, final int pos,
					final long arg3) {
				// TODO Auto-generated method stub
				ListAdapter adapter = (ListAdapter) parent.getAdapter();
				CardData c = (CardData) adapter.getItem(pos);
				detailDialog(c);
			}
		});

    }

    /**
     * 카드 사용내역을 리스트에 담는다.
     * @return
     * 	내역 ArrayList 리스트 객체
     */
    private ArrayList<CardData> getList(final String card) {
		// db에서 카드 sms 내역을 불러온다.
    	SQLiteOpenHelper dbhelper = new DBHelper(this);
    	SQLiteDatabase db = dbhelper.getReadableDatabase();
    	Cursor cursor = db.query(DBHelper.CARD_TABLE, null, "company= ?", new String[]{card,} , null, null, "date desc");
    	ArrayList<CardData> list = null;	// 카드 사용내역을 담을 리스트
    	CardData data = null;
        if(cursor.moveToFirst()){			// 내역이 있으면
        	list = new ArrayList<CardData>();
        	do{
        		data = new CardData();
        		data.setCardCompany(cursor.getString(cursor.getColumnIndex("company")));
        		data.setPrice(cursor.getString(cursor.getColumnIndex("price")));
        		data.setShop(cursor.getString(cursor.getColumnIndex("shop")));
        		data.setBuy(cursor.getInt(cursor.getColumnIndex("buy")));
        		data.setDate(cursor.getString(cursor.getColumnIndex("date")));
        		list.add(data);				// 카드내역 데이터 추가
        	}while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        dbhelper.close();
 		return list;

	}

	/**
	 * 세부내역 다이얼로그
	 * @param c
	 * 	내역정보 객체
	 */
	private void detailDialog(final CardData c) {
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.detail_info);
		dialog.setTitle("사용내역");
		// 다이얼로그 후킹
		((TextView)dialog.findViewById(R.id.card_name)).setText(c.getCardCompany());
		((TextView)dialog.findViewById(R.id.card_date)).setText(c.getDate());
		((TextView)dialog.findViewById(R.id.card_shop)).setText(c.getShop());
		((TextView)dialog.findViewById(R.id.card_price)).setText(c.getPrice());

		dialog.show();
	}

}
