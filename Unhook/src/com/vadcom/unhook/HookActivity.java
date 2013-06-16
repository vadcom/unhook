package com.vadcom.unhook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class HookActivity extends Activity {
	
	// имена атрибутов для Map
    final String ATTRIBUTE_NAME_TEXT1 = "text1";
    final String ATTRIBUTE_NAME_TEXT2 = "text2";
    final String ATTRIBUTE_NAME_CHECKED = "checked";
    final String ATTRIBUTE_NAME_IMAGE = "image";
  
    ListView lvSimple;
    SimpleAdapter sAdapter;

    // массивы данных
    String[] text1 = { "sometext 1", "sometext 2", "sometext 3",
        "sometext 4", "sometext 5" };
    String[] text2 = { "nexttext 1", "nexttext 2", "nexttext 3",
	        "nexttext 4", "nexttext 5" };
    boolean[] checked = { true, false, false, true, false };
    
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
	    ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
	        text1.length);
	    Map<String, Object> m;
	    for (int i = 0; i < text1.length; i++) {
	      m = new HashMap<String, Object>();
	      m.put(ATTRIBUTE_NAME_TEXT1, text1[i]);
	      m.put(ATTRIBUTE_NAME_TEXT2, text2[i]);
	      m.put(ATTRIBUTE_NAME_CHECKED, checked[i]);
	      //m.put(ATTRIBUTE_NAME_IMAGE, img);
	      data.add(m);
	    }

	    // массив имен атрибутов, из которых будут читаться данные
	    String[] from = { ATTRIBUTE_NAME_TEXT1, ATTRIBUTE_NAME_TEXT2,
	    				  ATTRIBUTE_NAME_CHECKED };
	    //ATTRIBUTE_NAME_IMAGE 
	    // массив ID View-компонентов, в которые будут вставлять данные
	    int[] to = { R.id.text1, R.id.text2, R.id.check};

	    // создаем адаптер
	    sAdapter = new SimpleAdapter(this, data, R.layout.item,
	        from, to);
	    lvSimple.setAdapter(sAdapter);
	   // lvSimple.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		//lvSimple.setClickable(true);
		
		//listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		
		lvSimple.setOnItemClickListener (new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {				
				boolean unhook=(Boolean) ((Map<String, Object>)lvSimple.getItemAtPosition(arg2)).get(ATTRIBUTE_NAME_CHECKED);
				((Map<String, Object>)lvSimple.getItemAtPosition(arg2)).put(ATTRIBUTE_NAME_CHECKED, !unhook);
				sAdapter.notifyDataSetChanged ();
			}
			
		});	
		lvSimple.setSelection(0);
	}
	
	public void checkClick(View view) {
		int s=lvSimple.getSelectedItemPosition();
		//lvSimple.get
		if (s>=0) {
			text1[s]="ready";
		}
		lvSimple.invalidate();
	}
	
	
}
