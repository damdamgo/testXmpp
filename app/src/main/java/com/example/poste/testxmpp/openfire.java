        package com.example.poste.testxmpp;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.app.Service;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.os.Binder;
        import android.os.IBinder;
        import android.os.Vibrator;
        import android.support.v4.app.NotificationCompat;
        import android.support.v4.app.TaskStackBuilder;
        import android.util.Log;
        import android.widget.Toast;

        import org.jivesoftware.smack.AbstractXMPPConnection;
        import org.jivesoftware.smack.ConnectionListener;
        import org.jivesoftware.smack.MessageListener;
        import org.jivesoftware.smack.SmackConfiguration;
        import org.jivesoftware.smack.SmackException;
        import org.jivesoftware.smack.XMPPConnection;
        import org.jivesoftware.smack.XMPPException;
        import org.jivesoftware.smack.chat.Chat;
        import org.jivesoftware.smack.chat.ChatManager;
        import org.jivesoftware.smack.chat.ChatManagerListener;
        import org.jivesoftware.smack.chat.ChatMessageListener;
        import org.jivesoftware.smack.packet.Message;
        import org.jivesoftware.smack.packet.Presence;
        import org.jivesoftware.smack.roster.Roster;
        import org.jivesoftware.smack.roster.RosterListener;
        import org.jivesoftware.smack.tcp.XMPPTCPConnection;
        import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
        import org.jivesoftware.smack.util.StringUtils;
        import org.jivesoftware.smackx.muc.DiscussionHistory;
        import org.jivesoftware.smackx.muc.MUCNotJoinedException;
        import org.jivesoftware.smackx.muc.MultiUserChat;
        import org.jivesoftware.smackx.muc.MultiUserChatManager;
        import org.jivesoftware.smackx.muc.Occupant;
        import org.jivesoftware.smackx.muc.RoomInfo;
        import org.jivesoftware.smackx.offline.OfflineMessageManager;

        import java.io.IOException;
        import java.security.KeyManagementException;
        import java.security.NoSuchAlgorithmException;
        import java.security.cert.X509Certificate;
        import java.sql.Timestamp;
        import java.util.Collection;
        import java.util.Iterator;
        import java.util.LinkedList;
        import java.util.List;
        import java.util.Set;

        import javax.net.ssl.SSLContext;
        import javax.net.ssl.TrustManager;
        import javax.net.ssl.X509TrustManager;

        /**
         * Created by Poste on 09/05/2015.
         */
        public class openfire extends Service {

                // interface for clients that bind
            boolean mAllowRebind;
            public static final String PREFS_NAME = "enregistrement";
            public static final String CUSTOM_INTENT = "reception.message";
            public static AbstractXMPPConnection connection=null;
            private final IBinder mBinder = new LocalBinder();
            public static LinkedList<Chat> listechat=new LinkedList<Chat>();
            public static Intent intent;
            private LinkedList<String> dispo=new LinkedList<String>();
            public static Boolean connecterinternet;
            private Boolean autoriserreconnection=true;
            public int onStartCommand(Intent intent, int flags, int startId) {
                    new nerworkConnection(this);
                    this.intent=intent;
                    return START_STICKY;
                }


            @Override
                public void onDestroy() {
                    super.onDestroy();
                    autoriserreconnection=false;
                    connection.disconnect();

            }
            @Override
            public void onCreate() {
                super.onCreate();
                Log.w("myApp", "creeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeationnnnnnnn");
                new Connexion().execute();
                connecterinternet=true;
            }

            public IBinder onBind(Intent intent) {
                // A client is binding to the service with bindService()
                return mBinder;
            }
            @Override
            public boolean onUnbind(Intent intent) {
                // All clients have unbound with unbindService()
                return mAllowRebind;
            }

            public int sendMessage(String message )
            {
                try {
                    for(Chat c : listechat)
                    {
                        c.sendMessage(message);
                    }
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    String pseudo = settings.getString("name","ee");
                    SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(openfire.this);
                    java.util.Date date= new java.util.Date();
                    Timestamp time = new Timestamp(date.getTime());
                    message m=new message(1,message,pseudo+"@",time);
                    db.addOne(m);
                    Intent i = new Intent();
                    i.setAction(CUSTOM_INTENT);
                    i.putExtra("message", message);
                    i.putExtra("from",pseudo+"@");
                    sendBroadcast(i);
                    return 1;
                } catch (SmackException.NotConnectedException e) {
                    return 0;
                }
            }
            public class LocalBinder extends Binder {
                openfire getService() {
                    // Return this instance of LocalService so clients can call public methods
                    return openfire.this;
                }
            }

            public void notification(Message mess)
            {
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.logo);
                mBuilder.setContentTitle(mess.getFrom().split("@")[0]);
                mBuilder.setAutoCancel(true);
                mBuilder.setVibrate(new long[]{500, 500});
               // mBuilder.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getPackageName() + "/drawable/son"));
                mBuilder.setContentText(mess.getBody());
                Intent resultIntent = new Intent(this, openfire.class);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                new Intent(this, MainActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotifyMgr =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(1, mBuilder.build());
            }

            public void reconexion()
            {
                if(connection!=null && !connection.isConnected()) {
                    connection.disconnect();
                    new Connexion().execute();
                }
            }
            public void gestionConnexion()
            {


                /*MultiUserChatManager mult = MultiUserChatManager.getInstanceFor(connection);
                multichat = mult.getMultiUserChat("vilfamily@conference.vps167932.ovh.net");
                try {

                    multichat.join("damdam");
                    Log.w("myApp", "ee");

                    multichat.addMessageListener(new MessageListener() {
                        @Override
                        public void processMessage(Message message) {
                            if (message.getBody() != null) {
                                Intent i = new Intent();
                                i.setAction(CUSTOM_INTENT);
                                Log.w("myApp", String.valueOf(message.getType()));
                                i.putExtra("message", message.getBody());
                                i.putExtra("from", message.getFrom());
                                sendBroadcast(i);
                            }
                        }
                    });

                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }*/
                listechat.clear();
                ChatManager chatmanager = ChatManager.getInstanceFor(connection);
                Chat newChat = chatmanager.createChat("guigui@vps167932.ovh.net");
                listechat.add(newChat);

                newChat = chatmanager.createChat("leti@vps167932.ovh.net");
                listechat.add(newChat);
                newChat = chatmanager.createChat("damdam@vps167932.ovh.net");
                listechat.add(newChat);
                newChat = chatmanager.createChat("papounet@vps167932.ovh.net");
                listechat.add(newChat);
                newChat = chatmanager.createChat("mamounette@vps167932.ovh.net");
                listechat.add(newChat);

                chatmanager.addChatListener(new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        chat.addMessageListener(new ChatMessageListener() {
                            @Override
                            public void processMessage(Chat chat, Message message) {
                                if (message.getBody() != null) {
                                    if(Messagerie.open && Messagerie.foreground)
                                    {
                                        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(openfire.this);
                                        java.util.Date date= new java.util.Date();
                                        Timestamp time = new Timestamp(date.getTime());
                                        message m=new message(1,message.getBody(),message.getFrom(),time);
                                        db.addOne(m);
                                        Intent i = new Intent();
                                        i.setAction(CUSTOM_INTENT);
                                        i.putExtra("message", message.getBody());
                                        i.putExtra("from", message.getFrom());
                                        sendBroadcast(i);
                                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        v.vibrate(500);
                                    }
                                    else
                                    {
                                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                        int nbnewmessage = Integer.parseInt(settings.getString("nbnewmessage", "0"));
                                        nbnewmessage++;
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("nbnewmessage", String.valueOf(nbnewmessage));
                                        editor.commit();
                                        SQLiteDatabaseHandler db = new SQLiteDatabaseHandler(openfire.this);
                                        java.util.Date date= new java.util.Date();
                                        Timestamp time = new Timestamp(date.getTime());
                                        message m=new message(1,message.getBody(),message.getFrom(),time);
                                        db.addOne(m);
                                        notification(message);
                                    }
                                    if( !Messagerie.foreground && Messagerie.open)
                                    {
                                        Intent i = new Intent();
                                        i.setAction(CUSTOM_INTENT);
                                        i.putExtra("message", message.getBody());
                                        i.putExtra("from", message.getFrom());
                                        sendBroadcast(i);
                                    }
                                }
                            }
                        });

                    }
                });
                Log.w("myApp", "aaa");
                Roster roster = Roster.getInstanceFor(connection);
                roster.addRosterListener(new RosterListener() {

                    public void entriesDeleted(Collection<String> addresses) {}

                    @Override
                    public void entriesAdded(Collection<String> addresses) {

                    }

                    public void entriesUpdated(Collection<String> addresses) {}
                    public void presenceChanged(Presence presence) {
                        if(presence.getStatus()!=null) {
                            dispo.add(presence.getFrom().split("@")[0]);
                        }
                        else
                        {
                            for(int i=0;i<dispo.size();i++)
                            {
                                if(dispo.get(i).equals(presence.getFrom().split("@")[0]))
                                {
                                    dispo.remove(i);
                                }
                            }
                        }
                        Log.w("myApp", dispo.toString());
                    }
                });
            }

            public class Connexion extends AsyncTask<Void, Integer, Integer> {

                @Override
                protected Integer doInBackground(Void... params) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                        public void checkClientTrusted( final X509Certificate[] chain, final String authType ) {
                        }
                        public void checkServerTrusted( final X509Certificate[] chain, final String authType ) {
                        }
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    } };
                    XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    String pseudo = settings.getString("name","ee");
                    builder.setUsernameAndPassword(pseudo.replaceAll("\\s+", ""), "adgjmptw168");
                    builder.setServiceName("vps167932.ovh.net");
                    builder.setHost("151.80.149.128");
                    SSLContext sslContext;
                    try {
                        sslContext = SSLContext.getInstance("SSL");
                        sslContext.init( null, trustAllCerts, new java.security.SecureRandom() );
                        builder.setCustomSSLContext(sslContext);
                    } catch (NoSuchAlgorithmException | KeyManagementException e1) {
                        return 0;
                    }
                    if(connection!=null)connection.disconnect();
                    connection = new XMPPTCPConnection(builder.build());
                    connection.addConnectionListener(
                            new ConnectionListener() {
                                @Override
                                public void connected(XMPPConnection connection) {
                                    Log.w("myApp", "c1");
                                }

                                @Override
                                public void authenticated(XMPPConnection connection, boolean resumed) {
                                    Log.w("myApp", "c2");
                                }

                                @Override
                                public void connectionClosed() {
                                    Log.w("myApp", "c3");
                                    if(connecterinternet && autoriserreconnection)new Connexion().execute();
                                }

                                @Override
                                public void connectionClosedOnError(Exception e) {
                                    Log.w("myApp", "c4");
                                    if(connecterinternet)new Connexion().execute();

                                }

                                @Override
                                public void reconnectionSuccessful() {
                                    Log.w("myApp", "c5");
                                }

                                @Override
                                public void reconnectingIn(int seconds) {
                                    Log.w("myApp", "c6");
                                }

                                @Override
                                public void reconnectionFailed(Exception e) {
                                    Log.w("myApp", "c7");
                                }
                            }
                    );
                    try {
                        connection.connect();
                        connection.login();
                        return 1;
                    } catch (SmackException e) {
                        return 0;
                    } catch (IOException e) {
                        return 0;
                    } catch (XMPPException e) {
                        return 0;
                    }

                }

                protected void onPostExecute(Integer result) {
                    if(result==0)
                    {
                        Log.w("myApp", "c054");
                    }
                    else
                    {
                        gestionConnexion();
                    }

                }
            }

    }
