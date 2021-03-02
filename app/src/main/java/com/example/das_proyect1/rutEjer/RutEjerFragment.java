package com.example.das_proyect1.rutEjer;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.das_proyect1.R;

public class RutEjerFragment extends Fragment {
    private TextView titulo;
    private TextView desc;
    private TextView elemPendientes;
    private ImageView imageView;
    private Button btn_next;
    private TextView temporizador;
    private Button btn_startStop;

    private CountDownTimer countDownTimer;
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

        imageView=root.findViewById(R.id.image_view);
        temporizador=root.findViewById(R.id.temporizador);
        btn_startStop=root.findViewById(R.id.btn_stopStart);
        titulo=root.findViewById(R.id.tituloEjer);
        desc=root.findViewById(R.id.descEjer);
        encendido=true;
        btn_next=root.findViewById(R.id.btn_next);
        elemPendientes=root.findViewById(R.id.elemPendientes);


        //insertamos los textos del primer elemento de la base de datos
        tiempoFaltante=600000;//10mins
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
    }
}
