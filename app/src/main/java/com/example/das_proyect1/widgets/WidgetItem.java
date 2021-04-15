package com.example.das_proyect1.widgets;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.annotation.NonNull;

import com.example.das_proyect1.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

public class WidgetItem extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ExampleWidgetItemFactory(getApplicationContext(), intent,1);
    }
    class ExampleWidgetItemFactory implements RemoteViewsFactory {
        private Context context;
        private int appWidgetId;
        private int num;

        Calendar cal = Calendar.getInstance();
        String DAY = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String YEAR = String.valueOf(cal.get(Calendar.YEAR));
        String MONTH = String.valueOf(cal.get(Calendar.MONTH));
        String date=" "+DAY+"/"+MONTH+"/"+YEAR+" ";


        String date2=" ";
        String date3=" ";

        BufferedReader ficherointerno;

        private ArrayList<String[]> lista= new ArrayList<String[]>();
        {
            try {
                ficherointerno = new BufferedReader(new InputStreamReader(openFileInput("rutinasCompletadas.txt")));
                String linea= ficherointerno.readLine();
                if (num==2){
                    String DAY2 = String.valueOf(cal.get(Calendar.DAY_OF_MONTH-1));
                    String date2=" "+DAY2+"/"+MONTH+"/"+YEAR+" ";
                }if(num==3){
                    String DAY3 = String.valueOf(cal.get(Calendar.DAY_OF_MONTH-2));
                    String date3=" "+DAY3+"/"+MONTH+"/"+YEAR+" ";
                }
                while (linea!=null){

                    if(linea.contains(date)&&linea.contains(date2)&&linea.contains(date3)) { //Si la linea, pertenece al dia actual,
                       // text.setText(text.getText() + "\n\n" + linea);
                        //- nerea: Has completado la rutina Ejercicios abdominales el día 15/03/2021 a las 18:15
                        String usuario=linea.split(":")[0].split("- ")[1];
                        String a=linea.split("la rutina ")[1];
                        String[] e=a.split(" el día ");
                        String tituloRut=e[0];
                        String[] h=e[1].split(" a las ");
                        String fechaHora=h[0]+" "+h[1];
                        String[] dato= {usuario,tituloRut,fechaHora};
                        Log.d("Logs", usuario+"  "+tituloRut+"  "+fechaHora);
                        lista.add(dato);
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



        ExampleWidgetItemFactory(Context context, Intent intent, int num) {
            this.context = context;
            this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            this.num=num;
        }
        @Override
        public void onCreate() {
            //connect to data source
            SystemClock.sleep(3000);
        }
        @Override
        public void onDataSetChanged() {
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

            //views.setTextViewText(R.id.example_widget_item_text, "Fecha: "+lista.get(position)[2]);
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
    }
}