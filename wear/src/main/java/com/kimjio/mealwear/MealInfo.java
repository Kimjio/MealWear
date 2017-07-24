package com.kimjio.mealwear;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;

public class MealInfo extends Activity {

    TextView mMealText;
    TextView mMealTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealinfo);
        Log.d("MealActivity", "sendBroadcast2!");

        mMealText = (TextView) findViewById(R.id.meal);
        mMealTitle = (TextView) findViewById(R.id.title);

        Intent I = new Intent(this, MealStubBroadcastActivity.class);
        I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(I);

    }

}
