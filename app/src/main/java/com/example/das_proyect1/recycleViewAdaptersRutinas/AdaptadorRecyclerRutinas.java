package com.example.das_proyect1.recycleViewAdaptersRutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;

public class AdaptadorRecyclerRutinas extends RecyclerView.Adapter<ViewHolderNewRutinas> {
    private String[] losnombres;
    private int[] lasimagenes;
    public AdaptadorRecyclerRutinas(String[] nombres, int[] imagenes)
    {
        losnombres=nombres;
        lasimagenes=imagenes;
    }

    @NonNull
    @Override
    public ViewHolderNewRutinas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rutinas,null);
        ViewHolderNewRutinas evh = new ViewHolderNewRutinas(elLayoutDeCadaItem);
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewRutinas holder, int position) {
        holder.eltexto.setText(losnombres[position]);
        holder.laimagen.setImageResource(lasimagenes[position]);


        /**holder.v.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v){
                Fragment.onItemLongClicked(position);
                return true
            }
        })**/

    }

    public interface OnItemLongClickListener{
        public boolean onItemLongClicked(int position);
    }

    public interface OnItemClickListener{
        public boolean onItemClicked(int position);
    }

    @Override
    public int getItemCount() {
        return losnombres.length;
    }
}
