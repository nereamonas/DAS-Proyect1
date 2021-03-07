package com.example.das_proyect1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.das_proyect1.helpClass.Usuario;

import java.util.Locale;

public class LogInActivity extends AppCompatActivity {
    private MiDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db=new MiDB(this);
        db.añadirPrimerosElementos();

        //Comprobamos el idioma de la aplicacion que tenemos guardado para cargarlo
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = "";
        if (prefs.contains("idioma")) {
            idioma = prefs.getString("idioma", null);
        }

        String idiomaActual=Locale.getDefault().getLanguage();
        Log.d("Logs", "LANGUAGE de ajustes "+idioma);
        Log.d("Logs", "LANGUAGE "+idiomaActual);
        if (!idiomaActual.equals(idioma)) {   //Cuando cierras y vuelves a abrir la app se va el idioma
/**
            Locale nuevaloc = new Locale(idioma);
            Locale.setDefault(nuevaloc);
            Configuration configuration = this.getBaseContext().getResources().getConfiguration();

            configuration.setLocale(nuevaloc);
            configuration.setLayoutDirection(nuevaloc);

            Context context = this.getBaseContext().createConfigurationContext(configuration);
            this.getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
            this.finish();
            startActivity(this.getIntent());**/
        }

    }


    public void clickIniciarSesion(View v){
        //Iniciamos el controlador de la base de datos


        EditText editView_user = (EditText)findViewById(R.id.editView_user);
        EditText editView_pass = (EditText)findViewById(R.id.editView_pass);
        String user=editView_user.getText().toString();
        String pass=editView_pass.getText().toString();

        if (user!="" && pass!="") {
            Log.d("Logs", "Usuario"+user+ " Contraseña"+pass);
            Usuario usuario = db.comprobarUsuario(user, pass);

            if (usuario != null) {
                Log.d("Logs", usuario.toString());
                Intent i = new Intent(this, PrincipalActivity.class);
                i.putExtra("usuario", user);
                startActivity(i);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error");
                builder.setMessage("El usuario o la contraseña son incorrectos");
                builder.setPositiveButton("Aceptar", null);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    public void clickCrearCuenta(View v){
        Intent i = new Intent(this, SingUpActivity.class);

        startActivity(i);
    }
}