package app.taxipizza.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app.taxipizza.R;
import app.taxipizza.Utils.Utils;
import app.taxipizza.activities.LeaveCommentActivity;
import app.taxipizza.activities.TrackOrderActivity;
import app.taxipizza.models.Request;

public class ListenOrderUpdateService extends Service{

    public ListenOrderUpdateService() {
    }

    FirebaseDatabase db;
    DatabaseReference requests;

    String orderId;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startServiceForeground(intent, flags, startId);
        if(intent != null) {
            orderId = intent.getStringExtra("orderId");
            requests.child(orderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Request request = dataSnapshot.getValue(Request.class);
                    //TODO Error here
                    showNotification(orderId, request);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return START_STICKY;
    }

    public int startServiceForeground(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, ListenOrderUpdateService.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("File Observer Service")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(300, notification);

        return START_STICKY;
    }

    private void showNotification(String key, Request request) {
        Intent trackOrder = new Intent(getBaseContext(), TrackOrderActivity.class);
        trackOrder.putExtra("orderId", key);
        trackOrder.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent trackOrderIntent = PendingIntent.getActivity(getBaseContext(), 0, trackOrder, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent comment = new Intent(getBaseContext(), LeaveCommentActivity.class);
        comment.putExtra("orderId", key);
        comment.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent commentIntent = PendingIntent.getActivity(getBaseContext(), 0, comment, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_restaurant_white_24dp)
                .setContentInfo("Info");
        if (request != null) {
            if (request.getStatus().equals("0")) {
                builder.setContentText("Commande #" + key);
                builder.setContentTitle("Votre commande a été envoyée.");
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = builder.build();
                notificationManager.notify(1, notification);

            } else if (request.getStatus().equals("2")) {
                builder.setContentText("Cliquez pour suivre votre commande");
                builder.setContentTitle("Commande #" + key + " est " + Utils.convertCodeToStatus(request.getStatus()));
                builder.setContentIntent(trackOrderIntent);
                builder.setOngoing(true);
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(1, notification);

            } else if (request.getStatus().equals("3")) {
                builder.setContentText("Cliquez pour suivre votre commande");
                builder.setContentTitle("Commande #" + key + " est " + Utils.convertCodeToStatus(request.getStatus()));
                builder.setContentIntent(trackOrderIntent);
                builder.setOngoing(true);
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(1, notification);
            } else if (request.getStatus().equals("4")) {
                builder.setContentText("Cliquez pour laisser un commentaire");
                builder.setContentTitle("On vous souhaite un bon appétit \ud83d\ude0b");
                builder.setContentIntent(commentIntent);
                builder.setOngoing(true);
                NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification = builder.build();
                notificationManager.notify(1, notification);
            }
        }
    }
}
