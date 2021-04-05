package com.example.das_proyect1.ui.recomendar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.das_proyect1.R;
import com.example.das_proyect1.adaptadores.ListViewAdapterContactos.ListViewAdapterContactos;
import com.example.das_proyect1.base.BaseViewModel;
import com.example.das_proyect1.helpClass.Contacto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class RecomendarFragment extends Fragment {

    private BaseViewModel recomendarViewModel;
    private ListViewAdapterContactos eladap;

    private ListView listViewContactos;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        recomendarViewModel =
                new ViewModelProvider(this).get(BaseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recomendar, container, false);
        //final TextView textView = root.findViewById(R.id.text_gallery);


        listViewContactos=(ListView)root.findViewById(R.id.listContactos);

        solicitarPermisoLeerContactos();


        //Cuando se clicke en un elemento de la lista saltará a este metodo. Que cambiara la navegacion a rutEjerFragment. Le apsara el usuario y el identificador de la rutina seleccionada
        listViewContactos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String telefonoElegido=String.valueOf(eladap.getItemTelef(position));
                String nombreElegido=String.valueOf(eladap.getItemName(position));

                AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
                dialogo.setTitle("Enviar mensaje");
                dialogo.setMessage("Quieres enviar un mensaje de recomendación al usuario: "+nombreElegido+" - "+telefonoElegido);

                dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {  //Botón si. es decir, queremos eliminar la rutina
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent= new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("smsto:"+telefonoElegido));
                        intent.putExtra("sms_body","Hola. Te recomiendo que te descarges la aplicación DAS_PROYECT1, es genial!");
                        startActivity(intent);
                    }
                });
                //En el caso de que el usuario diga que no quiere borrarlo, pues no hará nada. se cerrará el dialogo
                dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Log.d("Logs", "no se eliminara la rutina");
                    }
                });
                dialogo.show();





                }

        });


        //Cuando hagamos pulsación larga sobre un elemento entrará aqui. Se dará la opción a borrar la rutina seleccionada
        listViewContactos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String telefonoElegido=String.valueOf(eladap.getItemTelef(i));
                //Mostramos una alerta indicando si se quiere eliminar la rutina, antes de eliminarla, necesitaremos una confirmación del usuario
                AlertDialog.Builder dialogo = new AlertDialog.Builder(view.getContext());
                dialogo.setTitle("Editar contacto");
                dialogo.setMessage("Quieres editar el contacto?");

                dialogo.setPositiveButton(getString(R.string.si), new DialogInterface.OnClickListener() {  //Botón si. es decir, queremos eliminar la rutina
                    public void onClick(DialogInterface dialogo1, int id) {
                           Uri uri=Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI,Uri.encode(telefonoElegido));
                           Cursor cursor=getActivity().getContentResolver().query(uri,null,null,null,null);
                           if (cursor.moveToFirst()){
                               long idTelefono=Long.valueOf(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup._ID)));
                               Intent editarIntent= new Intent(Intent.ACTION_EDIT);
                               editarIntent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,idTelefono));
                               startActivity(editarIntent);
                           }
                           cursor.close();

                    }
                });
                //En el caso de que el usuario diga que no quiere borrarlo, pues no hará nada. se cerrará el dialogo
                dialogo.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Log.d("Logs", "no se eliminara el contacto");
                    }
                });
                dialogo.show();

                return true;
            }
        });


        //Hemos añadido un boton flotante en la esquina inferior derecha con el icono +. Donde se da la opción de añadir una nueva rutina. Primero nos saltará un dialogo para ingresar un nombre y posteriormente otro para seleccionar los ejercicios a añadir.
        FloatingActionButton myFab = (FloatingActionButton) root.findViewById(R.id.floatingActionButtonAñadirContacto);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Logs", "CLICKFLOAT");

                //Creamos una alerta
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Añade los datos del nuevo contacto");
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                //Añadimos en la alerta un edit text
                final EditText inputNombre = new EditText(getContext());  //Creamos un edit text. para q el usuairo pueda insertar el titulo
                inputNombre.setHint("Nombre");
                layout.addView(inputNombre);

                final EditText inputTelefono = new EditText(getContext());  //Creamos un edit text. para q el usuairo pueda insertar el titulo
                inputTelefono.setHint("Telefono");
                layout.addView(inputTelefono);

                builder.setView(layout);
                //Si el usuario da al ok
                builder.setPositiveButton(getString(R.string.alert_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreContacto = inputNombre.getText().toString();
                        String telefonoContacto = inputTelefono.getText().toString();

                        Intent intent=new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, telefonoContacto);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, nombreContacto);

                        startActivity(intent);

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

    public void conseguirContactos(){
        ArrayList<Contacto> contacts= new ArrayList<Contacto>();

        Uri uri= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection= {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER};
        String selection=null;
        String[] selectionArgs=null;
        String sortOrder=null;


        ContentResolver resolver=getActivity().getContentResolver();
        Cursor cursor=resolver.query(uri, projection,selection,selectionArgs,sortOrder);

        while (cursor.moveToNext()){
            String name= cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String num=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


            Log.d("Logs", "Name: "+name+", NUm: "+num);
            Contacto c= new Contacto(name,num);
            contacts.add(c);
        }
        cursor.close();

        eladap= new ListViewAdapterContactos(getContext(),contacts);
        listViewContactos.setAdapter(eladap);


    }

    public void solicitarPermisoLeerContactos(){
        Log.d("Logs","PERMISO LEER CONTACTOS");
        boolean permiso=false;
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Logs","NO ESTA CONCEDIDO");
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO
                Log.d("Logs","DECIR XQ ES NECESARIO");
            }
            else{
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
                Log.d("Logs","NO SE HA CONCEDIDO");
                Toast.makeText(getActivity(), "No se ha concedido el permiso de escritura", Toast.LENGTH_SHORT).show();
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);  //ACTIVITY_RECOGNITION

            Log.d("Logs","PEDIR PERMISO");
            //ya estará aceptado asiq:
            while(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)!=
                    PackageManager.PERMISSION_GRANTED){

            }
            conseguirContactos();
        }
        else {
            Log.d("Logs","YA LO TIENE");
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            conseguirContactos();
        }
    }
}