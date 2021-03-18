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
import android.widget.EditText;
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

import com.example.das_proyect1.adaptadores.ListViewAdapterRutinas.ListViewAdapter;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Rutina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
                dialogo.setTitle(getString(R.string.alert_eliminarRutina));
                dialogo.setMessage(getString(R.string.alert_seguroquequiereseliminarlarutina));
                //dialogo.setCancelable(false);
                dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        //Si dice que si quiere eliminar. Actualizamos la lista y lo borramos de la base de datos
                        int idRutina= (int) eladap.getItemId(i);
                        db = new MiDB(getContext());
                        db.eliminarRutinaDelUsuario(usuario,idRutina);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

                        rutinas.remove(i);
                        eladap.notifyDataSetChanged();
                        //Most4ramos un toast diciendo que se ha eliminado correctamente
                        if (prefs.contains("notiftoast")) {
                            Boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(),getString(R.string.toast_sehaeliminadolarutina), Toast.LENGTH_SHORT);
                                toast.show();
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


        FloatingActionButton myFab = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Logs", "CLICKFLOAT");

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.alert_insertatitulorutina));

                final EditText input = new EditText(getContext());  //Creamos un edit text. para q el usuairo pueda insertar el titulo
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {  //Si el usuario acepta, mostramos otra alerta con los ejercicios que puede agregar
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tituloRutina = input.getText().toString();

                        if (!tituloRutina.equals("")){
                            //Mostramos una checkbox
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                            builder2.setTitle(getString(R.string.alert_eligeejerciciosaañadir));

                            db = new MiDB(getContext());
                            ArrayList<String> ejercicioslist = db.getNombreTodosLosEjercicios(usuario);
                            String[] ejercicios = new String[ejercicioslist.size()];
                            ejercicios = ejercicioslist.toArray(ejercicios);

                            final ArrayList <Integer> loselegidos=new ArrayList<>();
                            builder2.setMultiChoiceItems(ejercicios, null, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                    if (b == true){
                                        //Hemos seleccionado un elemento, lo añadimos a la lista
                                        loselegidos.add(i);
                                    }
                                    else if (loselegidos.contains(i)){
                                        //Hemos desseleccionado el elemento, lo eliminamos de la lista
                                        loselegidos.remove(Integer.valueOf((i)));
                                    }
                                }
                            });

                            builder2.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<String> nombreElegidos= new ArrayList<String>();
                                    for (int i=0 ; i<loselegidos.size() ; i++) {
                                        nombreElegidos.add(ejercicioslist.get(loselegidos.get(i)));
                                    }
                                    db.añadirRutinaConEjerciciosAlUsuario(usuario,tituloRutina,nombreElegidos);
                                    Rutina r=db.getRutinaConNombre(tituloRutina);
                                    db.cerrarConexion();

                                    rutinas.add(r);
                                    eladap.notifyDataSetChanged();

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    //Most4ramos un toast diciendo que se ha eliminado correctamente
                                    if (prefs.contains("notiftoast")) {
                                        Boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                                        if (activadas) {
                                            Toast toast = Toast.makeText(getContext(),getString(R.string.toast_rutinaCreada), Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }
                            });
                            builder2.setNegativeButton(getString(R.string.alert_cancelar), null);
                            builder2.show();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.alert_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();



            }
        });


        return root;
    }
}