package com.example.das_proyect1.ui.alarma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.serviceBroadcast.AlarmNotificationManagerBroadcastReceiver;
import com.example.das_proyect1.serviceBroadcast.AlarmWidgetManagerBroadcastReceiver;

import java.util.ArrayList;
import java.util.Calendar;

public class AlarmaFragment  extends BaseFragment {

    //Clase para programar una alarma de recordatorios

    private BaseViewModel alarmaViewModel;

    private EditText horas;
    private EditText minutos;
    private EditText mensaje;
    private Button btnElegirHora;
    private Button btnEstablecerAlarma;
    private Button btnCrearnotif;

    private Calendar calendario;
    private int horaActual;
    private int minActual;
    private TimePickerDialog timePickerDialog;

    RadioButton rlunes,rmartes,rmiercoles,rjueves,rviernes,rsabado,rdomingo;


    private ArrayList<Integer> diasSeleccionados = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alarmaViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_alarma, container, false);


        //Recogemos todos los elementos
        horas= root.findViewById(R.id.editTextHoras);
        minutos= root.findViewById(R.id.editTextMinutos);
        mensaje=root.findViewById(R.id.editTextMensajeAlarma);
        btnElegirHora= root.findViewById(R.id.buttonElegirHora);
        btnEstablecerAlarma= root.findViewById(R.id.buttonCrearAlarma);
        btnCrearnotif= root.findViewById(R.id.buttonCrearnotif);

        rlunes=root.findViewById(R.id.radioButtonLunes);
        rmartes=root.findViewById(R.id.radioButtonMartes);
        rmiercoles=root.findViewById(R.id.radioButtonMiercoles);
        rjueves=root.findViewById(R.id.radioButtonJueves);
        rviernes=root.findViewById(R.id.radioButtonViernes);
        rsabado=root.findViewById(R.id.radioButtonSabado);
        rdomingo=root.findViewById(R.id.radioButtonDomingo);

        btnElegirHora.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en elegir hora se abrira un dialogo que mostrara un reloj para elegir la hora
            @Override
            public void onClick(View v) {
                dialogoElegirHora();
            }
        });

        btnEstablecerAlarma.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en establecer alarma, estableceremos la alarma con los datos indicados
            @Override
            public void onClick(View v) {
                establecerAlarma();
            }
        });

        btnCrearnotif.setOnClickListener(new View.OnClickListener() {  //Cuando clickemos en establecer alarma, estableceremos la alarma con los datos indicados
            @Override
            public void onClick(View v) {
                crearNotif();
            }
        });


        return root;
    }

    public void dialogoElegirHora(){ //Mostramos un dialogo de reloj, para que el usuario pueda elegir la hora. y esta hora elegida, se pondra en los textview de horas y minutos
        calendario=Calendar.getInstance();
        horaActual=calendario.get(Calendar.HOUR_OF_DAY);
        minActual=calendario.get(Calendar.MINUTE);

        timePickerDialog= new TimePickerDialog(getActivity(),(view, hourOfDay, minute) -> {
            horas.setText(String.format("%02d", hourOfDay)); //En el editText donde se pone la hora lo actualizamos con los datos elegidos en el dialogo
            minutos.setText(String.format("%02d", minute)); //Lo mismo con los minutos
        },horaActual,minActual,false);
        timePickerDialog.show();

    }


    public void establecerAlarma(){ //Creamos la alarma
        if (!horas.getText().toString().isEmpty()&&!minutos.getText().toString().isEmpty()) { //Si se ha elegido una hora, podemos crear la alarma
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM); //Intent de alarma
            intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(horas.getText().toString())); //Ponemos la h
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(minutos.getText().toString())); //Ponemos los minutos
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, mensaje.getText().toString()); //Ponemos el mensaje del usuario.
            getDiasSeleccionados();
            intent.putExtra(AlarmClock.EXTRA_DAYS,diasSeleccionados); //Añadimos los dias seleccionados q queremos q suene
            intent.putExtra(AlarmClock.EXTRA_VIBRATE,false);
            intent.putExtra(AlarmClock.EXTRA_SKIP_UI,true);  //Para no dirigir al usuario a la ventana de alarma. sino q lo pone directamente. ns q es mjr

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent); //Empezamos la intent
            } else {
                Toast.makeText(getActivity(), getString(R.string.alarma_toast_Nopuedesoportarestaaccion), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(getActivity(), getString(R.string.alarma_toast_Tienesqueestablecerunahora), Toast.LENGTH_SHORT).show(); //Es obligatorio establecer la h
        }

    }

    public void crearNotif(){ //Creamos una alarma para la notificacion
        Calendar calendar = Calendar.getInstance(); //Creamos un calendario para añadir la hora q ha elegido el usuario
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horas.getText().toString()));//hora en formato 24h
        calendar.set(Calendar.MINUTE, Integer.parseInt(minutos.getText().toString())); //minuto

        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmNotificationManagerBroadcastReceiver.class);
        intent.putExtra("titulo",mensaje.getText().toString()); //Añadimos el mensaje que ha elegido el usuario
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); //programamos la alarma
        Toast.makeText(getActivity(), getString(R.string.alarma_notifcacionProgramada), Toast.LENGTH_SHORT).show(); //Mostramos toast diciendo q s ha programado
    }

    public void getDiasSeleccionados(){ //Miramos los dias seleccionados con el radio button y los añadimos a la lista en el caso de estar seleccionados

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