package com.example.das_proyect1.serviceBroadcast;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;
import com.example.das_proyect1.ui.calendario.CalendarioFragment;
import com.example.das_proyect1.widgets.WidgetRutinas;

public class AlarmNotificationManagerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("Logs","notif CON ALARMA");  //Ahora si va

        if (prefs.contains("notif")) { //Comprobamos si existe
            Boolean activadas = prefs.getBoolean("notif", true);  //Comprobamos si las notificaciones estan activadas
            Log.d("Logs", "estado notificaciones: "+activadas);
            if (activadas) {

                NotificationManager elManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(context, "IdCanal");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    elManager.createNotificationChannel(elCanal);
                }
                elBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_notif))
                        .setSmallIcon(R.drawable.ic_notif)
                        .setContentTitle(intent.getStringExtra("titulo"))  //cogemos el mensaje que hemos escrito antes, para q sea personalizado
                        //.setContentText(context.getString(R.string.notif_cuerpo_hassuperadoelentrena))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setLights(Color.BLUE, 200, 200)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

                elManager.notify(1, elBuilder.build());
            }
        }

    }

}