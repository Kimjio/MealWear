package com.kimjio.mealwear;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class MealBroadCast extends BroadcastReceiver {

    @Override
    public void onReceive(Context mContext, Intent mIntent) {
        String ACTION = mIntent.getAction();

        Intent i = new Intent();
        i.setAction("com.kimjio.mealwear.SHOW_NOTIFICATION");
        Log.d("Mealboot_onReceive", "sendBroadcast!");
        mContext.sendBroadcast(i);
        //BapWidget.updateAllBapWidget(mContext);

        if (Intent.ACTION_BOOT_COMPLETED.equals(ACTION)) {
            // 1시간마다 앱 위젯 업데이트하기
            Calendar mCalendar = Calendar.getInstance();
            AlarmManager mAlarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

            Intent mIntentDate = new Intent(mContext, MealBroadCast.class);

            PendingIntent mPending = PendingIntent.getBroadcast(mContext, 0, mIntentDate, 0);
            mCalendar.set(Calendar.SECOND, mCalendar.get(Calendar.SECOND));
            Log.d("Mealboot_BOOT_COMPLETED", "setInexactRepeating!");
            mAlarm.setRepeating(AlarmManager.RTC_WAKEUP, mCalendar.getTimeInMillis(), 60*60*1000 ,mPending);
        }
    }
}
