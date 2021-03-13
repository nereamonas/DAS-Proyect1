package com.example.das_proyect1.controlarCambios;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;

import java.util.Locale;

public class ControlarCambios extends AppCompatActivity {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            // Gets the saved theme ID from SharedPrefs,
            // or uses default_theme if no theme ID has been saved

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String tema = "";
            if (prefs.contains("tema")) {
                tema = prefs.getString("tema", null);
            }
            switch (tema) {
                case "morado":
                    this.setTheme(R.style.Theme_Morado);
                    super.onCreate(savedInstanceState);
                    break;
                case "naranja":
                    this.setTheme(R.style.Theme_Naranja);
                    super.onCreate(savedInstanceState);
                    break;
                case "verde":
                    this.setTheme(R.style.Theme_Verde);
                    super.onCreate(savedInstanceState);
                    break;
                case "azul":
                    this.setTheme(R.style.Theme_Azul);
                    super.onCreate(savedInstanceState);
                    break;
                default:
                    this.setTheme(R.style.Theme_Morado);
                    super.onCreate(savedInstanceState);
                    break;
            }
            String idioma = "";
            if (prefs.contains("idioma")) {
                idioma = prefs.getString("idioma", null);
            }

            Locale locale = new Locale(idioma);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getResources().updateConfiguration(config, null);

        }
        public String returnTema(){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String tema = "";
            if (prefs.contains("tema")) {
                tema = prefs.getString("tema", null);
            }
            return tema;
        }

        public void setSupportActionBar(Toolbar toolbar){
            super.setSupportActionBar(toolbar);

        }
    }