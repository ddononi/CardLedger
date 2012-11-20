package kr.co.cardledger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class StartActivity extends MyActivity {
	/** Called when the activity is first created. */
	private ArrayList<CardData> cardData = new ArrayList<CardData>();
	private final ArrayList<CardData> insertData = new ArrayList<CardData>();
	private int mCount;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);
		if (!checkNetWork(false)) {
			finish(); // 네트워크 연결이 안될때는 프로그램 종료
		}
		Log.i(DEBUG_TAG, "MANUFACTURER->" + Build.MANUFACTURER);
		// 공유환경 설정 가져오기 setting.xml 값
		// 서비스로 알람설정
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isSetAlarm = sp.getBoolean("alarm", true);
		if (isSetAlarm) { // 서비스가 설정되어 있으면 서비스시작
			startService(new Intent(this, CardSmsCheckService.class));
		}
		new AsyncTaskFileUpload().execute();
	}

	@Override
	public void onBackPressed() { // 뒤로 가기버튼 클릭시 종료 여부
		// TODO Auto-generated method stub
		// super.onBackPressed();
		finishDialog(this);

	}

	/**
	 * sms 내용을 읽어와 카드사용내역이면 db에 저장처리한다.
	 */
	private void checkSMSConent() {
		// SQLiteOpenHelper db = new SQLiteOpenHelper(this, null, null, 0);
		ContentResolver cr = getContentResolver();
		// Cursor cursor = cr.query(Uri.parse("content://sms/inbox"), null,
		// null, null, null);
		Cursor cursor = null;
		// 제조사별(LG, SAMSUNG)로 uri 형식이 다르기 때문에 메이저 제조사만 분류
		if (Build.MANUFACTURER.equalsIgnoreCase("lg")) {
			cursor = cr.query(
					Uri.parse("content://com.lge.messageprovider/msg/inbox"),
					null, null, null, null);
			// }else if(Build.MANUFACTURER.equalsIgnoreCase("samsung")){
			// cursor =
			// cr.query(Uri.parse("content://com.sec.mms.provider/message"),
			// null, null, null, null);
		} else {
			cursor = cr.query(Uri.parse("content://sms/inbox"), null, null,
					null, null);
		}
		// content://com.sec.mms.provider/message
		CardContentInsert cardInsert = new CardContentInsert(this);
		mCount = 0;
		CardData data = null;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				String body = cursor.getString(cursor.getColumnIndex("body"));
				long date = cursor.getLong(cursor.getColumnIndex("date"));
				String address = cursor.getString(cursor
						.getColumnIndex("address"));
				data = cardInsert.insert(address, body, date);
				if (data != null) { // 추가된 데이터면 서버에 전송하기 위해 리스트에 담아둔다.
					insertData.add(data);
					mCount++;
				}
			} while (cursor.moveToNext());

			// ui 쓰레드로
			StartActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(StartActivity.this,
							mCount + "건 사용내역이 서버에 전송됩니다.", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}

		cardInsert.close();

	}

	/**
	 * 카드 사용내역을 리스트에 담는다.
	 * 
	 * @return 내역 ArrayList 리스트 객체
	 */
	private ArrayList<CardData> getCardDatase() {
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
				list.add(data); // 카드내역 데이터 추가
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		dbhelper.close();
		return list;
	}

	/**
	 * 파일 전송 및 위치 전송 처리 클래스
	 * 
	 */
	private class AsyncTaskFileUpload extends
			AsyncTask<Object, String, Integer> {
		ProgressDialog dialog = null;

		@Override
		protected void onPostExecute(final Integer count) { // 전송 완료후
			// 모든 파일이 전송이 완료되면 다이얼로그를 닫는다.
			dialog.dismiss(); // 프로그레스 다이얼로그 닫기

			// 파일 전송 결과를 출력
			if (count == insertData.size()) { // 파일 전송이 정상이면
				if (count > 0) {
					Toast.makeText(StartActivity.this, "모든 내역이 전송되었습니다.",
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(StartActivity.this, "전송 내역이 없습니다.",
							Toast.LENGTH_LONG).show();
				}
			} else {
				Toast.makeText(
						StartActivity.this,
						"총 " + insertData.size() + "건중 " + count
								+ "건이 전송되었습니다.", Toast.LENGTH_LONG).show();

			}
			cardData = getCardDatase();
			Intent intent = new Intent(StartActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
			StartActivity.this
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute() 파일 전송중 로딩바 나타내기
		 */
		@Override
		protected void onPreExecute() { // 전송전 프로그래스 다이얼로그로 전송중임을 사용자에게 알린다.
			dialog = ProgressDialog.show(StartActivity.this, "전송중",
					"카드사용문자내역을 서버로 전송중입니다. 잠시만 기다려 주세요.", true);
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(final String... values) {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[]) 비동기 모드로 전송
		 */
		@Override
		protected Integer doInBackground(final Object... params) { // 전송중
			if (!StartActivity.this.checkNetWork(true)) { // 네트워크 상태 체크
				return 0;
			}

			checkSMSConent();

			// http 로 보낼 이름 값 쌍 컬랙션
			Vector<NameValuePair> vars = new Vector<NameValuePair>();
			DeviceInfo di = DeviceInfo
					.setDeviceInfo((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
			String phone = di.getDeviceNumber();
			Log.i(DEBUG_TAG, "phone num" + phone);
			int count = 0;
			int i = 0;
			vars.add(new BasicNameValuePair("count", String.valueOf(insertData
					.size()))); // 카드사

			for (CardData data : insertData) {
				// HTTP GET 메서드를 이용하여 데이터 업로드 처리
				i++;
				vars.clear();
				vars.add(new BasicNameValuePair("count", String
						.valueOf(insertData.size()))); // 카드사
				vars.add(new BasicNameValuePair("phone_num", phone)); // 전화번호
				vars.add(new BasicNameValuePair("company", data
						.getCardCompany())); // 카드사
				vars.add(new BasicNameValuePair("shop", data.getShop())); // 상호명
				vars.add(new BasicNameValuePair("price", data.getPrice())); // 가격
				vars.add(new BasicNameValuePair("date", String.valueOf(data
						.getDate())));// 날짜
				vars.add(new BasicNameValuePair("buy", String.valueOf(data
						.getBuy()))); // 날짜
				Log.i("cardledger",
						data.getCardCompany() + " " + data.getShop()
								+ "   price : " + data.getPrice()
								+ "   date : " + String.valueOf(data.getDate())
								+ "   buy :" + String.valueOf(data.getBuy())
								+ "   phone_num : " + phone);
				try {
					String url = "http://" + SERVER_URL
							+ "/cardledger/insert.php?"
							+ URLEncodedUtils.format(vars, null);
					url += URLEncodedUtils.format(vars, "UTF-8");
					HttpGet request = new HttpGet(url);
					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					HttpClient client = new DefaultHttpClient();
					final String responseBody = client.execute(request,
							responseHandler); // 전송
					if (responseBody.equals("ok")) {
						// Toast.makeText(getBaseContext(), responseBody,
						// Toast.LENGTH_LONG).show();
						Log.i(DEBUG_TAG, responseBody);
						// result = true;
						count++;
					} else {
						StartActivity.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Toast.makeText(StartActivity.this,
										responseBody, Toast.LENGTH_LONG).show();

							}
						});
					}
				} catch (ClientProtocolException e) {
					Log.e(DEBUG_TAG, "Failed to get playerId (protocol): ", e);
					// return false;
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "Failed to get playerId (io): ", e);
					// return false;
				}
			}

			return count;
		}

	}

}