package com.example.das_proyect1.ui.ajustes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.das_proyect1.R;

public class AjustesGeneralesFragment extends Fragment {
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