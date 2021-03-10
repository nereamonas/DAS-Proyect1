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

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;
import com.example.das_proyect1.ui.rutinas.RutinasFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class RutEjerFragment extends Fragment {
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
    private int posicion;

    private ArrayList<Ejercicio> ejercicios;
    private CountDownTimer countDownTimer;
    private ImgCorrespondiente imgCorrespondiente;
    private long tiempoFaltante;
    private boolean encendido;

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
        Log.d("Logs"," rutid siguientefrag"+rutId);

        this.titulo.setText(this.ejercicios.get(this.posicion).getNombre());
        this.desc.setText(this.ejercicios.get(this.posicion).getDescripcion());
        this.tiempoFaltante=Long.parseLong(this.ejercicios.get(this.posicion).getDuracion());
        this.elemPendientes.setText((this.posicion+1)+"/"+this.ejercicios.size());
        //La imagen
        imgCorrespondiente= new ImgCorrespondiente();
        this.imageView.setImageResource(imgCorrespondiente.devolver(this.ejercicios.get(this.posicion).getFoto()));

        empezarTemporizador();
        actualizarTemporizador();

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
        countDownTimer= new CountDownTimer(tiempoFaltante,1000) {
            @Override
            public void onTick(long l) {
                tiempoFaltante=l;
                actualizarTemporizador();
            }

            @Override
            public void onFinish() {

            }
        }.start();
        btn_startStop.setText("Pause");
        encendido=true;
    }

    public void pararTemporizador(){
        countDownTimer.cancel();
        btn_startStop.setText("Start");
        encendido=false;
    }

    public void actualizarTemporizador(){
        int minutos=(int) tiempoFaltante/60000;
        int segundos=(int) tiempoFaltante % 60000 / 1000;

        String texto;

        texto=""+minutos+":";
        if(segundos<10){
            texto+="0";
        }
        texto+=segundos;

        if(texto=="00:00"){
            //El contador a llegado al final, hay que pasar de elemento
            pasarAlSiguienteElemento();
        }

        temporizador.setText(texto);
    }
    public void pasarAlSiguienteElemento(){
        //Actualizamos todos los elementos al siguiente de la bbdd
        //hasieratuamos el contador
        this.posicion++;
        if(this.posicion<this.ejercicios.size()){
            this.titulo.setText(this.ejercicios.get(this.posicion).getNombre());
            this.desc.setText(this.ejercicios.get(this.posicion).getDescripcion());
            this.tiempoFaltante=Long.parseLong(this.ejercicios.get(this.posicion).getDuracion());
            this.elemPendientes.setText((this.posicion+1)+"/"+this.ejercicios.size());
            this.imageView.setImageResource(imgCorrespondiente.devolver(this.ejercicios.get(this.posicion).getFoto()));

        }else{
            //Has terminado la rutina. saltar notificacion
            mostrarNotificacion();

            //Escribiremos en el fichero que ha acabado la rutina
            escribirEnFichero();

            //Volvemos al menu de rutinas
            Bundle bundle = new Bundle();
            bundle.putString("usuario", this.usuario);
            Navigation.findNavController(getView()).navigate(R.id.action_rutEjerViewPagerFragment_to_nav_rutinas, bundle);


        }
    }

    public void mostrarNotificacion(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.contains("notif")) { //Comprobamos si existe
            Boolean activadas = prefs.getBoolean("notif", true);  //Comprobamos si las notificaciones estan activadas
            Log.d("Logs", "estado notificaciones: "+activadas);
            if (activadas) {
                NotificationManager elManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(getContext());
                Intent intent = new Intent(getContext(), RutinasFragment.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);

                elBuilder.setSmallIcon(R.drawable.bart)
                        .setContentTitle("Fin entrenamiento")
                        .setContentText("Has superado todo el entrenamiento. Felicidades")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLights(Color.MAGENTA, 1000, 1000)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true) //para q desaparezca una vez hacer click sobre ella
                        .addAction(R.drawable.bart, "Iniciar otra rutina", pendingIntent);  //no funciona el q abra otra rutina pero bueno

                elManager.notify(1, elBuilder.build());
            }
        }
    }

    public void escribirEnFichero(){
        try {
            Log.d("Logs", "entra en el try ");

            OutputStreamWriter fichero = new OutputStreamWriter(getContext().openFileOutput("rutinasCompletadas.txt",Context.MODE_APPEND));

            java.util.Date fecha = new Date();
            //fichero.append(this.usuario+": Has completado la rutina "+db.getNombreRutina(this.rutId)+" con fecha: "+fecha+"\n");

            fichero.write(this.usuario+": Has completado la rutina "+db.getNombreRutina(this.rutId)+" con fecha: "+fecha+"\n");
            fichero.close();
            Log.d("Logs", "ha entrado a insertar datos ");


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

}


