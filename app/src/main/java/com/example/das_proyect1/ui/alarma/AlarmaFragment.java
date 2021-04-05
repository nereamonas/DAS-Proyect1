package com.example.das_proyect1.ui.alarma;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmaFragment  extends BaseFragment {
    private AlarmaViewModel alarmaViewModel;

    private EditText horas;
    private EditText minutos;
    private EditText mensaje;
    private Button btnElegirHora;
    private Button btnEstablecerAlarma;

    private Calendar calendario;
    private int horaActual;
    private int minActual;
    private TimePickerDialog timePickerDialog;

    RadioButton rlunes,rmartes,rmiercoles,rjueves,rviernes,rsabado,rdomingo;


    private ArrayList<Integer> diasSeleccionados = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmaViewModel =
                new ViewModelProvider(this).get(AlarmaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarma, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        alarmaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });


        horas= root.findViewById(R.id.editTextHoras);
        minutos= root.findViewById(R.id.editTextMinutos);
        mensaje=root.findViewById(R.id.editTextMensajeAlarma);
        btnElegirHora= root.findViewById(R.id.buttonElegirHora);
        btnEstablecerAlarma= root.findViewById(R.id.buttonCrearAlarma);

        rlunes=root.findViewById(R.id.radioButtonLunes);
        rmartes=root.findViewById(R.id.radioButtonMartes);
        rmiercoles=root.findViewById(R.id.radioButtonMiercoles);
        rjueves=root.findViewById(R.id.radioButtonJueves);
        rviernes=root.findViewById(R.id.radioButtonViernes);
        rsabado=root.findViewById(R.id.radioButtonSabado);
        rdomingo=root.findViewById(R.id.radioButtonDomingo);

        btnElegirHora.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en la galeria se abrira la galeria
            @Override
            public void onClick(View v) {
                dialogoElegirHora();
            }
        });

        btnEstablecerAlarma.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en la galeria se abrira la galeria
            @Override
            public void onClick(View v) {
                establecerAlarma();
            }
        });



        return root;
    }

    public void dialogoElegirHora(){
        calendario=Calendar.getInstance();
        horaActual=calendario.get(Calendar.HOUR_OF_DAY);
        minActual=calendario.get(Calendar.MINUTE);

        timePickerDialog= new TimePickerDialog(getActivity(),(view, hourOfDay, minute) -> {
            horas.setText(String.format("%02d", hourOfDay));
            minutos.setText(String.format("%02d", minute));
        },horaActual,minActual,false);
        timePickerDialog.show();


    }



    public void establecerAlarma(){
        if (!horas.getText().toString().isEmpty()&&!minutos.getText().toString().isEmpty()) {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(horas.getText().toString()));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minutos.getText().toString()));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, mensaje.getText().toString());
            getDiasSeleccionados();
            intent.putExtra(AlarmClock.EXTRA_DAYS,diasSeleccionados);
            intent.putExtra(AlarmClock.EXTRA_VIBRATE,false);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);  //Para no dirigir al usuario a la ventana de alarma. sino q lo pone directamente. ns q es mjr

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getActivity(), "No puede soportar esta accion", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), "Tienes que establecer una hora", Toast.LENGTH_SHORT).show();
        }
    }

    public void getDiasSeleccionados(){

        if (rlunes.isChecked()==true){
            diasSeleccionados.add(Calendar.MONDAY);
        }
        if (rmartes.isChecked()==true){
            diasSeleccionados.add(Calendar.TUESDAY);
        }
        if (rmiercoles.isChecked()==true){
            diasSeleccionados.add(Calendar.WEDNESDAY);
        }
        if (rjueves.isChecked()==true){
            diasSeleccionados.add(Calendar.THURSDAY);
        }
        if (rviernes.isChecked()==true){
            diasSeleccionados.add(Calendar.FRIDAY);
        }
        if (rsabado.isChecked()==true){
            diasSeleccionados.add(Calendar.SATURDAY);
        }
        if (rdomingo.isChecked()==true){
            diasSeleccionados.add(Calendar.SUNDAY);
        }
    }

}