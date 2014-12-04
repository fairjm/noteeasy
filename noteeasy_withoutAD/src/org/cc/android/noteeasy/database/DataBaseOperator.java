package org.cc.android.noteeasy.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseOperator {
  private SQLiteDatabase sds=null;
  private String tablename="NOTES";
  public DataBaseOperator(SQLiteDatabase sds){
	  this.sds=sds;
  }
  
  public void insert(String content,Date date){
     ContentValues cv=new ContentValues();
     SimpleDateFormat sd=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss E");
     cv.put("content", content);
     cv.put("cdate",sd.format(date));
     sds.insert(tablename, null,cv);
     this.sds.close();
  }
  
  public void update(int id,String content){
    String where="_id=?";
    String args[]=new String[]{String.valueOf(id)};
    ContentValues cv=new ContentValues();
    cv.put("content",content);
    this.sds.update(tablename, cv, where, args);
    this.sds.close();
  }
  
  public void delete(int id){
	String where="_id=?";
	String args[]=new String[]{String.valueOf(id)};
    this.sds.delete(tablename, where, args);
    this.sds.close();
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public List loadAll(){
	  List list=new ArrayList();
	  String sql="select _id,content,cdate from "+tablename+" order by cdate desc";
      Cursor cur=this.sds.rawQuery(sql, null);
	  for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
		  Map map=new HashMap();
		  map.put("_id",String.valueOf(cur.getInt(0)));
		  map.put("content", cur.getString(1));
		  if(cur.getString(1).length()<15){
			  map.put("content_short", cur.getString(1));
		  }else{
			  map.put("content_short", cur.getString(1).substring(0, 10)+"......");
		  }
		  map.put("cdate", cur.getString(2));
		  list.add(map);
		  }	
	  sds.close();
	  System.out.println(list);
	  return list;
  }
 
 @SuppressWarnings({ "unchecked", "rawtypes" })
 public List loadByContent(String content){
	  List list=new ArrayList();
	  String sql="select _id,content,cdate from "+tablename+" where content like ? order by cdate desc";
     Cursor cur=this.sds.rawQuery(sql, new String[]{"%"+content+"%"});
	  for(cur.moveToFirst();!cur.isAfterLast();cur.moveToNext()){
		  Map map=new HashMap();
		  map.put("_id",String.valueOf(cur.getInt(0)));
		  map.put("content", cur.getString(1));
		  if(cur.getString(1).length()<15){
			  map.put("content_short", cur.getString(1));
		  }else{
			  map.put("content_short", cur.getString(1).substring(0, 10)+"......");
		  }
		  map.put("cdate", cur.getString(2));
		  list.add(map);
		  }	
	  sds.close();
	  System.out.println(list);
	  return list;
 }
 
  
 public int getCount() { // 返回记录数
		int count = 0;
		String sql = "SELECT COUNT(id) FROM " + tablename; // 查询SQL
		Cursor result = this.sds.rawQuery(sql, null);
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) { // 采用循环的方式检索数据
			count = result.getInt(0);
		}
		sds.close();
		return count;
	} 
  
}
