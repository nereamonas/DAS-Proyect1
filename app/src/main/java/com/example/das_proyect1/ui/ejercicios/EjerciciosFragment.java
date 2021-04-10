package com.example.das_proyect1.ui.ejercicios;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.helpClass.ExternalDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.adaptadores.RecycleViewAdapterEjercicios.AdaptadorRecyclerEjercicios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EjerciciosFragment extends BaseFragment {
    //Aqui se mostraran todos los ejercicios q hay añadidos en la abse de datos para ese usuario en un recycle view
    private BaseViewModel ejerciciosViewModel;

    private ArrayList<Ejercicio> ejercicios=null;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        ejerciciosViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_ejercicios, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);


        String usuario = getArguments().getString("usuario");  //cogemos el id de la rutina


        RecyclerView lalista= root.findViewById(R.id.recycleView);

        Data datos = new Data.Builder()
                .putString("tarea","getTodosLosEjercicios")
                .putString("usuario", usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).enqueue(otwr);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String ejer=status.getOutputData().getString("ejercicios");
                        Log.d("Logs","Resultado obtenido ejercicios: "+ejer);
                        convertJsonArrayRutinas(ejer);


                        AdaptadorRecyclerEjercicios eladaptador = new AdaptadorRecyclerEjercicios(ejercicios);
                        lalista.setAdapter(eladaptador);
                    }
                });


        GridLayoutManager elLayoutRejillaIgual;
        //Cogemos la orientacion de la pantalla. ya q si está en vertical, saldran solamente dos columnas, y si está en horizontal saldran 3 columnas
        int rotacion=getActivity().getWindowManager().getDefaultDisplay().getRotation();
        if (rotacion== Surface.ROTATION_0 || rotacion==Surface.ROTATION_180){
            //La pantalla está en vertical
            elLayoutRejillaIgual= new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
        }else{
            //La pantalla esta en horizontal
            elLayoutRejillaIgual= new GridLayoutManager(getContext(),3, GridLayoutManager.VERTICAL,false);
        }

        lalista.setLayoutManager(elLayoutRejillaIgual);

        return root;
    }



    public void convertJsonArrayRutinas(String ejer){

        JSONObject obj = null;
        try {
            obj = new JSONObject(ejer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray arr = null; // notice that `"posts": [...]`
        try {
            arr = obj.getJSONArray("ejercicios");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Ejercicio> lista = new ArrayList<Ejercicio>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject json=arr.getJSONObject(i);
                String id= (String) json.getString("id");
                String nombre= (String)json.getString("nombre");
                String descripcion= (String) json.getString("descripcion");
                String foto= (String) json.getString("foto");
                String duracion= (String) json.getString("duracion");
                Ejercicio e = new Ejercicio(Integer.parseInt(id),nombre, descripcion,foto,duracion); //Cogemos todos los elementos y con ellos creamos una rutina
                lista.add(e); //Añadimos a la lista la rutina
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        ejercicios=lista;
    }
}