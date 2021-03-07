package com.example.das_proyect1.ui.ejercicios;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.recycleViewAdapterEjercicios.AdaptadorRecyclerEjercicios;
import com.example.das_proyect1.recycleViewAdaptersRutinas.AdaptadorRecyclerRutinas;

import java.util.ArrayList;

public class EjerciciosFragment extends Fragment {
    private MiDB db;
    private EjerciciosViewModel ejerciciosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ejerciciosViewModel =
                new ViewModelProvider(this).get(EjerciciosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        ejerciciosViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        String usuario = getArguments().getString("usuario");  //cogemos el id de la rutina


        RecyclerView lalista= root.findViewById(R.id.recycleView);
        db=new MiDB(getContext());
        ArrayList<Ejercicio> ejercicios= db.getTodosLosEjercicios(usuario);

        AdaptadorRecyclerEjercicios eladaptador = new AdaptadorRecyclerEjercicios(ejercicios);
        lalista.setAdapter(eladaptador);

        GridLayoutManager elLayoutRejillaIgual;
        int rotacion=getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (rotacion== Surface.ROTATION_0 || rotacion==Surface.ROTATION_180){
            //La pantalla está en vertical
            elLayoutRejillaIgual= new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
        }else{
            //La pantalla esta en horizontal
            elLayoutRejillaIgual= new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false);

        }

        lalista.setLayoutManager(elLayoutRejillaIgual);


        return root;
    }
}