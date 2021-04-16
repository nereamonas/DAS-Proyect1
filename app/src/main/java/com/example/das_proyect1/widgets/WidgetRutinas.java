package com.example.das_proyect1.widgets;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.das_proyect1.LogInActivity;
import com.example.das_proyect1.R;

import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetRutinasConfigureActivity WidgetRutinasConfigureActivity}
 */
public class WidgetRutinas extends AppWidgetProvider {

    private static final String PREFS_NAME = "com.example.das_proyect1.widgets.WidgetRutinas";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        //Actualizar
        Intent intent = new Intent(context, LogInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent serviceIntent = new Intent(context, WidgetItem.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_rutinas);
        views.setOnClickPendingIntent(R.id.example_widget_text, pendingIntent);
        views.setRemoteAdapter(R.id.example_widget_stack_view, serviceIntent);
        views.setEmptyView(R.id.example_widget_stack_view, R.id.example_widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.example_widget_stack_view); //Importante para q tambien se actualice la lista de las rutinas

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) { //Actualizamos todos los widgets q tengamos
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            WidgetRutinasConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        //Alarma a las 00:00
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 00); //hora en formato 24h
        calendar.set(Calendar.MINUTE, 00); //minuto
        calendar.set(Calendar.SECOND, 00); //segundo
        PendingIntent pi = PendingIntent.getBroadcast(context, 7475, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, pi);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}