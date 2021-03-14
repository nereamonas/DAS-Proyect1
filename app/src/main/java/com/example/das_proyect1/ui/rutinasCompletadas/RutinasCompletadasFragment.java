package com.example.das_proyect1.ui.rutinasCompletadas;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class RutinasCompletadasFragment extends BaseFragment {
    //Sera una lista un poco fea, que muestra todas las rutinas que ha completado el usuario. Nombre de la rutina + fecha + hora.
    //Coge la informacion del fichero que se genera dentro d la app.
    //Funciona igual q el calendario, pero cogiendo todas las fechas.
    private RutinasCompletadasViewModel rutinasCompletadasViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rutinasCompletadasViewModel =
                new ViewModelProvider(this).get(RutinasCompletadasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rutinas_completadas, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        rutinasCompletadasViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String user=" ";
        if (prefs.contains("username")) {//Comprobamos si existe  Deberia de pasar el username por parametro.
            user = " "+prefs.getString("username", null)+":";
        }
        TextView text= root.findViewById(R.id.textrutCompletadasDelDia);
        text.setText("Rutinas completadas:");
        try {
            BufferedReader ficherointerno= new BufferedReader(new InputStreamReader(getContext().openFileInput("rutinasCompletadas.txt")));
            String linea= ficherointerno.readLine();
            Log.d("Logs", "linea: "+linea);
            while (linea!=null){
                if(linea.contains(user)) {
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


        return root;
    }
}