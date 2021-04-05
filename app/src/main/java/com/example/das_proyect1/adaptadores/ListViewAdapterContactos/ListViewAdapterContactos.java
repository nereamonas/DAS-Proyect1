package com.example.das_proyect1.adaptadores.ListViewAdapterContactos;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Contacto;

import java.util.ArrayList;

public class ListViewAdapterContactos extends BaseAdapter {

    //Esta clase es el adaptador para crear la list view de las rutinas. será un array list de rutinas

    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<Contacto> contactos;

    public ListViewAdapterContactos(Context context, ArrayList<Contacto> contactos) {
        this.contexto=context;
        this.inflater=(LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.contactos=contactos;

    }

    @Override
    public int getCount() {
        return this.contactos.size();
    }

    @Override
    public Object getItem(int position) {
        return this.contactos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    public String getItemTelef(int position) {
        Log.d("Logs"," posicion "+position+" el telefono: "+this.contactos.get(position).getTelefono());
        return this.contactos.get(position).getTelefono();
    }
    public String getItemName(int position) {
        Log.d("Logs"," posicion "+position+" el telefono: "+this.contactos.get(position).getTelefono());
        return this.contactos.get(position).getNombre();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        //Insertamos en el view todos los elementos. q los cogemos del arraylist
        view=inflater.inflate(R.layout.listviewelement_contactos,null);
        TextView nombre= (TextView) view.findViewById(R.id.nombreContacto);
        TextView telefono= (TextView) view.findViewById(R.id.telefonoContacto);
        nombre.setText(this.contactos.get(position).getNombre());
        telefono.setText(this.contactos.get(position).getTelefono());
        return view;
    }
}
