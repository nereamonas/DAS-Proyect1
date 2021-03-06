package com.example.das_proyect1.ui.rutinas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.helpClass.Rutina;
import com.example.das_proyect1.recycleViewAdaptersRutinas.AdaptadorRecyclerRutinas;

import java.util.ArrayList;

public class RutinasFragmentRecycler extends Fragment {
    private MiDB db;
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
        db=new MiDB(getContext());
        ArrayList<Rutina> rutinas= db.getRutinasDelUsuario("nerea");

        AdaptadorRecyclerRutinas eladaptador = new AdaptadorRecyclerRutinas(rutinas);
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