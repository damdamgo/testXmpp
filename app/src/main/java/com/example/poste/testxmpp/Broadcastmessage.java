package com.example.poste.testxmpp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Poste on 16/05/2015.
 */
public class Broadcastmessage extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        java.util.Date date= new java.util.Date();
        Timestamp time = new Timestamp(date.getTime());
        Bundle extra = intent.getExtras();
        message m=new message(1,(String)extra.getString("message"),(String)extra.getString("from"),time);
        Messagerie.newmessage(m);
    }
}
