/*   _   _       _                 _    
	| | | |_ __ | |__   ___   ___ | | __
	| | | | '_ \| '_ \ / _ \ / _ \| |/ /
	| |_| | | | | | | | (_) | (_) |   < 
	 \___/|_| |_|_| |_|\___/ \___/|_|\_\
*/	 
package com.vadcom.unhook;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
/**
 * Поток для установки соединения
 * 
 */
public class ConnectThread extends Thread {
	private final UUID MY_UUID =  UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	public static BluetoothSocket mmSocket=null;
	private Handler mHandler;
	public static final int BLUETOUCH_CONNECTED = 0;
	public static final int BLUETOUCH_NOTCONNECTED = -1;
	
	ConnectThread (BluetoothDevice device,Handler handler){
        mHandler=handler;
        try {
			mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
        	mmSocket = null;
            mHandler.sendEmptyMessage(BLUETOUCH_NOTCONNECTED);
        }        		
	}
	/**
	 * Выполнение соединения
	 */
	public void run() {
        // Cancel discovery because it will slow down the connection
		// MainActivity.mBluetoothAdapter.cancelDiscovery(); 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            // Посылаем сообщение о том что не удалось соединиться
            mHandler.sendEmptyMessage(BLUETOUCH_NOTCONNECTED);
            return;
        }
        mHandler.sendEmptyMessage(BLUETOUCH_CONNECTED);
    }

}
