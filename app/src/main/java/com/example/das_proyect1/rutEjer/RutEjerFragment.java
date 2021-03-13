package com.example.das_proyect1.rutEjer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;
import com.example.das_proyect1.controlarCambios.ControlarCambiosFragment;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;
import com.example.das_proyect1.ui.rutinas.RutinasFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class RutEjerFragment extends ControlarCambiosFragment {
    private TextView titulo;
    private TextView desc;
    private TextView elemPendientes;
    private ImageView imageView;
    private Button btn_next;
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
        this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        this.rutId = Integer.parseInt(getArguments().getString("idRut"));  //cogemos el id de la rutina
        this.usuario=getArguments().getString("usuario");

        this.imageView=root.findViewById(R.id.image_view);
        this.temporizador=root.findViewById(R.id.temporizador);
        this.btn_startStop=root.findViewById(R.id.btn_stopStart);
        this.titulo=root.findViewById(R.id.tituloEjer);
        this.desc=root.findViewById(R.id.descEjer);
        this.encendido=true;
        this.btn_next=root.findViewById(R.id.btn_next);
        this.elemPendientes=root.findViewById(R.id.elemPendientes);


        //insertamos los textos del primer elemento de la base de datos
        db=new MiDB(getContext());
        this.ejercicios= db.getEjerciciosDeLaRutina(rutId);
        if (savedInstanceState != null) {
            Log.d("Logs","POSICItiempoON RECUPERADA: "+savedInstanceState.getLong("tiempoFaltante"));
            this.posicion=savedInstanceState.getInt("posicion");
            this.tiempoFaltante=savedInstanceState.getLong("tiempoFaltante");
        }else{
            this.posicion=getArguments().getInt("posicion");
            this.tiempoFaltante=Long.parseLong(this.ejercicios.get(this.posicion).getDuracion());
        }
        Log.d("Logs"," rutid siguientefrag"+rutId);
        this.titulo.setText(this.ejercicios.get(this.posicion).getNombre());
        this.desc.setText(this.ejercicios.get(this.posicion).getDescripcion());
        Log.d("Logs"," tiempo faltante inicial"+this.tiempoFaltante);
        this.elemPendientes.setText((this.posicion+1)+"/"+this.ejercicios.size());
        //La imagen
        imgCorrespondiente= new ImgCorrespondiente();
        this.imageView.setImageResource(imgCorrespondiente.devolver(this.ejercicios.get(this.posicion).getFoto()));

        empezarTemporizador();

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pasarAlSiguienteElemento();
            }
        });
        btn_startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStop();
            }
        });
        return root;
    }

    public void startStop(){
        if(encendido){
            pararTemporizador();
        }else{
            empezarTemporizador();
        }
    }

    public void empezarTemporizador(){
        this.countDownTimer=null;
        this.countDownTimer= new CountDownTimer(this.tiempoFaltante,1000) {
            @Override
            public void onTick(long l) {
                tiempoFaltante=l;
                actualizarTemporizador();
            }

            @Override
            public void onFinish() {

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
            if (this.prefs.contains("notiftoast")) {
                Boolean activadas = this.prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                Log.d("Logs", "estado notificaciones toast: " + activadas);
                if (activadas) {
                    Toast toast = Toast.makeText(getContext(), getString(R.string.toast_siguienteEjercicio), Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            this.countDownTimer=null;
            pasarAlSiguienteElemento();
        }

        temporizador.setText(texto);
    }
    public void pasarAlSiguienteElemento(){
        //Actualizamos todos los elementos al siguiente de la bbdd
        //hasieratuamos el contador
        this.posicion++;
        this.countDownTimer.cancel();
        this.countDownTimer=null;
        if(this.posicion<this.ejercicios.size()){
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
        if (this.prefs.contains("notif")) { //Comprobamos si existe
            Boolean activadas = prefs.getBoolean("notif", true);  //Comprobamos si las notificaciones estan activadas
            Log.d("Logs", "estado notificaciones: "+activadas);
            if (activadas) {
                NotificationManager elManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getContext());
                Intent intent = new Intent(getContext(), RutinasFragment.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);


                elBuilder.setSmallIcon(R.drawable.ic_rutinas)
                        .setContentTitle(getString(R.string.notif_titulo_finEntrenamiento))
                        .setContentText(getString(R.string.notif_cuerpo_hassuperadoelentrena))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLights(Color.MAGENTA, 1000, 1000)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true) //para q desaparezca una vez hacer click sobre ella
                        .addAction(R.drawable.ic_rutinas, getString(R.string.notif_boton_iniciarOtraRutina), pendingIntent);  //no funciona el q abra otra rutina pero bueno

                elManager.notify(1, elBuilder.build());
            }
        }
    }

    public void escribirEnFichero(){
        try {

            OutputStreamWriter fichero = new OutputStreamWriter(getContext().openFileOutput("rutinasCompletadas.txt",Context.MODE_APPEND));

            java.util.Date fecha = new Date();
            Calendar cal = Calendar.getInstance();
            String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            String YEAR = String.valueOf(cal.get(Calendar.YEAR));
            String MONTH = String.valueOf(cal.get(Calendar.MONTH));
            Log.d("Logs","El dia "+DAY+"/"+MONTH+"/"+YEAR+" a las "+fecha.getHours()+":"+fecha.getMinutes());

            fichero.write("- "+this.usuario+": Has completado la rutina "+db.getNombreRutina(this.rutId)+" el día "+DAY+"/"+MONTH+"/"+YEAR+" a las "+fecha.getHours()+":"+fecha.getMinutes()+"\n\n");
            fichero.close();
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
        super.onSaveInstanceState(outState);
        outState.putInt("posicion",this.posicion);
        outState.putLong("tiempoFaltante",this.tiempoFaltante);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //se coge en el create
        super.onActivityCreated(savedInstanceState);
    }

}