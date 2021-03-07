package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class SingUpActivity extends AppCompatActivity {
    private MiDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                    startActivity(i);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Error");
                    builder.setMessage("No se ha podido crear el usuario");
                    builder.setPositiveButton("Aceptar", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("Las contraseñas no coinciden");
                builder.setPositiveButton("Aceptar", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void clickIniciarSesion(View v){
        Intent i = new Intent(this, LogInActivity.class);

        startActivity(i);
    }
}