package com.example.poste.testxmpp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Poste on 09/05/2015.
 */
public class Messagerie extends AppCompatActivity implements ObservableScrollViewCallbacks {

    public static final String PREFS_NAME = "enregistrement";
    public static boolean open;
    private Toolbar mToolbar;
    public openfire mService=null;
    private SQLiteDatabaseHandler db;
    boolean mBound = false;
    public static Messagerie messagerie;
    private String pseudo;
    private String pseudoPrecedent=null;
    private int nbmessage=25;
    private Button chargementmessage;
    public static boolean foreground;
    private List<message> l= new LinkedList<message>();
    private static AdaptaterMessage adp;
    private SimpleDateFormat jourformat = new SimpleDateFormat("dd-MM-yyyy");
    private Intent intentservice;
    private Dialogcouleur dialogcouleur;
    private int y=0;
    private int autoriseryup=0;
    private int autoriserydown=0;
    private ObservableListView list;
    public String filePath;
    public String filename;
    private float mPreviousY=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(Messagerie.this, openfire.class);
        intentservice =intent;
       // stopService(new Intent(this, openfire.class));
        startService(intent);
        new Broadcastmessage();
        open=true;
        foreground=open;
        messagerie=this;
        bindService(intentservice, mConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.messagerie);
        /*mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //getWindow().setBackgroundDrawableResource(R.drawable.message);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        RelativeLayout principal =(RelativeLayout)findViewById(R.id.principale);
        principal.setBackgroundColor(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB")));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB"))));
        CardView mCardView = (CardView)findViewById(R.id.card_view);
        mCardView.setCardBackgroundColor(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB")));
        pseudo = settings.getString("name", "ee");
        Log.w("myApp", pseudo);
        list = (ObservableListView) findViewById(R.id.list);
        View view = View.inflate(this, R.layout.listview_header, null);
        list.addHeaderView(view);
        list.setScrollViewCallbacks(this);
        db = new SQLiteDatabaseHandler(this);
        List<message>  listtemporaire=db.showAll(0,25+Integer.parseInt(settings.getString("nbnewmessage", "0")));

        for(message mestemporaire : listtemporaire)l.add(0,mestemporaire);

        adp = new AdaptaterMessage(this,l);
        /*chargementmessage = new Button(this);
        chargementmessage.setText("charger messages");
        chargementmessage.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {
                    chargermessages();
            }
        });
        list.addHeaderView(chargementmessage);*/

        if(adp.getCount()!=0)list.setAdapter(adp);


