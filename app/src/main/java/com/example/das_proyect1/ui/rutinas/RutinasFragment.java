package com.example.das_proyect1.ui.rutinas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.recycleViewAdaptersRutinas.AdaptadorRecyclerRutinas;

public class RutinasFragment extends Fragment {

    private RutinasViewModel rutinasViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        rutinasViewModel =
                new ViewModelProvider(this).get(RutinasViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rutinas, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);
        rutinasViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        RecyclerView lalista= root.findViewById(R.id.recycleView);

        int[] personajes= {R.drawable.bart, R.drawable.edna, R.drawable.homer, R.drawable.lisa,
                R.drawable.skinner};
        String[] nombres={"Bart Simpson","Edna Krabappel", "Homer Simpson", "Lisa Simpson",
                "Seymour Skinner"};
        AdaptadorRecyclerRutinas eladaptador = new AdaptadorRecyclerRutinas(nombres,personajes);
        lalista.setAdapter(eladaptador);

        LinearLayoutManager elLayoutLineal= new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false);
        lalista.setLayoutManager(elLayoutLineal);
/**
        lalista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                elementosLista.remove(i);
                adapt.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),"Se ha eliminado la tarea",Toast.LENGTH_LONG).show();
                return true;
            }
        });
**/
        return root;
    }
}