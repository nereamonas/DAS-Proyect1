package com.example.das_proyect1.ui.ajustes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;
import com.example.das_proyect1.SingUpActivity;
import com.google.android.material.appbar.AppBarLayout;

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
                reload();

                Log.d("Logs", "idioma nuevo: " + idioma);

                break;
            case "tema":
                Log.d("Logs", "cambio tema");

                String tema = "";
                if (this.prefs.contains("tema")) {
                    tema = this.prefs.getString("tema", null);
                }
                switch (tema) {
                    case "morado":
                        getContext().setTheme(R.style.Theme_Morado);
                        getActivity().setTheme(R.style.Theme_Morado);
                        reload();
                        Log.d("Logs", "color elegido morado");
                        break;
                    case "naranja":
                        getContext().setTheme(R.style.Theme_Naranja);
                        getActivity().setTheme(R.style.Theme_Naranja);
                        reload();
                        Log.d("Logs", "color elegido naranja");
                        break;
                    case "verde":
                        getContext().setTheme(R.style.Theme_Verde);
                        getActivity().setTheme(R.style.Theme_Verde);
                        reload();
                        Log.d("Logs", "color elegido verde");
                        break;
                    case "azul":
                        getContext().setTheme(R.style.Theme_Azul);
                        getActivity().setTheme(R.style.Theme_Azul);
                        reload();
                        Log.d("Logs", "color elegido azul");
                        break;
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
        getActivity().finish();
        startActivity(getActivity().getIntent());
        Intent i = new Intent(getActivity(), PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("ajustes", true);
        startActivity(i);
    }


}
//https://www.develou.com/como-crear-actividad-preferencias-android/
//https://www.youtube.com/watch?v=Pay4nZu9Kuc
//https://www.youtube.com/watch?v=BxOafejwT3c

//generan un fichero d preferencias enlazado a la app