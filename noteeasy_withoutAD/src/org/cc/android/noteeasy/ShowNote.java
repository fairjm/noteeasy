package org.cc.android.noteeasy;

import java.util.Date;

import org.cc.android.noteeasy.database.DataBaseOperator;
import org.cc.android.noteeasy.database.MyOpenHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ShowNote extends Activity {

	private EditText edit=null;
	private Button button=null;
	private Button cancel=null;
	private LinearLayout contentLayout=null;
	
	private String content=null;
	private String _id=null;
	
	private boolean isMeasured=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.shownote_layout);
		edit=(EditText)super.findViewById(R.id.show_note_edit);
		button=(Button)super.findViewById(R.id.show_note_button);
		cancel=(Button)super.findViewById(R.id.show_note_cancel);
		contentLayout=(LinearLayout)super.findViewById(R.id.show_note_content);
		Intent intent=this.getIntent();
		
		content=intent.getStringExtra("content");
		if(content==null){
			content="";
		}
		 _id=intent.getStringExtra("_id");
		 if(_id==null){
			 _id="-1";
		 }
		 
		 ViewTreeObserver vt=contentLayout.getViewTreeObserver();
		 vt.addOnPreDrawListener(new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				if(!isMeasured){
					DisplayMetrics dm = new DisplayMetrics();
					WindowManager wm=ShowNote.this.getWindowManager();
					wm.getDefaultDisplay().getMetrics(dm);
					edit.setLayoutParams(new LinearLayout.LayoutParams(contentLayout.getWidth(), (int) (dm.heightPixels*0.5)));
				    isMeasured=true;
				}
				return true;
			}
		});
//		Typeface mFace=Typeface.createFromAsset(getAssets(),"fonts/cartoon.TTF");
//        
//		
//		edit.setTypeface(mFace);
		edit.setText(content);
		button.setEnabled(false);

		init();
	}


	
	private void init(){
		edit.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void afterTextChanged(Editable s) {
               if(s.toString().equals(content)){
            	   button.setEnabled(false);
               }else{
            	   button.setEnabled(true);
               }
			}
			
		});
		
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String now=edit.getText().toString();
				MyOpenHelper helper=new MyOpenHelper(ShowNote.this);
				DataBaseOperator db=new DataBaseOperator(helper.getWritableDatabase());
				int id=Integer.valueOf(_id);
				if(id!=-1){
				    db.update(id, now);
				}else{
					db.insert(now, new Date());
				}
				Toast.makeText(ShowNote.this, R.string.shownote_success, Toast.LENGTH_SHORT).show();
				ShowNote.this.setResult(RESULT_OK, ShowNote.this.getIntent());
				button.setEnabled(false);
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			  ShowNote.this.finish();	
			}
		});
	}
}
