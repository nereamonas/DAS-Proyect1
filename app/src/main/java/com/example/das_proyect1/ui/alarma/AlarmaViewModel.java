package com.example.das_proyect1.ui.alarma;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlarmaViewModel extends ViewModel {
//Este metodo al crear el menu, se añadia. Es para q al entrar en el fragment, salgan las tres rallicas q dan la opcion a abrir el menu

    private MutableLiveData<String> mText;

    public AlarmaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Este es el fragmento de alarma");
    }

    public LiveData<String> getText() {
        return mText;
    }
}