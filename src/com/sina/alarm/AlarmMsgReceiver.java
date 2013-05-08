package com.sina.alarm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.Consts;

public class AlarmMsgReceiver extends BroadcastReceiver {
	private static int notification_id = 1000;
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		Log.d("GexinSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(Consts.CMD_ACTION)) {

		case Consts.GET_MSG_DATA:
			// 获取透传数据
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");

			if (payload != null) {
				String data = new String(payload);
if(AlarmMsgReceiver.isAppRunning(context) == false){//如果程序没有在跑.. 就在通知中心显示
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification();
				notification.icon = R.drawable.arrow_down;
				notification.defaults |= Notification.DEFAULT_VIBRATE;// 震动
				Intent notificationIntent = new Intent(context,
						MainActivity.class); // 点击该通知后要跳转的Activity
				PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				notification.setLatestEventInfo(context, "有报警!", data,pendingIntent);
				notificationManager.notify(++notification_id, notification);// todo 换了这个id
}
				Log.d("reset Listview", "重置数据ing");
				if (ChatActivity.data != null) {
					ChatActivity.resetListView();
				}
				if(MessageList.data != null){
					MessageList.resetListView();
				}

				Log.d("AlarmMsgReceiver", "Got Payload:" + data);
			}

			break;
		case Consts.NOTIFY_MSG:
			Log.d("NOTIFY_MSG", "NOTIFY_MSG");
			break;
		case Consts.GET_CLIENTID:
			// 获取ClientID(CID)
			// 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
			String cid = bundle.getString("clientid");
			UserModel.client_id = cid;
			UserModel.updateClientId();
			Log.d("AlarmMsgReceiver", cid);
			break;

		case Consts.BIND_CELL_STATUS:
			String cell = bundle.getString("cell");

			Log.d("GexinSdkDemo", "BIND_CELL_STATUS:" + cell);

			break;
		default:
			break;
		}
	}

	public static boolean isAppRunning(Context context) {
		String packageName = "com.sina.alarm";
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}
}
