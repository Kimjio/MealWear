package com.kimjio.mealwear;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Example shell activity which simply broadcasts to our receiver and exits.
 */
public class MealStubBroadcastActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = new Intent();
        i.setAction("com.kimjio.mealwear.SHOW_NOTIFICATION");
        //i.putExtra(MealPostNotificationReceiver.CONTENT_KEY, getString(R.string.title));
        sendBroadcast(i);
        finish();
    }
}
