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
import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.helpClass.MiDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Rutina;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RutinasFragment extends BaseFragment {
    //Creara una list view de rutinas, q al pulsar en una te lleva al fragment RutEjerFragment, parandole la rutina seleccionada. Tambien se puede eliminar una rutina haciendo una pulsación larga sobre ella. Y tambien se puede crear una nueva rutina mediante el botón + de abajo a la derecha. Donde nos saltará un dialogo para añadir el nombre que le queremos dar a la rutina y posteriormente otro dialogo para seleccionar los ejercicios que queramos añadir
    private MiDB db;
    private BaseViewModel rutinasViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        rutinasViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rutinas, container, false);


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

        //Cuando hagamos pulsación larga sobre un elemento entrará aqui. Se dará la opción a borrar la rutina seleccionada
        lalista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Mostramos una alerta indicando si se quiere eliminar la rutina, antes de eliminarla, necesitaremos una confirmación del usuario
                AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
                dialogo.setTitle(getString(R.string.alert_eliminarRutina));
                dialogo.setMessage(getString(R.string.alert_seguroquequiereseliminarlarutina));

                dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {  //Botón si. es decir, queremos eliminar la rutina
                    public void onClick(DialogInterface dialogo1, int id) {
                        //Si dice que si quiere eliminar. Actualizamos la lista y lo borramos de la base de datos
                        //Cogemos el id del elemento seleccionado por el usuario
                        int idRutina= (int) eladap.getItemId(i);
                        db = new MiDB(getContext()); //Creamos conexion con base de datos
                        db.eliminarRutinaDelUsuario(usuario,idRutina); //Llamamos al metodo para eliminar la rutina, pasandole el usuario y el id de la rutina
                        db.cerrarConexion();  //Cerramos conexion con base de datos
                        rutinas.remove(i);  //Eliminamos de la lista de rutinas la rutina
                        eladap.notifyDataSetChanged();  //Y actualizamos el adaptador para que se muestre la lista actualizada, es decir, eliminando la rutina.

                        //Llamamos a las preferencias para ver si tiene las notificaciones toast activadas
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        //En el caso de que las tenga activadas, Mostramos un toast diciendo que se ha eliminado correctamente
                        if (prefs.contains("notiftoast")) {
                            Boolean activadas = prefs.getBoolean("notiftoast", true);  //Comprobamos si las notificaciones estan activadas
                            if (activadas) {
                                Toast toast = Toast.makeText(getContext(),getString(R.string.toast_sehaeliminadolarutina), Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }

                    }
                });
                //En el caso de que el usuario diga que no quiere borrarlo, pues no hará nada. se cerrará el dialogo
                dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Log.d("Logs", "no se eliminara la rutina");
                    }
                });
                dialogo.show();

                return true;
            }
        });


        //Hemos añadido un boton flotante en la esquina inferior derecha con el icono +. Donde se da la opción de añadir una nueva rutina. Primero nos saltará un dialogo para ingresar un nombre y posteriormente otro para seleccionar los ejercicios a añadir.
        FloatingActionButton myFab = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Logs", "CLICKFLOAT");

                //Creamos una alerta
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.alert_insertatitulorutina));
                //Añadimos en la alerta un edit text
                final EditText input = new EditText(getContext());  //Creamos un edit text. para q el usuairo pueda insertar el titulo
                builder.setView(input);
                //Si el usuario da al ok
                builder.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {  //Si el usuario acepta, mostramos otra alerta con los ejercicios que puede agregar
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String tituloRutina = input.getText().toString();

                        if (!tituloRutina.equals("")){  //Comprobamos que haya ingresado un titulo, ya que si el titulo es nulo, no se creará la rutina
                            //Mostramos una checkbox
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                            builder2.setTitle(getString(R.string.alert_eligeejerciciosaañadir));

                            db = new MiDB(getContext());  //Creamos conexion con base de datos
                            ArrayList<String> ejercicioslist = db.getNombreTodosLosEjercicios(usuario);  //Devolvemos un arraylist coon los ejercicios que tiene ese usuario
                            db.cerrarConexion();   //Cerramos conexion con base de datos
                            String[] ejercicios = new String[ejercicioslist.size()];
                            ejercicios = ejercicioslist.toArray(ejercicios);  //Convertimos el arraylist en String[]

                            final ArrayList <Integer> loselegidos=new ArrayList<>(); //Aqui guardaremos los identificadores de los elementos que selecciona el usuario
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

                            //Cuando el usuario de al ok, se procederá a añadir los ejercicios a la rutina
                            builder2.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ArrayList<String> nombreElegidos= new ArrayList<String>();  //Crearemos un array list con los nombres de las rutinas seleccionadas, que le pasaremos al metodo que haga los insert en la bbdd
                                    for (int i=0 ; i<loselegidos.size() ; i++) {  //Recorremos la lista de elegidos para coger su nombre por el id
                                        nombreElegidos.add(ejercicioslist.get(loselegidos.get(i)));
                                    }
                                    db = new MiDB(getContext());  //Creamos conexion con base de datos
                                    Rutina r = db.añadirRutinaConEjerciciosAlUsuario(usuario,tituloRutina,nombreElegidos);  //Llamamos al metodo que añadira los elementos a la bbdd
                                    db.cerrarConexion();   //Cerramos conexion con base de datos

                                    rutinas.add(r);  //Actualizaremos la lista de rutinas
                                    eladap.notifyDataSetChanged();  //Actualizamos el adaptador para que muestre los cambios en pantalla

                                    //Cogeremos las preferencias, para ver si tiene las notificaciones toast activadas para mostrar el mensaje
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
                            //Si hace click en cancelar, se cancelará la operación y no se añadiran ejercicios a la nueva rutina
                            builder2.setNegativeButton(getString(R.string.alert_cancelar), null);
                            builder2.show();
                        }
                    }
                });

                //Si se cancela, no se creará la rutina y se cancelará el dialogo
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