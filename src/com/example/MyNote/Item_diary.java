package com.example.MyNote;

import java.util.Calendar;

public class Item_diary{
	public boolean isEmpty;
	public Calendar date;
	public String content;
	
	public  Item_diary(Calendar date,boolean isEmpty) {
		this.isEmpty=isEmpty;
		this.date=date;
		this.content=null;
	}
	public boolean setItem(String content){
		if(content==null||content.equals("")){
			this.isEmpty=true;
			return false;
		}
		this.content=new String(content);
		this.isEmpty=false;
		return true;
	}
	public String getweekday_full_s(){
		String weekday[]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
		int a=date.get(Calendar.DAY_OF_WEEK);
		return weekday[a-1];
	}
	public int getdate(){
		return date.get(Calendar.DAY_OF_MONTH);
	}
	public String getweekday(){
		String weekday[]={"SUN","MON","TUE","WED","THU","FRI","SAT"};
		int a=date.get(Calendar.DAY_OF_WEEK);
		return weekday[a-1];
	}

}