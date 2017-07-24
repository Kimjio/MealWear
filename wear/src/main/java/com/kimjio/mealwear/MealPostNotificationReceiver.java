package com.kimjio.mealwear;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MealPostNotificationReceiver extends BroadcastReceiver {
    //public static final String CONTENT_KEY = "contentText";

    String Meal, MealTitle;

    public MealPostNotificationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Intent displayIntent = new Intent(context, MealInfo.class);
        //String text = intent.getStringExtra(CONTENT_KEY);

        Log.d("Meal", "Start!");

        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        BapTool.restoreBapDateClass mData = BapTool.restoreBapData(context, year, month, day);

        if (mData.isBlankDay) {
            // 데이터 없음
            if (Tools.isOnline(context)) {
                // Only Wifi && Not Wifi
                if (new Preference(context).getBoolean("updateWiFi", true) && !Tools.isWifi(context)) {
                    MealTitle = context.getString(R.string.no_data_title);
                    Meal = context.getString(R.string.no_data_message);
                }
                // 급식 데이터 받아옴
                try {//sleep 사용을 위해 try 사용
                    BapDownloadTask mProcessTask = new BapDownloadTask(context);
                    //중복 처리가 되지 않도록 sleep 처리 (2초)
                    Thread.sleep(2000);
                    mProcessTask.execute(year, month, day);

                    Log.d("Meal", "BapDownloadTask!");

                } catch (InterruptedException e) {
                    //예상치 못한 오류 처리
                    Log.e("Meal","InterruptedException!");
                }

                    Intent i = new Intent();

                    i.setAction("com.kimjio.mealwear.SHOW_NOTIFICATION");
                    Log.d("Meal", "sendBroadcast!");

                    context.sendBroadcast(i);
            } else {
                MealTitle = context.getString(R.string.no_data_title);
                Meal = context.getString(R.string.no_data_message);
            }
        } else {
            // 데이터 있음

            /**
             * hour : 0~23
             *
             * 0~13 : Lunch
             * 14~23 : Dinner
             */
            String mTitle, mTodayMeal;
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

            if (mData.Breakfast == null && mData.Dinner == null && mData.Lunch != null) {
                Log.d("Meal", "onlylunch");
                Log.d("Hour", ""+hour);
                mTitle = context.getString(R.string.lunch);
                mTodayMeal = mData.Lunch;
            } else {
                if (hour <= 9) {
                    Log.d("Meal", "breakfast");
                    Log.d("Hour", "h :"+ hour);
                    mTitle = context.getString(R.string.breakfast);
                    mTodayMeal = mData.Breakfast;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        Log.d("Meal", "nobreakfast");
                        mTodayMeal = context.getString(R.string.no_data_breakfast);
                    } else {
                        Log.d("Meal", "replacebreakfast");
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                } else if (9 <= hour && hour <= 12) {
                    //Log.d("Meal", "lunch" + hour);
                    Log.d("Hour", "h :");
                    mTitle = context.getString(R.string.lunch);
                    mTodayMeal = mData.Lunch;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        Log.d("Meal", "nolunch");
                        mTodayMeal = context.getString(R.string.no_data_lunch);
                    } else {
                        Log.d("Meal", "replacelunch");
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                } else {
                    mTitle = context.getString(R.string.dinner);
                    mTodayMeal = mData.Dinner;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        mTodayMeal = context.getString(R.string.no_data_dinner);
                    } else {
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                }
            }

            Log.d("Meal", mTodayMeal);
            Meal = mTodayMeal;
            MealTitle = mTitle;

        }
        Log.d("Meal", "meal");

        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(MealTitle)
                .setContentText(Meal)
                .setStyle(new Notification.BigTextStyle())
                //.extend(new Notification.WearableExtender()
                        //.setDisplayIntent(PendingIntent.getActivity(context, 0, displayIntent,
                         //       PendingIntent.FLAG_UPDATE_CURRENT)))
                .build();
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, notification);

        //Toast.makeText(context, context.getString(R.string.notification_posted), Toast.LENGTH_SHORT).show();

    }

    public static class BapDownloadTask extends ProcessTask {
        Context mContext;

        public BapDownloadTask(Context mContext) {
            super(mContext);
            this.mContext = mContext;
        }

        @Override
        public void onPreDownload() {
        }

        @Override
        public void onUpdate(int progress) {
        }

        @Override
        public void onFinish(long result) {
            Intent mIntent = new Intent(mContext, MealBroadCast.class);
            mContext.sendBroadcast(mIntent);
        }
    }
}
