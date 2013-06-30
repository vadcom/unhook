/*   _   _       _                 _    
	| | | |_ __ | |__   ___   ___ | | __
	| | | | '_ \| '_ \ / _ \ / _ \| |/ /
	| |_| | | | | | | | (_) | (_) |   < 
	 \___/|_| |_|_| |_|\___/ \___/|_|\_\
*/	 
package com.vadcom.unhook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
/**
 * Исполнение команды в отдельном потоке
 * @author Дубина Вадим
 */
public class CommThread extends Thread {
	private InputStream mmInStream;
    private OutputStream mmOutStream;
    private Handler mHandler;
    private int mCommand;
    private BufferedReader bReader;
    private PrintWriter pWriter;
    // enum Comm {READ,UNHOOK,CHECK}; // Поддерживаемые комманды
    
    final static int READ_COMM=1; 
    final static int UNHOOK_COMM=2; 
    final static int CHECK_COMM=3; 
    
//    public class UnhookData
    
	CommThread(int сommand,BluetoothSocket socket,Handler handler){
		mCommand=сommand;
		mHandler=handler;
        try {
        	mmInStream = socket.getInputStream();
            mmOutStream = socket.getOutputStream();
            bReader=new BufferedReader(new InputStreamReader(mmInStream));
            pWriter=new PrintWriter(new OutputStreamWriter(mmOutStream));
            
        } catch (IOException e) { 
        	mmInStream=null;
        	mmOutStream=null;
        }
	}
	
	@Override
	public void run(){
		switch (mCommand) {
			case READ_COMM:{
				try {
					doRead();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			}
			default: System.err.println("Неизвестная команда ПГС->ТГС");				
		}
	}
	/**
	 * Получение списка расцепов от табло 
	 * @throws IOException 
	 */
	private void doRead() throws IOException{
		// Отправляем запрос
		String command="read\n";
   	 	write(command.getBytes());
   	 	// Получаем ответ
        String md5=bReader.readLine();
        String currentTxt=bReader.readLine();
        int current=Integer.valueOf(currentTxt);
        String countTxt=bReader.readLine();
        int count=Integer.valueOf(countTxt);
        String unhooks[]=new String[count];
        for (int i=0;i<count;i++) {
        	unhooks[i]=bReader.readLine();
        }
        UnhookData data=new UnhookData();
        data.currentLine=current;
        for (int i=0;i<unhooks.length;i++) {
        	data.add(unhooks[i]);
        }
        mHandler.obtainMessage(READ_COMM, data).sendToTarget();
	}
	
    private void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) { }
    }
    
    private String read() throws IOException {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        mmInStream.read(buffer);
        return new String(buffer);     	
    }
 
}
