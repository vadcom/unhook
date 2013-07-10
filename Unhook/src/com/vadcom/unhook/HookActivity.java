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
import java.util.Timer;
import java.util.TimerTask;

import com.vadcom.unhook.UnhookData.UnhookLine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
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
    final String ATTRIBUTE_NAME_METKA = "metka";
    final String ATTRIBUTE_NAME_UKAZ = "image";
    final String ATTRIBUTE_NAME_BACK = "back";
    final String ATTRIBUTE_NAME_TOP = "top";
    final String ATTRIBUTE_NAME_BOTTOM = "bottom";
    final String ATTRIBUTE_NAME_LEFT = "left";
    final String ATTRIBUTE_NAME_RIGHT = "right";
  
    ListView lvSimple;
    SimpleAdapter sAdapter;
    UnhookData unhooks=new UnhookData();		// Расцепы
    
    ArrayList<Map<String, Object>> data;
    Timer tm;
    Vibrator vibro;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		vibro=(Vibrator) getSystemService (VIBRATOR_SERVICE); 
		
		lvSimple = (ListView) findViewById(R.id.lvSimple);
		
        tm=new Timer();
        tm.schedule(timerTask, 5000, 5000);        	// 5 сек			

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
				if (unhooks.currentLine==0) pos--;
				
				if ((pos==unhooks.currentLine) && (unhooks.getCount()>unhooks.currentLine)) {
					if (unhooks.currentLine==0) removeFirstBlank();
					unhooks.currentLine++;
					updateListView();
					lvSimple.smoothScrollToPositionFromTop(unhooks.currentLine, 0);
	    			new CommThread(CommThread.UNHOOK_COMM, ConnectThread.mmSocket, handler).start(); // Команда серверу
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
				loadList();
				break;
			}
			case CommThread.CURRENT_COMM:{
				unhooks.currentLine=msg.arg1;
				updateListView();
				break;
			}
			case CommThread.UNHOOK_COMM:{
				vibro.vibrate(300);
				break;
			}
			default: System.err.println("Неизвестный ответ от потока команд");				        		
        	}        	
        }
    };
    /**
     * Загрузка списка отображаемых расцепов
     */
    private void loadList(){
	    // упаковываем данные в понятную для адаптера структуру
	    data = new ArrayList<Map<String, Object>>(unhooks.getCount());
	    Map<String, Object> m;
	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = { ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2,ATTRIBUTE_NAME_TEXT3,ATTRIBUTE_NAME_METKA,
	    				  ATTRIBUTE_NAME_UKAZ,ATTRIBUTE_NAME_BACK,
	    				  ATTRIBUTE_NAME_TOP,
					      ATTRIBUTE_NAME_BOTTOM,
					      ATTRIBUTE_NAME_LEFT,
					      ATTRIBUTE_NAME_RIGHT};
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.textNomer, R.id.textCount,  R.id.textVagon,R.id.textMetka,R.id.imageUkaz,R.id.imageView1,
	    		     R.id.borderTop,R.id.borderBottom,R.id.borderLeft,R.id.borderRight};	    
	    for (int i = 0; i < unhooks.getCount(); i++) {
		      m = new HashMap<String, Object>();
		      UnhookLine line=unhooks.getLine(i);		      
		      m.put(ATTRIBUTE_NAME_TEXT1, line.nomer);
		      m.put(ATTRIBUTE_NAME_TEXT2, line.count);
		      m.put(ATTRIBUTE_NAME_TEXT3, line.vagon);
		      m.put(ATTRIBUTE_NAME_METKA, line.metka);
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
	  	    	  else m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.nopoint);   
	  	      }
	  	      // Рамка
	  	      // Заполняем все пустым
  	    	  m.put(ATTRIBUTE_NAME_TOP, R.drawable.bproz);   
  	    	  m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.bproz);   
  	    	  m.put(ATTRIBUTE_NAME_LEFT, R.drawable.bproz);   
  	    	  m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.bproz);   
	  	      // Расставляем края
	  	      if ((i>=unhooks.currentLine-1) && (i<unhooks.currentLine+4)) {
	  	    	  m.put(ATTRIBUTE_NAME_LEFT, R.drawable.border);   
	  	    	  m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.border);   	  	    	  
	  	      };
	  	      // верх
	  	      if (i==unhooks.currentLine-1) {
	  	    	  m.put(ATTRIBUTE_NAME_TOP, R.drawable.border);   	  	    	  	  	    	
		  	  }	  	    		
	  	      // низ
	  	      if (i==unhooks.currentLine+3) {
	  	    	  m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.border);   	  	    	  	  	    	
		  	  }
	  	      // Вставляем данные
		      data.add(m);
		    }
	    if (unhooks.currentLine==0) insertFirstBlank();
	    setLastBlank(); // Добавляем пустые при необходимости
	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.item,
	        from, to);
	    lvSimple.setAdapter(sAdapter);
		lvSimple.smoothScrollToPositionFromTop(unhooks.currentLine, 0);
    }
    
    @Override
    public void onBackPressed (){
    	if (unhooks.currentLine>0) {
			backStep();
    	}    	
    }
    
    @Override
    protected void onStart(){
    	super.onStart();
    	new CommThread(CommThread.READ_COMM, ConnectThread.mmSocket, handler).start();
    }
    
    // Отмена расцепа
    private void backStep(){
    	AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
    			HookActivity.this);
    	 
    	// Setting Dialog Title
    	alertDialog2.setTitle("Подтвердите отмену");
    	 
    	// Setting Dialog Message
    	alertDialog2.setMessage("Вы точно хотите отменить расцеп №"+unhooks.getLine(unhooks.currentLine-1).nomer+" ?");

    	// Setting Positive "Yes" Btn
    	alertDialog2.setPositiveButton("ДА",
    	        new DialogInterface.OnClickListener() {
    	            public void onClick(DialogInterface dialog, int which) {
    	                // Write your code here to execute after dialog
    	    	    	// По возврату отменяем выполненную расцепку
    	    			unhooks.currentLine--;
    	    			if (unhooks.currentLine==0) insertFirstBlank();
    	    			updateListView();
    					lvSimple.smoothScrollToPositionFromTop(unhooks.currentLine, 0);
    	    			new CommThread(CommThread.BACK_COMM, ConnectThread.mmSocket, handler).start(); // Команда серверу
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
    
    private void insertFirstBlank(){
    	  HashMap<String, Object> m = new HashMap<String, Object>();
	      m.put(ATTRIBUTE_NAME_TEXT1, "--");
	      m.put(ATTRIBUTE_NAME_TEXT2, "--");
	      m.put(ATTRIBUTE_NAME_TEXT3, "-----");
	      // фон
	      m.put(ATTRIBUTE_NAME_BACK, R.drawable.clean);
	      // Указатель или выполнено
    	  m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.nopoint);   
	      // Рамка
	      // Заполняем все пустым
    	  m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.bproz);   
	      // Расставляем края
    	  m.put(ATTRIBUTE_NAME_LEFT, R.drawable.border);   
    	  m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.border);   	  	    	  
	      // верх
    	  m.put(ATTRIBUTE_NAME_TOP, R.drawable.border);   	  	    	  	  	    	
	      // Вставляем данные
	      data.add(0,m);    	
    }
    
    private void removeFirstBlank(){
    	data.remove(0);
    }
    
    private int getStep(){
    	if (unhooks.currentLine==0) return 1;
    	else return 0;
    }
    
    private void setLastBlank(){
    	// Удаляем старые
    	int i=(unhooks.getCount()+getStep());
    	while (i<data.size()){
    		data.remove(i);
    	}
    	// Определяем сколько надо последних блоков
    	int count=4-(unhooks.getCount()-unhooks.currentLine);
    	for (i=0;i<count;i++) {
	      	HashMap<String, Object> m = new HashMap<String, Object>();
	  	    m.put(ATTRIBUTE_NAME_TEXT1, "--");
	  	    m.put(ATTRIBUTE_NAME_TEXT2, "--");
	  	    m.put(ATTRIBUTE_NAME_TEXT3, "-----");
	  	    // фон
	  	    m.put(ATTRIBUTE_NAME_BACK, R.drawable.clean);
	  	    // Указатель или выполнено
	      	m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.nopoint);   
	  	    // Рамка
	  	    // Заполняем все пустым
	      	m.put(ATTRIBUTE_NAME_TOP, R.drawable.bproz);   
	      	m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.bproz);   	  	    	  	  	    	
	  	    // Расставляем края
	      	m.put(ATTRIBUTE_NAME_LEFT, R.drawable.border);   
	      	m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.border);   	  	    	  
	  	    // низ
	      	if (i==count-1) m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.border);   	  	    	  	  	    	
	  	    // Вставляем данные
    		data.add(m);
    	}
    
    }
    
    // Обновление данных в списке
    private void updateListView(){
	    for (int i = 0; i < unhooks.getCount(); i++) {
	    	  HashMap<String, Object> m = new HashMap<String, Object>();
		      UnhookLine line=unhooks.getLine(i);		      
		      m.put(ATTRIBUTE_NAME_TEXT1, line.nomer);
		      m.put(ATTRIBUTE_NAME_TEXT2, line.count);
		      m.put(ATTRIBUTE_NAME_TEXT3, line.vagon);
		      m.put(ATTRIBUTE_NAME_METKA, line.metka);
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
	  	    	  else m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.nopoint);   
	  	      }
	  	      // Рамка
	  	      // Заполняем все пустым
	    	  m.put(ATTRIBUTE_NAME_TOP, R.drawable.bproz);   
	    	  m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.bproz);   
	    	  m.put(ATTRIBUTE_NAME_LEFT, R.drawable.bproz);   
	    	  m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.bproz);   
	  	      // Расставляем края
	  	      if ((i>=unhooks.currentLine-1) && (i<unhooks.currentLine+4)) {
	  	    	  m.put(ATTRIBUTE_NAME_LEFT, R.drawable.border);   
	  	    	  m.put(ATTRIBUTE_NAME_RIGHT, R.drawable.border);   	  	    	  
	  	      };
	  	      // верх
	  	      if (i==unhooks.currentLine-1) {
	  	    	  m.put(ATTRIBUTE_NAME_TOP, R.drawable.border);   	  	    	  	  	    	
		  	  }	  	    		
	  	      // низ
	  	      if (i==unhooks.currentLine+3) {
	  	    	  m.put(ATTRIBUTE_NAME_BOTTOM, R.drawable.border);   	  	    	  	  	    	
		  	  }
	  	      // Вставляем данные
		      data.set(i+getStep(), m);
		    }
	    setLastBlank(); // последние пустые
		sAdapter.notifyDataSetChanged ();	    
    }
    
    // таймер..
	TimerTask timerTask=new TimerTask() {
		@Override
		public void run() {
			new CommThread(CommThread.CURRENT_COMM, ConnectThread.mmSocket, handler).start(); // Команда серверу
		}
    	
    };
    
}
