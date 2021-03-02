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

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_ajustes, rootKey);
    }
    @Override
    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String s){
        switch (s) {
            case "username":
                Log.d("myTag", "ha insertado un nombre");

                break;
            case "idioma":
                Log.d("myTag", "cambio idioma");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String idioma = "";
                if (prefs.contains("idioma")) {
                    idioma = prefs.getString("idioma", null);
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

                Log.d("myTag", "idioma nuevo: " + idioma);

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

}
//https://www.develou.com/como-crear-actividad-preferencias-android/
//https://www.youtube.com/watch?v=Pay4nZu9Kuc
//https://www.youtube.com/watch?v=BxOafejwT3c

//generan un fichero d preferencias enlazado a la app