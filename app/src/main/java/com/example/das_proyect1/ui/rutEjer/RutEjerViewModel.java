package com.example.das_proyect1.ui.rutEjer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RutEjerViewModel extends ViewModel {
//Este metodo al crear el menu, se añadia. Es para q al entrar en el fragment, salgan las tres rallicas q dan la opcion a abrir el menu

    private MutableLiveData<String> mText;

    public RutEjerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}