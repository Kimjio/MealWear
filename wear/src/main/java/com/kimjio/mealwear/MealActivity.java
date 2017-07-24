package com.kimjio.mealwear;

import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MealActivity extends Activity {

    TextView mMealText, mMealTitle;
    public static String Meal, MealTitle;

    private static final int NUM_ROWS = 3;
    private static final int NUM_COLS = 3;

    public static Context mContext;

    MainAdapter mAdapter;
    GridViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        mPager = (GridViewPager) findViewById(R.id.fragment_container);
        mAdapter = new MainAdapter(this, getFragmentManager());
        mPager.setAdapter(mAdapter);
        mContext = getBaseContext();


        Calendar mCalendar = Calendar.getInstance();

        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);

        BapTool.restoreBapDateClass mData = BapTool.restoreBapData(this, year, month, day);

        if (mData.isBlankDay) {
            // 데이터 없음
            if (Tools.isOnline(this)) {
                // Only Wifi && Not Wifi
                if (new Preference(this).getBoolean("updateWiFi", true) && !Tools.isWifi(this)) {
                    MealTitle = this.getString(R.string.no_data_title);
                    Meal = this.getString(R.string.no_data_message);
                }
                // 급식 데이터 받아옴
                try {//sleep 사용을 위해 try 사용
                    BapDownloadTask mProcessTask = new BapDownloadTask(this);
                    //중복 처리가 되지 않도록 sleep 처리 (2초)
                    Thread.sleep(2000);
                    mProcessTask.execute(year, month, day);

                    Log.d("Meal", "BapDownloadTask!");

                } catch (InterruptedException e) {
                    //예상치 못한 오류 처리
                    Log.e("Meal","InterruptedException!");
                }

            } else {
                MealTitle = this.getString(R.string.no_data_title);
                Meal = this.getString(R.string.no_data_message);
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
                //Log.d("Meal", "onlylunch");
                //Log.d("Hour", ""+hour);
                mTitle = this.getString(R.string.lunch);
                mTodayMeal = mData.Lunch;
            } else {
                if (hour <= 9) {
                    //Log.d("Meal", "breakfast");
                    //Log.d("Hour", "h :"+ hour);
                    mTitle = this.getString(R.string.breakfast);
                    mTodayMeal = mData.Breakfast;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        //Log.d("Meal", "nobreakfast");
                        mTodayMeal = this.getString(R.string.no_data_breakfast);
                    } else {
                        //Log.d("Meal", "replacebreakfast");
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                } else if (9 <= hour && hour <= 12) {
                    //Log.d("Meal", "lunch" + hour);
                    //Log.d("Hour", "h :");
                    mTitle = this.getString(R.string.lunch);
                    mTodayMeal = mData.Lunch;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        //Log.d("Meal", "nolunch");
                        mTodayMeal = this.getString(R.string.no_data_lunch);
                    } else {
                        //Log.d("Meal", "replacelunch");
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                } else {
                    mTitle = this.getString(R.string.dinner);
                    mTodayMeal = mData.Dinner;
                    if (BapTool.mStringCheck(mTodayMeal)) {
                        mTodayMeal = this.getString(R.string.no_data_dinner);
                    } else {
                        mTodayMeal = BapTool.replaceString(mTodayMeal);
                    }
                }
            }

            //Log.d("Meal", mTodayMeal);
            Meal = mTodayMeal;
            MealTitle = mTitle;

        }
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
        }
    }

    private static class MainAdapter extends FragmentGridPagerAdapter {
        public MainAdapter(Context context, FragmentManager fragmentManager) {
            super(fragmentManager);
            mContext = context;
        }

        @Override
        public int getRowCount() {
            return NUM_ROWS;
        }

        @Override
        public int getColumnCount(int rowNum) {
            return NUM_COLS;
        }

        @Override
        public Fragment getFragment(int rowNum, int colNum) {
            return MainFragment.newInstance(rowNum, colNum);
        }

    }

    public static class MainFragment extends CardFragment {

        static Calendar mCalendar = Calendar.getInstance();

        static int year = mCalendar.get(Calendar.YEAR);
        static int month = mCalendar.get(Calendar.MONTH);
        static int day = mCalendar.get(Calendar.DAY_OF_MONTH);




        //String Meal, MealTitle;
        private static MainFragment newInstance(int rowNum, int colNum) {
            Bundle args = new Bundle();
            String[] Meal = {"","",""}, MealTitle = {"","",""};
            BapTool.restoreBapDateClass mData = BapTool.restoreBapData(mContext, year, month, day + colNum);
            String mDay="";
            if (mData.isBlankDay) {
                // 데이터 없음
                if (Tools.isOnline(mContext)) {
                    // Only Wifi && Not Wifi
                    if (new Preference(mContext).getBoolean("updateWiFi", true) && !Tools.isWifi(mContext)) {

                        MealTitle[rowNum] = mContext.getString(R.string.no_data_title);
                        Meal[rowNum] = mContext.getString(R.string.no_data_message);
                    }
                    // 급식 데이터 받아옴
                    try {//sleep 사용을 위해 try 사용
                        BapDownloadTask mProcessTask = new BapDownloadTask(mContext);
                        //중복 처리가 되지 않도록 sleep 처리 (2초)
                        Thread.sleep(2000);
                        mProcessTask.execute(year, month, day+colNum);

                        Log.d("Meal", "BapDownloadTask!");

                    } catch (InterruptedException e) {
                        //예상치 못한 오류 처리
                        Log.e("Meal", "InterruptedException!");
                    }

                } else {
                    MealTitle[rowNum] = mContext.getString(R.string.no_data_title);
                    Meal[rowNum] = mContext.getString(R.string.no_data_message);
                }
            } else {
                // 데이터 있음
                String mTitle, mTodayMeal;
                //int hour = mCalendar.get(Calendar.HOUR_OF_DAY);

                if (mData.Breakfast == null && mData.Dinner == null && mData.Lunch != null) {
                    //Log.d("Meal", "onlylunch");
                    //Log.d("Hour", ""+hour);
                    MealTitle[rowNum] = mContext.getString(R.string.lunch);
                    Meal[rowNum] = mData.Lunch;
                } else {
                        MealTitle[0] = mContext.getString(R.string.breakfast);
                        Meal[0] = mData.Breakfast;
                        if (BapTool.mStringCheck(Meal[0])) {
                            Meal[0] = mContext.getString(R.string.no_data_breakfast);
                        } else {
                            Meal[0] = BapTool.replaceString(Meal[0]);
                        }
                        //Log.d("Meal", "lunch" + hour);
                        //Log.d("Hour", "h :");

                        MealTitle[1] = mContext.getString(R.string.lunch);
                        Meal[1] = mData.Lunch;
                        if (BapTool.mStringCheck(Meal[1])) {
                            //Log.d("Meal", "nolunch");
                            Meal[1] = mContext.getString(R.string.no_data_lunch);
                        } else {
                            //Log.d("Meal", "replacelunch");
                            Meal[1] = BapTool.replaceString(Meal[1]);
                        }

                        MealTitle[2] = mContext.getString(R.string.dinner);
                        Meal[2] = mData.Dinner;
                        if (BapTool.mStringCheck(Meal[2])) {
                            Meal[2] = mContext.getString(R.string.no_data_dinner);
                        } else {
                            Meal[2] = BapTool.replaceString(Meal[2]);
                        }
                }

                //Log.d("Meal", mTodayMeal);
                //Meal[rowNum] = mTodayMeal;
                //MealTitle[rowNum] = mTitle;

            }
            switch (colNum) {
                case 0: mDay = "오늘 "; break;
                case 1: mDay = "내일 "; break;
                case 2: mDay = "모레 "; break;
            }
            args.putString(CardFragment.KEY_TITLE, mDay+MealTitle[rowNum]);
            args.putString(CardFragment.KEY_TEXT, Meal[rowNum]+(colNum+1)+"-"+(rowNum+1));

            MainFragment f = new MainFragment();
            f.setArguments(args);
            return f;
        }
    }

}
