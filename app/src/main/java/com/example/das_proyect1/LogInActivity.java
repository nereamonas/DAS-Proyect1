package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.das_proyect1.base.BaseActivity;
import com.example.das_proyect1.helpClass.ExternalDB;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.helpClass.Usuario;

public class LogInActivity extends BaseActivity {

    //La actividad que se encarga de gestionar el inicio de sesión, si los datos introducidos por el usuaario son correctos en base de datos, redirige a la ventana principal. En caso contrario muestra un mensaje de error

    private MiDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=new MiDB(this);
        db.añadirPrimerosElementos();
        this.db.cerrarConexion();
        ImageView img= findViewById(R.id.img);
        img.setImageResource(R.mipmap.avatar);

    }


    public void clickIniciarSesion(View v){
        //Iniciamos el controlador de la base de datos

        EditText editView_user = (EditText)findViewById(R.id.editView_user);
        EditText editView_pass = (EditText)findViewById(R.id.editView_pass);
        String user=editView_user.getText().toString();  //Cogemos el nombre d usuario q ha insertado el usuario
        String pass=editView_pass.getText().toString(); //Cogemos la pass q ha insertado el usuario

        if (!user.equals("") && !pass.equals("")) { //si es distinto null
            Log.d("Logs", "Usuario"+user+ " Contraseña"+pass);
            db=new MiDB(this);  //abrimos conexion con bbdd




            //ExternalDB ex=new ExternalDB();
            //Usuario usuario=ex.comprobarUsuario(user,pass);






            Usuario usuario = db.comprobarUsuario(user, pass);  //Comprobamos si existe
            this.db.cerrarConexion();//Cerramos conexion
            if (usuario != null) {//si el usuario existe
                Log.d("Logs", usuario.toString());
                Intent i = new Intent(this, PrincipalActivity.class);  //Abrimos el intent Principal para hacer el cambio
                i.putExtra("usuario", user);  //Le pasamos el nombre del usuario
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish(); //Cerramos el actual para q no s pueda volver
            } else {  //Saltamos una alerta d error
                saltarAlerta();
            }
        }else {  //Saltamos una alerta de error
            saltarAlerta();
        }
    }

    public void saltarAlerta(){  //Crearemos un alert dialog para decirle al usuario q los datos introducidos no son correctos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.alert_error));
        builder.setMessage(getString(R.string.alert_useropassincorrecto));
        builder.setPositiveButton(getString(R.string.alert_aceptar), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void clickCrearCuenta(View v){  //Si el usuario clica en crear una cuenta. abriremos ese intent y cerramos el actual
        Intent i = new Intent(this, SingUpActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}