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

public class SingUpActivity extends BaseActivity {
    private MiDB db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        ImageView img= findViewById(R.id.img2);
        img.setImageResource(R.mipmap.avatar);
    }

    public void clickCrearCuenta(View v){
        EditText editView_user = (EditText)findViewById(R.id.editView_user2);
        EditText editView_mail = (EditText)findViewById(R.id.editView_mail);
        EditText editView_pass = (EditText)findViewById(R.id.editView_pass2);
        EditText editView_passAgain = (EditText)findViewById(R.id.editView_passAgain);
        //Cogemos los 4 valores insertados por el usuario
        String user=editView_user.getText().toString();
        String pass=editView_pass.getText().toString();
        String passAgain=editView_passAgain.getText().toString();
        String mail=editView_mail.getText().toString();


        if (user!="" && pass!=""&& passAgain!=""&& mail!="") {  //Si todos son distintos de null
            Log.d("Logs", "Usuario: "+user+ " Mail: "+mail+" Contraseña: "+pass+" Contraseña again: "+passAgain);

            if(passAgain.equals(pass)) {  //Ski las contraseñas son iguales
                //Iniciamos el controlador de la base de datos
                db=new MiDB(this);
                boolean resultado = db.crearUsuario(user,mail,pass);  //Creamos el usuario
                this.db.cerrarConexion();   //Cerramos conexion con bbdd
                if (resultado) {  //Si se ha creado bien el usuario
                    Log.d("Logs", "Usuario creado");
                    //Cerramos el intent actual y abrimos el principal
                    Intent i = new Intent(this, PrincipalActivity.class);
                    i.putExtra("usuario", user);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                    finish();  //Lo cerramos para q no s pueda volver atras
                } else { //El usuario no se ha creado bnien por lo q mostramos una alerta al usuario nformando de ello
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getString(R.string.alert_error));
                    builder.setMessage(getString(R.string.alert_nosehapodidocrealelusuario));
                    builder.setPositiveButton(getString(R.string.alert_aceptar), null);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }else{//Las contraseñas no son iguales por lo q mostraremos una alerta para informar al usuario de ello
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.alert_error));
                builder.setMessage(getString(R.string.alert_lascontraseñasnocoinciden));
                builder.setPositiveButton(getString(R.string.alert_aceptar), null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void clickIniciarSesion(View v){ //Si el usuario clica en iniciar sesion. abriremos ese intent y cerramos el actual
        Intent i = new Intent(this, LogInActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
        finish();
    }
}