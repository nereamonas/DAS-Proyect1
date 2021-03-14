package com.example.das_proyect1.ui.ajustes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;

public class AjustesUsuarioFragment  extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    //Aqui cambairemos la informacion  del usuario. username, pass y correo
    private MiDB db;
    private SharedPreferences prefs;
    private String user;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_ajustes_usuario, rootKey);
        this.db=new
                MiDB(getContext());
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (this.prefs.contains("username")) {//Comprobamos si existe  Deberia de pasar el username por parametro.
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
                    username = this.prefs.getString("username", null);  //Cogemos el usuaario nuevo

                    Log.d("Logs", "nombre nuevo: "+username+" viejo"+this.user);
                    if (!this.user.equals(username)) { //Miramos si tiene las notificaciones toast activadas
                        boolean todobien = this.db.editarNombreDeUsuario(this.user, username);
                        if (todobien) {
                            this.user = username;
                            if(this.prefs.contains("notiftoast")) { //Comprobamos si las notificaciones toast estan activadas, para mandarle una notificacion
                                boolean activadas = prefs.getBoolean("notiftoast", true);
                                Log.d("Logs", "estado notificaciones toast: " + activadas);
                                if (activadas) {
                                    Toast toast = Toast.makeText(getContext(), getString(R.string.toast_usuarioCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                    toast.show();

                                }
                            }
                            reload();
                        }else{ //No se ha podido actualizar
                            Log.d("Logs","No se puede actualizar xq son iguales");
                            SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
                            editor.putString("username", this.user);  //Devolvemos el valor q estaba anteriormente
                            editor.apply();
                            if (this.prefs.contains("notiftoast")) {///Miramos si estan activadas las notificaciones
                                boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                                Log.d("Logs", "NO-estado notificaciones toast: " + activadas);
                                if (activadas) {
                                    Toast toast = Toast.makeText(getContext(), getString(R.string.toast_usuarionoCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }
                    }


                }
                break;
            case "email":
                Log.d("Logs", "ha insertado un email");
                String email = "";
                if (this.prefs.contains("email")) {//Comprobamos si existe
                    email = this.prefs.getString("email", null);
                    Log.d("Logs", "email nuevo: "+email);
                    boolean todobien=this.db.editarEmailDeUsuario(this.user,email);
                    if (todobien){
                        if (this.prefs.contains("notiftoast")) {
                            boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            Log.d("Logs", "estado notificaciones toast: " + activadas);
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_emailCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        reload();
                    }else{ //Volvemos el valor al estado anterior
                        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
                        editor.putString("email", db.getCorreoConUsuario(this.user));
                        editor.apply();
                        if (this.prefs.contains("notiftoast")) {
                            boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            Log.d("Logs", "NO-estado notificaciones toast: " + activadas);
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_emailnoCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }

                }
                break;
            case "pass":
                Log.d("Logs", "ha insertado un pass");
                String pass = "";
                if (this.prefs.contains("pass")) {//Comprobamos si existe
                    pass = this.prefs.getString("pass", null);
                    Log.d("Logs", "pass nuevo: "+pass);
                    boolean todobien=this.db.editarPassDeUsuario(this.user,pass);
                    if (todobien){
                        if (this.prefs.contains("notiftoast")) {
                            boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            Log.d("Logs", "estado notificaciones toast: " + activadas);
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_contraseñaCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                        reload();
                    }else{ //Volvemos el valor al estado anterior
                        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
                        editor.putString("pass", db.getPassConUsuario(this.user));
                        editor.apply();
                        if (this.prefs.contains("notiftoast")) {
                            boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            Log.d("Logs", "NO - estado notificaciones toast: " + activadas);
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_contraseñanoCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }

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
        //Reiniciamos. porq si se ha actualizado bien. en el intent principal en el menu arriba a la derecha sale el nombre de usuario y el correo, entonces hay q actualizar esos. por eso se reinicia
        getActivity().finish();
        startActivity(getActivity().getIntent());
        Intent i = new Intent(getActivity(), PrincipalActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        i.putExtra("ajustes", true);
        i.putExtra("usuario", this.user);
        startActivity(i);
    }

}