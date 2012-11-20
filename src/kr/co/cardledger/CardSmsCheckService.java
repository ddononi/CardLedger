package kr.co.cardledger;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * 문자 도착시 수신하는 서비스 클래스 카드 문자인지 체크하여 사용금액을 체크한후 설정한 금액이 넘을경우 알람으로 알려준다.
 */
public class CardSmsCheckService extends Service implements iCardConstant {
	private SharedPreferences defaultSharedPref;
	// BroadcastReceiver for SMS
	BroadcastReceiver rcvIncoming = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			// 공유환경 설정 가져오기 setting.xml 값
			defaultSharedPref = PreferenceManager
					.getDefaultSharedPreferences(context);
			Log.i("NAUTES", "SMS received");
			Bundle data = intent.getExtras();
			if (data != null) {
				Object pdus[] = (Object[]) data.get("pdus");
				String message = "New message:\n";
				String sender = null;
				for (Object pdu : pdus) {
					SmsMessage part = SmsMessage.createFromPdu((byte[]) pdu);
					message += part.getDisplayMessageBody();
					if (sender == null) {
						sender = part.getDisplayOriginatingAddress();
					}
				}
				sender = sender.replace("-", "");
				// 카드사 문자인지 체크
				if (sender.equals(KB_CARD_NUMBER)
						|| sender.equals(LOTTE_CARD_NUMBER)
						|| sender.equals(CITY_CARD_NUMBER)
						|| sender.equals(HD_CARD_NUMBER)
						|| sender.equals(BC_CARD_NUMBER)
						|| sender.equals(SH_CARD_NUMBER)
						|| sender.equals(HANA_SK_CARD_NUMBER)
						|| sender.equals(TEST_CARD_NUMBER)) {

					CardContentInsert cardInsert = new CardContentInsert(
							context);
					cardInsert.insert(sender, message,
							System.currentTimeMillis());

					checkLimit();

					Log.i("NAUTES", "From: " + sender);
					Log.i("NAUTES", "Message: " + message);

					Toast.makeText(context, "Message: " + message,
							Toast.LENGTH_LONG).show();
				}
			}
		}
	};

	/**
	 * 설정값 한도 카드값을 넘는지 체크한다.
	 * 
	 */
	private void checkLimit() {
		// db에서 카드 sms 내역을 불러온다.
		SQLiteOpenHelper dbhelper = new DBHelper(this);
		SQLiteDatabase db = dbhelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.CARD_TABLE, null, null, null, null,
				null, "date desc");
		ArrayList<CardData> list = null; // 카드 사용내역을 담을 리스트
		CardData data = null;
		if (cursor.moveToFirst()) { // 내역이 있으면
			list = new ArrayList<CardData>();
			do {
				data = new CardData();
				data.setCardCompany(cursor.getString(cursor
						.getColumnIndex("company")));
				data.setPrice(cursor.getString(cursor.getColumnIndex("price")));
				data.setShop(cursor.getString(cursor.getColumnIndex("shop")));
				data.setDate(cursor.getString(cursor.getColumnIndex("date")));
				data.setMillis(cursor.getLong(cursor.getColumnIndex("no")));
				list.add(data); // 카드내역 데이터 추가
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		dbhelper.close();

		// 이달의 카드 내역을 가져온다.
		Calendar cal = Calendar.getInstance();
		Calendar currentCal = Calendar.getInstance();
		int amount = 0; // 사용한 금액
		// 한도 금액가져오기
		int limitAmount = Integer.valueOf(defaultSharedPref.getString(
				"card_limit_value_preference", "100000"));
		for (CardData card : list) {
			long d; // = System.currentTimeMillis();
			try {
				cal.setTimeInMillis(card.getMillis());
			} catch (NumberFormatException e) {
				continue;
			}

			Log.i("NAUTES", "YEAR  " + cal.get(Calendar.YEAR));
			Log.i("NAUTES", "MONTH : " + cal.get(Calendar.MONTH));

			Log.i("NAUTES", "currentCal YEAR  " + currentCal.get(Calendar.YEAR));
			Log.i("NAUTES",
					"currentCal MONTH : " + currentCal.get(Calendar.MONTH));

			// 현재 년과 월이 같은지 체크한다.
			if (cal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR)
					&& cal.get(Calendar.MONTH) == currentCal
							.get(Calendar.MONTH)) {
				String price;
				try {
					price = card.getPrice();
					price = price.replace(",", "").replace("원", "");
					if (price.contains("(")) {
						price = price.substring(0, price.indexOf("("));
					}
					if (!TextUtils.isEmpty(price))
						amount += Integer.valueOf(price);
					Log.i("card", "	amount : " + amount);
				} catch (NumberFormatException e) {
					continue;
				}
				Log.i("card", "price : " + price);
				if (amount > limitAmount) { // 총 사용한 금액이 넘을경우
					// 알람 설정
					// 리시버 발생시 수신할 알람리시버
					Intent i = new Intent(getBaseContext(),
							CardLimitReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							getBaseContext(), 0, i,
							PendingIntent.FLAG_CANCEL_CURRENT);
					try {
						sender.send();
					} catch (CanceledException e) {
						e.printStackTrace();
					}
					break;
				}

			}
		}
	}

	/** 서비스가 실행될때 */
	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		registerReceiver(rcvIncoming, new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED"));
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
