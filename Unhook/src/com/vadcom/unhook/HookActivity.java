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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
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
		//lvSimple.setSelector(R.drawable.ic_launcher);
	    //gridview.setAdapter(new ImageAdapter(this));
		/*
		lvSimple.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});
		*/
		
		//lvSimple.setItemsCanFocus (true);
		
		//inflater.inflate(R.layout.header, null);
		
		// lvSimple.addHeaderView(findViewById(R.layout.header));

	    //int img = R.drawable.ic_launcher;

	    // упаковываем данные в понятную для адаптера структуру
		/*
	    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
	        text1.length);
	    Map<String, Object> m;
	    Resources res = getResources();
	    for (int i = 0; i < text1.length; i++) {
	      m = new HashMap<String, Object>();
	      m.put(ATTRIBUTE_NAME_TEXT1, text1[i]);
	      m.put(ATTRIBUTE_NAME_TEXT2, text2[i]);
	      m.put(ATTRIBUTE_NAME_CHECKED, checked[i]);
  	      //Drawable drawable = res.getDrawable(R.drawable.ic_launcher);
  	      if (i%2==0) {
  	    	  m.put(ATTRIBUTE_NAME_BACK, R.drawable.red);
  	      }
	     // m.put(ATTRIBUTE_NAME_BACK, new Drawable());
	      //m.put(ATTRIBUTE_NAME_IMAGE, img);
	      data.add(m);
	    }

	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = { ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2,
	    				  ATTRIBUTE_NAME_CHECKED,ATTRIBUTE_NAME_BACK};
	    //ATTRIBUTE_NAME_IMAGE 
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.text1, R.id.text2, R.id.check,R.id.imageView1};

	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.item,
	        from, to);
	    lvSimple.setAdapter(sAdapter);
		*/
		lvSimple.setOnItemClickListener (new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				/*
				boolean unhook=(Boolean) ((Map<String, Object>)lvSimple.getItemAtPosition(arg2)).get(ATTRIBUTE_NAME_CHECKED);
				((Map<String, Object>)lvSimple.getItemAtPosition(arg2)).put(ATTRIBUTE_NAME_CHECKED, !unhook);
				sAdapter.notifyDataSetChanged ();
				*/
			}
			
		});	
		//lvSimple.setSelection(0);
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
	    //Resources res = getResources();
	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = { ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2,ATTRIBUTE_NAME_TEXT3,
	    				  ATTRIBUTE_NAME_UKAZ,ATTRIBUTE_NAME_BACK};
	    //ATTRIBUTE_NAME_IMAGE 
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.textNomer, R.id.textCount,  R.id.textVagon,R.id.imageUkaz,R.id.imageView1};
	    
	    for (int i = 0; i < unhooks.getCount(); i++) {
		      m = new HashMap<String, Object>();
		      UnhookLine line=unhooks.getLine(i);		      
		      m.put(ATTRIBUTE_NAME_TEXT1, line.nomer);
		      m.put(ATTRIBUTE_NAME_TEXT2, line.count);
		      m.put(ATTRIBUTE_NAME_TEXT3, line.vagon);
	  	      if (line.color==1) {
	  	    	  m.put(ATTRIBUTE_NAME_BACK, R.drawable.red);
	  	      }	  	      
	  	      if (i+1==unhooks.currentLine) {
	  	    	m.put(ATTRIBUTE_NAME_UKAZ, R.drawable.ic_launcher);
	  	      } else {
	  	    	m.put(ATTRIBUTE_NAME_UKAZ, 0); ///???  
	  	      }
		      data.add(m);
		    }
	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.item,
	        from, to);
	    lvSimple.setAdapter(sAdapter);
    }
	
    @Override
    protected void onStart(){
    	super.onStart();
    	new CommThread(CommThread.READ_COMM, ConnectThread.mmSocket, handler).start();
    }
}
