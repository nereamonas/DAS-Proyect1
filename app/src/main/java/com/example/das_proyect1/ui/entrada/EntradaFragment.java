package com.example.das_proyect1.ui.entrada;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.recycleViewAdapterEjercicios.AdaptadorRecyclerEjercicios;
import com.example.das_proyect1.ui.ejercicios.EjerciciosViewModel;

import java.util.ArrayList;

public class EntradaFragment extends Fragment {
    private MiDB db;
    private EntradaViewModel entradaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        entradaViewModel =
                new ViewModelProvider(this).get(EntradaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        entradaViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        return root;
    }

}