        if(adp.getCount()!=0)list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(l.get(firstVisibleItem).getTime()!=null)
                {
                    TextView datext = (TextView) findViewById(R.id.datetextinfff);
                    datext.setText(jourformat.format(l.get(firstVisibleItem).getTime()));
                }
            }
        });
        if(adp.getCount()!=0)list.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                            if(y==0)y= (int) event.getY();
                            else if(y< event.getY() )
                            {
                                autoriserydown=0;
                                autoriseryup++;
                                if(autoriseryup==20) {
                                    onUpOrCancelMotionEvent(ScrollState.DOWN);
                                    y = (int) event.getY();
                                    Log.w("ee", "up");
                                    autoriseryup=0;
                                }
                            }
                            else if (y> event.getY())
                            {
                                autoriserydown++;
                                autoriseryup=0;
                                if(autoriserydown==20) {
                                    onUpOrCancelMotionEvent(ScrollState.UP);
                                    y = (int) event.getY();
                                    Log.w("ee", "down");
                                    autoriserydown=0;
                                }
                            }
                        return false;
                    }
                }

        );

        CircleImageView send = (CircleImageView) findViewById(R.id.boutonsend);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) Messagerie.this.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    InputMethodManager imm = (InputMethodManager) Messagerie.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    EditText edi = (EditText) findViewById(R.id.messageedit);
                    int information = mService.sendMessage(edi.getText().toString());
                    EditText editText = (EditText) findViewById(R.id.messageedit);
                    editText.setText("");
                    if (information == 0) {
                        Toast.makeText(getApplicationContext(), "impossible à envoyer",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "pas de connexion internet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                chargermessages();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView txt = (TextView) view.findViewById(R.id.textmessage);
                message mess = (message) adp.getItem(position);
                Toast.makeText(getApplicationContext(), "copié",
                        Toast.LENGTH_LONG).show();
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                String text = txt.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
            }
        });

        ImageButton multimedia = (ImageButton)findViewById(R.id.multimedia);
        multimedia.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              String[] tab = {"Prendre une photo", "ajouter photo"};
                                              AlertDialog.Builder buildermultimedia = new AlertDialog.Builder(Messagerie.this);
                                              buildermultimedia.setTitle("Ajout Pièce jointe");
                                              buildermultimedia.setItems(tab, new DialogInterface.OnClickListener() {
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      if (which == 1) {
                                                          Intent intent = new Intent();
                                                          intent.setType("image/*");
                                                          intent.setAction(Intent.ACTION_GET_CONTENT);
                                                          startActivityForResult(Intent.createChooser(intent, "selectionner image"), 0);
                                                      }
                                                  }
                                              });
                                              buildermultimedia.show();
                                          }
                                      }

        );
        final LinearLayout linphoto = (LinearLayout)findViewById(R.id.layoutshowphotoenvoit);
        linphoto.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

               if(mPreviousY==0)mPreviousY=event.getY();
                else if(event.getY()>mPreviousY)
               {
                   Log.w("ee1", String.valueOf(linphoto.getLayoutParams().height));
                   linphoto.getLayoutParams().height= (int) (linphoto.getLayoutParams().height-(event.getY()-mPreviousY));
                   mPreviousY=event.getY();
                   Log.w("ee2", String.valueOf(linphoto.getLayoutParams().height));
                   Log.w("ee3",String.valueOf(mPreviousY));
               }
                return false;
            }
        });
        }


        private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            openfire.LocalBinder binder = (openfire.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void onDestroy()
    {
        super.onDestroy();
        open=false;
       unbindService(mConnection);


    }

    public  void onPause()
    {
        super.onPause();
        foreground=false;
        int i=0;
        while(i<adp.getCount() &&((message)adp.getItem(i)).getId()!=-1 )i++;
        if(i<adp.getCount())adp.destroy(i);
    }

    public void onResume()
    {
        super.onResume();
        foreground=true;
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if(Integer.parseInt(settings.getString("nbnewmessage", "0"))!=0)l.add(l.size()-Integer.parseInt(settings.getString("nbnewmessage", "0")),new message());
        adp.notifyDataSetChanged();
        ListView list = (ListView) findViewById(R.id.list);
        list.post(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView) findViewById(R.id.list);
                Log.w("eerere", String.valueOf(Integer.parseInt(settings.getString("nbnewmessage", "0"))));
                if(adp.getCount()>2)list.setSelection(adp.getCount() - Integer.parseInt(settings.getString("nbnewmessage", "0"))-2);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("nbnewmessage", String.valueOf(0));
                editor.commit();
            }
        });

    }

    public static void newmessage(message mess)
    {
        if(messagerie!=null)messagerie.addmessage(mess);
    }

    public void addmessage(message mess)
    {
        nbmessage++;
        l.add(mess);
        if(foreground) {
            adp.notifyDataSetChanged();
            ListView list = (ListView) findViewById(R.id.list);
            list.post(new Runnable() {
                @Override
                public void run() {
                    ListView list = (ListView) findViewById(R.id.list);
                    list.setSelection(adp.getCount() - 1);
                }
            });
        }
    }

    public void chargermessages() {
        final List<message> listetempo = db.showAll(nbmessage, nbmessage + 25);
        for(message mestemporaire : listetempo)l.add(0,mestemporaire);
        adp = new AdaptaterMessage(this,l);
        nbmessage += 25;
        ListView list = (ListView) findViewById(R.id.list);
        list.post(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView) findViewById(R.id.list);
                list.setSelection(listetempo.size());
            }
        });
        SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setRefreshing(false);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.colormenu:
                    dialogcouleur = new Dialogcouleur();
                    dialogcouleur.show(getFragmentManager(),"coucou");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void cliquecouleur(View v) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if(dialogcouleur.choix==0)
        {
            editor.putString("couleurbackgroung", String.valueOf(v.getTag()));
        }
        if(dialogcouleur.choix==1)
        {
            editor.putString("couleurtheirmessage", String.valueOf(v.getTag()));
        }
        if(dialogcouleur.choix==2)
        {
            editor.putString("couleurmymessage",  String.valueOf(v.getTag()));
        }
        editor.commit();
        adp.notifyDataSetChanged();
        dialogcouleur.dialogcolor.dismiss();
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(null);
        if(adp.getCount()!=0)list.setAdapter(adp);
        if(adp.getCount()!=0)list.post(new Runnable() {
            @Override
            public void run() {
                ListView list = (ListView) findViewById(R.id.list);
                list.setSelection(adp.getCount() - 1);
            }
        });
        RelativeLayout principal =(RelativeLayout)findViewById(R.id.principale);
        principal.setBackgroundColor(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB")));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB"))));
        CardView mCardView = (CardView)findViewById(R.id.card_view);
        mCardView.setCardBackgroundColor(Color.parseColor(settings.getString("couleurbackgroung", "#9AFFCB")));
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b1) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar ab = getSupportActionBar();

        if (scrollState == ScrollState.DOWN) {
            if (ab.isShowing()) {
                LinearLayout li =(LinearLayout)findViewById(R.id.layoutdateinffff);
                li.animate().translationY(-getSupportActionBar().getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
           // mToolbar.animate().translationY(-mToolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

            ab.hide();
           }
        } else if (scrollState == ScrollState.UP) {
            if (!ab.isShowing()) {
            ab.show();
                LinearLayout li =(LinearLayout)findViewById(R.id.layoutdateinffff);
                li.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
           // mToolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }
        }
        else
        {
            autoriseryup=0;
            autoriserydown=0;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            filename = filePath.substring(filePath.lastIndexOf("/") + 1);
            cursor.close();
            ImageView img = (ImageView) findViewById(R.id.showphoto);
            img.setImageURI(selectedImage);
        }
    }
}
