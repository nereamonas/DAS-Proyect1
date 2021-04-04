package com.example.das_proyect1.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import com.example.das_proyect1.R;
import com.example.das_proyect1.ui.rutinas.RutinasFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebaseMensajes extends FirebaseMessagingService {

    public ServicioFirebaseMensajes() {

    }



    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData().size() > 0) {
            Log.d("Logs","Tamaño data recibido: "+remoteMessage.getData().size());
        }
        if (remoteMessage.getNotification() != null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.contains("notif")) { //Comprobamos si existe
                Boolean activadas = prefs.getBoolean("notif", true);  //Comprobamos si las notificaciones estan activadas
                Log.d("Logs", "estado notificaciones: " + activadas);
                if (activadas) {
                    NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel elCanal = new NotificationChannel("IdCanalRecordatorioDiario", "NombreCanal",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        elManager.createNotificationChannel(elCanal);
                    }
                    Intent intent = new Intent(this, RutinasFragment.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notif))
                            .setSmallIcon(R.drawable.ic_notif)
                            .setContentTitle(remoteMessage.getNotification().getTitle())
                            .setContentText(remoteMessage.getNotification().getBody())
                            .setVibrate(new long[]{0, 1000, 500, 1000})
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent);

                    elManager.notify(1, elBuilder.build());
                    Log.d("Logs", "Mensaje: Notificacion recibida: " + remoteMessage.getNotification().getBody());
                }
            }
        }
    }
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

}
