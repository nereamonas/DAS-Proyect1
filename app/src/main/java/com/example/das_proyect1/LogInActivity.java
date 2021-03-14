package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.das_proyect1.base.BaseActivity;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.helpClass.Usuario;

public class LogInActivity extends BaseActivity {
    private MiDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=new MiDB(this);
        db.añadirPrimerosElementos();

        ImageView img= findViewById(R.id.img);
        img.setImageResource(R.mipmap.avatar);

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
                this.db.cerrarConexion();
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