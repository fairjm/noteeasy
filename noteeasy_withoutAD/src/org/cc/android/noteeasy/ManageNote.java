package org.cc.android.noteeasy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.cc.android.noteeasy.database.DataBaseOperator;
import org.cc.android.noteeasy.database.MyOpenHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ManageNote extends Activity {

	private MyOpenHelper helper = null;

	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private ListView lv = null;
	private MyAdapter adapter = null;

	private Button del = null;
	private Button save = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.manage_layout);
		lv=(ListView)super.findViewById(R.id.manage_list);
		helper=new MyOpenHelper(this);
		initListView();
		
        del=(Button)super.findViewById(R.id.manage_delete);
        save=(Button)super.findViewById(R.id.manage_save);
        
        del.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean change=false;
				for(Map<String,String> map:list){
					if(map.get("select").equals("true")){
						change=true;
						int id=Integer.parseInt(map.get("_id"));
						new DataBaseOperator(helper.getWritableDatabase()).delete(id);
					}
				}
				if(change){
				loadNote();
				refresh();
				Toast.makeText(ManageNote.this, R.string.manage_have_deleted, Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(ManageNote.this, R.string.manage_no_checked, Toast.LENGTH_SHORT).show();
				}
			}
		});
        
        
        save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					    String dir=new SimpleDateFormat("yyyyMMdd_hhmmss_E").format(new Date());
					    String path=null;
					    boolean change=false;
					    for(Map<String,String> map:list){
							if(map.get("select").equals("true")){
								change=true;
								String name=(map.get("_id")+"_"+map.get("content_short")+".txt");
					            File file=new File(Environment.getExternalStorageDirectory()+File.separator+
					        	        "EasyNoteBackUp"+File.separator+
			        	        dir+File.separator+name);
			        	        if(!file.getParentFile().exists()){
			        		      file.getParentFile().mkdirs();
			        		      path=file.getParentFile().getPath();
			        	        }
			                    PrintStream ps=null;
			                    try {
									ps=new PrintStream(new FileOutputStream(file));
									ps.println(map.get("content"));
									ps.print(map.get("cdate"));
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								}
							} //if
					    } //for
					    Resources res=ManageNote.this.getResources();
					    if(change){
					    String info=res.getString(R.string.manage_have_saved)+path;
				        Toast.makeText(ManageNote.this,info, Toast.LENGTH_SHORT).show();
					    }else{
					    	Toast.makeText(ManageNote.this,R.string.manage_no_checked, Toast.LENGTH_SHORT).show();
					    }
				 }else{
					 Toast.makeText(ManageNote.this,R.string.manage_save_error, Toast.LENGTH_SHORT).show();
				 }
			}
        });

	}

	private void initListView() {
		adapter = new MyAdapter(list, this);
		loadNote();
		refresh();
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Map<String,String> map=(Map<String,String>)lv.getItemAtPosition(position);
				Intent intent=new Intent();
				intent.setClass(ManageNote.this, ShowNote.class);
				intent.putExtra("content", map.get("content"));
				intent.putExtra("_id", map.get("_id"));
				ManageNote.this.startActivityForResult(intent,1);
			}
		});
	}

	// 加载数据库数据 不会刷新 没有进行分页操作
	private void loadNote() {
		DataBaseOperator db = new DataBaseOperator(helper.getReadableDatabase());
		list.clear();
		list.addAll(db.loadAll());
		for (Map<String, String> map : list) {
			map.put("select", "false");
		}
	}

	private void refresh() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			this.finish();
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      if(resultCode==RESULT_OK){
	    	  refresh();
	      }
		}
	
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(Menu.NONE, Menu.FIRST, 0,R.string.manage_select_all);
		menu.add(Menu.NONE, Menu.FIRST+1, 1,R.string.manage_unselect_all);
		menu.add(Menu.NONE, Menu.FIRST+2, 2,R.string.manage_cancel_select);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case Menu.FIRST:
			for(Map<String,String> map:list){
				map.put("select", "true");
			}
			adapter.notifyDataSetChanged();
			System.out.println("已经刷新");
			
			break;
		case Menu.FIRST+1:
			for(Map<String,String> map:list){
				map.put("select", "false");
			}
		    adapter.notifyDataSetChanged();
			break;
		}
		return super.onOptionsItemSelected(item);
		
	}

}

class MyAdapter extends BaseAdapter {

	private List<Map<String, String>> list = null;
	private LayoutInflater mInflater = null;

	public MyAdapter(List<Map<String, String>> list, Context context) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View view = mInflater.inflate(R.layout.manage_listview_layout, null);
		((TextView) view.findViewById(R.id.manage_adapter_content))
				.setText(list.get(arg0).get("content_short").toString());
		((TextView) view.findViewById(R.id.manage_adapter_date)).setText(list
				.get(arg0).get("cdate").toString());
		CheckBox cb = (CheckBox) view
				.findViewById(R.id.manage_adapter_checkbox);
		final int po = arg0;
		cb.setChecked(Boolean.parseBoolean(list.get(arg0).get("select")
				.toString()));
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					list.get(po).put("select", "true");
					System.out.println(po + "选中");
				} else {
					list.get(po).put("select", "false");
				}

			}
		});

		return view;
	}

}