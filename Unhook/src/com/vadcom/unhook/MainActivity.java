/*   _   _       _                 _    
	| | | |_ __ | |__   ___   ___ | | __
	| | | | '_ \| '_ \ / _ \ / _ \| |/ /
	| |_| | | | | | | | (_) | (_) |   < 
	 \___/|_| |_|_| |_|\___/ \___/|_|\_\
*/	 
package com.vadcom.unhook;

import java.util.Set;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
// Стартовый экран
public class MainActivity extends Activity {
	final int REQUEST_ENABLE_BT=1;
	static BluetoothAdapter mBluetoothAdapter;
	private TextView tv;
	//Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//timer=new Timer();
	}
	
/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
*/
	
	/**
	 * Нажатие на кнопку "соединиться"
	 * @param view
	 */
	public void onClick(View view) {
		// Тушим кнопку
		view.setVisibility(View.INVISIBLE);
		// Стартуем соединение
		StartConnect();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (ConnectThread.mmSocket==null) {
			StartConnect();
		} else {
			startHook();
		}
		/*
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
		*/
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
	
	/**
	 * Обработчик сообщений от потока установки соединения
	 */
	Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
        		case ConnectThread.BLUETOUCH_CONNECTED:{
        			startHook();
        			break;
        		}
        		case ConnectThread.BLUETOUCH_NOTCONNECTED:{
        			ErrorConnect();
        			break;
        		}
        		default:System.err.println("Неопознанный тип сообщения");
    		}        		
        };
    };
    /**
     * Стартуем основную активность  
     */
	private void startHook(){
		// Пишем о начале получения данных
		TextView tvLoad = (TextView) findViewById(R.id.textView1);		
		String getted  =  getString (R.string.get_data); 		
		tvLoad.setText(getted);
		
		Intent myIntent = new Intent(MainActivity.this, HookActivity.class);
		MainActivity.this.startActivity( myIntent);    		
	}
	/**
	 * Начало соединения
	 */
	private void StartConnect() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		TextView tv = (TextView) findViewById(R.id.textView1);		
		if (mBluetoothAdapter==null) {
			// Зажигаем надпись об отсутствии адаптера
			tv.setText("No bluetouch adapter!");					
		} else {
			if (!mBluetoothAdapter.isEnabled()) {
				// Запрос на включение адаптера
				tv.setText("Adapter is not enabled!");
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);							
			} else {
				// Выбираем нужное устройство
				BluetoothDevice dev=getDevice("notebook-1");
				if (dev!=null) {				
					// Стартуем соединение
					Thread ct=new ConnectThread(dev,handler);
					ct.start();
				} else {
					tv.setText("Сервер не согласован с ПГС");
					//ErrorConnect();
				}
			}
		}
	}
	/**
	 * Реакция на ошибку соединения
	 */
	private void ErrorConnect() {
		// Зажигаем надпись об ошибке
		TextView tvLoad = (TextView) findViewById(R.id.textView1);		
		String getted  =  getString (R.string.errorConnect); 		
		tvLoad.setText(getted);		
		// Тушим прогрессбар
		findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
		// Показываем кнопку
		findViewById(R.id.buttonConnect).setVisibility(View.VISIBLE);
	}
	/**
	 * 
	 */
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (resultCode==RESULT_CANCELED) {
			tv.setText("Adapter is not enabled!");
		} else {
			// Dluetouch включили, пробуем опять
			tv.setText("Adapter enabled!");
			StartConnect();
		}
	}	
	/**
	 * Выбор устройства из списка согласованных устройств
	 * @param name
	 * @return
	 */
	private BluetoothDevice getDevice(String name) {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        if (device.getName().equalsIgnoreCase("notebook-1")){
		        	return device; 
		        }
		    }
		}
		return null;
	}
		
}
