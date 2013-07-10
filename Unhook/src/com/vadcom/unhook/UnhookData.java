/*   _   _       _                 _    
	| | | |_ __ | |__   ___   ___ | | __
	| | | | '_ \| '_ \ / _ \ / _ \| |/ /
	| |_| | | | | | | | (_) | (_) |   < 
	 \___/|_| |_|_| |_|\___/ \___/|_|\_\
*/	 
package com.vadcom.unhook;

import java.util.ArrayList;
import java.util.List;

/**
 * Данные по расцепке
 * @author Дубина Вадим
 */
public class UnhookData {
	public class UnhookLine {
		int nomer;		// номер сцепки
		int count;		// количество вагонов в сцепке
		String vagon;   // 5 цифр последнего вагона
		int color;		// цветовое предупреждение
		String metka;	// Символьная метка
		// Инициализация через строчку
		UnhookLine (String line) {			
			String[] param=line.split(" ", 5);
			nomer=Integer.valueOf(param[0]);
			count=Integer.valueOf(param[1]);
			vagon=param[2];
			color=Integer.valueOf(param[3]);
			metka=param[4];
		}
	}
	List<UnhookLine> data=new ArrayList<UnhookLine>();
	public int currentLine;	// Начинается от 0
	
	public UnhookData (){
		currentLine=0;
	}
	
	public int getCount() {
		return data.size();
	}
	
	public UnhookLine getLine(int index) {
		return data.get(index);
	}
	
	public UnhookLine getLine() {
		if (currentLine<data.size()) {
			return data.get(currentLine);
		} else return null;
	}
	
	
	public void add(String line) {
		data.add(new UnhookLine(line));
	}
	
	public String getMd5(){
		return "0000";
	}
}
