package com.vadcom.unhook;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
// Стартовый экран
public class MainActivity extends Activity {

	Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		timer=new Timer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onClick(View view) {
		startHook();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		TextView tvLoad = (TextView) findViewById(R.id.textView1);		
		String get_data  =  getString (R.string.get_data); 		
		tvLoad.setText(get_data);
		// Запуск таймера
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				//timer.cancel();
				timer.purge();
				handler.sendEmptyMessage(0);			
			}}
		, 1000);
	}
	/*
	TimerTask tt=new TimerTask(){

		@Override
		public void run() {
			timer.cancel();
			timer.purge();
			handler.sendEmptyMessage(0);			
		}
		
	};
	*/
	Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	startHook();
        	};
      };
      
	private void startHook(){
		TextView tvLoad = (TextView) findViewById(R.id.textView1);		
		String getted  =  getString (R.string.getted); 		
		tvLoad.setText(getted);
		
		Intent myIntent = new Intent(MainActivity.this, HookActivity.class);
		MainActivity.this.startActivity( myIntent);    		
	}

}
