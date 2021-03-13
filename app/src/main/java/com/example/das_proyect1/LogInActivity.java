package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.das_proyect1.controlarCambios.ControlarCambios;
import com.example.das_proyect1.helpClass.Usuario;

import java.util.Locale;

public class LogInActivity extends ControlarCambios {
    private MiDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=new MiDB(this);
        db.añadirPrimerosElementos();

        ImageView img= findViewById(R.id.img);
        img.setImageResource(R.mipmap.avatar);
        /**if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)!=
                PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACTIVITY_RECOGNITION))
            {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs","OSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO");
            }
            else{
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs","EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR");

            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 1);  //ACTIVITY_RECOGNITION
            Log.d("Logs","PEDIR EL PERMISO");

        }
        else {
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            Log.d("Logs","ELSE, el permiso ya esta concedido");
        }

**/
    }


    public void clickIniciarSesion(View v){
        //Iniciamos el controlador de la base de datos


        EditText editView_user = (EditText)findViewById(R.id.editView_user);
        EditText editView_pass = (EditText)findViewById(R.id.editView_pass);
        String user=editView_user.getText().toString();
        String pass=editView_pass.getText().toString();

        if (!user.equals("") && !pass.equals("")) {
            Log.d("Logs", "Usuario"+user+ " Contraseña"+pass);
            Usuario usuario = db.comprobarUsuario(user, pass);

            if (usuario != null) {
                Log.d("Logs", usuario.toString());
                Intent i = new Intent(this, PrincipalActivity.class);
                i.putExtra("usuario", user);
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            } else {
                saltarAlerta();
            }
        }else {
            saltarAlerta();
        }
    }

    public void saltarAlerta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_error));
        builder.setMessage(getString(R.string.alert_useropassincorrecto));
        builder.setPositiveButton(getString(R.string.alert_aceptar), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickCrearCuenta(View v){
        Intent i = new Intent(this, SingUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}