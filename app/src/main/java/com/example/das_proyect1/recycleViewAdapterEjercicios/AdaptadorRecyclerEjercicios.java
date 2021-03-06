package com.example.das_proyect1.recycleViewAdapterEjercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;
import com.example.das_proyect1.helpClass.Rutina;

import java.util.ArrayList;

public class AdaptadorRecyclerEjercicios extends RecyclerView.Adapter<ViewHolderNewEjercicios> implements View.OnClickListener{
    private ArrayList<Ejercicio> ejercicios;
    private View.OnClickListener listener;

    public AdaptadorRecyclerEjercicios(ArrayList<Ejercicio> ejercicios)
    {
        this.ejercicios=ejercicios;
    }

    @NonNull
    @Override
    public ViewHolderNewEjercicios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_ejercicios,null);
        ViewHolderNewEjercicios evh = new ViewHolderNewEjercicios(elLayoutDeCadaItem);


        elLayoutDeCadaItem.setOnClickListener(this);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewEjercicios holder, int position) {
        holder.eltexto.setText(this.ejercicios.get(position).getNombre());
        ImgCorrespondiente i = new ImgCorrespondiente();
        holder.laimagen.setImageResource(i.devolver(this.ejercicios.get(position).getFoto()));
    }

    @Override
    public int getItemCount() {
        return this.ejercicios.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener=listener;
    }

    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }
}
