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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_proyect1.R;
import com.example.das_proyect1.adaptadores.RecycleViewAdapterEjercicios.AdaptadorRecyclerEjercicios;
import com.example.das_proyect1.adaptadores.RecyclerViewAdapterImagenesFirebase.AdaptadorRecyclerImagenesFirebase;
import com.example.das_proyect1.base.BaseFragment;
import com.example.das_proyect1.helpClass.Ejercicio;
import com.example.das_proyect1.helpClass.ImagenFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImagenesFirebaseFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_imagenes_firebase, container, false);

        ArrayList<ImagenFirebase> imagenes=new ArrayList<ImagenFirebase>();

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("imagenes");  //Abrimos la referencia del firebase
        RecyclerView lalista= root.findViewById(R.id.recycleViewImagenesFirebase);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        lalista.setLayoutManager(manager);
        lalista.setHasFixedSize(true);
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d: snapshot.getChildren()) {
                    String value = d.getValue(String.class);
                    String[] split = value.split("###");
                    ImagenFirebase img = new ImagenFirebase(split[0], split[1]);
                    imagenes.add(img);
                }
                AdaptadorRecyclerImagenesFirebase eladaptador = new AdaptadorRecyclerImagenesFirebase(getContext(),imagenes);
                lalista.setAdapter(eladaptador);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_SHORT);

            }
        });

        return root;

    }
}