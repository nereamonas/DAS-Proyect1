package com.example.das_proyect1.recycleViewAdaptersRutinas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.Rutina;

import java.util.ArrayList;

public class AdaptadorRecyclerRutinas extends RecyclerView.Adapter<ViewHolderNewRutinas>  implements View.OnClickListener{
    private ArrayList<Rutina> rutinas;
    private View.OnClickListener listener;
    public AdaptadorRecyclerRutinas(ArrayList<Rutina> rutinas)
    {
        this.rutinas=rutinas;
    }

    @NonNull
    @Override
    public ViewHolderNewRutinas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_rutinas,null);
        ViewHolderNewRutinas evh = new ViewHolderNewRutinas(elLayoutDeCadaItem);


        elLayoutDeCadaItem.setOnClickListener(this);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNewRutinas holder, int position) {
        holder.eltexto.setText(this.rutinas.get(position).getNombre());
        holder.laimagen.setImageResource(R.drawable.edna);


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
        public boolean onItemClicked(View view, int position);
    }

    @Override
    public int getItemCount() {
        return this.rutinas.size();
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

