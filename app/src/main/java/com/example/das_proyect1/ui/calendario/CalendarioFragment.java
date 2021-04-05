package com.example.das_proyect1.ui.calendario;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.base.BaseViewModel;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class CalendarioFragment extends BaseFragment {
    //Es un calendario, que cuando tu clicas en un día te muestra las rutinas q has completado en ese dia por ese usuario

    private BaseViewModel calendarioViewModel;
    private TextView text;
    String user=" ";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarioViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);

        //Cogemos el usuario con el q se ha iniciado sesion
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (prefs.contains("username")) {//Comprobamos si existe  Deberia de pasar el username por parametro.
            this.user = " "+prefs.getString("username", null)+":";
        }

        //Cogemos la fecha actual
        Calendar cal = Calendar.getInstance();
        String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String YEAR = String.valueOf(cal.get(Calendar.YEAR));
        String MONTH = String.valueOf(cal.get(Calendar.MONTH));
        String date=" "+DAY+"/"+MONTH+"/"+YEAR+" ";

        //Cargamos el text view donde escribiremos las rutinas completadas. Esta informacion la coeremos del fichero creado dentro de la aplicacion
        this.text= root.findViewById(R.id.textrutCompletadasDelDia);
        rellenarTextView(date);  //Llamamos al metodo que rellenara el textView

        //Cada vez q la fecha en el calendario cambie, se ejecutara:
        CalendarView calendar=root.findViewById(R.id.calendario);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("Logs", "Year: " + year + " Month: " + month + " Day: " + dayOfMonth);
                //Cogemos la data seleccionada y llamamos a rellenar TextView para actualizar los datos
                String date = " "+dayOfMonth + "/" + month + "/" + year+" ";
                rellenarTextView(date);
            }
        });


        return root;
    }

    public void rellenarTextView(String date){

        text.setText("Rutinas completadas del día: "+date);
        try {
            BufferedReader ficherointerno= new BufferedReader(new InputStreamReader(getContext().openFileInput("rutinasCompletadas.txt")));
            String linea= ficherointerno.readLine();
            Log.d("Logs", "linea: "+linea);
            while (linea!=null){
                if(linea.contains(date) && linea.contains(this.user)) { //Si la linea, pertenece al dia actual,
                    text.setText(text.getText() + "\n\n" + linea);
                }
                linea = ficherointerno.readLine();
            }

            ficherointerno.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}