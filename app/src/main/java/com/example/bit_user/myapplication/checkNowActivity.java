package com.example.bit_user.myapplication;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bit_user.myapllication.core.JSONResult;
import com.example.bit_user.myapllication.core.SafeAsyncTask;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.github.kevinsawicki.http.HttpRequest.post;


public class  checkNowActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,OnClickListener {


    public static final String KEY_SIMPLE_DATA = "data";
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    int i=-1;
    String id;
    String position;
    String lesson;
    String endTime;
    String endDate;
    int count_timer;
    String codenum;
    String classNo;
    String timer;
    Bundle bundleData;
    TextView code_num;
    TextView count;
    Button start_btn;
    ProgressBar mProgressBar;
    ArrayList<String> datalist;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private CountDownTimer countDownTimer; // built in android class
    // CountDownTimer
    private long totalTimeCountInMilliseconds; // total count down time in
    // milliseconds
    private long timeBlinkInMilliseconds; // start time of start blinking
    private boolean blink; // controls the blinking .. on and off

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_now);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        datalist = new ArrayList<String>();
        bundleData = intent.getBundleExtra("DATA");


        datalist = bundleData.getStringArrayList("DATA_LIST");


        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd a hh:mm:ss", java.util.Locale.getDefault());
        endTime = dateformat.format(date);


        code_num = (TextView) findViewById(R.id.code_num);
        count = (TextView) findViewById(R.id.count);
        start_btn = (Button) findViewById(R.id.start_btn);

        arrayList = new ArrayList<String>();

        id = datalist.get(0);
        position = datalist.get(5);

        codenum = datalist.get(2);//앞 엑티비티에서 인증번호 값 받아오기
        code_num.setText("" + codenum);//TextView에 인증번호 띄우기

        classNo = datalist.get(4);
        timer = datalist.get(1);//앞 엑티비티에서 타이머 값 받아오기
        //count.setText(timer);
        count.setText(timer + "분");
        start_btn.setOnClickListener(this);

    }
    public void onClick(View v) {
        TimerData timerData = new TimerData();
        timerData.execute();

        if(v.getId() == R.id.start_btn){
            setTimer();

            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            start_btn.setVisibility(View.GONE);
            startTimer();
        }
    }



    private void setTimer() {

        count_timer = 0;
        if (!timer.toString().equals("")) {
            count_timer = Integer.parseInt(timer);
        } else
            Toast.makeText(checkNowActivity.this, "Please Enter Minutes...",
                    Toast.LENGTH_LONG).show();
        totalTimeCountInMilliseconds = 60 * count_timer * 1000;
        timeBlinkInMilliseconds = 30 * 1000;
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 500) {
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;
                if (leftTimeInMilliseconds < timeBlinkInMilliseconds) {
                    if (blink) {
                        count.setVisibility(View.VISIBLE);
                        // if blink is true, textview will be visible
                    } else {
                        count.setVisibility(View.INVISIBLE);
                    }

                    blink = !blink; // toggle the value of blink
                }

                count.setText(String.format("%02d", seconds / 60)
                        + ":" + String.format("%02d", seconds % 60));

            }

            @Override
            public void onFinish() {
                count.setText("Time up!");
                count.setVisibility(View.VISIBLE);
                start_btn.setVisibility(View.VISIBLE);

                if(count.toString().equals("Time up!")) {

                    Intent intent = new Intent(getBaseContext(), checkListActivity.class);
                    Bundle bundleData = new Bundle();
                    bundleData.putString("ID", id);
                    intent.putExtra("ID_DATA", bundleData);
                    startActivity(intent);
                    finish();

                }

            }

        }.start();

    }

    public class TimerData extends SafeAsyncTask<ArrayList<String>> {
        @Override
        //1.data 통신
        public ArrayList<String>  call() throws Exception {
            JSONResultString result = null;
            ArrayList<String> arrayList1 = new ArrayList<String>();
            try {

                HttpRequest request = post("http://192.168.0.5:8088/bitin/api/class/start-attd ");

                // reiquest 설정
                request.connectTimeout(2000).readTimeout(2000);
                // JSON  포맷으로 보내기  => POST 방식
                request.acceptCharset("UTF-8");
                request.acceptJson();
                request.accept(HttpRequest.CONTENT_TYPE_JSON);
                request.contentType("application/json", "UTF-8");

                // 데이터 세팅
                JSONObject params1 = new JSONObject();
                params1.put("classNo",classNo);
                params1.put("startTime",endTime);
                params1.put("timer",count_timer);


                Log.d("JoinData-->", params1.toString());
                request.send(params1.toString());

                // 3. 요청
                int responseCode = request.code();
                if (HttpURLConnection.HTTP_OK != responseCode) {
                    Log.e("HTTP fail-->", "Http Response Fail:" + responseCode);
                    return null;
                } else {
                    Log.e("HTTPRequest-->", "정상");

                }

                //4. JSON 파싱
                Reader reader = request.bufferedReader();
                //Log.d("Reader",reader);
                result = GSON.fromJson(reader, JSONResultString.class);
                reader.close();

                //5. 사용하기
                Log.d("---> ResponseResult-->", result.getResult());  // "success"? or "fail"?
                Log.d("--->Data-->",result.getData().toString());

                return result.getData();

            } catch (Exception e3) {
                e3.printStackTrace();
            }
            return result.getData();

        }
        //2.오류시
        @Override
        protected void onException(Exception e) throws RuntimeException {
            super.onException(e);
        }

        protected void onSuccess(ArrayList<String> String) throws Exception {

            super.onSuccess(String);

            for(int i = 0; i<String.size(); i++){

                arrayList.add(String.get(i).toString());
                Log.d(" arrayList",arrayList.toString());
            }


        }
        private class JSONResultString extends JSONResult<ArrayList<String>> {

        }

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int getid = item.getItemId();

        if (getid == R.id.nav_home) {
            Intent intent1 = new Intent(this, MenuActivity.class);
            bundleData = new Bundle();
            bundleData.putString("ID",id);
            bundleData.putString("POSITION",position);

            intent1.putExtra("ID_DATA", bundleData);
            startActivity(intent1);

            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
        } else if (getid == R.id.nav_checkup) {
            Intent intent2 = new Intent(this, checkUpActivity.class);
            bundleData = new Bundle();
            bundleData.putString("ID",id);
            bundleData.putString("POSITION",position);
            intent2.putExtra("ID_DATA", bundleData);
            startActivity(intent2);
            Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();

        } else if (getid == R.id.nav_checkNow) {
            Intent intent3 = new Intent(this, checkNowActivity.class);
            bundleData = new Bundle();
            bundleData.putString("ID",id);
            bundleData.putString("POSITION",position);
            intent3.putExtra("ID_DATA", bundleData);
            startActivity(intent3);
            Toast.makeText(this, "nav_checkList", Toast.LENGTH_SHORT).show();
        } else if (getid == R.id.nav_ThChecklist) {
            Intent intent3 = new Intent(this, checkListActivity.class);
            bundleData = new Bundle();
            bundleData.putString("ID",id);
            bundleData.putString("POSITION",position);
            intent3.putExtra("ID_DATA", bundleData);
            startActivity(intent3);
            Toast.makeText(this, "nav_checkList", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
