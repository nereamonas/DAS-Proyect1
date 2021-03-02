package com.example.das_proyect1.recycleViewAdaptersRutinas;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;

public class ViewHolderNewRutinas extends RecyclerView.ViewHolder {
    public TextView eltexto;
    public ImageView laimagen;
    public ViewHolderNewRutinas(@NonNull View itemView){
        super(itemView);
        eltexto=itemView.findViewById(R.id.texto);
        laimagen=itemView.findViewById(R.id.foto);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_nav_rutinas_to_rutEjerViewPagerFragment);
            }
        });

    }
}
