package com.trencadis.mvd;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmMessageHandler extends IntentService {

	private static final String MESSAGE = "message";


	private String message;

	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();

		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		message = extras.getString(MESSAGE, "something not found");

		String messageType = gcm.getMessageType(intent);

		message = extras.getString(MESSAGE);
		Log.i("GCM", "Received: (" + messageType + ") " + message);
		
		System.out.println("notification received");
		
		if(message == null){
			return;
		}
		
		generateNotification();
		
		GcmReceiver.completeWakefulIntent(intent);
	}

	private void generateNotification() {
        // TODO
    }
}
