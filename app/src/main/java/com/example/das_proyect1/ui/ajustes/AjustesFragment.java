package com.example.das_proyect1.ui.ajustes;

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

import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;

import java.util.Locale;

public class AjustesFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    //Clase donde se administran los ajustes principales de la aplicación. El cambio de idioma, el tema y la activación de notificaciones

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
        this.db.cerrarConexion(); //Cerramos la conexion porq no lo vamos a usar mas

        //Vamos a configurar q depende la rotacion de pantalla funcione de una forma o otra. si esta en vertical y se clica en info usuario devera pasar a otro fragment
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
                //A cambiado el valor del idioma. Pues aplicamos la configuracion para cambiar el idioma
                //Como hemos creado esa clase de herencia, solo tenemos q reiniciar el intent y ya se aplican los cambios. puesto q cada vez q se crea, mirara en los ajustes el idioma establecido
                Log.d("Logs", "cambio idioma");
                reload();
                break;
            case "tema":
                //A cambiado el tema
                //Lo mismo q el idioma, realmente, solo tenemos q reiniciar el intent porq ya aplicamos la configuracion al crearlo. sino esa configuracions  aplica aqui
                Log.d("Logs", "cambio tema");
                reload();
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
        //Reiniciamos el intent. y le pasamos el parametro ajustes, para q lo inicialice en este fragment
        getActivity().finish();
        startActivity(getActivity().getIntent());
        Intent i = new Intent(getActivity(), PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("ajustes", true);
        i.putExtra("usuario", this.user);
        startActivity(i);
    }


}
//generan un fichero d preferencias enlazado a la app