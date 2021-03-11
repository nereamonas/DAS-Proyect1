package com.example.das_proyect1.ui.ajustes;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.das_proyect1.R;
import com.example.das_proyect1.controlarCambios.ControlarCambiosFragment;

public class AjustesGeneralesFragment extends ControlarCambiosFragment {
    private MutableLiveData<String> mText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        return inflater.inflate(R.layout.fragment_ajustes_generales, container, false);


    }
}