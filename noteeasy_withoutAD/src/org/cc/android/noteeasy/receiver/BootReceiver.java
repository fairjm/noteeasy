package org.cc.android.noteeasy.receiver;

import org.cc.android.noteeasy.floatview.ShowService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences sp=null;
	@Override
	public void onReceive(Context context, Intent intent) {
          sp=context.getSharedPreferences("condition", Context.MODE_PRIVATE);          
          if(sp.getInt("open", 0)==1){
        	  Toast.makeText(context,String.valueOf(sp.getInt("open", 0)),Toast.LENGTH_LONG).show();
              Intent send=new Intent(context,ShowService.class);
              send.putExtra("open", true);
              context.startService(send);
          }
	}

}
