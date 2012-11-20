package kr.co.cardledger;

import java.util.ArrayList;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivity extends MyActivity {
	private ArrayList<CardData> list;
	private ListAdapter la;
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        list = getList();
        ListView lv = (ListView)findViewById(R.id.list);

        la = new ListAdapter(this, list, R.layout.list_item_layout);
        TextView countTv = (TextView)findViewById(R.id.list_count);
        countTv.setText("사용 내역 건수 : " + list.size());
        lv.setAdapter(la);
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
    private ArrayList<CardData> getList() {
		// db에서 카드 sms 내역을 불러온다.
    	SQLiteOpenHelper dbhelper = new DBHelper(this);
    	SQLiteDatabase db = dbhelper.getReadableDatabase();
    	Cursor cursor = db.query(DBHelper.CARD_TABLE, null, null, null, null, null, "date desc");
    	ArrayList<CardData> list = null;	// 카드 사용내역을 담을 리스트
    	CardData data = null;
        if(cursor.moveToFirst()){			// 내역이 있으면
        	list = new ArrayList<CardData>();
        	Log.i("card", "insert~~~");
        	
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
