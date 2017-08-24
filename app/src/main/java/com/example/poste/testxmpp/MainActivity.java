package com.example.poste.testxmpp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity  {
    public static final String PREFS_NAME = "enregistrement";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String mailauto = settings.getString("mail","ee");
        if(!mailauto.equals("ee"))
        {
            EditText mmautotext = (EditText)findViewById(R.id.mailsignin);
            mmautotext.setText(mailauto);
        }
        String mdpauto = settings.getString("mdp","ee");
        if(!mdpauto.equals("ee"))
        {
            EditText passwordautotext = (EditText)findViewById(R.id.passwordsignin);
            passwordautotext.setText(mdpauto);
        }

       Button buttonsignin = (Button) findViewById(R.id.Bsignin);
        buttonsignin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout layoutsignin = (RelativeLayout) findViewById(R.id.layout_signin);
                layoutsignin.setVisibility(View.VISIBLE);
                Button buttonsignin = (Button) findViewById(R.id.Bsignin);
                buttonsignin.setVisibility(View.INVISIBLE);
                RelativeLayout layoutsignup = (RelativeLayout) findViewById(R.id.layout_signup);
                layoutsignup.setVisibility(View.INVISIBLE);
                Button buttonsignup = (Button) findViewById(R.id.Bsignup);
                buttonsignup.setVisibility(View.VISIBLE);

            }
        });

        Button buttonsignup = (Button) findViewById(R.id.Bsignup);
        buttonsignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                RelativeLayout layoutsignin = (RelativeLayout) findViewById(R.id.layout_signin);
                layoutsignin.setVisibility(View.INVISIBLE);
                Button buttonsignin = (Button) findViewById(R.id.Bsignin);
                buttonsignin.setVisibility(View.VISIBLE);
                RelativeLayout layoutsignup = (RelativeLayout) findViewById(R.id.layout_signup);
                layoutsignup.setVisibility(View.VISIBLE);
                Button buttonsignup = (Button) findViewById(R.id.Bsignup);
                buttonsignup.setVisibility(View.INVISIBLE);
            }
        });

        Button buttonconnection = (Button) findViewById(R.id.connection);
        buttonconnection.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText mail = (EditText)findViewById(R.id.mailsignin);
                EditText password = (EditText)findViewById(R.id.passwordsignin);
                if(mail.getText().toString().equals(""))
                {
                    TranslateAnimation animation = new TranslateAnimation(0.0f, 20.0f,0.0f, 0.0f);
                    animation.setDuration(150);
                    animation.setRepeatCount(5);
                    animation.setRepeatMode(2);
                    animation.setFillAfter(true);
                    mail.startAnimation(animation);
                }
                else if(password.getText().toString().equals(""))
                {
                    TranslateAnimation animation = new TranslateAnimation(0.0f, 20.0f,0.0f, 0.0f);
                    animation.setDuration(150);
                    animation.setRepeatCount(5);
                    animation.setRepeatMode(2);
                    animation.setFillAfter(true);
                    password.startAnimation(animation);
                }
                else
                {
                   // InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                   // imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("mail", mail.getText().toString());
                    editor.putString("mdp", password.getText().toString());
                    editor.commit();
                    new Connexion(mail.getText().toString(),password.getText().toString()).execute();
                }
            }
        });

        Button buttonenregistrer = (Button) findViewById(R.id.enregistrement);
        buttonenregistrer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }
        });
    }

    public class Connexion extends AsyncTask<Void, Integer, Integer> {

        private String adresse;
        private String motpass;
        private String resultat="";
        public Connexion(String adresse,String motpass)
        {
            this.adresse=adresse;
            this.motpass=motpass;
        }
        @Override
        protected void onPreExecute() {

            ProgressBar p1 =(ProgressBar)findViewById(R.id.p1_2);
            p1.setVisibility(View.VISIBLE);
            ProgressBar p2 =(ProgressBar)findViewById(R.id.p2_1);
            p2.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            InputStream is = null;
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("adresse", adresse));
            nameValuePairs.add(new BasicNameValuePair("mdp", motpass));
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://151.80.149.128/android_co.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                resultat = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Integer... prog) {


        }

        @Override
        protected void onPostExecute(Integer result) {
            ProgressBar p1 =(ProgressBar)findViewById(R.id.p1_2);
            p1.setVisibility(View.INVISIBLE);
            ProgressBar p2 =(ProgressBar)findViewById(R.id.p2_1);
            p2.setVisibility(View.INVISIBLE);
            if(resultat.equals(""))
            {
                Button buttonconnection = (Button) findViewById(R.id.connection);
                TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f,0.0f, -20.0f);
                animation.setDuration(100);
                animation.setRepeatCount(5);
                animation.setRepeatMode(2);
                animation.setFillAfter(true);
                buttonconnection.startAnimation(animation);
            }
            else
            {
                resultat = resultat.replace("\"","");
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("name", resultat);
                editor.commit();
                Intent intent;
                intent = new Intent(MainActivity.this, Messagerie.class);
                startActivity(intent);
            }
        }
    }
}
