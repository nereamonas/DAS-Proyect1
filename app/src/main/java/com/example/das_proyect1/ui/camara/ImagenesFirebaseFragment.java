package com.example.das_proyect1.ui.camara;

import android.media.Image;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.adaptadores.RecyclerViewAdapterImagenesFirebase.AdaptadorRecyclerImagenesFirebase;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.ImagenFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ImagenesFirebaseFragment extends BaseFragment {

    //Veremos todas las imagenes que esten subidas a firebase con esta aplicacion. Cualquier usuario podra ver las fotos del resto de usuarios

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_imagenes_firebase, container, false);

        ArrayList<ImagenFirebase> imagenes=new ArrayList<ImagenFirebase>();  //Creamos un array donde guardaremos todas las imagenes

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("imagenes");  //Abrimos la referencia del firebase
        RecyclerView lalista= root.findViewById(R.id.recycleViewImagenesFirebase);  //Hemos creado un recyclerview para mostrar las imagenes con su titulo

        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d: snapshot.getChildren()) { //Por cada datasnapshot (es decir cada foto subida a firebase)
                    String value = d.getValue(String.class);
                    String[] split = value.split("###");
                    ImagenFirebase img = new ImagenFirebase(split[0], split[1]); //Cogemos sus datos, creamos una imagen y la añadimos a la lista
                    imagenes.add(img);
                }
                AdaptadorRecyclerImagenesFirebase eladaptador = new AdaptadorRecyclerImagenesFirebase(getContext(),imagenes);
                lalista.setAdapter(eladaptador);


                GridLayoutManager elLayoutRejillaIgual;  //En modo vertical mostraremos solo una columna y en modo horizontal dos columnas
                //Cogemos la orientacion de la pantalla. ya q si está en vertical, saldran solamente dos columnas, y si está en horizontal saldran 3 columnas
                int rotacion=getActivity().getWindowManager().getDefaultDisplay().getRotation();
                if (rotacion== Surface.ROTATION_0 || rotacion==Surface.ROTATION_180){
                    //La pantalla está en vertical
                    elLayoutRejillaIgual= new GridLayoutManager(getContext(),1, GridLayoutManager.VERTICAL,false);
                }else{
                    //La pantalla esta en horizontal
                    elLayoutRejillaIgual= new GridLayoutManager(getContext(),2, GridLayoutManager.VERTICAL,false);
                }

                lalista.setLayoutManager(elLayoutRejillaIgual);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT);
            }
        });

        return root;

    }
}