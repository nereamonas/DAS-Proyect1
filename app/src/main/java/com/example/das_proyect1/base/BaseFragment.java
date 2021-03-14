package com.example.das_proyect1.base;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Gets the saved theme ID from SharedPrefs,
        // or uses default_theme if no theme ID has been saved

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String tema = "";
        if (prefs.contains("tema")) {
            tema = prefs.getString("tema", null);
        }
        switch (tema) {
            case "morado":
                getContext().setTheme(R.style.Theme_Morado);
                getActivity().setTheme(R.style.Theme_Morado);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#DA6CED")));
                super.onCreate(savedInstanceState);
                break;
            case "naranja":
                getContext().setTheme(R.style.Theme_Naranja);
                getActivity().setTheme(R.style.Theme_Naranja);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF9800")));
                super.onCreate(savedInstanceState);
                break;
            case "verde":
                getContext().setTheme(R.style.Theme_Verde);
                getActivity().setTheme(R.style.Theme_Verde);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#8BC34A")));
                super.onCreate(savedInstanceState);
                break;
            case "azul":
                getContext().setTheme(R.style.Theme_Azul);
                getActivity().setTheme(R.style.Theme_Azul);
                //((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#86E9F6")));
                super.onCreate(savedInstanceState);
                break;
            default:
                getContext().setTheme(R.style.Theme_Morado);
                getActivity().setTheme(R.style.Theme_Morado);
                super.onCreate(savedInstanceState);
                break;
        }


    }
}