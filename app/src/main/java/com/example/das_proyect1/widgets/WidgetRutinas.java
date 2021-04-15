package com.example.das_proyect1.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.das_proyect1.LogInActivity;
import com.example.das_proyect1.PrincipalActivity;
import com.example.das_proyect1.R;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetRutinasConfigureActivity WidgetRutinasConfigureActivity}
 */
public class WidgetRutinas extends AppWidgetProvider {

    private static final String PREFS_NAME = "com.example.das_proyect1.widgets.WidgetRutinas";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {




        Intent intent = new Intent(context, LogInActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        String buttonText = prefs.getString("keyButtonText" + appWidgetId, "Press me");

        Intent serviceIntent = new Intent(context, WidgetItem.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));


        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_rutinas);
        views.setOnClickPendingIntent(R.id.example_widget_text, pendingIntent);
        //views.setCharSequence(R.id.example_widget_button, "setText", buttonText);
        views.setRemoteAdapter(R.id.example_widget_stack_view, serviceIntent);
        views.setEmptyView(R.id.example_widget_stack_view, R.id.example_widget_empty_view);

        appWidgetManager.updateAppWidget(appWidgetId, views);


    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.d("Logs", "update widgets");
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
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}