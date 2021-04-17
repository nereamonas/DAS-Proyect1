package com.example.das_proyect1.ui.rutinas;

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
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.das_proyect1.adaptadores.ListViewAdapterRutinas.ListViewAdapter;
import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.helpClass.ExternalDB;
import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Rutina;
import com.example.das_proyect1.serviceBroadcast.MusicService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.*;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;

public class RutinasFragment extends BaseFragment {
    //Creara una list view de rutinas, q al pulsar en una te lleva al fragment RutEjerFragment, parandole la rutina seleccionada. Tambien se puede eliminar una rutina haciendo una pulsación larga sobre ella. Y tambien se puede crear una nueva rutina mediante el botón + de abajo a la derecha. Donde nos saltará un dialogo para añadir el nombre que le queremos dar a la rutina y posteriormente otro dialogo para seleccionar los ejercicios que queramos añadir

    private BaseViewModel rutinasViewModel;
    private String usuario;
    ArrayList<Rutina> listaRutinas=null;
    ListView lalista=null;
    FloatingActionButton myFab=null;
    ListViewAdapter eladap=null;

    @Override
    public void onResume() {

        super.onResume();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rutinasViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_rutinas, container, false);

        //try {
            usuario = getArguments().getString("usuario");
        //}catch(Exception e){

        //}


