
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
        
        String lineRead=bReader.readLine();
        System.out.println(lineRead);
        if (lineRead.equalsIgnoreCase("read")) {
        	commRead(pWriter);
        };
        
        //send response to spp client
        /*
        pWriter.write("Response String from SPP Server\r\n");
        pWriter.flush();
        */
        }
        //pWriter.close();
        //streamConnNotifier.close();
    }
    
    private void commRead(PrintWriter pWriter){
    	pWriter.write(md5summ+'\n');
    	pWriter.write(CurrentUnhook+'\n');
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
    
    public static void main(String[] args) throws IOException {
        
        //display local device address and name
        LocalDevice localDevice = LocalDevice.getLocalDevice();
        System.out.println("Address: "+localDevice.getBluetoothAddress());
        System.out.println("Name: "+localDevice.getFriendlyName());
       
        SampleSPPServer sampleSPPServer=new SampleSPPServer();
        sampleSPPServer.startServer();
       
    }
    
}
