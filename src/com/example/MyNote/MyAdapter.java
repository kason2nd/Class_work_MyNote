package com.example.MyNote;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.MyNote.Item_diary;
import com.example.MyNote.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by jafir on 15/9/14.
 */
public class MyAdapter extends BaseAdapter {


    private Context context;

    //整合数据
    private List<Item_diary> data;
    private int diary_pointer[]=new int[31];
    private int diary_count=0;
    private int mode=0;

    public MyAdapter(Context context) {
        this.context = context;

    }
    public void setPointer() {
    	int i,j=0;
    	int length=data.size();
    	for(i=0;i<length;i++){
    		if(!data.get(i).isEmpty){
    			diary_pointer[j]=i;
    			j++;
    		}
    	}
    	diary_count=j;
	}
    public void setMode(int mode){
    	this.mode=mode;
    }
    public int getMode(){
    	return mode;
    }
    public void setData(List<Item_diary> data){
    	this.data=data;
    	setPointer();
    }
    @Override
    public int getCount() {
    	if(mode==1)
    		return diary_count;
    	else {
    		return data.size();
		}
    }

    @Override
    public Item_diary getItem(int position) {
    	if(mode==1){
    		return data.get(diary_pointer[position]);
    	}else{
    		return data.get(position);
    	}
    }

    @Override
    public long getItemId(int position) {
    	if(mode==1){
    		return diary_pointer[position];
    	}else{
    		return position;
    	}
        
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建两种不同种类的viewHolder变量
        ViewHolder_Diary holder_Diary = null;
        ViewHolder_Empty holder_Empty = null;
        ViewHolder_Diary2 holder_Diary2 = null;
        
        Item_diary o = getItem(position);
        boolean isEmpty=o.isEmpty;
        if(mode==0){
        	
//            isEmpty=true;
            if(!isEmpty){
                if(convertView == null||(holder_Diary = (ViewHolder_Diary) convertView.getTag(R.id.tag_diary))==null){
                    holder_Diary = new ViewHolder_Diary();
                    convertView = View.inflate(context, R.layout.item_diary, null);
                    holder_Diary.weekday = (TextView) convertView.findViewById(R.id.item_diary_weekday);
                    holder_Diary.date = (TextView) convertView.findViewById(R.id.item_diary_date);
                    holder_Diary.content = (TextView) convertView.findViewById(R.id.item_diary_content);
                    convertView.setTag(R.id.tag_diary, holder_Diary);
                    Log.i("new_Diary", ""+o.getdate());
                }
                   holder_Diary.weekday.setText(o.getweekday());
                   holder_Diary.date.setText(""+o.getdate());
                   holder_Diary.content.setText(o.content);
                   if(o.getweekday().equals("SUN"))
                	   holder_Diary.weekday.setTextColor(android.graphics.Color.RED);
                   else
                	   holder_Diary.weekday.setTextColor(android.graphics.Color.BLACK);
            }else{
                if(convertView == null||((holder_Empty = (ViewHolder_Empty) convertView.getTag(R.id.tag_empty))==null)){
                    holder_Empty = new ViewHolder_Empty();
                    convertView = View.inflate(context, R.layout.item_empty, null);
                    holder_Empty.dot = (TextView) convertView.findViewById(R.id.item_empty_dot);
                    convertView.setTag(R.id.tag_empty, holder_Empty);
                    Log.i("new_Empty", ""+o.getdate());
                }
//                holder_Empty.dot.setText(".");
                if(o.getweekday().equals("SUN"))
                	holder_Empty.dot.setTextColor(android.graphics.Color.RED);
                else
                	holder_Empty.dot.setTextColor(android.graphics.Color.BLACK);
//                convertView.setVisibility(View.GONE);
            }
        }else {
//        	position=diary_pointer[position];
//            Item_diary o = data.get(position);
            if(convertView == null||(holder_Diary2 = (ViewHolder_Diary2) convertView.getTag(R.id.tag_diary2))==null){
                holder_Diary2 = new ViewHolder_Diary2();
                convertView = View.inflate(context, R.layout.item_diary2, null);
                holder_Diary2.content = (TextView) convertView.findViewById(R.id.item_diary2_content);
                convertView.setTag(R.id.tag_diary2, holder_Diary2);
                Log.i("new_Diary", ""+o.getdate());
            }
               String content=String.format("%02d", o.date.get(Calendar.DAY_OF_MONTH))+"."+o.getweekday_full_s()+"/"+o.content;
               if(o.getweekday().equals("SUN")){
            	   SpannableStringBuilder builder = new SpannableStringBuilder(content);  
	       			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
	       			builder.setSpan(redSpan, 3, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
	       			holder_Diary2.content.setText(builder);
               } else
            	   holder_Diary2.content.setText(content);
        }
        
        
        return convertView;
    }

    private static class ViewHolder_Diary {
        TextView weekday;
        TextView date;
        TextView content;
    }

    private static class ViewHolder_Empty{
        TextView dot;
    }
    private static class ViewHolder_Diary2{
        TextView content;
    }

}