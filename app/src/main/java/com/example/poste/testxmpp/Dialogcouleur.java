package com.example.poste.testxmpp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.rgb;

/**
 * Created by Poste on 04/06/2015.
 */
public class Dialogcouleur extends DialogFragment {

    public int choix;
    public AlertDialog dialogcolor;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] tab = {"arriere plan","messages recus","mes messages"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("a qui voulez vous attribuer la couleur")
                .setItems(tab, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                            choix=0;
                        }
                        if(which==1)
                        {
                            choix=1;
                        }
                        if(which==2)
                        {
                            choix=2;
                        }
                        AlertDialog.Builder buildercolor = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        buildercolor.setView(inflater.inflate(R.layout.dialogcouleur, null));
                        dialogcolor = buildercolor.create();
                        dialogcolor.show();
                    }
                });
        return builder.create();
    }

}

