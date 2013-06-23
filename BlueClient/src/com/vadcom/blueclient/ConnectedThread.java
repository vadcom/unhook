package com.vadcom.blueclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

/*
 * Сеанс связи по bluetooch
 */
public class ConnectedThread extends Thread {
	private static UUID MY_UUID;// =  UUID	.fromString("1101");
	private static final int MESSAGE_READ = 0;
	private final BluetoothSocket mmSocket;
	private final Handler mHandler;
	
    public ConnectedThread(BluetoothDevice device,Handler handler) {
    	//super();
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mHandler=handler;
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            MY_UUID =  UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            //MY_UUID = UUID.randomUUID();
            System.out.println("MY_UUID:"+MY_UUID);
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) { }
        mmSocket = tmp;
    }
    
    public void run() {
        // Cancel discovery because it will slow down the connection
    	ClientActivity.mBluetoothAdapter.cancelDiscovery();
 
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { }
            return;
        }
 
        // Do work to manage the connection (in a separate thread)
        manageConnectedSocket(mmSocket);
    }

    InputStream mmInStream;
    OutputStream mmOutStream;
    
    private void manageConnectedSocket(BluetoothSocket mmSocket){
        
         InputStream tmpIn = null;
         OutputStream tmpOut = null;
  
         // Get the input and output streams, using temp objects because
         // member streams are final
         try {
             tmpIn = mmSocket.getInputStream();
             tmpOut = mmSocket.getOutputStream();
         } catch (IOException e) { }
  
         mmInStream = tmpIn;
         mmOutStream = tmpOut;
         
         // Write - read
         byte[] buffer = new byte[1024];  // buffer store for the stream
         int bytes; // bytes returned from read()
  
         // Keep listening to the InputStream until an exception occurs
         while (true) {
             try {
            	 String hello="Hello from telephone!\n";
            	 write(hello.getBytes());
                 // Read from the InputStream
                 bytes = mmInStream.read(buffer);
                 // Send the obtained bytes to the UI activity
                 mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                         .sendToTarget();
             } catch (IOException e) {
                 break;
             }             
         }             
         cancel();    	
    }
    
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }    
 

}
