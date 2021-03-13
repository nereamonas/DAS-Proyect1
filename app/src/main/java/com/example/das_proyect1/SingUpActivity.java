package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.das_proyect1.controlarCambios.ControlarCambios;

public class SingUpActivity extends ControlarCambios {
    private MiDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        ImageView img= findViewById(R.id.img2);
        img.setImageResource(R.mipmap.avatar);
    }

    public void clickCrearCuenta(View v){
        //Iniciamos el controlador de la base de datos
        db=new MiDB(this);

        EditText editView_user = (EditText)findViewById(R.id.editView_user2);
        EditText editView_mail = (EditText)findViewById(R.id.editView_mail);
        EditText editView_pass = (EditText)findViewById(R.id.editView_pass2);
        EditText editView_passAgain = (EditText)findViewById(R.id.editView_passAgain);

        String user=editView_user.getText().toString();
        String pass=editView_pass.getText().toString();
        String passAgain=editView_passAgain.getText().toString();
        String mail=editView_mail.getText().toString();


        if (user!="" && pass!=""&& passAgain!=""&& mail!="") {
            Log.d("Logs", "Usuario: "+user+ " Mail: "+mail+" Contraseña: "+pass+" Contraseña again: "+passAgain);

            if(passAgain.equals(pass)) {
                Boolean resultado = db.crearUsuario(user,mail,pass);

                if (resultado) {
                    Log.d("Logs", "Usuario creado");

                    Intent i = new Intent(this, PrincipalActivity.class);
                    i.putExtra("usuario", user);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.alert_error));
                    builder.setMessage(getString(R.string.alert_nosehapodidocrealelusuario));
                    builder.setPositiveButton(getString(R.string.alert_aceptar), null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.alert_error));
                builder.setMessage(getString(R.string.alert_lascontraseñasnocoinciden));
                builder.setPositiveButton(getString(R.string.alert_aceptar), null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void clickIniciarSesion(View v){
        Intent i = new Intent(this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}