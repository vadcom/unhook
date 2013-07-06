/*   _   _       _                 _    
	| | | |_ __ | |__   ___   ___ | | __
	| | | | '_ \| '_ \ / _ \ / _ \| |/ /
	| |_| | | | | | | | (_) | (_) |   < 
	 \___/|_| |_|_| |_|\___/ \___/|_|\_\
*/	 
package com.vadcom.unhook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.vadcom.unhook.UnhookData.UnhookLine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.EdgeEffect;
import android.widget.ListView;
import android.widget.SimpleAdapter;
/**
 * Отображение списка расцепов и работа с ними
 * @author Дубина Вадим
 */
public class HookActivity extends Activity {	
	// имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT1 = "text1";
    final String ATTRIBUTE_NAME_TEXT2 = "text2";
    final String ATTRIBUTE_NAME_TEXT3 = "text3";
    final String ATTRIBUTE_NAME_UKAZ = "image";
    final String ATTRIBUTE_NAME_BACK = "back";
  
    ListView lvSimple;
    SimpleAdapter sAdapter;
    UnhookData unhooks=new UnhookData();		// Расцепы
    
    ArrayList<Map<String, Object>> data;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		lvSimple = (ListView) findViewById(R.id.lvSimple);
		/*
		lvSimple.setOnItemClickListener (new AdapterView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				if (pos+1==unhooks.currentLine) {
					((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine-1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.clean);
					unhooks.currentLine++;
					if (unhooks.currentLine<=unhooks.getCount()) {
						((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine-1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.pointer);
					}
					sAdapter.notifyDataSetChanged ();
				}
			}
			
		});	
		*/
		lvSimple.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {
				pos--;
				if (pos==unhooks.currentLine) {
					((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine+1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.check);
					unhooks.currentLine++;
					if (unhooks.currentLine<unhooks.getCount()) {
						((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine+1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.pointer);
					}
	    			new CommThread(CommThread.UNHOOK_COMM, ConnectThread.mmSocket, handler).start(); // Команда серверу
					sAdapter.notifyDataSetChanged ();
					return true;
				}
				return false;
			}
			
		});
		/*
		lvSimple.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
		View footer = getLayoutInflater().inflate(R.layout.bottom, null);
		View header = getLayoutInflater().inflate(R.layout.bottom, null);
		lvSimple.addFooterView(footer);
		lvSimple.addHeaderView(header);
		
		// lvSimple.setOverscrollFooter(R.drawable.red);
		//EdgeEffect ef=new EdgeEffect(this);
		//ef.setSize(100, 100);
		// lvSimple.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
		
	}
	
	/**
	 * Обработчик ответов от потока команд 
	 */
	Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        	switch (msg.what) {
			case CommThread.READ_COMM:{
				// Прочитан список расцепов
				unhooks=(UnhookData)msg.obj;
				refreshList();
				break;
			}
			default: System.err.println("Неизвестный ответ от потока команд");				        		
        	}        	
        }
    };
    /**
     * Обновление списка отображаемых расцепов
     */
    private void refreshList(){
	    // упаковываем данные в понятную для адаптера структуру
	    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(unhooks.getCount());
	    Map<String, Object> m;
	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = { ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2,ATTRIBUTE_NAME_TEXT3,
	    				  ATTRIBUTE_NAME_UKAZ,ATTRIBUTE_NAME_BACK};
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.textNomer, R.id.textCount,  R.id.textVagon,R.id.imageUkaz,R.id.imageView1};
	    
	    for (int i = 0; i < unhooks.getCount(); i++) {
		      m = new HashMap<String, Object>();
		      UnhookLine line=unhooks.getLine(i);		      
		      m.put(ATTRIBUTE_NAME_TEXT1, line.nomer);
		      m.put(ATTRIBUTE_NAME_TEXT2, line.count);
		      m.put(ATTRIBUTE_NAME_TEXT3, line.vagon);
		      // фон
	  	      if (line.color==1) {
	  	    	  m.put(ATTRIBUTE_NAME_BACK, R.drawable.red);
	  	      } else {	  	      
	  	    	  m.put(ATTRIBUTE_NAME_BACK, R.drawable.clean);
	  	      }	  	      
	  	      // Указатель или выполнено
	  	      if (i==unhooks.currentLine) {
	  	    	m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.pointer);
	  	      } else {
	  	    	  if (i<unhooks.currentLine) m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.check);
	  	    	  else m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.clean);   
	  	      }
		      data.add(m);
		    }
	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.item,
	        from, to);
	    lvSimple.setAdapter(sAdapter);
	    //lvSimple.setVerticalFadingEdgeEnabled(true);
	    //lvSimple.smoothScrollToPosition (12);
    }
    
    @Override
    public void onBackPressed (){
    	if (unhooks.currentLine>0) {
			BackStep();
    	}    	
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	new CommThread(CommThread.READ_COMM, ConnectThread.mmSocket, handler).start();
    }
    
    // Отмена расцепа
    private void BackStep(){
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
    			HookActivity.this);
    	 
    	// Setting Dialog Title
    	alertDialog2.setTitle("Подтвердите отмену");
    	 
    	// Setting Dialog Message
    	alertDialog2.setMessage("Вы точно хотите отменить расцеп №"+unhooks.getLine(unhooks.currentLine-1).nomer+" ?");
    	 
    	// Setting Icon to Dialog
    	// alertDialog2.setIcon(R.drawable.delete);
    	 
    	// Setting Positive "Yes" Btn
    	alertDialog2.setPositiveButton("ДА",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	                // Write your code here to execute after dialog
    	    	    	// По возврату отменяем выполненную расцепку
    	            	if (unhooks.currentLine<unhooks.getCount()) {
    	            		((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine+1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.clean);
    	            	}
    	    			unhooks.currentLine--;
    	    			if (unhooks.currentLine<unhooks.getCount()) {
    	    				((Map<String, Object>)lvSimple.getItemAtPosition(unhooks.currentLine+1)).put(ATTRIBUTE_NAME_UKAZ, R.drawable.pointer);
    	    			}
    	    			new CommThread(CommThread.BACK_COMM, ConnectThread.mmSocket, handler).start(); // Команда серверу
    	    			sAdapter.notifyDataSetChanged ();
    	    			dialog.cancel();
    	            }
    	        });
    	// Setting Negative "NO" Btn
    	alertDialog2.setNegativeButton("НЕТ",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	                // Write your code here to execute after dialog
    	                dialog.cancel();
    	            }
    	        });
    	 
    	// Showing Alert Dialog
    	alertDialog2.show();
    }
}
