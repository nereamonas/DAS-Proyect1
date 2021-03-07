package com.example.das_proyect1.ui.rutinasCompletadas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RutinasCompletadasViewModel  extends ViewModel {

    private MutableLiveData<String> mText;

    public RutinasCompletadasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}