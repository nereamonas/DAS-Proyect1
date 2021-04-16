package com.example.das_proyect1.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;


import com.example.das_proyect1.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class WidgetItem extends RemoteViewsService {

    //El item, osea las ventanitas q se pueden ir pasando y muestran cada una una rutina completada

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ExampleWidgetItemFactory(getApplicationContext(), intent);
    }

    class ExampleWidgetItemFactory implements RemoteViewsFactory {
        private Context context;
        private int appWidgetId;
        private int num;
        private ArrayList<String[]> lista;

        ExampleWidgetItemFactory(Context context, Intent intent) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

            SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            int i=Integer.parseInt(prefs.getString("keyButtonText","1"));
            Log.d("Logs",""+i);
            this.num = 2;//Integer.parseInt(i);//Integer.parseInt(String.valueOf(prefs.getInt("keyButtonText" + appWidgetId,1)));

        }
        @Override
        public void onCreate() {
            //connect to data source
            rellenarLista(); //Al crear rellenamos la lista
            SystemClock.sleep(3000);
        }
        @Override
        public void onDataSetChanged() {
            rellenarLista();//Cuando se produja algun cambio actualizamos la lista
        }


        @Override
        public void onDestroy() {
            //close data source
        }
        @Override
        public int getCount() {
            return lista.size();
        }
        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgets_item);
            views.setTextViewText(R.id.textViewWidNumero, ""+position);
            views.setTextViewText(R.id.textViewWidUsuario, lista.get(position)[0]);
            views.setTextViewText(R.id.textViewWidTituloRut, lista.get(position)[1]);
            views.setTextViewText(R.id.textViewWidFechaHora, "Fecha: "+lista.get(position)[2]);

            SystemClock.sleep(500);

            return views;
        }
        @Override
        public RemoteViews getLoadingView() {
            return null;
        }
        @Override
        public int getViewTypeCount() {
            return 1;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        @Override
        public boolean hasStableIds() {
            return true;
        }

        public void rellenarLista(){
            //Cogemos la fecha actual
            Calendar cal = Calendar.getInstance();
            String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
            String YEAR = String.valueOf(cal.get(Calendar.YEAR));
            String MONTH = String.valueOf(cal.get(Calendar.MONTH));
            String date=" "+DAY+"/"+MONTH+"/"+YEAR+" ";

            String date2="________";
            String date3="________";

            BufferedReader ficherointerno;

            lista= new ArrayList<String[]>(); //Creamos una lista
            {
                try {
                    ficherointerno = new BufferedReader(new InputStreamReader(openFileInput("rutinasCompletadas.txt"))); //Abrimos el fichero donde esta la info
                    String linea= ficherointerno.readLine();
                    if (num==2){ //Si hemos decidido mostrar las rut de los ultimos dos dias:
                        String DAY2 = String.valueOf(cal.get(Calendar.DAY_OF_MONTH-1));
                        date2=" "+DAY2+"/"+MONTH+"/"+YEAR+" ";
                    }if(num==3){//Si hemos decidido mostrar las rut de los ultimos tres dias:
                        String DAY3 = String.valueOf(cal.get(Calendar.DAY_OF_MONTH-2));
                        date3=" "+DAY3+"/"+MONTH+"/"+YEAR+" ";
                    }
                    while (linea!=null){ //Por cada linea del fichero
                        if(linea.contains(date)||linea.contains(date2)||linea.contains(date3)) { //Si la linea, pertenece al dia actual, o el anterior o el anterior
                            String usuario=linea.split(":")[0].split("- ")[1]; //Cogemos el nombre de usuario q la ha completado
                            String a=linea.split("la rutina ")[1];
                            String[] e=a.split(" el día ");
                            String tituloRut=e[0]; //Cogemos el titulo de la rutina
                            String[] h=e[1].split(" a las ");
                            String fechaHora=h[0]+" "+h[1]; //Cogemos la fecha en la q se ha completado
                            String[] dato= {usuario,tituloRut,fechaHora}; //Creamos un string con los datos
                            lista.add(dato); //Añadimos los datos a la lista
                        }
                        linea = ficherointerno.readLine();
                    }
                    ficherointerno.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}