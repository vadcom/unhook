
package com.vadcom.blueserver;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
 
import javax.bluetooth.*;
import javax.microedition.io.*;
 
/**
* Class that implements an SPP Server which accepts single line of
* message from an SPP client and sends a single line of response to the client.
*/
public class SampleSPPServer {
	
	ArrayList<String> unhooks=new ArrayList<String>();
	int CurrentUnhook=0;
	String md5summ="0123210";
   
    //start server
    private void startServer() throws IOException{
 
    	load("rospusk1.txt");
    	show();
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
       
        //open server url
	        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
	       
	        //Wait for client connection
	        System.out.println("connectionString: "+connectionString);
	        System.out.println("\nServer Started. Waiting for clients to connect...");
	        StreamConnection connection=streamConnNotifier.acceptAndOpen();
	        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
	        System.out.println("Remote device address: "+dev.getBluetoothAddress());
	        System.out.println("Remote device name: "+dev.getFriendlyName(true));
	              
	        //read string from spp client
	        InputStream inStream=connection.openInputStream();
	        BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
	        OutputStream outStream=connection.openOutputStream();
	        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));        
	        while (true) {
		        System.out.println("Ожидаем поступления команды...");
		        String lineRead=bReader.readLine();
		        if (lineRead==null) {
		        	connection.close();
		        	return;
		        }
		        if (lineRead.equalsIgnoreCase("read")) {
		        	commRead(pWriter);
		        } else {
		            System.out.println("Неизвестная команда: "+lineRead);        	
		        }        
	        }	        
    }
    /**
     * Команда чтения списка расцепов
     * @param pWriter
     */
    private void commRead(PrintWriter pWriter){
    	System.out.println("Выполняем команду READ");
    	pWriter.write(md5summ+'\n');
    	pWriter.write(String.valueOf(CurrentUnhook)+'\n');
    	pWriter.write(String.valueOf(unhooks.size())+'\n');
    	for (String line:unhooks) {
    		pWriter.write(line+'\n');
    	}
        pWriter.flush();
    }
 
 
    
    private void load(String file) throws IOException{
    	unhooks.clear();
    	BufferedReader br=new BufferedReader(new FileReader(file));
    	while (br.ready()) {
    		unhooks.add(br.readLine());
    	};   
    	br.close();
    	if (unhooks.size()>0) CurrentUnhook=1;
    }
    
    private void show(){
    	System.out.println("--- список расцепов ---");
    	for (String line:unhooks) {
    		System.out.println(line);
    	}
    	System.out.println("Текущий расцеп № "+CurrentUnhook); 
    	System.out.println("-----------------------");
    }
    
    public static void main(String[] args) {
        
    	System.out.println(" ____  _            ____");                           
    	System.out.println("| __ )| |_   _  ___/ ___|  ___ _ ____   _____ _ __"); 
    	System.out.println("|  _ \\| | | | |/ _ \\___ \\ / _ \\ '__\\ \\ / / _ \\ '__|");
    	System.out.println("| |_) | | |_| |  __/___) |  __/ |   \\ V /  __/ |   ");
    	System.out.println("|____/|_|\\__,_|\\___|____/ \\___|_|    \\_/ \\___|_|");    	

    	// while (true) {
	        //display local device address and name
	        LocalDevice localDevice;
			try {
				localDevice = LocalDevice.getLocalDevice();
		        System.out.println("Address: "+localDevice.getBluetoothAddress());
		        System.out.println("Name: "+localDevice.getFriendlyName());       
		        SampleSPPServer sampleSPPServer=new SampleSPPServer();        
		        sampleSPPServer.startServer();
			} catch (BluetoothStateException e) {
			} catch (IOException e) {
			}
			System.out.println("Соединение разорванно.");
    	// }       
    	
    }
    
}
