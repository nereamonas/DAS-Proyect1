package com.example.das_proyect1.ui.ajustes;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.das_proyect1.R;
import com.example.das_proyect1.base.BaseFragment;

public class AjustesGeneralesFragment extends BaseFragment {
    private MutableLiveData<String> mText;
    //Esta clase une el ajustesFragment y ajustesUsuario fragment. Si está en vertical solo muestra ajustes fragment y si esta en horizontal muestra los dos
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");

        return inflater.inflate(R.layout.fragment_ajustes_generales, container, false);


    }
}