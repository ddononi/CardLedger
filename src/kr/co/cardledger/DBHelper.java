package kr.co.cardledger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * db 설정 클래스
 */
public class DBHelper extends SQLiteOpenHelper {
	public static final int DB_VER = 1; // 디비버젼
	public static final String DB_FILE = "cardledger.db"; // 디비파일이음
	public static final String CARD_TABLE = "cardledger"; // 카드 사용내역 테이블

	public DBHelper(final Context context) {
		super(context, DB_FILE, null, DB_VER);
	}

	/** 디비 생성시 테이블을 만들어준다. */
	@Override
	public void onCreate(final SQLiteDatabase db) { // db가 생성될때 테이블도 생성
		// 메모 테이블 생성
		// 인덱스, 카드회사, 사용날짜, 거래처, 가격
		String sql = "CREATE TABLE "
				+ CARD_TABLE
				+ " (no INTEGER PRIMARY KEY,"
				+ " company TEXT NOT NULL, date TEXT NOT NULL, buy INT NOT NULL, "
				+ " shop TEXT NOT NULL, price TEXT NOT NULL);";
		db.execSQL(sql);
	}

	@Override
	public void onOpen(final SQLiteDatabase db) {
		// TODO Auto-generated method stub

		super.onOpen(db);
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		// TODO Auto-generated method stub
		// sqlite가 업그레이드 됐을경우 이전에 있던 테이블은 없앤다.
		db.execSQL("DROP TABLE IF EXISTS " + CARD_TABLE);
		onCreate(db);
	}
}