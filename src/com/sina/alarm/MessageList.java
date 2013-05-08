package com.sina.alarm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import com.sina.alarm.MyListView;
import com.sina.alarm.MyListView.OnRefreshListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public  class MessageList extends Activity {


	public static LinkedList<Map<String,String>> data;
	public static BaseAdapter adapter;
	public static  MyListView listView = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		data = new LinkedList<Map<String,String>>();
	

		listView = (MyListView) findViewById(R.id.listView);
		adapter = new BaseAdapter() {
			public View getView(int position, View convertView, ViewGroup parent) {
				if( data.get(position).get("level").equals("1")){
					convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
				}else if(data.get(position).get("level").equals("2")){
					convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_level2, null);
				}else if(data.get(position).get("level").equals("3")){
					convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_level3, null);
				}
				
				
				
				TextView textView = (TextView) convertView.findViewById(R.id.textView_item);
				textView.setText(data.get(position).get("message"));
			
				textView = (TextView) convertView.findViewById(R.id.textView_time);
				SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");
				Date dNow = new Date(Long.parseLong(data.get(position).get("time"))*1000 );
				textView.setText(ft.format(dNow));
				//textView = (TextView) convertView.findViewById(R.id.hidden_url);
				//textView.setText(data.get(position).get("link"));
				return convertView;
			}
		

			public long getItemId(int position) {
				return position;
			}

			public Object getItem(int position) {
				return data.get(position);
			}

			public int getCount() {
				return data.size();
			}
		};
		listView.setAdapter(adapter);
		listView.setonRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				MessageList.loadData();
			}
		});
		MessageList.loadData();
		
	}
	
	public static void resetListView(){
		data.removeAll(data);
		UserModel.msg_first_id = 0;
		adapter.notifyDataSetChanged();
		MessageList.loadData();
		
	}
	
	public static void loadData(){
		new AsyncTask<Void, Void, Void>() {
			protected Void doInBackground(Void... params) {				
				//获取数据
				String serviceUrl = "http://wangxin3.admin.alarm.mix.sina.com.cn/?p=report&s=client&a=lists&username="+UserModel.username+"&format=json&first_id="+UserModel.getMsgFirstId();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("username", UserModel.username);
				String result = HttpPostTool.httpPost(map, serviceUrl);
				Map<String, Map<String, Map<String, ?>>> obj=(Map<String, Map<String, Map<String, ?>>>) JSONValue.parse(result);
				if( obj.get("result").get("status").get("code").toString().equals("0") ){
				JSONArray	 jArray =  (JSONArray) obj.get("result").get("data");
				Map<String,String> m;
				for(Iterator<Map<String, String>> it = jArray.iterator(); it.hasNext();){
					m = it.next();
					data.addFirst(m);
					UserModel.setMsgFirstId(Integer.parseInt(m.get("id"))); 
				}
			
					
		
				}else{
					Log.d("fetch new lists failed", obj.get("result").get("status").get("msg").toString());
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				adapter.notifyDataSetChanged();
				listView.onRefreshComplete();
			}

		}.execute();
	}
	

}


