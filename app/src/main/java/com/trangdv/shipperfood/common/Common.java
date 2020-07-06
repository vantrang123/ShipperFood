package com.trangdv.shipperfood.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;
import com.trangdv.shipperfood.R;
import com.trangdv.shipperfood.model.Order;
import com.trangdv.shipperfood.remote.IGeoCoordinates;
import com.trangdv.shipperfood.remote.RetrofitClient;
import com.trangdv.shipperfood.model.Request;
import com.trangdv.shipperfood.model.Shipper;
import com.trangdv.shipperfood.model.ShippingInformation;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Common {
    public static final String SHIPPER_TABLE ="Shippers" ;
    public static final String ORDER_NEED_SHIP_TABLE = "OrdersNeedShip";
    public static final String SHIPPER_INFO_TABLE = "ShippingOrders";
    public static final int REQUEST_CODE = 1000;
    public static final String API_KEY = "1234";
//    public static final String API_ANNGON_ENDPOINT = "http://192.168.43.205:3000";
    public static final String API_ANNGON_ENDPOINT = "http://192.168.1.7:3000";
    public static final String fcmUrl = "https://fcm.googleapis.com/";
    public static final String NOTIFI_TITLE = "title";
    public static final String NOTIFI_CONTENT = "content";
    public static final String REMENBER_FBID = "REMENBER_FBID";
    public static final String API_KEY_TAG = "API_KEY";

    public static Shipper currentShipper;
    public static Request currentRequest;
    public static Order currentOrder;
    public static String currentOrderFBID;
    public static int curentRestaurantId;

    public static final String baseURL = "https://maps.googleapis.com/";

    public static String DISTANCE= "";
    public static String DURATION= "";
    public static String ESTIMATED_TIME = "";

    public static String convertCodeToStatus(int code) {
        switch (code) {
            case 1:
            case 0:
                return "Cần giao";
            case 4:
                return "Đang giao";
            case 5:
                return "Đã giao";
            case 2:
                return " Đăng ký giao";
            case 3:
                return "Đã chấp nhận";
            case -1:
                return "Đã hủy";
            default:
                return "";
        }
    }

    public static void showNotification(Context context, int notiId, String title, String body, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context, notiId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String NOTIFICATION_CHANNEL_ID = "an_ngon";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "An Ngon Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("An Ngon Client App");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.app_icon));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification mNotification = builder.build();
        notificationManager.notify(notiId, mNotification);


    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float)bitmap.getWidth();
        float scaleY = newHeight / (float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    public static String getTopicChannel(int restaurantId) {
        return new StringBuilder("Restaurant_").append(restaurantId).toString();
    }

    public static String createTopicSender(String topicChannel) {
        return new StringBuilder("/topics/").append(topicChannel).toString();
    }

    public static void animateStart(Context context) {
        Activity act = (Activity)context;
        act.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    public static void animateFinish(Context context) {
        Activity act = (Activity)context;
        act.overridePendingTransition(0, R.anim.right_to_left);
    }
}
