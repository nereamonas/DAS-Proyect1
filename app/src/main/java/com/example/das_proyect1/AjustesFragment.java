package com.example.das_proyect1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class AjustesFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MiDB db;
    private SharedPreferences prefs;
    private String user;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_ajustes, rootKey);
        this.db=new MiDB(getContext());
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        this.user=getArguments().getString("usuario");
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
            case "pass":
                Log.d("Logs", "ha insertado una pass");
                String pass = "";
                if (this.prefs.contains("pass")) {  //Comprobamos si existe
                    pass = this.prefs.getString("pass", null);
                    Log.d("Logs", "pass nuevo: "+pass);
                    this.db.editarPassDeUsuario(this.user,pass);
                    reload();
                }
                break;
            case "idioma":
                Log.d("Logs", "cambio idioma");

                String idioma = "";
                if (this.prefs.contains("idioma")) {
                    idioma = this.prefs.getString("idioma", null);
                }
                Locale nuevaloc = new Locale(idioma);
                Locale.setDefault(nuevaloc);
                Configuration configuration = getActivity().getBaseContext().getResources().getConfiguration();
                configuration.setLocale(nuevaloc);
                configuration.setLayoutDirection(nuevaloc);

                Context context =getActivity().getBaseContext().createConfigurationContext(configuration);
                getActivity().getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
                getActivity().finish();
                startActivity(getActivity().getIntent());

                Log.d("Logs", "idioma nuevo: " + idioma);

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
//https://www.develou.com/como-crear-actividad-preferencias-android/
//https://www.youtube.com/watch?v=Pay4nZu9Kuc
//https://www.youtube.com/watch?v=BxOafejwT3c

//generan un fichero d preferencias enlazado a la app