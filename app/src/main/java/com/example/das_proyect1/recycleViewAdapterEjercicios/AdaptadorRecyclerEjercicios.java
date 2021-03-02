package com.example.das_proyect1.recycleViewAdapterEjercicios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;

public class AdaptadorRecyclerEjercicios extends RecyclerView.Adapter<ViewHolderNewEjercicios> {
    private String[] losnombres;
    private int[] lasimagenes;
    public AdaptadorRecyclerEjercicios(String[] nombres, int[] imagenes)
    {
        losnombres=nombres;
        lasimagenes=imagenes;
    }

    @NonNull
    @Override
    public ViewHolderNewEjercicios onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_ejercicios,null);
        ViewHolderNewEjercicios evh = new ViewHolderNewEjercicios(elLayoutDeCadaItem);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewEjercicios holder, int position) {
        holder.eltexto.setText(losnombres[position]);
        holder.laimagen.setImageResource(lasimagenes[position]);
    }

    @Override
    public int getItemCount() {
        return losnombres.length;
    }
}
