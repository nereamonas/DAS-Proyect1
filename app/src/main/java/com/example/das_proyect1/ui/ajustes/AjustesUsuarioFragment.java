package com.example.das_proyect1.ui.ajustes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;

import java.util.Locale;

public class AjustesUsuarioFragment  extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MiDB db;
    private SharedPreferences prefs;
    private String user;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_ajustes_usuario, rootKey);
        this.db=new MiDB(getContext());
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (this.prefs.contains("username")) {//Comprobamos si existe
            this.user = this.prefs.getString("username", null);
        }

        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
        editor.putString("username", this.user);
        editor.putString("email", db.getCorreoConUsuario(this.user));
        editor.putString("pass", db.getPassConUsuario(this.user));
        editor.apply();
        boolean resultado=editor.commit();
        Log.d("Logs", "RESULTADO EDITAR DATOS "+resultado);

    }
    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        switch (s) {
            case "username":
                Log.d("Logs", "ha insertado un nombre");

                String username = "";
                if (this.prefs.contains("username")) { //Comprobamos si existe
                    username = this.prefs.getString("username", null);
                    Log.d("Logs", "nombre nuevo: "+username);
                    Boolean todobien=this.db.editarNombreDeUsuario(this.user,username);
                    if (todobien) {
                        this.user = username;
                        reload();
                    }

                }
                break;
            case "email":
                Log.d("Logs", "ha insertado un email");
                String email = "";
                if (this.prefs.contains("email")) {//Comprobamos si existe
                    email = this.prefs.getString("email", null);
                    Log.d("Logs", "email nuevo: "+email);
                    this.db.editarEmailDeUsuario(this.user,email);
                    reload();
                }
                break;

            default:
                break;
        }
    }
    @Override
    public void onResume () {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause () {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void reload(){
        Intent i = new Intent(getActivity(), PrincipalActivity.class);
        i.putExtra("usuario", this.user);
        startActivity(i);
    }

}