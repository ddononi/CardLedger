package kr.co.cardledger;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends MyActivity implements iCardConstant {
	private String currentMonth;
	private int totalPrice = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		// 현재 달 구하기
		int month = calenda.get(Calendar.MONTH) + 1;
		currentMonth = (month >= 10) ? "" + month : "0" + month;
		setLayout();
	}

	/**
	 * 뷰에 사용금액 넣기
	 */
	private void setLayout() {
		// 카드사 string-array 얻기
		// String[] arr = getResources().getStringArray(R.array.card_array);
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		currencyFormatter.setCurrency(Currency.getInstance(Locale.KOREA));
		int sum = getCurrentSum(KB_CARD);
		LinearLayout kb = (LinearLayout) findViewById(R.id.kb_label);
		if (sum > 0) {
			TextView kbSum = (TextView) findViewById(R.id.kb_sum);
			kbSum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			kb.setVisibility(View.VISIBLE);
			totalPrice += sum;
		} else {
			kb.setVisibility(View.GONE);
		}

		sum = getCurrentSum(LOTTE_CARD);
		if (sum > 0) {
			LinearLayout lotte = (LinearLayout) findViewById(R.id.lotte_label);
			TextView lotteSum = (TextView) findViewById(R.id.lotte_sum);
			lotteSum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			lotte.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		sum = getCurrentSum(HD_CARD);
		if (sum > 0) {
			LinearLayout hd = (LinearLayout) findViewById(R.id.hd_label);
			TextView hdSum = (TextView) findViewById(R.id.hd_sum);
			hdSum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			hd.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		sum = getCurrentSum(CITY_CARD);
		if (sum > 0) {
			LinearLayout city = (LinearLayout) findViewById(R.id.city_label);
			TextView citySum = (TextView) findViewById(R.id.city_sum);
			citySum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			city.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		sum = getCurrentSum(BC_CARD);
		if (sum > 0) {
			LinearLayout bc = (LinearLayout) findViewById(R.id.bc_label);
			TextView bcSum = (TextView) findViewById(R.id.bc_sum);
			bcSum.setText("+   "
					+ currencyFormatter.format(sum).replace(".00", "") + "원");
			bc.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		sum = getCurrentSum(HANA_SK_CARD);
		if (sum > 0) {
			LinearLayout hana = (LinearLayout) findViewById(R.id.hana_sk_label);
			TextView hanaSum = (TextView) findViewById(R.id.hana_sum);
			hanaSum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			hana.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		sum = getCurrentSum(SH_CARD);
		if (sum > 0) {
			LinearLayout hana = (LinearLayout) findViewById(R.id.sh_label);
			TextView hanaSum = (TextView) findViewById(R.id.sh_sum);
			hanaSum.setText(currencyFormatter.format(sum).replace(".00", "")
					+ "원");
			hana.setVisibility(View.VISIBLE);
			totalPrice += sum;
		}

		TextView total = (TextView) findViewById(R.id.total_sum);
		total.setText(currencyFormatter.format(totalPrice).replace(".00", "")
				+ "원");
		TextView count = (TextView) findViewById(R.id.total_count);
		count.setText("사용 건수 : " + getTotal() + "건");
	}

	/**
	 * 월벼 사용내역을 리스트에 담는다.
	 * 
	 * @return 내역 ArrayList 리스트 객체
	 */
	private int getCurrentSum(String card) {
		// db에서 카드 sms 내역을 불러온다.
		SQLiteOpenHelper dbhelper = new DBHelper(this);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.CARD_TABLE, null,
				"substr(date, 1, 2) = ? and company like ?", new String[] {
						currentMonth, card }, null, null, "date desc");
		int sum = 0;
		String price;
		if (cursor.moveToFirst()) { // 내역이 있으면
			do {
				price = cursor.getString(cursor.getColumnIndex("price"));
				price = price.replace(",", "").replace("원", "");
				if (price.contains("(")) {
					price = price.substring(0, price.indexOf("("));
				}
				if (!TextUtils.isEmpty(price))
					sum += Integer.valueOf(price);
				// 카드내역 데이터 추가
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		dbhelper.close();
		return sum;

	}

	/**
	 * 이달 전체 건수 구하기
	 * 
	 * @return
	 */
	private int getTotal() {
		// db에서 카드 sms 내역을 불러온다.
		SQLiteOpenHelper dbhelper = new DBHelper(this);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.CARD_TABLE, null,
				"substr(date, 1, 2) = ?", new String[] { currentMonth, }, null,
				null, "date desc");
		int count = cursor.getCount();

		cursor.close();
		db.close();
		dbhelper.close();
		return count;
	}

}
