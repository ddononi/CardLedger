package kr.co.cardledger;

import kr.co.utils.BaseActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

public class IntroActivity extends BaseActivity {
	String cellNum = "";

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_layout);
	}

	/**
	 * 다음 화면으로 넘기기
	 */
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Intent intent = new Intent(this, StartActivity.class);
			startActivity(intent);
			finish();
			return true;
		}

		return super.onTouchEvent(event);

	}

}
