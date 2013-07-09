/**
 *   DEMO BLUETOUCH SERVER
 */
package com.vadcom.blueserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

/**
 * Класс для работы с одним соединением
 * @author vadim
 *
 */
public class ConnectionThread extends Thread {
	
	SampleSPPServer server;
	StreamConnection connection;
	ArrayList<String> unhooks;
	BufferedReader bReader;
	PrintWriter pWriter;
	String md5summ="0123210";
	
	ConnectionThread (SampleSPPServer server,StreamConnection connection) throws IOException{
		this.server=server;
		this.connection=connection;
        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: "+dev.getBluetoothAddress());
        System.out.println("Remote device name: "+dev.getFriendlyName(true));
              
        //read string from spp client
        InputStream inStream=connection.openInputStream();
        bReader=new BufferedReader(new InputStreamReader(inStream));
        OutputStream outStream=connection.openOutputStream();
        pWriter=new PrintWriter(new OutputStreamWriter(outStream));        		
	}
	
	@Override
	public void run() {
		super.run();
		try {
	        while (true) {
		        System.out.println("Wait command...");
		        String lineRead;
					lineRead = bReader.readLine();
		        if (lineRead==null) {
		        	connection.close();
		        	System.out.println("Connection closed...");
		        	return;
		        }
		        if (lineRead.equalsIgnoreCase("read")) {
		        	commRead(pWriter);
		        	continue;
		        }
		        if (lineRead.equalsIgnoreCase("unhook")) {
		        	commUnhook(pWriter);
		        	continue;
		        } 		        
		        if (lineRead.equalsIgnoreCase("back")) {
		        	commBack(pWriter);
		        	continue;
		        }
		        if (lineRead.equalsIgnoreCase("current")) {
		        	commCurrent(pWriter);
		        	continue;
		        } 		        		        
		        System.out.println("Unknown command: "+lineRead);        		               
	        }	        
		} catch (IOException e) {
        	System.err.println("Ошибка! Соединение закрыто...");
			e.printStackTrace();
		}			
	}
	
    /**
     * Команда чтения списка расцепов
     * @param pWriter
     */
    private void commRead(PrintWriter pWriter){
    	unhooks=server.getUnhooks();
    	System.out.println("Process command READ");
    	pWriter.write(md5summ+'\n');
    	pWriter.write(String.valueOf(server.getCurrent())+'\n');
    	pWriter.write(String.valueOf(unhooks.size())+'\n');
    	for (String line:unhooks) {
    		pWriter.write(line+'\n');
    	}
        pWriter.flush();
    }
    
    private void commUnhook(PrintWriter pWriter){
    	System.out.println("Process command UNHOOK");
    	unhooks=server.getUnhooks();
    	int CurrentUnhook=server.getCurrent();
    	if (CurrentUnhook<unhooks.size()) {
    		CurrentUnhook++;
    		server.setCurrent(CurrentUnhook);
    		if (CurrentUnhook<unhooks.size()) System.out.println("Current unhook -> "+unhooks.get(CurrentUnhook));
    		else System.out.println("All unhooks done.");
    		pWriter.write("ok\n");
    	} else {
    		System.out.println("Error, out of bounds list");
    		System.out.println("Current unhook -> "+unhooks.get(CurrentUnhook));
    		pWriter.write("error\n");
    	}
        pWriter.flush();
    }

    private void commBack(PrintWriter pWriter){
    	System.out.println("Process command BACK");
    	unhooks=server.getUnhooks();
    	int CurrentUnhook=server.getCurrent();
    	if (CurrentUnhook>0) {
    		CurrentUnhook--;
    		server.setCurrent(CurrentUnhook);
    		System.out.println("Current unhook -> "+unhooks.get(CurrentUnhook));
    		pWriter.write("ok\n");
    	} else {
    		System.out.println("Error, out of bounds list");
    		System.out.println("Current unhook -> "+unhooks.get(CurrentUnhook));
    		pWriter.write("error\n");
    	}
        pWriter.flush();
    }
    
    private void commCurrent(PrintWriter pWriter){
    	int CurrentUnhook=server.getCurrent();
    	System.out.println("Process command CURRENT");
		System.out.println("Current unhook -> "+unhooks.get(CurrentUnhook));
    	pWriter.write(String.valueOf(server.getCurrent())+'\n');    	
        pWriter.flush();
    }
	

}
