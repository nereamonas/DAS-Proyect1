package com.example.das_proyect1.adaptadores.RecycleViewAdapterEjercicios;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;

public class ViewHolderNewEjercicios extends RecyclerView.ViewHolder {
    public TextView eltexto;
    public ImageView laimagen;
    public ViewHolderNewEjercicios(@NonNull View itemView){
        super(itemView);
        eltexto=itemView.findViewById(R.id.texto);
        laimagen=itemView.findViewById(R.id.foto);
    }
}
