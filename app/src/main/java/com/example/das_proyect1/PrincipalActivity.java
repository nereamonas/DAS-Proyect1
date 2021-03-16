package com.example.das_proyect1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.example.das_proyect1.base.BaseActivity;
import com.example.das_proyect1.helpClass.MiDB;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.preference.PreferenceManager;

public class PrincipalActivity extends BaseActivity {//ControlarCambios   AppCompatActivity
    private String usuario;
    private AppBarConfiguration mAppBarConfiguration;
    private MiDB db;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        String tema=super.returnTema();  //Hemos creado un BaseActivity para controlar los temas y el idioma. pero, en el principal tenemos q desactivar el action bar, por lo q habria q ponerle otro tema. asique vamos a cambiar aqui el tema

        switch (tema) {
            case "morado":
                this.setTheme(R.style.Theme_Morado_NoActionBar);
                break;
            case "naranja":
                this.setTheme(R.style.Theme_Naranja_NoActionBar);
                break;
            case "verde":
                this.setTheme(R.style.Theme_Verde_NoActionBar);
                break;
            case "azul":
                this.setTheme(R.style.Theme_Azul_NoActionBar);
                break;
            default:
                this.setTheme(R.style.Theme_Morado_NoActionBar);
                break;
        }
        setContentView(R.layout.activity_principal);
        //getSupportActionBar().hide();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        this.usuario = "";  //Cogemos el usuario que le hemos pasado del intent anterior
        if (getIntent().hasExtra("usuario")) {
            this.usuario = getIntent().getExtras().getString("usuario");
        }
        //En ajustes al cambiar el idioma y el tema, tenemos q resetear el intent, entonces si le pasamos el aldagai ajustes significa que venimos desde ajustes y nos abrira ajustes en vez d quedarse en la principal
        if (getIntent().hasExtra("ajustes")) {  //si viene de ajustes
            open_nav_ajustes();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_rutinas, R.id.nav_ejercicios, R.id.nav_rutinasCompletadas, R.id.ajustesGeneralesFragment, R.id.nav_contactanos, R.id.nav_logoff,R.id.nav_calendar)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View vi = navigationView.getHeaderView(0);//Ponemos en el menu el nombre de usuario y el correo electronico

        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
        TextView correo = (TextView) vi.findViewById(R.id.correo);

        db = new MiDB(this);  //En el menu arriba a la derecha, queremos ponerle el nombre del usuario y el correo
        nombre.setText(this.usuario);
        correo.setText(db.getCorreoConUsuario(this.usuario));
        this.db.cerrarConexion();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);  //Editaremos las preferencias, para ponerle el usuario con el que ha iniciado sesion. Es importante actualizarlo cuando se inicia sesion, porq sino en preferencias se guarda el anterior q estaba
        //if (!prefs.contains("username")) {//si no existe, insertamos el usuario. siempre. por si hay cambio de usuario
            SharedPreferences.Editor editor= prefs.edit();  //Creamos un editor para asignarle los valores d la bbdd
            editor.putString("username", this.usuario);
            editor.apply();
            editor.commit();
        //}

        //Controlaremos todos los botones del menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_rutinas) {  //Si clicamos en rutinas, abriremos con navigation, la ventana donde se muestran las rutinas. he igual con todos
                    Log.d("Logs", "nav_rutinas");
                    open_nav_rutinas();
                } else if (id == R.id.nav_ejercicios) {
                    Log.d("Logs", "nav_ejercicios");
                    open_nav_ejercicios();
                }else if (id==R.id.nav_rutinasCompletadas){
                    Log.d("Logs", "nav_rutinasCompletadas");
                    open_nav_rutinascompletadas();
                } else if (id == R.id.nav_ajustes) {
                    Log.d("Logs", "nav_ajustes");
                    open_nav_ajustes();
                } else if (id == R.id.nav_contactanos) {  //Si vamos a contactanos, queremos q nos abra el gmail o outlook o lo q sea para enviar un email, le pasaremos el email destino, un asunto y un texto
                    Log.d("Logs", "nav_contactanos");
                    String to= "nereamonasterio99@gmail.com";
                    String subject = getString(R.string.email_dudaValoracionaplicacion);
                    String body = getString(R.string.email_mandanosmensaje);
                    //SOLO FUNCIONA EN OUTLOOK
                    //Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+to));
                    //emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    //startActivity(emailIntent);

                    //ASI FUNCIONA TAMBIEN PARA GMAIL
                    String uriText="mailto:"+to+"?subject="+Uri.encode(subject)+"&body="+Uri.encode(body);
                    Uri uri= Uri.parse(uriText);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+to));
                    emailIntent.setData(uri);
                    startActivity(Intent.createChooser(emailIntent,"Send email"));

                } else if (id == R.id.nav_logoff) {  //En logoff mostraremos una alerta, para asegurarnos q el usuario quiere cerrar su sesión. si dice q si, la llevaremos al intent iniciar sesion, y si dice q no se quedara tal cual
                    Log.d("Logs", "nav_logoff");
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(vi.getContext());
                    dialogo.setTitle(getString(R.string.alert_cerrarSesion));
                    dialogo.setMessage(getString(R.string.alert_estasseguroquequierescerrarsesion));
                    //dialogo.setCancelable(false);
                    dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Intent i = new Intent(getApplication(), LogInActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Log.d("Logs", "no cerrar sesion");
                        }
                    });
                    dialogo.show();
                }else if (id == R.id.nav_calendar) {
                    Log.d("Logs", "calendar");
                    open_calendar();
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void open_nav_rutinas(){
        //Creamos un bundle, para insertar los datos q queremos pasar entre los diferentes fragmentos
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)  //Queremos q siga el modelo singleTop. es decir q si es el fragment de arriba, coga ese y no lo vuelva a crear
                .build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_rutinas, bundle,options); //Navegamos
    }
    public void open_nav_ejercicios(){
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_ejercicios, bundle,options);
    }
    public void open_nav_ajustes(){
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_ajustesGeneralesFragment, bundle,options);
    }
    public void open_nav_rutinascompletadas(){
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_rutinasCompletadasFragment,null,options);
    }
    public void open_calendar(){
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .build();
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_fragment_calen,null,options);
    }

}