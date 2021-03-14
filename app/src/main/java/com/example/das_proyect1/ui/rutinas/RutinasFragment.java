package com.example.das_proyect1.ui.rutinas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.example.das_proyect1.adaptadores.ListViewAdapterRutinas.ListViewAdapter;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Rutina;

import java.util.ArrayList;

public class RutinasFragment extends BaseFragment {
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
        this.db.cerrarConexion(); //Cerramos la conexion porq no lo vamos a usar mas

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

        return root;
    }
}