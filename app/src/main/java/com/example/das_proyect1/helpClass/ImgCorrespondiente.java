package com.example.das_proyect1.helpClass;

import com.example.das_proyect1.R;

public class ImgCorrespondiente {

    //Como tengo las imagenes en R.mipmap.tal  / eso devuelve un numero, y no puedo concatenar el nombre d la bd con R.mipmap para q m de directamente el numero q corresponde. es un poco ñapa esto pero bueno...
    public ImgCorrespondiente(){

    }

    public int devolver(String s){
        int resultado = 0;
        if (s.equals("todos")){
            resultado= R.mipmap.todos;
        }else if (s.equals("rutinaEjerAbdominal")){
            resultado= R.mipmap.rutinaejerabdominal;
        }else if (s.equals("rutinaEjerBrazo")){
            resultado= R.mipmap.rutinaejerbrazo;
        }else if (s.equals("rutinaEstiramiento")){
            resultado= R.mipmap.rutinaestiramiento;
        }else if (s.equals("deadBug")){
            resultado= R.mipmap.deadbug;
        }else if (s.equals("plancha")){
            resultado= R.mipmap.plancha;
        }else if (s.equals("planchaFlexion")){
            resultado= R.mipmap.planchaflexion;
        }else if (s.equals("giroRuso")){
            resultado= R.mipmap.giroruso;
        }else if (s.equals("bicycleCrunch")){
            resultado= R.mipmap.bicyclecrunch;
        }else if (s.equals("oneArmToeTouchCrunch")){
            resultado= R.mipmap.onearmtoetouchcrunch;
        }else if (s.equals("mountainClimber")){
            resultado= R.mipmap.mountainclimber;
        }else if (s.equals("reverseCrunch")){
            resultado= R.mipmap.reversecrunch;
        }else if (s.equals("sicsorsAbs")){
            resultado= R.mipmap.sicsorsabs;
        }else if (s.equals("rollingPlank")){
            resultado= R.mipmap.rollingplank;
        }

        return resultado;
    }
}
