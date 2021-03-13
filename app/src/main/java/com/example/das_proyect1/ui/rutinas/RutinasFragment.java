package com.example.das_proyect1.ui.rutinas;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.ListViewAdapterRutinas.ListViewAdapter;
import com.example.das_proyect1.LogInActivity;
import com.example.das_proyect1.MiDB;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;
import com.example.das_proyect1.controlarCambios.ControlarCambiosFragment;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.Rutina;
import com.example.das_proyect1.recycleViewAdaptersRutinas.AdaptadorRecyclerRutinas;
import com.example.das_proyect1.rutEjer.RutEjerFragment;

import java.util.ArrayList;

public class RutinasFragment extends ControlarCambiosFragment {
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

        db=new MiDB(getContext());
        String usuario = getArguments().getString("usuario");

        ArrayList<Rutina> rutinas= db.getRutinasDelUsuario(usuario);


        ListView lalista= (ListView) root.findViewById(R.id.listView);
        ListViewAdapter eladap= new ListViewAdapter(getContext(),rutinas);
        lalista.setAdapter(eladap);


        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("idRut", (String.valueOf(eladap.getItemId(position))));
                bundle.putString("usuario", usuario);
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build();
                Navigation.findNavController(view).navigate(R.id.action_nav_rutinas_to_rutEjerViewPagerFragment, bundle,options);
            }

        });


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