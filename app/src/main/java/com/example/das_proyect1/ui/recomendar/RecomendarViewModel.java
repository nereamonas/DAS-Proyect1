package com.example.das_proyect1.ui.recomendar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecomendarViewModel extends ViewModel {
//Este metodo al crear el menu, se añadia. Es para q al entrar en el fragment, salgan las tres rallicas q dan la opcion a abrir el menu

    private MutableLiveData<String> mText;

    public RecomendarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este es el fragmento de recomendar");
    }

    public LiveData<String> getText() {
        return mText;
    }
}