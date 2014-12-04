package org.cc.android.noteeasy.floatview;

import org.cc.android.noteeasy.R;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class ShowService extends Service {

	private FloatViewIcon fv=null;
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private boolean openWindow=false;
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		openWindow=intent.getBooleanExtra("open", false);
		if(openWindow){
			if(fv==null){
		      fv=new FloatViewIcon(this.getApplicationContext());
			}
			Toast.makeText(getApplicationContext(),R.string.showService_open, Toast.LENGTH_SHORT).show();
			//如果两个悬浮窗口有一个已经在了就不要再加了
			if((!fv.isInWindow())&&(fv.getEditor()==null||!fv.getEditor().isInWindow())){
		    fv.addToWindow();
			}
		}else{
			Toast.makeText(getApplicationContext(), R.string.showService_close, Toast.LENGTH_SHORT).show();
			if(fv!=null){
				fv.removeFromWindow();
				if(fv.getEditor()!=null){
				  fv.getEditor().removeFromWindow();
				}
				
			}
			this.stopSelf(); //会调用Destory
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLowMemory() {
		if(fv!=null){
			fv.removeFromWindow();
			if(fv.getEditor()!=null){
			  fv.getEditor().submit(false);
			  fv.getEditor().removeFromWindow();
			}
		}
	Toast.makeText(getApplicationContext(), R.string.showService_close, Toast.LENGTH_SHORT).show();
		super.onLowMemory();
	}


}
