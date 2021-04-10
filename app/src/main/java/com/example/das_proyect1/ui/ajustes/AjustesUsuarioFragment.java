package com.example.das_proyect1.ui.ajustes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.das_proyect1.helpClass.ExternalDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;

public class AjustesUsuarioFragment  extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    //Aqui cambairemos la informacion  del usuario. username, pass y correo

    private SharedPreferences prefs;
    private String user;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.fragment_ajustes_usuario, rootKey);

        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (this.prefs.contains("username")) {//Comprobamos si existe  Deberia de pasar el username por parametro.
            this.user = this.prefs.getString("username", null);
        }

        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
        editor.putString("username", this.user);
        editor.apply();
        Data datos = new Data.Builder()
                .putString("tarea","getCorreoConUsuario")
                .putString("usuario",this.user)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).enqueue(otwr);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String email=status.getOutputData().getString("email");
                        editor.putString("email", email);
                        editor.apply();
                    }
                });

        Data datos2 = new Data.Builder()
                .putString("tarea","getPassConUsuario")
                .putString("usuario",this.user)
                .build();
        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos2).build();
        WorkManager.getInstance(getActivity()).enqueue(otwr2);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr2.getId())
                .observe(getActivity(), status2 -> {
                    if (status2 != null && status2.getState().isFinished()) {
                        String pass=status2.getOutputData().getString("pass");
                        editor.putString("pass", pass);
                        editor.apply();
                    }
                });


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
                    if (!this.user.equals(username)) { //Si el usuario insertado es distinto al actual, es decir, lo ha editado, procedemos a modificarlo

                        Data datos = new Data.Builder()
                                .putString("tarea","editarNombreDeUsuario")
                                .putString("userViejo",this.user)
                                .putString("userNuevo",username)
                                .build();
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
                        WorkManager.getInstance(getActivity()).enqueue(otwr);
                        String finalUsername = username;
                        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                                .observe(getActivity(), status -> {
                                    if (status != null && status.getState().isFinished()) {
                                        boolean todobien=status.getOutputData().getBoolean("resultado",false);
                                        if (todobien) {//si se ha editado correctamente. mostramos un toast en el caso de q esten activdaas de como se ha modificado
                                            this.user = finalUsername;
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
                                });

                    }


                }
                break;
            case "email":
                Log.d("Logs", "ha insertado un email");
                String email = "";
                if (this.prefs.contains("email")) {//Comprobamos si existe
                    email = this.prefs.getString("email", null);
                    Log.d("Logs", "email nuevo: "+email);

                    Data datos = new Data.Builder()
                            .putString("tarea","editarEmailDeUsuario")
                            .putString("user",this.user)
                            .putString("email",email)
                            .build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
                    WorkManager.getInstance(getActivity()).enqueue(otwr);
                    WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                            .observe(getActivity(), status -> {
                                if (status != null && status.getState().isFinished()) {
                                    boolean todobien=status.getOutputData().getBoolean("resultado",false);

                                    if (todobien){//si se ha editado correctamente. mostramos un toast en el caso de q esten activdaas de como se ha modificado
                                        if (this.prefs.contains("notiftoast")) {
                                            boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                                            Log.d("Logs", "estado notificaciones toast: " + activadas);
                                            if (activadas) {
                                                Toast toast = Toast.makeText(getContext(), getString(R.string.toast_emailCambiadoCorrectamente), Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                        reload();
                                    }else{ //no se ha actualizado, por lo que Volvemos el valor al estado anterior
                                        SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd


                                        Data datos2 = new Data.Builder()
                                                .putString("tarea","getCorreoConUsuario")
                                                .putString("usuario",this.user)
                                                .build();
                                        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos2).build();
                                        WorkManager.getInstance(getActivity()).enqueue(otwr2);
                                        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr2.getId())
                                                .observe(getActivity(), status2 -> {
                                                    if (status2 != null && status2.getState().isFinished()) {
                                                        editor.putString("email", status2.getOutputData().getString("email"));
                                                        editor.apply();
                                                    }
                                                });

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
                            });

                }
                break;
            case "pass":
                Log.d("Logs", "ha insertado un pass");
                String pass = "";
                if (this.prefs.contains("pass")) {//Comprobamos si existe
                    pass = this.prefs.getString("pass", null);
                    Log.d("Logs", "pass nuevo: "+pass);




                    Data datos = new Data.Builder()
                            .putString("tarea","editarPassDeUsuario")
                            .putString("user",this.user)
                            .putString("pass",pass)
                            .build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
                    WorkManager.getInstance(getActivity()).enqueue(otwr);
                    WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                            .observe(getActivity(), status -> {
                                if (status != null && status.getState().isFinished()) {
                                    boolean todobien=status.getOutputData().getBoolean("resultado",false);


                                    if (todobien){  //si se ha editado correctamente. mostramos un toast en el caso de q esten activdaas de como se ha modificado
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

                                        Data datos2 = new Data.Builder()
                                                .putString("tarea","getPassConUsuario")
                                                .putString("usuario",this.user)
                                                .build();
                                        OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos2).build();
                                        WorkManager.getInstance(getActivity()).enqueue(otwr2);
                                        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr2.getId())
                                                .observe(getActivity(), status2 -> {
                                                    if (status2 != null && status2.getState().isFinished()) {
                                                        editor.putString("pass", status2.getOutputData().getString("pass"));
                                                        editor.apply();
                                                    }
                                                });

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
                            });

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