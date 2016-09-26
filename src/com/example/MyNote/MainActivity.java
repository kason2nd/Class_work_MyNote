package com.example.MyNote;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.MyNote.MyAdapter;
import com.example.MyNote.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity   {

	private ListView listview;
	private int seletedMonth;
	private int seletedYear;
	private  List<Item_diary> listdata;
	private MyAdapter adapter;
	private HorizontalScrollView month_list;
	private RelativeLayout bottom_menu;
	private TextView menu_month;
	private TextView menu_add;
	private TextView menu_browse;
	private TextView menu_year;
	private AlertDialog.Builder inputYear;
	private EditText yearEditText;
	private Context context;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context=this;
        Calendar curDate=Calendar.getInstance();
        int curYear=curDate.get(Calendar.YEAR);
        int curMonth=curDate.get(Calendar.MONTH)+1;
        
         listview=(ListView)this.findViewById(R.id.main_list);
         menu_month=(TextView)findViewById(R.id.bottom_menu_month);
         menu_add=(TextView)findViewById(R.id.bottom_menu_add);
         menu_browse=(TextView)findViewById(R.id.bottom_menu_browse);
         menu_year=(TextView)findViewById(R.id.bottom_menu_year);
         month_list=(HorizontalScrollView)findViewById(R.id.bottom_month_list);
        bottom_menu=(RelativeLayout)findViewById(R.id.bottom_menu);
        
        selectMonth(curMonth);
        seletedMonth=curMonth;
        seletedYear=curYear;
        
        adapter=new MyAdapter(this);
        setListData(seletedYear,seletedMonth);
       

        
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new DiaryItemListener());
        
        TextView month_item;
        for(int i=0;i<12;i++){
        	month_item = (TextView)findViewById(R.id.month_1+i); 
        	month_item.setOnClickListener(new MonthItemListener());
        }
       
        MenuItemClickListener miclicklisterner=new MenuItemClickListener();
        menu_month.setOnClickListener(miclicklisterner);
        menu_add.setOnClickListener(miclicklisterner);
        menu_browse.setOnClickListener(miclicklisterner);
        menu_year.setOnClickListener(miclicklisterner);
        
        inputYear= new AlertDialog.Builder(this);

        inputYear.setTitle("请输入年份")  
        .setIcon(android.R.drawable.ic_dialog_info)  
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	try {
            		seletedYear=Integer.valueOf(yearEditText.getText().toString());
            		menu_year.setText(""+seletedYear);
            		setListData(seletedYear,seletedMonth);
    				adapter.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
					Toast.makeText(MainActivity.this,"输入有误",Toast.LENGTH_SHORT).show();
					return;
				}
            	
             }
        })  
        .setNegativeButton("取消", null);
    }
    
    public void setListData(int year,int month){

    	List<Item_diary> data= new ArrayList<>();
        
        int  maxday=getDaysByYearMonth(year,month);
        for(int day=1;day<=maxday;day++){
        	Calendar date = Calendar.getInstance(); 
        	date.set(year, month-1, day);
        	data.add(new Item_diary(date,true));
        }

        loadAllData(year,month,data);
        listdata=data;
        adapter.setData(data);
        
    }
    class MenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id=v.getId();
			if(id==R.id.bottom_menu_month){
				month_list.setVisibility(View.VISIBLE);
				bottom_menu.setVisibility(View.INVISIBLE);
			}else if(id==R.id.bottom_menu_add){
				Calendar curDate=Calendar.getInstance();
		        int curYear=curDate.get(Calendar.YEAR);
		        int curMonth=curDate.get(Calendar.MONTH)+1;
		        int curDay=curDate.get(Calendar.DAY_OF_MONTH);
		        
		        Intent intent =new Intent();
				intent.setClass(MainActivity.this, DetailActivity.class);
		        int item_id=-1;
		        if(curYear==seletedYear&&curMonth==seletedMonth){
		        	item_id=curDay-1;
		        	Item_diary itemdata=listdata.get(item_id);
		        	intent.putExtra("content",itemdata.content);
		        }else{
		            String path=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+curYear+String.format("%02d", curMonth)+"/"+String.format("%d%02d%02d",curYear,curMonth,curDay);
		            intent.putExtra("content",load(path));
		        }
		        	
				intent.putExtra("year",curYear);
				intent.putExtra("month",curMonth);
				intent.putExtra("day",curDay);
				startActivityForResult(intent,item_id);
			}else if(id==R.id.bottom_menu_browse){
				if(adapter.getMode()==0){
					adapter.setMode(1);
				}else {
					adapter.setMode(0);
				}
				adapter.notifyDataSetChanged();
			}else if(id==R.id.bottom_menu_year){
//				yearEditText.removeView();
		        yearEditText=new EditText(context);
		        yearEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		        yearEditText.setText(""+seletedYear);
		        inputYear.setView(yearEditText);
		        
		        yearEditText.selectAll();
		       
		        inputYear.show(); 
		        Timer timer = new Timer();
		        timer.schedule(new TimerTask() {
			        public void run() {
				        InputMethodManager inputManager = (InputMethodManager) yearEditText .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				        inputManager.showSoftInput(yearEditText, 0);
			        }
		        }, 200);
			}
		}
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==1000){
        	if(requestCode!=-1){
        		String content = data.getStringExtra("content");
	        	Item_diary item=listdata.get(requestCode);
	        	item.setItem(content);
	        	adapter.setPointer();
//	        	adapter.setItem(requestCode,content);
	        	adapter.notifyDataSetChanged();
	        	System.out.println("DataChanged");
        	}
//        	save(this,item.date.get(Calendar.YEAR), item.date.get(Calendar.MONTH)+1, item.date.get(Calendar.DAY_OF_MONTH), content);
//        	Toast.makeText(MainActivity.this,content,Toast.LENGTH_LONG).show();
        }
            
            
    }
    class DiaryItemListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			Item_diary itemdata=listdata.get((int)id);
			Intent intent =new Intent();
			intent.setClass(MainActivity.this, DetailActivity.class);
			intent.putExtra("content", itemdata.content);
			intent.putExtra("year", itemdata.date.get(Calendar.YEAR));
			intent.putExtra("month",itemdata.date.get(Calendar.MONTH)+1);
			intent.putExtra("day", itemdata.date.get(Calendar.DAY_OF_MONTH));
			startActivityForResult(intent,(int)id);
		}
    	
    }
    public void selectMonth(int month){
    	String mon[]={"JAN","FEB","MAR","APR","MAY","JUNE","JULY","AUG","SEPT","OCT","NOV","DEC"};
		menu_month.setText(mon[month-1]);
		(findViewById(R.id.month_1+month-1)).setSelected(true);
		if(0<seletedMonth&&seletedMonth<13){
			(findViewById(R.id.month_1+seletedMonth-1)).setSelected(false);
		}
    }
    class MonthItemListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			int month=v.getId()-R.id.month_1+1;
			if(seletedMonth!=month){
//				v.setSelected(true);
//				if(0<seletedMonth&&seletedMonth<13){
//					(findViewById(R.id.month_1+seletedMonth-1)).setSelected(false);
//				}
				selectMonth(month);
				seletedMonth=month;
				setListData(seletedYear,seletedMonth);
				adapter.notifyDataSetChanged();
			}
			month_list.setVisibility(View.GONE);
			bottom_menu.setVisibility(View.VISIBLE);
		}
    	
    }

    public void loadAllData(int year,int month,List<Item_diary> data){
    	String dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/MyNote/"+year+String.format("%02d", month);
    	File destDir = new File(dir);
    	String paths[]=destDir.list();
    	if(paths!=null)
	    	for(String name:paths){
	    		data.get(nameToInt(name)).setItem(load(dir+"/"+name));
	    	}
    }

    public String load(String path)
    {
        try {
            FileInputStream inStream=new FileInputStream(path);
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            byte[] buffer=new byte[10];
            int length=-1;
            while((length=inStream.read(buffer))!=-1)   {
                stream.write(buffer,0,length);
            }
            stream.close();
            inStream.close();
            return stream.toString();
        } catch (FileNotFoundException e) {
        	return null;
        }catch (IOException e){
            return null;
        }
    }  
    public int nameToInt(String name){
    	String str_day=name.substring(6);
    	return Integer.valueOf(str_day)-1;
    }
    public int getDaysByYearMonth(int year, int month) {  
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
//        	File destDir = new File(getFilesDir().toString()+"");
//        	String paths[]=destDir.list();
//        	for(String path:paths){
//        		if(new File(getFilesDir()+"/"+path).isDirectory())
//        			Log.i("Dir", path);
//        		else {
//        			Log.i("file", path);
//				}
//        	}
            return true;
        }else if(id == R.id.action_save){
//        	save(2016,7,1,"这是最气的哈哈哈哈");
//        	save(2016,7,3,"这个是需要换行的！！！！\n好好好\n再换\n");
//        	save(2016,7,5,"这个是不换行但是很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很很长");
//        	save(2016,7,12,"                                                                         这个前面后面都是空格                                                                         ");
//        	save(2016,7,25,"xxxxxxx");
        	return true;
        }else if(id == R.id.action_load){
//        	load();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
