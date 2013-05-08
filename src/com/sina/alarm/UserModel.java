package com.sina.alarm;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class UserModel {
	public static String username ="";
	public static String client_id = "";
	public static boolean updated = false;
	public static int msg_first_id = 0;
	public static int session_msg_first_id = 0;
	public static String session = "";
	
	public static void updateClientId(){
		if(updated == false && false == username.equals("")){
			String  serviceUrl = "http://wangxin3.admin.alarm.mix.sina.com.cn/?p=report&s=client&a=updateClientId&format=json";
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("username", username);
			map.put("client_id", client_id);
			String result = HttpPostTool.httpPost(map, serviceUrl);
			updated = true;
			Log.d("updateClientId", result);
		}else{
			Log.d("updateClientId failed ", "failed");
		}
		
	}
	
	public static void setMsgFirstId(int id){
		
		if(msg_first_id == 0){
			msg_first_id = id;
		}else{
			if(msg_first_id > id){
				msg_first_id = id;
			}
		}
	}
	
	public static void setSessionMsgFirstId(int id){
		
		if(session_msg_first_id == 0){
			session_msg_first_id = id;
		}else{
			if(session_msg_first_id > id){
				session_msg_first_id = id;
			}
		}
	}
	public static String getSessionMsgFirstId(){
		if(session_msg_first_id == 0) return "";
		return session_msg_first_id+"";
	}
	
	public static String getMsgFirstId(){
		if(msg_first_id == 0) return "";
		return msg_first_id+"";
	}
}
