package com.example.das_proyect1.widgets;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.das_proyect1.R;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
    //Para actualizar cada 24h
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Logs","UPDATE CON ALARMA");  //Ahora si va
        /*RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_rutinas);
        ComponentName tipowidget = new ComponentName(context, WidgetRutinas.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(tipowidget, remoteViews);
        //WidgetRutinas.updateAppWidget(context,manager,)

*/
        //Da igual la accion, siempre hay q actualizar

        Intent inten = new Intent(context, WidgetRutinas.class);
        inten.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.layout.widget_rutinas};

        inten.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(inten);

    }

}