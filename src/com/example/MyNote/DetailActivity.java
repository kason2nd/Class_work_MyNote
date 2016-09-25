package com.example.MyNote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.MyNote.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;

public class DetailActivity extends Activity {

	private EditText editText;
	private TextView back;
	private TextView time;
	private TextView done;
	private Context context;
	private int year,month,day;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		context=this;
		Intent intent=getIntent();
		String content=intent.getStringExtra("content");
		
		year=intent.getIntExtra("year",-1);
		month=intent.getIntExtra("month",-1);
		day=intent.getIntExtra("day",-1);
		
		Calendar date=Calendar.getInstance();
		date.set(year, month-1,day);
			
//		System.out.println(weekday);
		String title=getweekday_full(date)+"/"+getmonth(date)+" "+String.format("%02d",day)+"/"+year;
		TextView text_title = (TextView) findViewById(R.id.detail_title);  
		if(date.get(Calendar.DAY_OF_WEEK)==1){
//			System.out.println("SUNdddd");
			SpannableStringBuilder builder = new SpannableStringBuilder(title);  
			ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
			builder.setSpan(redSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
			text_title.setText(builder);
		}else{
			text_title.setText(title);
		}
		back=(TextView)findViewById(R.id.detail_bottom_back);
		time=(TextView)findViewById(R.id.detail_bottom_time);
		done=(TextView)findViewById(R.id.detail_bottom_done);
		 editText=(EditText)findViewById(R.id.detail_content);
		 editText.setText(content);
		 ClickListener clicklistener=new ClickListener();
		
		done.setOnClickListener(clicklistener);
		back.setOnClickListener(clicklistener);
		time.setOnClickListener(clicklistener);
		editText.setOnFocusChangeListener(new FocusListener());
		
		
	}
	public String getweekday_full(Calendar date){
		String weekday[]={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
		int a=date.get(Calendar.DAY_OF_WEEK);
		return weekday[a-1];
	}
	public String getmonth(Calendar date){
		String mon[]={"JAN","FEB","MAR","APR","MAY","JUNE","JULY","AUG","SEPT","OCT","NOV","DEC"};
		return mon[date.get(Calendar.MONTH)];
	}
	class FocusListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			int id=v.getId();
			if(id==R.id.detail_content){
				if(hasFocus==true){
					done.setVisibility(View.VISIBLE);
					time.setVisibility(View.VISIBLE);
					back.setVisibility(View.GONE);
				}
			}
		}
		
	}
	class ClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			int id=v.getId();
			if(id==R.id.detail_bottom_done){
				String content = editText.getText().toString();
				save(context,year,month,day,content);
		        Intent intent = new Intent();
		        intent.putExtra("content", content);
		        setResult(1000, intent);
		        finish();
			}else if(id==R.id.detail_bottom_back){
				Intent intent = new Intent();
		        setResult(999, intent);
		        finish();
			}else if(id==R.id.detail_bottom_time){
				int index = editText.getSelectionStart();  
				Editable editable = editText.getText();  
				SimpleDateFormat sdf= new SimpleDateFormat("h:mma");
				editable.insert(index, sdf.format(Calendar.getInstance().getTime()));  
			}
			
		}
		
	}

    public static boolean save(Context context,int year,int month,int day,String content)
    {
//    	  Log.i("rootdir", getFilesDir()+"");
    	String dir_str=String.format("%d%02d",year,month);
//          File destDir = new File(context.getFilesDir()+"/"+dir_str);
    	
           File destDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+dir_str);
          	if (!destDir.exists()) {
           if(destDir.mkdirs()==false)
        	   return false;
          }
           try {
               FileOutputStream outStream=new FileOutputStream(destDir+"/"+String.format("%d%02d%02d",year,month,day));
               String string=content;
               outStream.write(string.getBytes());
               outStream.close();
//               Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
               return true;
           } catch (FileNotFoundException e) {
               return false;
           }catch (IOException e){
               return false;
           } 
    }
	
		
}