        Data datos = new Data.Builder()
                .putString("tarea","getRutinasDelUsuario")
                .putString("usuario",usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).enqueue(otwr);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        String rutinas=status.getOutputData().getString("rutinas");
                        Log.d("Logs","Resultado obtenido rutinas:"+rutinas);
                        convertJsonArrayRutinas(rutinas);
                        continuar();
                    }
                });

        lalista= (ListView) root.findViewById(R.id.listView);
        myFab = (FloatingActionButton) root.findViewById(R.id.floatingActionButton);

        return root;
    }

    public void continuar(){

        eladap= new ListViewAdapter(getContext(),this.listaRutinas);
        lalista.setAdapter(eladap);

        //Cuando se clicke en un elemento de la lista saltará a este metodo. Que cambiara la navegacion a rutEjerFragment. Le apsara el usuario y el identificador de la rutina seleccionada
        lalista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Empezamos una rutina asique reproducimos la musica. Reggaeton para hacer ejercicio no viene mal
                getContext().startService(new Intent(getContext(), MusicService.class));  //Arrancamos el servicio de musica

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



                        Data datos = new Data.Builder()
                                .putString("tarea","eliminarRutinaDelUsuario")
                                .putString("usuario",usuario)
                                .putString("idRutina", String.valueOf(idRutina))
                                .build();
                        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
                        WorkManager.getInstance(getActivity()).enqueue(otwr);
                        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                                .observe(getActivity(), status -> {
                                    if (status != null && status.getState().isFinished()) {
                                        boolean resultado=status.getOutputData().getBoolean("resultado",false);
                                        if(resultado){

                                            listaRutinas.remove(i);  //Eliminamos de la lista de rutinas la rutina
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
                                    }
                                });

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




                            Data datos = new Data.Builder()
                                    .putString("tarea","getNombreTodosLosEjercicios")
                                    .putString("usuario", String.valueOf(usuario))
                                    .build();
                            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
                            WorkManager.getInstance(getActivity()).enqueue(otwr);
                            WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                                    .observe(getActivity(), status -> {
                                        if (status != null && status.getState().isFinished()) {
                                            String nombreEjercicios=status.getOutputData().getString("nombreEjercicios");
                                            Log.d("Logs","Resultado obtenido nombre ejercicios:"+nombreEjercicios);
                                            ArrayList<String> ejercicioslist =convertJsonArray(nombreEjercicios);

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

                                                    añadirRutinaConEjerciciosAlUsuario(usuario,tituloRutina,nombreElegidos);

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
                                    });



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


    }

    public void convertJsonArrayRutinas(String rutinas){

        JSONObject obj = null;
        try {
            obj = new JSONObject(rutinas);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONArray arr = null; // notice that `"posts": [...]`
        try {
            arr = obj.getJSONArray("rutinas");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Rutina> lista = new ArrayList<Rutina>();
        for (int i = 0; i < arr.length(); i++) {
            try {
                JSONObject json=arr.getJSONObject(i);
                String id= (String) json.getString("id");
                String nombre= (String)json.getString("nombre");
                String foto= (String) json.getString("foto");
                Rutina r = new Rutina(Integer.parseInt(id), nombre, foto); //Cogemos todos los elementos y con ellos creamos una rutina
                lista.add(r); //Añadimos a la lista la rutina
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        this.listaRutinas=lista;
    }


    public ArrayList<String>  convertJsonArray(String nombreEjercicios){
        JSONArray jsonArray=null;
        try {
            jsonArray = new JSONArray(nombreEjercicios);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<String> list = new ArrayList<String>();
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                list.add( jsonArray.getString(i) );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public void añadirRutinaConEjerciciosAlUsuario(String usuario, String nombre, ArrayList<String> ejercicios){

        Data datos = new Data.Builder()
                .putString("tarea","añadirRutina")
                .putString("nombre",nombre)
                .putString("usuario",usuario)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos).build();
        WorkManager.getInstance(getActivity()).enqueue(otwr);
        WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr.getId())
                .observe(getActivity(), status -> {
                    if (status != null && status.getState().isFinished()) {
                        boolean result=status.getOutputData().getBoolean("resultado",false);
                        if(result){
                            String idRut=status.getOutputData().getString("idRut");

                            for (int i=0 ; i<ejercicios.size() ; i++) {
                                String nombreEjer=ejercicios.get(i);


                                Data datos2 = new Data.Builder()
                                        .putString("tarea","añadirRutEjer")
                                        .putString("nombre",nombreEjer)
                                        .putString("idRut", idRut)
                                        .build();
                                OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos2).build();
                                WorkManager.getInstance(getActivity()).enqueue(otwr2);
                                WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr2.getId())
                                        .observe(getActivity(), status2 -> {
                                            if (status2 != null && status2.getState().isFinished()) {
                                                status.getOutputData().getBoolean("resultado", false);

                                            }
                                        });
                                }

                                Data datos3 = new Data.Builder()
                                        .putString("tarea","getRutinaConId")
                                        .putString("idRut", idRut)
                                        .build();
                                OneTimeWorkRequest otwr3 = new OneTimeWorkRequest.Builder(ExternalDB.class).setInputData(datos3).build();
                                WorkManager.getInstance(getActivity()).enqueue(otwr3);
                                WorkManager.getInstance(getActivity()).getWorkInfoByIdLiveData(otwr3.getId())
                                        .observe(getActivity(), status3 -> {
                                            if (status3 != null && status3.getState().isFinished()) {
                                                String rutina=status3.getOutputData().getString("rutina");

                                                Log.d("Logs","Resultado obtenido rutina:"+rutina);
                                                convertJsonRutinaYActualizarLista(rutina);

                                            }
                                        });
                            }

                        }

                });



    }


    public void convertJsonRutinaYActualizarLista(String rutinas){

        JSONParser parser= new JSONParser();
        org.json.simple.JSONObject json =null;

        try {
            json = (org.json.simple.JSONObject) parser.parse(rutinas);
        }catch (Exception e){
            Log.d("Logs"," "+e);
        }
        try {
            String id= (String) json.get("id");
            String nombre= (String)json.get("nombre");
            String foto= (String) json.get("foto");
            Rutina r=new Rutina(Integer.parseInt(id),nombre,foto);

            listaRutinas.add(r);  //Actualizaremos la lista de rutinas
            eladap.notifyDataSetChanged();  //Actualizamos el adaptador para que muestre los cambios en pantalla


        }catch (Exception e){
            Log.d("Logs","3 "+e);
        }


    }


}