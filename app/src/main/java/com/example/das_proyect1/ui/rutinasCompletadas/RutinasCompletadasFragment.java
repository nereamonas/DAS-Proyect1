package com.example.das_proyect1.ui.rutinasCompletadas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.das_proyect1.R;
import com.example.das_proyect1.controlarCambios.ControlarCambiosFragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class RutinasCompletadasFragment extends ControlarCambiosFragment {
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


        TextView text= root.findViewById(R.id.text_rutinasCompletadas);
        text.setText("Rutinas completadas: \n");
        try {
            BufferedReader ficherointerno= new BufferedReader(new InputStreamReader(getContext().openFileInput("rutinasCompletadas.txt")));
            String linea= ficherointerno.readLine();
            Log.d("Logs", "linea: "+linea);
            while (linea!=null){
                text.setText(text.getText()+"\n"+linea);
                linea= ficherointerno.readLine();
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