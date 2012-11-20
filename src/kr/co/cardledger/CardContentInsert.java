package kr.co.cardledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

public class CardContentInsert implements iCardConstant {

	private final SQLiteOpenHelper dbhelper;
	private final SQLiteDatabase db;
	private final ContentValues value;

	public CardContentInsert(final Context context) {
		dbhelper = new DBHelper(context);
		db = dbhelper.getWritableDatabase();
		value = new ContentValues();
	}

	/**
	 * DB에 카드 사용내역을 분류하여 저장한다.
	 * 
	 * @param body
	 *            문자내용
	 * @param date
	 *            수신 날짜
	 */
	public CardData insert(final String cardNumber, final String body,
			final long date) {
		if ((cardNumber.equals(KB_CARD_NUMBER) || cardNumber
				.equals(TEST_CARD_NUMBER)) && body.contains("누적")) { // 국민카드
			String[] arr = body.split("\n"); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			// 승인 혹은 승인 취소
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", KB_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[2]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[3]); // 사용금액
				value.put("shop", arr[4].replace("사용", "").trim()); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(KB_CARD);
				data.setDate(arr[2]);
				data.setPrice(arr[3]);
				data.setShop(arr[4].replace("사용", "").trim());

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}
			// Log.i("cardledger", body);
			/*
			 * Log.i("cardledger", body); value.clear(); value.put("company",
			 * KB_CARD); // 카드 회사 value.put("price", arr[2]); // 사용금액
			 * value.put("shop", arr[4]); // 사용처 value.put("date", date); // 날짜
			 */
			// 에러가 없다면 정상적으로 중복되지 않은 내용으로 간주
			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}

		} else if (cardNumber.equals(LOTTE_CARD_NUMBER)) { // 롯데카드
			String[] arr = body.split(" "); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			// 승인 혹은 승인 취소
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", LOTTE_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[5] + " " + arr[6]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[3]); // 사용금액
				value.put("shop", arr[7]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(LOTTE_CARD);
				data.setDate(arr[5] + " " + arr[6]);
				data.setPrice(arr[3]);
				data.setShop(arr[7]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(CITY_CARD_NUMBER)) { // 씨티카드
			String[] arr = body.split("\n"); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", CITY_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[2]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[3].split(" ")[1]); // 사용금액
				value.put("shop", arr[4]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(CITY_CARD);
				data.setDate(arr[2]);
				data.setPrice(arr[3].split(" ")[1]);
				data.setShop(arr[4]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(HD_CARD_NUMBER)) { // 현대카드
			String[] arr = body.split("\n"); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", HD_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[2]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[3].split("원")[0]); // 사용금액
				value.put("shop", arr[4]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(HD_CARD);
				data.setDate(arr[2]);
				data.setPrice(arr[3].split("원")[0] + "원");
				data.setShop(arr[4]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(BC_CARD_NUMBER)) { // 비씨카드
			String[] arr = body.split("\n"); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", BC_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[3]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[1]); // 사용금액
				value.put("shop", arr[4]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(BC_CARD);
				data.setDate(arr[3]);
				data.setPrice(arr[1]);
				data.setShop(arr[4]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(BC_CARD_NUMBER)) { // 비씨카드
			String[] arr = body.split("\n"); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", BC_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[3]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[1]); // 사용금액
				value.put("shop", arr[4]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(BC_CARD);
				data.setDate(arr[3]);
				data.setPrice(arr[1]);
				data.setShop(arr[4]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(SH_CARD_NUMBER)) { // 신한카드
			String[] arr = body.split(" "); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			// 승인 혹은 승인 취소
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {

				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", SH_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[4] + " " + arr[5]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[10].split("원")[0]); // 사용금액
				String shop;
				if (TextUtils.isEmpty(arr[18]) == false) {
					shop = arr[18];
				} else {
					shop = arr[19];
				}
				value.put("shop", shop.trim()); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(SH_CARD);
				data.setDate(arr[4] + " " + arr[5]);
				data.setPrice(arr[10].split("원")[0]);
				data.setShop(shop.trim());

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		} else if (cardNumber.equals(HANA_SK_CARD_NUMBER)) { // 하나SK카드
			String[] arr = body.split(" "); // 내용 자르기
			value.clear();
			CardData data = null;
			int buy = 1;
			// 승인 혹은 승인 취소
			if (body.contains("취소")) {
				buy = 0; // 1이면 승인 0이면 승인 취소
			}

			try {
				value.put("no", date); // 날짜 밀리세컨드를 인덱스값으로
				value.put("company", HANA_SK_CARD); // 카드 회사
				Log.i(MyActivity.DEBUG_TAG, "long date->" + date);
				value.put("date", arr[1] + " " + arr[2]); // 날짜
				value.put("buy", buy); // 승인여부
				value.put("price", arr[4].split("원")[0]); // 사용금액
				value.put("shop", arr[5]); // 사용처

				data = new CardData();
				data.setMillis(date);
				data.setCardCompany(HANA_SK_CARD);
				data.setDate(arr[1] + " " + arr[2]);
				data.setPrice(arr[4].split("원")[0]);
				data.setShop(arr[5]);

			} catch (IndexOutOfBoundsException iobe) {
				return null;
			}

			if (db.insert(DBHelper.CARD_TABLE, null, value) != -1) {
				return data;
			}
		}

		return null;
	}

	/**
	 * 디비 닫기
	 */
	public void close() {
		db.close();
		dbhelper.close();
	}

}
