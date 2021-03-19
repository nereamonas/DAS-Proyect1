package com.example.das_proyect1.adaptadores.ListViewAdapterRutinas;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.ImgCorrespondiente;
import com.example.das_proyect1.helpClass.Rutina;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    //Esta clase es el adaptador para crear la list view de las rutinas. será un array list de rutinas

    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<Rutina> rutinas;

    public ListViewAdapter(Context context,ArrayList<Rutina> rutinas) {
        this.contexto=context;
        this.inflater=(LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.rutinas=rutinas;

    }

    @Override
    public int getCount() {
        return this.rutinas.size();
    }

    @Override
    public Object getItem(int position) {
        return this.rutinas.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d("Logs"," posicion "+position+" el id: "+this.rutinas.get(position).getId());
        return this.rutinas.get(position).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //Insertamos en el view todos los elementos. q los cogemos del arraylist
        view=inflater.inflate(R.layout.listviewelement_rutinas,null);
        TextView titulo= (TextView) view.findViewById(R.id.titulo);
        ImageView img=(ImageView) view.findViewById(R.id.image);
        TextView duracion= (TextView) view.findViewById(R.id.duracion);
        titulo.setText(this.rutinas.get(position).getNombre());
        ImgCorrespondiente i = new ImgCorrespondiente();
        img.setImageResource(i.devolver(this.rutinas.get(position).getFoto()));
        duracion.setText("Entra para empezar la rutina");
        return view;
    }
}
