package com.example.das_proyect1.ui.rutEjer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;
import com.example.das_proyect1.ui.calendario.CalendarioFragment;
import com.example.das_proyect1.ui.rutinas.RutinasFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RutEjerFragment extends BaseFragment {
    //Aviso - es un poco locurote de clase porq hace muchas cosas.
    //TIENE UN ERROR - Cuando le das al boton atras, no consigo para el CountDownTimer, por lo q posiblemente de error al de unos minutos la aplicacion y se cierre
    //He intentado con herencia y asi controlar el onBackClick, pero no puedo... en los fragment no es tan facil como en los intent porq no cuentan con ese metodo. Y hay una manera desde el intent que lo kudeatua, pero me lo hace en todos los fragmentos no solo este. Continuare investigando pero... M ha llevamo mazo tiempo intentaar arreglarlo pero nada... :(
    private TextView titulo;
    private TextView desc;
    private TextView elemPendientes;
    private ImageView imageView;
    private Button btn_next;
    private Button btn_atras;
    private TextView temporizador;
    private Button btn_startStop;
    private MiDB db;

    private String usuario;
    private int rutId;
    private int posicion=0;

    private ArrayList<Ejercicio> ejercicios;
    private CountDownTimer countDownTimer;
    private ImgCorrespondiente imgCorrespondiente;
    private long tiempoFaltante;
    private boolean encendido;

    private SharedPreferences prefs;

    private RutEjerViewModel rutEjerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rutEjerViewModel = new ViewModelProvider(this).get(RutEjerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rutejerviewpager, container, false);
        super.onCreate(savedInstanceState);

        rutEjerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        //Inicializamos todas las variables necesarias
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        this.rutId = Integer.parseInt(getArguments().getString("idRut"));  //cogemos el id de la rutina
        this.usuario = getArguments().getString("usuario");
        this.imageView = root.findViewById(R.id.image_view);
        this.temporizador = root.findViewById(R.id.temporizador);
        this.btn_startStop = root.findViewById(R.id.btn_stopStart);
        this.titulo = root.findViewById(R.id.tituloEjer);
        this.desc = root.findViewById(R.id.descEjer);
        this.encendido = true;
        this.btn_next = root.findViewById(R.id.btn_next);
        this.btn_atras = root.findViewById(R.id.button_atras);
        this.elemPendientes = root.findViewById(R.id.elemPendientes);


        //insertamos los textos del primer elemento de la base de datos
        db = new MiDB(getContext());
        this.ejercicios = db.getEjerciciosDeLaRutina(rutId); //Cogemos los ejercicios de esa rutina y los guardamos en la lista
        this.db.cerrarConexion(); //Cerramos la conexion porq no lo vamos a usar mas
        if (savedInstanceState != null) { //Si viene de la rotacion de pantalla q tiene cosas guardadas. el tiempo y la posicion lo ogera de ahi
            this.posicion = savedInstanceState.getInt("posicion");
            this.tiempoFaltante = savedInstanceState.getLong("tiempoFaltante");
        } else { //Si no tiene elemen guardados, es la primera vez x lo q:
            this.posicion = getArguments().getInt("posicion"); //Cogemos d la info q se le pasa la posicion del elemento en el q está.  Cada vez q acaba uno para pasar al otro hay q reiniciar, porq sino el Counter este se queda de fondo arrancado y salta error y se cierra la app
            this.tiempoFaltante = Long.parseLong(this.ejercicios.get(this.posicion).getDuracion()); //Y el tiempo lo cogemos d la base de datos ya q tendria q empezar desde el principio.
        }
        //El resto de elementos los cogemos normal de la bbdd
        this.titulo.setText(this.ejercicios.get(this.posicion).getNombre());
        this.desc.setText(this.ejercicios.get(this.posicion).getDescripcion());
        this.elemPendientes.setText((this.posicion + 1) + "/" + this.ejercicios.size());
        imgCorrespondiente = new ImgCorrespondiente();
        this.imageView.setImageResource(imgCorrespondiente.devolver(this.ejercicios.get(this.posicion).getFoto()));

        //Empezamos el temporizador
        empezarTemporizador();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si clicamos en el boton next, pasaremos al siguiente elemento
                pasarAlSiguienteElemento();
            }
        });
        btn_startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si clicamos en el boton StartStop, pararemos o arrancaremos de nuevo el contador
                startStop();
            }
        });
        btn_atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Si vamos atras, cancela el counter (importante)(he metido este boton ya q al darle al atras del movil no consigo cambiarlo, asi se puede ir atras correctamente sin recibir errores) y va al fragment d rutina
                calcelarCounter();
                Bundle bundle = new Bundle();
                bundle.putString("usuario", getUsuario());
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(R.id.nav_rutinas,false)
                        .build();
                Navigation.findNavController(getView()).navigate(R.id.action_rutEjerViewPagerFragment_to_nav_rutinas, bundle,options);

            }
        });
        return root;
    }
    private String getUsuario(){
        return this.usuario;
    }

    private void calcelarCounter(){
        //Cancelamos el counter activo
        this.countDownTimer.cancel();
        this.countDownTimer=null;
    }

    public void startStop(){
        //Dependiendo del estado del contador, lo pararemos lo lo arrancamos
        if(encendido){
            pararTemporizador();
        }else{
            empezarTemporizador();
        }
    }

    public void empezarTemporizador(){
        //Empieza el contador
        if(this.countDownTimer!=null){
            calcelarCounter();
        }
        this.countDownTimer= new CountDownTimer(this.tiempoFaltante,1000) {
            @Override
            public void onTick(long l) {
                tiempoFaltante=l;
                Log.d("Logs","Temporizador corriendo");
                actualizarTemporizador();
            }
            @Override
            public void onFinish() {
                calcelarCounter(); //Cuando le daws a back con el mvl, aunq este esto, no s cancela, da error
            }
        }.start();
        btn_startStop.setText(getString(R.string.pause));
        encendido=true;
    }

    public void pararTemporizador(){
        countDownTimer.cancel();
        btn_startStop.setText(getString(R.string.reanudar));
        encendido=false;
    }

    public void actualizarTemporizador(){
        int minutos=(int) this.tiempoFaltante/60000;
        int segundos=(int) this.tiempoFaltante % 60000 / 1000;
        //Log.d("Logs","ACTUALIZAR "+this.tiempoFaltante);
        String texto;
        texto=""+minutos+":";
        if(segundos<10){
            texto+="0";
        }
        texto+=segundos;
        if("0:01".equals(texto)){
            //El contador a llegado al final, hay que pasar de elemento
            pasarAlSiguienteElemento();
        }
        temporizador.setText(texto);
    }
    public void pasarAlSiguienteElemento(){
        //Actualizamos todos los elementos al siguiente de la bbdd
        //hasieratuamos el contador
        this.posicion++;
        calcelarCounter(); //Cancelamos el counter activo. improtante
        if(this.posicion<this.ejercicios.size()){
            //Lanzar un toast
            if (this.prefs.contains("notiftoast")) { //si estan activadas las notificaciones toast, lanzamos una
                Boolean activadas = this.prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                Log.d("Logs", "estado notificaciones toast: " + activadas);
                if (activadas) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.toast_siguienteEjercicio), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            //Recargo la pagina
            Bundle bundle = new Bundle();
            bundle.putString("usuario", this.usuario);
            bundle.putString("idRut", (String.valueOf(this.rutId)));
            bundle.putInt("posicion",this.posicion);
            NavOptions options = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.nav_rutinas,false)
                    .build();
            Navigation.findNavController(getView()).navigate(R.id.action_rutEjerViewPagerFragment_self, bundle,options);

        }else{
            //Has terminado la rutina. lanzar un toast
            if (this.prefs.contains("notiftoast")) {
                Boolean activadas = this.prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                Log.d("Logs", "estado notificaciones toast: " + activadas);
                if (activadas) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.notif_cuerpo_hassuperadoelentrena), Toast.LENGTH_LONG);
                    toast.show();
                }
            }

            // saltar notificacion
            mostrarNotificacion();

            //Escribiremos en el fichero que ha acabado la rutina
            escribirEnFichero();

            //Volvemos al menu de rutinas
            Bundle bundle = new Bundle();
            bundle.putString("usuario", this.usuario);
            NavOptions options = new NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .setPopUpTo(R.id.nav_rutinas,false)
                    .build();
            Navigation.findNavController(getView()).navigate(R.id.action_rutEjerViewPagerFragment_to_nav_rutinas, bundle,options);


        }
    }

    public void mostrarNotificacion(){
        //Si las notificaciones estan activadas en ajustes, salta una notificacion de q has completado la rutina
        if (this.prefs.contains("notif")) { //Comprobamos si existe
            Boolean activadas = prefs.getBoolean("notif", true);  //Comprobamos si las notificaciones estan activadas
            Log.d("Logs", "estado notificaciones: "+activadas);
            if (activadas) {
                Intent intent = new Intent(getContext(), CalendarioFragment.class);//RutinasFragment
                intent.putExtra("usuario", this.usuario);
                intent.putExtra("id", 1);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationManager elManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getContext(), "IdCanal");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    elManager.createNotificationChannel(elCanal);
                }
                boolean permiso=comprobarPermisoVibracion();
                elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.ic_notif))
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(getString(R.string.notif_titulo_finEntrenamiento))
                        .setContentText(getString(R.string.notif_cuerpo_hassuperadoelentrena))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLights(Color.BLUE, 200, 200)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                if(permiso) {
                    elBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                }
                elManager.notify(1, elBuilder.build());
            }
        }
    }

    public void escribirEnFichero(){
        //Cuardara en un fichero el usuario, la rutina y la fecha y hora en la q se ha compeltado
        try {
            OutputStreamWriter fichero = new OutputStreamWriter(getContext().openFileOutput("rutinasCompletadas.txt",Context.MODE_APPEND));

            java.util.Date fecha = new Date();
            Calendar cal = Calendar.getInstance();
            String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            String YEAR = String.valueOf(cal.get(Calendar.YEAR));
            String MONTH = String.valueOf(cal.get(Calendar.MONTH));
            Log.d("Logs","El dia "+DAY+"/"+MONTH+"/"+YEAR+" a las "+fecha.getHours()+":"+fecha.getMinutes());
            db = new MiDB(getContext());
            fichero.write("- "+this.usuario+": Has completado la rutina "+db.getNombreRutina(this.rutId)+" el día "+DAY+"/"+MONTH+"/"+YEAR+" a las "+fecha.getHours()+":"+fecha.getMinutes()+"\n\n");
            fichero.close();
            this.db.cerrarConexion(); //Cerramos la conexion porq no lo vamos a usar mas
            Log.d("Logs", "Ha insertado los datos en el fichero ");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Logs", "file not found ");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Logs", "io excepcion ");
        }catch(Exception e){
            Log.d("Logs", "error al insertar datos ");

        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Cuando se rota la pantalla tendremos q guardar x informacion. la posicion en la que estamos y el tiempo q falta del contador para accabar
        super.onSaveInstanceState(outState);
        calcelarCounter();
        outState.putInt("posicion",this.posicion);
        outState.putLong("tiempoFaltante",this.tiempoFaltante);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //lo he añadido en el create asiq
        super.onActivityCreated(savedInstanceState);
    }

    public boolean comprobarPermisoVibracion(){
        Log.d("Logs","PERMISO");
        boolean permiso=false;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.VIBRATE)!=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Logs","NO ESTA CONCEDIDO");
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.VIBRATE)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs","DECIR XQ ES NECESARIO");
            }
            else{
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs","NO SE HA CONCEDIDO");
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.VIBRATE}, 1);  //ACTIVITY_RECOGNITION
            permiso=true;
            Log.d("Logs","PEDIR PERMISO");
            //ya estará aceptado asiq:
        }
        else {
            Log.d("Logs","YA LO TIENE");
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            permiso=true;
        }
        return permiso;
    }


    @Override
    public void onDetach() {
        calcelarCounter();
        Log.d("Logs","Ha pulsado atras, cancelar counter");
        super.onDetach(); }




}