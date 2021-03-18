package com.example.das_proyect1.ui.rutinas;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.LogInActivity;
import com.example.das_proyect1.adaptadores.ListViewAdapterRutinas.ListViewAdapter;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Rutina;

import java.util.ArrayList;

public class RutinasFragment extends BaseFragment {
    //Creara una list view de rutinas, q al pulsar en una te lleva al fragment RutEjerFragment, parandole la rutina seleccionada
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

        ArrayList<Rutina> rutinas= db.getRutinasDelUsuario(usuario);  //Llamamos a la base de datos apra q devuelva las rutinas q tiene ese usuario asignadas
        this.db.cerrarConexion(); //Cerramos la conexion porq no lo vamos a usar mas

        ListView lalista= (ListView) root.findViewById(R.id.listView);
        ListViewAdapter eladap= new ListViewAdapter(getContext(),rutinas);
        lalista.setAdapter(eladap);

        //Cuando se clicke en un elemento de la lista saltará a este metodo. Que cambiara la navegacion a rutEjerFragment. Le apsara el usuario y el identificador de la rutina seleccionada
        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle(); //Con el bundle podemos pasar datos
                bundle.putString("idRut", (String.valueOf(eladap.getItemId(position))));
                bundle.putString("usuario", usuario);
                NavOptions options = new NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .build();
                Navigation.findNavController(view).navigate(R.id.action_nav_rutinas_to_rutEjerViewPagerFragment, bundle,options);
            }

        });

        lalista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Mostramos una alerta indicando si se quiere eliminar la rutina
                AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
                dialogo.setTitle("Eliminar rutina");
                dialogo.setMessage("Estas seguro que quieres eliminar la rutina?");
                //dialogo.setCancelable(false);
                dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //Si dice que si quiere eliminar. Actualizamos la lista y lo borramos de la base de datos
                        int idRutina= (int) eladap.getItemId(i);
                        db = new MiDB(getContext());
                        boolean todobien=db.eliminarRutinaDelUsuario(usuario,idRutina);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        if(todobien){
                            rutinas.remove(i);
                            eladap.notifyDataSetChanged();
                            //Most4ramos un toast diciendo que se ha eliminado correctamente
                            if (prefs.contains("notiftoast")) {
                                Boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                                if (activadas) {
                                    Toast toast = Toast.makeText(getContext(),"Se ha eliminado la rutina", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }

                        }else{
                            if (prefs.contains("notiftoast")) {
                                Boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                                if (activadas) {
                                    Toast toast = Toast.makeText(getContext(),"No se ha podido eliminar la rutina", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        }
                        db.cerrarConexion();
                    }
                });
                dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Log.d("Logs", "no cerrar sesion");
                    }
                });
                dialogo.show();

                return true;
            }
        });



        return root;
    }
}