package com.sina.alarm;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import com.sina.alarm.ChatListView.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ChatActivity extends Activity {
	public static LinkedList<Map<String,String>> data =null;
	public static ChatListView listView ;
	public static BaseAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		
		data = new LinkedList<Map<String,String>>();
		listView = (ChatListView) findViewById(R.id.listView_chat_session);

		adapter = new BaseAdapter() {
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = new TextView(ChatActivity.this);

				if(data.get(position).get("send_user").equals(UserModel.username)){
					 convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_reply, null);
				}else{
					 convertView=LayoutInflater.from(getApplicationContext()).inflate(R.layout.item, null);
				}
		

				TextView textView = (TextView) convertView.findViewById(R.id.textView_item);
				textView.setText(data.get(position).get("message"));
			
				textView = (TextView) convertView.findViewById(R.id.textView_time);
				SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' HH:mm:ss");
				Date dNow = new Date(Long.parseLong(data.get(position).get("time"))*1000 );
				textView.setText(ft.format(dNow));
				
				
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
		listView.setonRefreshListener(new OnRefreshListener(){

			@Override
			public void onRefresh() {
				ChatActivity.loadData();
				
			}
			
		});
		
		loadData();


		
	}
	public static void loadData(){
		 new AsyncTask<Void, Void, Void>() {
				@Override
				protected void onPostExecute(Void result) {
					adapter.notifyDataSetChanged();
					listView.onRefreshComplete();
				}

				@Override
				protected Void doInBackground(Void... arg0) {
					
					String serviceUrl = "http://wangxin3.admin.alarm.mix.sina.com.cn/?p=report&s=client&a=sessionList&session="+UserModel.session+"&format=json&first_id="+UserModel.getSessionMsgFirstId();
					Log.d("CharActivity:serviceUrl", serviceUrl);
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
						UserModel.setSessionMsgFirstId(Integer.parseInt(m.get("id"))); 
					}
				
					}else{
						Log.d("fetch new lists failed", obj.get("result").get("status").get("msg").toString());
					}

					
					
					return null;
				}

				
			}.execute();
	}
	
	public void postMsg(View view){
		EditText editText =	(EditText)findViewById(R.id.editText_input);
		String content = editText.getText().toString();
		if(content.equals("")){
			Toast.makeText(this, "再多敲点内容吧", Toast.LENGTH_SHORT).show();
		}else{
			//post msg
			String serviceUrl = "http://wangxin3.admin.alarm.mix.sina.com.cn/?p=report&s=client&a=message&session="+UserModel.session+"&format=json&from="+UserModel.username+"&content="+ URLEncoder.encode(content);
			Log.d("CharActivity:postMsg", serviceUrl);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("username", UserModel.username);
			String result = HttpPostTool.httpPost(map, serviceUrl);
			resetListView();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chat, menu);
		return true;
	}
	
	public static void resetListView(){
		data.removeAll(data);
		UserModel.session_msg_first_id = 0;
		adapter.notifyDataSetChanged();
		ChatActivity.loadData();
		
	}

}
