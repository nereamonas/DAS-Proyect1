package com.example.das_proyect1.adaptadores.RecyclerViewAdapterImagenesFirebase;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;

public class ViewHolderNewImagenesFirebase extends RecyclerView.ViewHolder {
    //Guardaremos los elementos que tiene cada elemento del recycler view. en este caso un titulo y una imagen
    public TextView eltexto;
    public ImageView laimagen;
    public ViewHolderNewImagenesFirebase(@NonNull View itemView){
        super(itemView);
        eltexto=itemView.findViewById(R.id.textoTituloImagenFirebase);
        laimagen=itemView.findViewById(R.id.fotoImagenFirebase);
    }
}
