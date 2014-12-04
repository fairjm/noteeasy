package org.cc.android.noteeasy.floatview;

import org.cc.android.noteeasy.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class FloatViewIcon {
	private boolean isInWindow=false;
	private WindowManager.LayoutParams params=new WindowManager.LayoutParams();
	private View floatView=null;
	private Context context=null;
	private WindowManager wm=null;
	private float x=0f;
	private float y=0f;
	
	private FloatViewEditor editor=null;
	
	public FloatViewEditor getEditor() {
		return editor;
	}
	public FloatViewIcon(Context context){
		this.context=context;
		editor=new FloatViewEditor(context,this);
		initView();
	}
	//完成对floatview的初始化
	private void initView(){
		floatView=LayoutInflater.from(context).inflate(R.layout.floatview_ico, null);		
		wm=(WindowManager) context.getSystemService("window");		
		params.type=LayoutParams.TYPE_PHONE;
		params.gravity=Gravity.LEFT | Gravity.TOP;
		params.x=0;
		params.y=200;
		params.flags|=LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.format=PixelFormat.RGBA_8888; //这个要设置 不然图片颜色会有问题

		floatView.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getEventTime()-event.getDownTime()<300){ //结束和开始小于300毫秒就不移动
					return false;
				}else{
				x=event.getRawX()-floatView.getWidth()/2; //确定左上角x的坐标
				y=event.getRawY()-floatView.getHeight()/2; //确定左上角y的坐标
				}
				switch(event.getAction()){	
				case MotionEvent.ACTION_DOWN:					
					break;
				case MotionEvent.ACTION_MOVE:
					updateView(x,y);
					break;
				case MotionEvent.ACTION_UP:
					float bounds=wm.getDefaultDisplay().getWidth()/2; //屏幕宽度一半
					if(x+floatView.getWidth()/2>bounds){
						//移动到屏幕的右边
						x=2*bounds-floatView.getWidth();  
					}else{
						x=0;
					}
					updateView(x,y);
				}
				return false;
			}
		});
		
		floatView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			if(editor!=null){
               editor.addToWindow();     
			}
			FloatViewIcon.this.removeFromWindow();
			}
		});
	}
	
	//更新窗口信息
	private void updateView(float x,float y){
		params.x=(int) x;
		params.y=(int) y;
		wm.updateViewLayout(floatView, params);
	}
	
	//从窗口中移除
	public boolean removeFromWindow(){
		if(isInWindow){
			wm.removeView(floatView);
			isInWindow=false;
			return true;
		}
		return false;
	}
	
	//加入到窗口
	public boolean addToWindow(){
		if(!isInWindow){
			wm.addView(floatView, params);
			isInWindow=true;
			return true;
		}
		return false;
	}
	public boolean isInWindow() {
		return isInWindow;
	}
	public void setInWindow(boolean isInWindow) {
		this.isInWindow = isInWindow;
	}
}
