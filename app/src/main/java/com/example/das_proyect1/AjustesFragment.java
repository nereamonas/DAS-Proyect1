package com.example.das_proyect1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;

import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

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
        if (this.prefs.contains("username")) {//Comprobamos si existe
            this.user = this.prefs.getString("username", null);
        }
        //this.user=getArguments().getString("usuario");
        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
        editor.putString("username", this.user);
        editor.putString("email", db.getCorreoConUsuario(this.user));
        editor.putString("pass", db.getPassConUsuario(this.user));
        editor.apply();
        boolean resultado=editor.commit();
        Log.d("Logs", "RESULTADO EDITAR DATOS "+resultado);


        Preference infouser=(Preference) findPreference("infouser");
        infouser.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                int rotacion=getActivity().getWindowManager().getDefaultDisplay().getRotation();
                if (rotacion== Surface.ROTATION_0 || rotacion==Surface.ROTATION_180) {
                    //La pantalla está en vertical
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_ajustesGeneralesFragment_to_ajustesUsuarioFragment);
                }
                return false;
            }
        });


    }


    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        switch (s) {
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