package com.example.poste.testxmpp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.rgb;

/**
 * Created by Poste on 21/05/2015.
 */
public class AdaptaterMessage extends BaseAdapter {
    private List<message> messageList;
    private Activity activity;
    public static final String PREFS_NAME = "enregistrement";
    private String pseudo;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    private String jourstring=null;
    private SharedPreferences settings;
    public AdaptaterMessage(Activity act,List<message> l)
    {
        messageList=l;
        activity=act;
        settings = activity.getSharedPreferences(PREFS_NAME, 0);
        pseudo = settings.getString("name","ee");

    }

    @Override
    public int getCount() {
        return messageList.size();
    }

    public void destroy(int em)
    {
        messageList.remove(em);
    }
    @Override
    public Object getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        String pseudoPrecedent = null;
        if(position-1>=0 && messageList.get(position - 1).getNom()!=null) {
            pseudoPrecedent = messageList.get(position - 1).getNom().split("@")[0];

        }
        message m = (message) getItem(position);
        if(m.getId()==-1)return 4;
        if (m.getNom().split("@")[0].equals(pseudo)) {
            if (m.getNom().split("@")[0].equals(pseudoPrecedent)) {
                return 1;
            } else {
                return 0;

            }
        }
        else
        {
            if (m.getNom().split("@")[0].equals(pseudoPrecedent)) {
                return 3;
            }
            else
            {
                return 2;
            }
        }
    }
    @Override

    public int getViewTypeCount() {

        return 5;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        int type = getItemViewType(position);
        message m = (message) getItem(position);

        if(convertView==null)
        {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if(type==4)
            {
                convertView = (LinearLayout) inflater.inflate(R.layout.newmessage, null);
                convertView.setTag(viewHolder);
            }
            if(type==0)
            {
                convertView = (RelativeLayout) inflater.inflate(R.layout.messaged, null);
                viewHolder.textmessage = (TextView) convertView.findViewById(R.id.textmessage);
                viewHolder.textmessage.setText(m.getContenu());
                viewHolder.textdate = (TextView) convertView.findViewById(R.id.textdate);
                viewHolder.textdate.setText(sdf.format(m.getTime()));
                viewHolder.photo = (CircleImageView) convertView.findViewById(R.id.profile_image);
                int id = activity.getResources().getIdentifier(pseudo.replaceAll("\\s+", ""), "drawable", activity.getPackageName());
                viewHolder.photo.setImageResource(id);
                viewHolder.nomholder=pseudo;
                LinearLayout li = (LinearLayout) convertView.findViewById(R.id.messagela);
                li.setBackgroundColor(Color.parseColor(settings.getString("couleurmymessage","#FF98FF")) );
                li = (LinearLayout) convertView.findViewById(R.id.me);
                LayerDrawable bgShape = (LayerDrawable)li.getBackground();
                RotateDrawable shape = (RotateDrawable)  bgShape.findDrawableByLayerId(R.id.rec);
                GradientDrawable drawable = (GradientDrawable) shape.getDrawable();
                drawable.setColor(Color.parseColor(settings.getString("couleurmymessage","#FF98FF")) );
                convertView.setTag(viewHolder);
            }
            if(type==1)
            {

                convertView = (RelativeLayout) inflater.inflate(R.layout.messagedplus, null);
                viewHolder.textmessage = (TextView) convertView.findViewById(R.id.textmessage);
                viewHolder.textmessage.setText(m.getContenu());
                viewHolder.textdate = (TextView) convertView.findViewById(R.id.textdate);
                viewHolder.textdate.setText(sdf.format(m.getTime()));
                viewHolder.nomholder=pseudo;
                LinearLayout li = (LinearLayout) convertView.findViewById(R.id.messagelap);
                li.setBackgroundColor(Color.parseColor(settings.getString("couleurmymessage","#FF98FF")) );
                convertView.setTag(viewHolder);
            }
             if(type==2)
             {
                 convertView = (RelativeLayout) inflater.inflate(R.layout.messageg, null);
                 viewHolder.textmessage = (TextView) convertView.findViewById(R.id.textmessage);
                 viewHolder.textmessage.setText(m.getContenu());
                 viewHolder.textdate = (TextView) convertView.findViewById(R.id.textdate);
                 viewHolder.textdate.setText(sdf.format(m.getTime()));
                 viewHolder.photo = (CircleImageView) convertView.findViewById(R.id.profile_image);
                 int id = activity.getResources().getIdentifier(m.getNom().split("@")[0], "drawable", activity.getPackageName());
                 viewHolder.photo.setImageResource(id);
                 viewHolder.nomholder=m.getNom().split("@")[0];
                 LinearLayout li = (LinearLayout) convertView.findViewById(R.id.messagela);
                 li.setBackgroundColor(Color.parseColor(settings.getString("couleurtheirmessage","#FF98FF")) );
                 li = (LinearLayout) convertView.findViewById(R.id.me);

                 LayerDrawable bgShape = (LayerDrawable)li.getBackground();
                 RotateDrawable shape = (RotateDrawable)  bgShape.findDrawableByLayerId(R.id.rec);
                 GradientDrawable drawable = (GradientDrawable) shape.getDrawable();
                 drawable.setColor(Color.parseColor(settings.getString("couleurtheirmessage", "#FF98FF")));

                 convertView.setTag(viewHolder);
             }
            if(type==3)
            {
                convertView = (RelativeLayout) inflater.inflate(R.layout.messagegplus, null);
                viewHolder.textmessage = (TextView) convertView.findViewById(R.id.textmessage);
                viewHolder.textmessage.setText(m.getContenu());
                viewHolder.textdate = (TextView) convertView.findViewById(R.id.textdate);
                viewHolder.textdate.setText(sdf.format(m.getTime()));
                viewHolder.nomholder=m.getNom().split("@")[0];
                LinearLayout li = (LinearLayout) convertView.findViewById(R.id.messagelap);
                li.setBackgroundColor(Color.parseColor(settings.getString("couleurtheirmessage","#FF98FF")) );
                convertView.setTag(viewHolder);
            }
        }
       else viewHolder=(ViewHolder) convertView.getTag();
        if(type!=4) {
            viewHolder.textmessage.setText(m.getContenu());
            viewHolder.textdate.setText(sdf.format(m.getTime()));
            if (type == 0) {
                int id = activity.getResources().getIdentifier(viewHolder.nomholder.replaceAll("\\s+", ""), "drawable", activity.getPackageName());
                if (viewHolder.photo != null) viewHolder.photo.setImageResource(id);
            }
            if (type == 2) {

                int id = activity.getResources().getIdentifier(m.getNom().split("@")[0], "drawable", activity.getPackageName());
                if (viewHolder.photo != null) viewHolder.photo.setImageResource(id);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        public  TextView textdate;
        public  TextView textmessage;
        public   CircleImageView photo;
        public  String nomholder;
    }
}
