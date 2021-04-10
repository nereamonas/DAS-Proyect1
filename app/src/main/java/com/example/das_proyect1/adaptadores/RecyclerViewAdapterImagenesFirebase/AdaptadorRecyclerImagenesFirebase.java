package com.example.das_proyect1.adaptadores.RecyclerViewAdapterImagenesFirebase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImagenFirebase;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;

import java.util.ArrayList;

public class AdaptadorRecyclerImagenesFirebase extends RecyclerView.Adapter<ViewHolderNewImagenesFirebase> implements View.OnClickListener{

    //Esta clase es el adaptador para crear la recycle view de todos los ejercicios. será un array list de ejercicios

    private Context c;
    private ArrayList<ImagenFirebase> imagenes;
    private View.OnClickListener listener;

    public AdaptadorRecyclerImagenesFirebase(Context c, ArrayList<ImagenFirebase> imagenes)
    {
        this.c=c;
        this.imagenes=imagenes;
    }

    @NonNull
    @Override
    public ViewHolderNewImagenesFirebase onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_imagenesfirebase,null);
        ViewHolderNewImagenesFirebase evh = new ViewHolderNewImagenesFirebase(elLayoutDeCadaItem);


        elLayoutDeCadaItem.setOnClickListener(this);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewImagenesFirebase holder, int position) {
        holder.eltexto.setText(this.imagenes.get(position).getNombre());
        Glide.with(c.getApplicationContext()).load(this.imagenes.get(position).getUrl()).into(holder.laimagen);
    }

    @Override
    public int getItemCount() {
        return this.imagenes.size();
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
