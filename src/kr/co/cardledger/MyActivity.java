package kr.co.cardledger;

import java.util.Calendar;

import kr.co.utils.BaseActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MyActivity extends BaseActivity {
	/* Debug setting */
	public static final String DEBUG_TAG = "cardledger";

	/* Server setting */
	public static final String SERVER_URL = "sungdog.cafe24.com";

	/* Preferences setting */
	public static final String PREFERENCE = "Prefs";
	public static final String APP_VERSION = "1.1";
	public static final String PUBLISH_VERSION = "1.10r";
	public static final String APP_NAME = "카드가계부";
	public static final String PGNAME = "카드가계부";
	public static final String VERION_ID = "카드가계부.1.1";

	public static final int ACTION_RESULT = 0;
	public static final String RECEIVE_OK = "ok"; //
	public static final int MAX_TIME_LIMIT = 2012;

	/* font */
	public static Typeface typeFace = null;
	public static Typeface typeFaceBold = null;

	/* menu */
	public final static int MAIN_MENU = 0;
	public final static int LIST_MENU = 1;
	public final static int MONTH_MENU = 2;
	public final static int CARD_MENU = 3;
	public final static int OPTION_MENU = 4;
	public static Calendar calenda;
	static {
		calenda = Calendar.getInstance();
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 화면 세로 고정
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (calenda.get(1) > MAX_TIME_LIMIT)
			finish();
		// 폰트 설정
		if (typeFace == null) {
			// 나눔고딕 글꼴
			typeFace = Typeface.createFromAsset(getAssets(),
					"fonts/NanumGothic.ttf");
			typeFaceBold = Typeface.createFromAsset(getAssets(),
					"fonts/NanumGothicBold.ttf");
		}
	}

	/**
	 * 폰트 설정(나눔 글꼴)
	 */
	@Override
	public void setContentView(final int viewId) {
		View view = LayoutInflater.from(this).inflate(viewId, null);
		ViewGroup group = (ViewGroup) view;
		recursiveViewSetTypeFace(group);
		super.setContentView(view);
	}

	/**
	 * 뷰를 탐색하여 폰트를 적용하는 재귀 메소드 tag를 가지고 있으면 볼드폰트로 적용
	 * 
	 * @param view
	 *            탐색할 뷰 그룹
	 */
	public static void recursiveViewSetTypeFace(final ViewGroup group) {
		int childCnt = group.getChildCount();
		TextView tv;
		Button b;
		EditText et;
		for (int i = 0; i < childCnt; i++) {
			View v = group.getChildAt(i);
			if (v instanceof TextView) {
				tv = (TextView) v;
				tv.setTypeface((tv.getTag() != null) ? typeFaceBold : typeFace);
			} else if (v instanceof Button) {
				b = (Button) v;
				b.setTypeface((b.getTag() != null) ? typeFaceBold : typeFace);
			} else if (v instanceof EditText) {
				et = (EditText) v;
				et.setTypeface((et.getTag() != null) ? typeFaceBold : typeFace);
			} else if (v instanceof ViewGroup) {
				recursiveViewSetTypeFace((ViewGroup) v);
			}
		}
	}

	/** 뒤로가기 버튼 클릭시 앱 종료 */
	@Override
	public void onBackPressed() {
		finishDialog(this);

	}

	/*
	 * 종료
	 */
	public void finishDialog(final Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("").setMessage("종료 하시겠습니까?")
				.setPositiveButton("종료", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						// TODO Auto-generated method stub
						moveTaskToBack(true);
						finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}
				}).setNegativeButton("취소", null).show();
	}

	/*
	 * 앱 정보
	 */
	public void appInfoDialog(final Context context) {
		AlertDialog.Builder ad = new AlertDialog.Builder(context);
		ad.setTitle("").setMessage(APP_NAME + " ver." + PUBLISH_VERSION)
				.setPositiveButton("확인", null).show();
	}

	/*
	 * 도움말
	 */
	public void appHelpDialog(final Context context) {
		/*
		 * String str = getResources().getString( R.string.app_help );
		 * AlertDialog.Builder ad = new AlertDialog.Builder(context);
		 * ad.setTitle("도움말").setMessage(str)
		 * .setPositiveButton("확인",null).show();
		 */
	}

	/** 옵션 메뉴 만들기 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MAIN_MENU, 0, "이달카드내역");
		menu.add(0, LIST_MENU, 0, "전체내역");
		menu.add(0, MONTH_MENU, 0, "월별 내역");
		menu.add(0, CARD_MENU, 0, "카드사별 내역");
		menu.add(0, OPTION_MENU, 0, "환경설정");
		// menu.add(0,4,0, "도움말");
		// item.setIcon();
		return true;
	}

	/** 옵션 메뉴 선택에 따라 해당 처리를 해줌 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case MAIN_MENU: // 메인 화면
			intent = new Intent(getBaseContext(), MainActivity.class);
			startActivity(intent);
			return true;
		case LIST_MENU: // 전체 리스트
			intent = new Intent(getBaseContext(), ListActivity.class);
			startActivity(intent);
			return true;
		case MONTH_MENU: // 월별 리스트
			intent = new Intent(getBaseContext(), MonthListActivity.class);
			startActivity(intent);
			return true;
		case CARD_MENU: // 카드사별
			intent = new Intent(getBaseContext(), CompanyListActivity.class);
			startActivity(intent);
			return true;

		case OPTION_MENU: // 환경설정
			intent = new Intent(getBaseContext(), SettingActivity.class);
			startActivity(intent);
			return true;

		}
		return false;
	}

}
