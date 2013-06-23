package com.vadcom.blueclient;

import java.util.Set;

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

public class ClientActivity extends Activity {

	static BluetoothAdapter mBluetoothAdapter;
	TextView tv;
	TextView td;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		tv=(TextView) findViewById(R.id.message);
		td=(TextView) findViewById(R.id.data);
		if (mBluetoothAdapter==null) {
			tv.setText("No bluetouch adapter!");
		} else {
			if (!mBluetoothAdapter.isEnabled()) tv.setText("Adapter is not enabled!");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
		return true;
	}
	
	public void doConnect(View view){
		// Здесь кодим SPP соединение
		final int REQUEST_ENABLE_BT=1; 
		if (mBluetoothAdapter != null) {
			if (!mBluetoothAdapter.isEnabled()) {
			    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			} else {
				// Можно подключаться 
				if (mBluetoothAdapter.isDiscovering()) {
					tv.setText("Идет поиск попробуйте позже...");
				} else {
					tv.setText("Список:");				
					Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
					// If there are paired devices
					if (pairedDevices.size() > 0) {
						ArrayAdapter<String> mArrayAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
						Thread thread=null;
					    // Loop through paired devices
					    for (BluetoothDevice device : pairedDevices) {
					        // Add the name and address to an array adapter to show in a ListView
					        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					        if (device.getName().equalsIgnoreCase("notebook-1")){
					        	thread=new ConnectedThread(device,handler);
					        }
					    }
					    ListView lv=(ListView) findViewById(R.id.listView1);
					    lv.setAdapter(mArrayAdapter);
					    // Стартуем поток для обмена данными
					    if (thread!=null) thread.start();
					}
				}
				
			}
		}
		
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data){
		if (resultCode==RESULT_CANCELED) {
			tv.setText("Adapter is not enabled!");
		} else tv.setText("Adapter enabled!");
	}
	
	
	Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	byte[] buffer=(byte[])msg.obj;
        	
        		td.setText("Данные получены:"+new String(buffer));        	
        	};
      };
	
	@Override
	public void onStart(){
		super.onStart();
	}

}
