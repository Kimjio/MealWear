package com.kimjio.mealwear;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.htmlparser.jericho.Source;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class SchoolSelectActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener {

    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;

    private GoogleApiClient mGoogleApiClient;
    private PutDataMapRequest mDataMap;

    String TAG = "SchoolSelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_select);

        mGoogleApiClient = new GoogleApiClient.Builder(SchoolSelectActivity.this).addApi(Wearable.API).
                addConnectionCallbacks(SchoolSelectActivity.this).
                addOnConnectionFailedListener(SchoolSelectActivity.this).build();

        new Thread() {
            public void run() {
                main();
            }
        }.start();

        // Android에서 제공하는 string 문자열 하나를 출력 가능한 layout으로 어댑터 생성

        // Xml에서 추가한 ListView 연결
        mListView = (ListView) findViewById(R.id.schoollist);

        mAdapter = new ListViewAdapter(this);

        // ListView에 어댑터 연결
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ListData mData = mAdapter.mListData.get(position);
                Toast.makeText(SchoolSelectActivity.this, mData.mSchoolName + mData.mOrgCode, Toast.LENGTH_SHORT).show();

        mDataMap = PutDataMapRequest.create("/orgcode");

        mDataMap.getDataMap().putString("orgCode", mData.mOrgCode);

        PutDataRequest request = mDataMap.asPutDataRequest();

        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, request);

            }
        });


    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: " + connectionHint);
        // Now you can use the Data Layer API
    }
    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "onConnectionFailed: " + result);
        }

    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/count") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    //updateCount(dataMap.getInt(COUNT_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }


    public String getHtmltoText(String sourceUrlString) {

        Source source = null;

        String content = null;

        try {
            source = new Source(new URL(sourceUrlString));
            source.fullSequentialParse();
            content = source.getSource().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public void main() {


        String yourUrl = "http://par.gbe.kr/spr_ccm_cm01_100.do?kraOrgNm=%EC%B2%AD%ED%95%98";

        try {

            JSONParser jsonParser = new JSONParser();

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = (JSONObject) jsonParser.parse(getHtmltoText(yourUrl));

            JSONObject resultObject = (JSONObject) jsonObject.get("resultSVO");
            Log.d("School", "List : " + jsonObject.get("resultSVO"));

            JSONObject dataObject = (JSONObject) resultObject.get("data");

            JSONArray schoolInfoArray = (JSONArray) dataObject.get("orgDVOList");


            for (int i = 0; i < schoolInfoArray.size(); i++) {

                //배열 안에 있는것도 JSON형식이기 때문에 JSON Object로 추출
                JSONObject schoolObject = (JSONObject) schoolInfoArray.get(i);

                //JSON name으로 추출
                Log.d("School", "orgCode : " + schoolObject.get("orgCode"));
                Log.d("School", "kraOrgNm :" + schoolObject.get("kraOrgNm"));
                Log.d("School", "zipAdres :" + schoolObject.get("zipAdres"));
                Log.d("School", "schulKndScCode :" + schoolObject.get("schulKndScCode"));
                Log.d("School", "schulCrseScCode :" + schoolObject.get("schulCrseScCode"));
                Log.d("School", "schulCrseScCodeNm :" + schoolObject.get("schulCrseScCodeNm"));

                mAdapter.addItem("" + schoolObject.get("kraOrgNm"), "" + schoolObject.get("zipAdres"), "" + schoolObject.get("orgCode"), "" + schoolObject.get("schulKndScCode"), "" + schoolObject.get("schulCrseScCode"));

            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private class ViewHolder {

        public TextView mSchoolName;

        public TextView mZipAdress;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public void addItem(String mSchoolName, String mZipAdres, String mOrgCode, String mSchulKndScCode, String mSchulCrseScCode) {
            ListData addInfo = null;
            addInfo = new ListData();
            addInfo.mSchoolName = mSchoolName;
            addInfo.mZipAdres = mZipAdres;
            addInfo.mOrgCode = mOrgCode;
            addInfo.mSchulKndScCode = mSchulKndScCode;
            addInfo.mSchulCrseScCode = mSchulCrseScCode;

            mListData.add(addInfo);
        }

        public void remove(int position) {
            mListData.remove(position);
            dataChange();
        }

        public void sort() {
            Collections.sort(mListData, ListData.ALPHA_COMPARATOR);
            dataChange();
        }

        public void dataChange() {
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_school_list, null);

                holder.mSchoolName = (TextView) convertView.findViewById(R.id.mSchoolName);
                holder.mZipAdres = (TextView) convertView.findViewById(R.id.mZipAdres);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ListData mData = mListData.get(position);

            holder.mSchoolName.setText(mData.mSchoolName);
            holder.mZipAdress.setText(mData.mZipAdres);

            return convertView;
        }
    }

    // 데이터 삭제
    public void clearApplicationData() {
        File cache = getCacheDir();
        try {
        } catch (Exception e) {
        }
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib") && !(s.equals("shared_prefs"))) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty or this is a file so delete it
        return dir.delete();
    }
}
