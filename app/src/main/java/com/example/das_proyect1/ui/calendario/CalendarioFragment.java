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
import com.example.das_proyect1.controlarCambios.ControlarCambiosFragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class CalendarioFragment extends ControlarCambiosFragment {
    private CalendarioViewModel calendarioViewModel;
    String user=" ";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        calendarioViewModel =
                new ViewModelProvider(this).get(CalendarioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_calendario, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        calendarioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        Calendar cal = Calendar.getInstance();
        String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String YEAR = String.valueOf(cal.get(Calendar.YEAR));
        String MONTH = String.valueOf(cal.get(Calendar.MONTH));
        String date=" "+DAY+"/"+MONTH+"/"+YEAR+" ";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (prefs.contains("username")) {//Comprobamos si existe  Deberia de pasar el username por parametro.
            this.user = " "+prefs.getString("username", null)+":";
        }

        TextView text= root.findViewById(R.id.textrutCompletadasDelDia);
        text.setText("Rutinas completadas del día: "+date);
        try {
            BufferedReader ficherointerno= new BufferedReader(new InputStreamReader(getContext().openFileInput("rutinasCompletadas.txt")));
            String linea= ficherointerno.readLine();
            Log.d("Logs", "linea: "+linea);
            while (linea!=null){
               if(linea.contains(date) && linea.contains(this.user)) {
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


        CalendarView calendar=root.findViewById(R.id.calendario);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Log.d("Logs", "Year: " + year + " Month: " + month + " Day: " + dayOfMonth);
                String date = " "+dayOfMonth + "/" + month + "/" + year+" ";

                TextView text= root.findViewById(R.id.textrutCompletadasDelDia);
                text.setText("Rutinas completadas del día: "+date+"\n");
                try {
                    BufferedReader ficherointerno= new BufferedReader(new InputStreamReader(getContext().openFileInput("rutinasCompletadas.txt")));
                    String linea= ficherointerno.readLine();
                    Log.d("Logs", "linea: "+linea);
                    while (linea!=null){
                        if(linea.contains(date) && linea.contains(getUser())) {
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
        });


        return root;
    }

    public String getUser(){
        return this.user;
    }


}