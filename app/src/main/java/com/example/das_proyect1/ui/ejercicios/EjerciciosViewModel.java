package com.example.das_proyect1.ui.ejercicios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EjerciciosViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EjerciciosViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}