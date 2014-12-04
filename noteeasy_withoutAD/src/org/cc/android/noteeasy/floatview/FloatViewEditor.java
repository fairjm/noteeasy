package org.cc.android.noteeasy.floatview;

import java.util.Date;

import org.cc.android.noteeasy.MainActivity;
import org.cc.android.noteeasy.R;
import org.cc.android.noteeasy.ShowNote;
import org.cc.android.noteeasy.database.DataBaseOperator;
import org.cc.android.noteeasy.database.MyOpenHelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/*
 * ��FloatViewIcon�а���֮�� ����
 */
public class FloatViewEditor {
	private Typeface mFace=null;
	private boolean isInWindow=false;
	private  WindowManager.LayoutParams params=new WindowManager.LayoutParams();
	private  View floatView=null;
	
	private TextView mytv=null;
	private Button closeButton=null;
	private Button submitButton=null;
	private Button backButton=null;
	private ImageButton cont=null;	
	private EditText editText=null;
	
	
	private Context context=null;
	private WindowManager wm=null;
	private FloatViewIcon icon=null;
	private MyOpenHelper helper=null;
	
	private float x=0f;
	private float y=0f;
	private boolean isButtonShow=false;
	public FloatViewEditor(Context context,FloatViewIcon icon){
		this.context=context;
		this.icon=icon;
		helper=new MyOpenHelper(context);
		initView();
	}
	
	//��ɶ�floatview�ĳ�ʼ��
	private void initView(){
		
		//��ȡ���еĲ������
		
		floatView=LayoutInflater.from(context).inflate(R.layout.floatview_editor, null);
		
		closeButton=(Button)floatView.findViewById(R.id.editor_close);
		submitButton=(Button)floatView.findViewById(R.id.editor_submit);
		backButton=(Button)floatView.findViewById(R.id.editor_back);
		cont=(ImageButton)floatView.findViewById(R.id.editor_cont);
		
		wm=(WindowManager) context.getSystemService("window");
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		
		editText=(EditText)floatView.findViewById(R.id.edittext);
		int height=editText.getLayoutParams().height;
		editText.setLayoutParams(new LinearLayout.LayoutParams((int) (dm.widthPixels*0.6), height));
//		mFace=Typeface.createFromAsset(context.getAssets(),"fonts/cartoon.TTF");
//		editText.setTypeface(mFace);
		mytv=(TextView)floatView.findViewById(R.id.editor_tv);
		mytv.setTypeface(mFace);
		
				
		params.type=LayoutParams.TYPE_PHONE;
		params.gravity=Gravity.LEFT | Gravity.TOP;
		params.x=200;
		params.y=200;
		//�������ȡ���� ��Ӱ���� 
		//��Ӱ��ϵͳ�������¼���Ӧ
		params.flags=LayoutParams.FLAG_NOT_FOCUSABLE;
		params.width=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.height=android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.format=PixelFormat.RGBA_8888; //���Ҫ���� ��ȻͼƬ��ɫ��������

		//���ô���ʱ���¼�����
		floatView.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if(event.getEventTime()-event.getDownTime()<300){ //�����Ϳ�ʼС��300����Ͳ��ƶ�
					return false;
				}else{
				x=event.getRawX()-floatView.getWidth()/2; //ȷ�����Ͻ�x������
				y=event.getRawY()-floatView.getHeight()/2; //ȷ�����Ͻ�y������
				}
				switch(event.getAction()){	
				case MotionEvent.ACTION_DOWN:					
					break;
				case MotionEvent.ACTION_MOVE:
					updateView(x,y);
					break;
				case MotionEvent.ACTION_UP:
					updateView(x,y);
				}
				return false;
			}
		});
		
		floatView.setOnClickListener(null);
		
		closeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(icon!=null){
			   icon.addToWindow();
				}
			   FloatViewEditor.this.removeFromWindow();
			}
		});
		
		//�����ʱ���õ�ǰ����������Ի�ý���
		editText.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				params.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL;
				updateView();
			}
		});
		
		editText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				//����Ƿ��ؼ� �����������ܻ�ý��� �������Ա��ⱳ���һЩ��ť�޷�����(��������Ӧ)���������
				if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
					params.flags=LayoutParams.FLAG_NOT_FOCUSABLE;
					updateView();
				}
				return false;
			}
		});
		
		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				submit(true);
			}
		});
		
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			  Intent intent=new Intent(context,MainActivity.class);
			  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  context.startActivity(intent);
			}
		});
		
		cont.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isButtonShow){
					AlphaAnimation aa=new AlphaAnimation(0f, 100f);
					aa.setDuration(1200);
					cont.setImageResource(android.R.drawable.arrow_down_float);
					cont.startAnimation(aa);
					
					Animation a1=AnimationUtils.loadAnimation(context, R.anim.out_to_right);
					Animation a2=AnimationUtils.loadAnimation(context, R.anim.out_to_right);
					
					a1.setAnimationListener(new AnimationListener() {
						public void onAnimationStart(Animation animation) {
						}
						public void onAnimationRepeat(Animation animation) {
						}
						public void onAnimationEnd(Animation animation) {
							submitButton.setVisibility(View.GONE);
						}
					});
					a2.setAnimationListener(new AnimationListener() {
						public void onAnimationStart(Animation animation) {
						}
						public void onAnimationRepeat(Animation animation) {
						}
						public void onAnimationEnd(Animation animation) {
							backButton.setVisibility(View.GONE);
						}
					});
					submitButton.startAnimation(a1);
					backButton.startAnimation(a2);
					isButtonShow=false;
				}else{
					Animation in=AnimationUtils.loadAnimation(context, R.anim.in_from_left); 
					cont.setImageResource(android.R.drawable.arrow_up_float);
					submitButton.setVisibility(View.VISIBLE);
					backButton.setVisibility(View.VISIBLE);
					submitButton.startAnimation(in);
					backButton.startAnimation(in);
					isButtonShow=true;
				}
				
			}
		});
	}
	public void submit(boolean showMsg){
		String text=FloatViewEditor.this.editText.getEditableText().toString();
		if(text==null||"".equals(text.trim())){
			if(showMsg){
			Toast.makeText(context, R.string.floatvieweditor_input, Toast.LENGTH_SHORT).show();
			}
			return;
		}
		DataBaseOperator db=new DataBaseOperator(helper.getWritableDatabase());
		db.insert(text, new Date());
		FloatViewEditor.this.editText.setText("");
		if(showMsg){
		Toast.makeText(context,R.string.floatvieweditor_success, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void updateView(){
		wm.updateViewLayout(floatView, params);
	}
	
	private void updateView(float x,float y){
		params.x=(int) x;
		params.y=(int) y;
		updateView();
	}
	
	//�Ӵ������Ƴ�
	public boolean removeFromWindow(){
		if(isInWindow){
			wm.removeView(floatView);
			isInWindow=false;
			return true;
		}
		return false;
	}
	
	//���뵽����
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
