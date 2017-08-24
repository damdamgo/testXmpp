package com.example.poste.testxmpp;

import java.sql.Timestamp;

/**
 * Created by Poste on 18/05/2015.
 */
public class message {
    private int id;
    private String nom;
    private String contenu;
    private Timestamp time;

    public message(int id,String contenu,  String nom,Timestamp time) {
        this.contenu = contenu;
        this.id = id;
        this.nom = nom;
        this.time=time;
    }

    public message()
    {
        this.contenu = null;
        this.id = -1;
        this.nom = null;
        this.time=null;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public Timestamp getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "message{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", contenu='" + contenu + '\'' +
                ", time=" + time +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getContenu() {
        return contenu;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }
}
