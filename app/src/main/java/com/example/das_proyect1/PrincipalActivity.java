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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

public class PrincipalActivity extends AppCompatActivity {
    private String usuario;
    private AppBarConfiguration mAppBarConfiguration;
    private MiDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.usuario = getIntent().getExtras().getString("usuario");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_entrada,R.id.nav_rutinas, R.id.nav_ejercicios, R.id.nav_ajustes, R.id.nav_contactanos, R.id.nav_logoff)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View vi = navigationView.getHeaderView(0);//Ponemos en el menu el nombre de usuario y el correo electronico

        TextView nombre = (TextView) vi.findViewById(R.id.nombre);
        TextView correo = (TextView) vi.findViewById(R.id.correo);

        db = new MiDB(this);
        nombre.setText(this.usuario);
        correo.setText(db.getCorreoConUsuario(this.usuario));

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_entrada) {
                    Log.d("Logs", "nav_entrada");
                    open_nav_entrada();
                }else if (id == R.id.nav_rutinas) {
                    Log.d("Logs", "nav_rutinas");
                    open_nav_rutinas();
                } else if (id == R.id.nav_ejercicios) {
                    Log.d("Logs", "nav_ejercicios");
                    open_nav_ejercicios();
                } else if (id == R.id.nav_ajustes) {
                    Log.d("Logs", "nav_ajustes");
                    open_nav_ajustes();
                } else if (id == R.id.nav_contactanos) {
                    Log.d("Logs", "nav_contactanos");

                    String email= "nereamonasterio99@gmail.com";
                    String subject = "Duda/Valoracion aplicacion";
                    String body = "Mandanos tu mensaje";
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+email));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);  //No m escribe el subject y el body
                    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(emailIntent);


                } else if (id == R.id.nav_logoff) {
                    Log.d("Logs", "nav_logoff");
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(vi.getContext());
                    dialogo.setTitle("Cerrar sesión");
                    dialogo.setMessage("Estas seguro que quieres cerrar la sesión?");
                    //dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Intent i = new Intent(getApplication(), LogInActivity.class);
                            startActivity(i);
                        }
                    });
                    dialogo.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                            Log.d("Logs", "no cerrar sesion");
                        }
                    });
                    dialogo.show();
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
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_rutinas, bundle);
    }
    public void open_nav_ejercicios(){
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_ejercicios, bundle);
    }
    public void open_nav_ajustes(){
        Bundle bundle = new Bundle();
        bundle.putString("usuario", this.usuario);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_ajustes, bundle);
    }
    public void open_nav_entrada(){
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.action_global_nav_entrada);
    }


}