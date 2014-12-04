package org.cc.android.noteeasy;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cc.android.noteeasy.database.DataBaseOperator;
import org.cc.android.noteeasy.database.MyOpenHelper;
import org.cc.android.noteeasy.floatview.ShowService;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    //字体有关
	private TextView title=null;
	private Typeface mFace=null;
	
	private SelectPicPopupWindow menu=null;
	
	//数据操作有关
	private SharedPreferences sp=null;
	private int open=0;
	private Editor editor=null;
	private MyOpenHelper helper=null;
	
	//刷新有关
	private Button refresh=null;
    private Handler handler=null;
    
    //显示笔记项有关
	private ListView mylv=null;
	private SimpleAdapter adapter=null;
	private List<Map<String,String>> list=null;
	
	//这个是和查找有关的
	private String searchContent="";
	private Button search=null;
	private View searchLine=null;
	private Button searchLineButton=null;
	private EditText searchEditor=null;
	private boolean isSearching=false;
	
	private Button addone=null;
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        //以下调整优先级 防止被杀
        ActivityManager activityManger=(ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList=activityManger.getRunningAppProcesses();
         if(list!=null)
        for(int i=0;i<list.size();i++)
        {
            ActivityManager.RunningAppProcessInfo apinfo=appList.get(i);
                        String[] pkgList=apinfo.pkgList;
       if(apinfo.processName.contains("org.cc.android.noteeasy"))
             apinfo.importance=ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE;//这样子就非常安全了
                 
      }            
        

		
        handler=new Handler(){
			@Override
			public void handleMessage(Message msg) {
				refresh.clearAnimation();
				refresh.setEnabled(true);
				Toast.makeText(MainActivity.this,R.string.main_load,Toast.LENGTH_SHORT).show();
				super.handleMessage(msg);
			}
        	
        };	
        
        sp=this.getSharedPreferences("condition",MODE_PRIVATE);
        editor=sp.edit();
        //0代表未定义 1代表打开 -1代表关闭
        open=sp.getInt("open", 0);
        if(open==0||open==1){
        	showFloatView();
        }
        
        menu = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
//        //改变字体样式
//        mFace=Typeface.createFromAsset(getAssets(),"fonts/cartoon.TTF");
//        title=(TextView)this.findViewById(R.id.title);   
//        title.setTypeface(mFace);
        
        list=new ArrayList<Map<String,String>>();
        mylv=(ListView)this.findViewById(R.id.mylv);
        
        refresh=(Button)super.findViewById(R.id.manual_fresh);
        
        helper=new MyOpenHelper(this);
        
        refresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Animation ani=AnimationUtils.loadAnimation(MainActivity.this, R.anim.refresh_anim);
				refresh.startAnimation(ani);
				refresh.setEnabled(false);
				MainActivity.this.refresh();				
				//这样的话 刷新的时候你至少也要给我转一圈啊......
				final Message ms=new Message();
				ms.setTarget(handler);	
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Looper.prepare();
						try {
							Thread.sleep(800);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						ms.sendToTarget();
						Looper.loop();
					}
				}).start();
			}
		});
        
        
        searchLine=super.findViewById(R.id.main_search_line);
        search=(Button)super.findViewById(R.id.main_search_button);
        searchEditor=(EditText)super.findViewById(R.id.main_search_editor);
        searchLineButton=(Button)super.findViewById(R.id.mian_search_line_button);
        
        addone=(Button)super.findViewById(R.id.main_add_one);
        addone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, ShowNote.class);
				MainActivity.this.startActivityForResult(intent,1);
			}
		});
        initSearchLine();
        
        //加载数据
        loadNote();        
        //初始化ListView
        initListView();
    }
    
    //打开浮游框
    //更新标志
    public void showFloatView(){
//    	System.out.println("打开");
        Intent intent=new Intent(this,ShowService.class);
        intent.putExtra("open", true);
        editor.putInt("open", 1);  //1代表已经打开了
        open=1;
        editor.commit();
        super.startService(intent);
    }
    
    
    public void closeFloatView(){
//    	System.out.println("关闭");
        Intent intent=new Intent(this,ShowService.class);
        intent.putExtra("open", false);
        editor.putInt("open", -1); //-1代表关闭状态
        open=-1;
        editor.commit();
        super.startService(intent);
    }
    
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode()==KeyEvent.KEYCODE_MENU){
			showPopupWindow();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
		
	private void showPopupWindow(){
		//System.out.println("showPopupWindow--->"+open);
		if(open==1){		
            this.menu.changeText(R.string.alert_dialog_close);		
            this.menu.changeBG(R.drawable.btn_style_alert_dialog_special);
		}else{
			if(open==-1){
				this.menu.changeText(R.string.alert_dialog_open);
	            this.menu.changeBG(R.drawable.btn_style_alert_dialog_special_2);
			}
		}		
		menu.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	
	//加载数据库数据 不会刷新 没有进行分页操作
	private void loadNote(){
		DataBaseOperator db=new DataBaseOperator(helper.getReadableDatabase());
		list.clear();
		list.addAll(db.loadAll());
	}
	
	private void loadSearchNote(){
		DataBaseOperator db=new DataBaseOperator(helper.getReadableDatabase());
		list.clear();
		list.addAll(db.loadByContent(searchContent));
	}
	
	
	//初始化ListView
	private void initListView(){
		//将ListView与上下文绑定
		this.registerForContextMenu(mylv);
		adapter=new SimpleAdapter(this, list, R.layout.listview_layout,
				new String[]{"content_short","cdate"}, new int[]{R.id.my_content,R.id.mydate});
		mylv.setAdapter(adapter);
		mylv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String,String> map=(Map<String,String>)mylv.getItemAtPosition(position);
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, ShowNote.class);
				intent.putExtra("content", map.get("content"));
				intent.putExtra("_id", map.get("_id"));
				MainActivity.this.startActivityForResult(intent,1);
			}
		});
	}
	
	private void initSearchLine(){
		
	    search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			   if(isSearching){
				//如果SearchLine打开的话
				   Animation anim=AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_to_left);
				   anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}					
					@Override
					public void onAnimationEnd(Animation animation) {
						searchLine.setVisibility(View.GONE);
					}
				});				
				   searchLine.startAnimation(anim);
				   isSearching=false;
				   searchContent="";
			   }else{
				//如果SearchLine关闭的话
				   searchLine.setVisibility(View.VISIBLE);
				   Animation anim=AnimationUtils.loadAnimation(MainActivity.this, R.anim.in_from_right_2);
				   searchLine.startAnimation(anim);
				   isSearching=true;
			   }
			}
		});
	    
	    searchLineButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchContent=searchEditor.getText().toString();
			   if(searchContent==null||"".equals(searchContent.trim())){
				   Toast.makeText(MainActivity.this, R.string.main_toast, Toast.LENGTH_SHORT).show();
			       return;
			   }else{
				   refresh();
			   }
			}
		});
	}
	
	//刷新ListView的内容
	private void refresh(){
		//当不是处于搜索或者搜索内容为空时 载入全部的
		if(!isSearching||"".equals(searchContent.trim())){
		   this.loadNote();
		}else{
		//不然载入要搜索的
		   this.loadSearchNote();
		}
		adapter.notifyDataSetChanged();
	}
	
	
	private void deleteNoteAt(int position){
	    Map<String,String> map=(Map<String,String>)this.mylv.getItemAtPosition(position);
//	    Toast.makeText(this, "序号"+map.get("_id"), Toast.LENGTH_SHORT).show();
	    DataBaseOperator db=new DataBaseOperator(helper.getWritableDatabase());
	    db.delete(Integer.valueOf(map.get("_id")));
	}

	
	private void copyToClipBoard(int position){
		Map<String,String> map=(Map<String,String>)this.mylv.getItemAtPosition(position);
		ClipboardManager cm=(ClipboardManager)this.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(map.get("content"));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.main_choose);
		menu.add(Menu.NONE, Menu.FIRST+1, 1, R.string.main_delete);
		menu.add(Menu.NONE,Menu.FIRST+2,2,R.string.main_copy);
		menu.add(Menu.NONE,Menu.FIRST+3,3,R.string.main_manage);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int position=((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
		switch(item.getItemId()){
		case Menu.FIRST+1:
			//删除内容并且重新更新
		    deleteNoteAt(position);
		    refresh();
			break;
		case Menu.FIRST+2:
			//复制内容到剪贴板
			copyToClipBoard(position);
		    break;
		case Menu.FIRST+3:
			toManageNote();
		    break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void toManageNote(){
		Intent intent=new Intent(this,ManageNote.class);
		this.startActivity(intent);
	}
    //结果返回时
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(resultCode==RESULT_OK){
    	  refresh();
      }
	}
	

    private OnClickListener  itemsOnClick = new OnClickListener(){

		public void onClick(View v) {
			menu.dismiss();
			switch (v.getId()) {
			case R.id.btn_cont_float:
				if(open==1){
					MainActivity.this.closeFloatView();
					}else{
						if(open==-1){
							MainActivity.this.showFloatView();
						}
					}
				break;
			case R.id.btn_about:
				Intent intent=new Intent(MainActivity.this,About.class);
				MainActivity.this.startActivity(intent);
				MainActivity.this.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			case R.id.btn_quit:
//				System.out.println("到这里了");
				MainActivity.this.finish();
				break;
			}				
		}    	
    };

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		loadNote();		
		refresh();
		super.onRestart();
	}

}
