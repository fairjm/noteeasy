package org.cc.android.noteeasy;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

public class About extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.about_layout);

	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
		return super.dispatchKeyEvent(event);
	}
	
	 @Override
	 public void onDestroy() {
	   super.onDestroy();
	 }

}
