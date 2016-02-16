package com.example.bit_user.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bit_user.myapllication.core.JSONResult;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.Reader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.github.kevinsawicki.http.HttpRequest.post;


public class checkUpActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String KEY_SIMPLE_DATA = "data";
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    private ArrayList<HashMap> lessonList;
    ArrayList<String> datalist;
    String status;
    String id;
    String position;
    String className;
    String codenum;
    String timer;
    String classNo;
    TextView check_day;
    TextView check_time;
    EditText count_timer;
    Spinner lesson_list;
    Button check_up_btn;
    TextView select_lesson;
    String strDay;
    String strTime;
    Bundle bundleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_up);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle bundleData = intent.getBundleExtra("ID_DATA");
        Log.e("login", "!!!!!!!sample" + bundleData.getString("ID"));

        if (bundleData == null) {
            Toast.makeText(this, "Bundle data is null!", Toast.LENGTH_LONG).show();
            return;
        }
        id = bundleData.getString("ID");
        position = bundleData.getString("POSITION");


        //  list_sp = (Spinner)findViewById(R.id.list_sp);
        lesson_list = (Spinner) findViewById(R.id.lesson_list);
        count_timer = (EditText) findViewById(R.id.count_timer);
        check_up_btn = (Button) findViewById(R.id.check_up_Btn);
        select_lesson = (TextView) findViewById(R.id.select_lesson);
        check_day = (TextView) findViewById(R.id.check_day);
        check_time = (TextView) findViewById(R.id.check_time);

        Date date = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        strTime = dateFormat.format(date);
        strDay = dateformat.format(date);


        check_day.setText(strDay);
        check_time.setText("TIME" + strTime);


        arrayList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setListViewData();

        lesson_list.setPrompt("강의");
        lesson_list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                className = (String) adapter.getItem(position);
                Toast.makeText(getBaseContext(), className, Toast.LENGTH_SHORT).show();
                select_lesson.setText("" + className);
                //  refresh  refresh

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        check_up_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                CheckUpTask checkUpTask = new CheckUpTask();
                checkUpTask.execute();
                timer = count_timer.getText().toString();
            }
        });

    }
    public void setListViewData(){
        LessonListTask lessonListTask = new LessonListTask();
        lessonListTask.execute();
    }
    public void addList(final ArrayList<HashMap> arrList){
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // 해당 작업을 처리함
                        for (int i = 0; i < arrList.size(); i++)

                        {
                            Log.d("addList", "addList--------------------->" + arrList.get(i) + "arrList.size()        " + arrList.size());
                            // lessonList.add(arrList.get(i));
                            adapter.add(arrList.get(i).get("CLASS_NAME").toString());
                        }
                        lesson_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();



    }
    //   public void
    public ArrayList<String> addAdapter(ArrayList<String> arrayList)
    {
        return null;
    }

    //출석(강의)리스트 구현
    private class LessonListTask extends AsyncTask<String, Void, ArrayList<HashMap>> {


        protected ArrayList<HashMap> doInBackground(String... params) {
            JSONResultString result;
            ArrayList<HashMap> arrayList1 = new ArrayList<HashMap>();
            try {

                HttpRequest request = post("http://192.168.0.5:8088/bitin/api/class/class-name-and-no");

                // reiquest 설정
                request.connectTimeout(2000).readTimeout(2000);
                // JSON  포맷으로 보내기  => POST 방식
                request.acceptCharset("UTF-8");
                request.acceptJson();
                request.accept(HttpRequest.CONTENT_TYPE_JSON);
                request.contentType("application/json", "UTF-8");

                // 데이터 세팅
                JSONObject params1 = new JSONObject();
                params1.put("userId",id.toString());

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
                Log.d("-->data",result.getData().toString());//데이터받아오기
                arrayList1 = result.getData();
                // arrayList.add(result.getData().toString());
                Log.d("ar",arrayList1.toString());
                /*for( int i=0 ; i< result.getData().size() ; i++) {
                    arrayList.add(result.getData().toString());
                }*/

                addList(arrayList1);

                return result.getData();

            } catch (Exception e3) {
                e3.printStackTrace();
            }
            return null;
        }

        private class JSONResultString extends JSONResult<ArrayList<HashMap>> {

        }

    }
    //출석체크 업로드
    private class CheckUpTask extends AsyncTask<String, Void,String> {


        protected String doInBackground(String... params) {
            JSONResultString result;
            //JSONResultString1 result1;
            try {

                HttpRequest request = post("http://192.168.0.5:8088/bitin/api/class/start-class");

                // reiquest 설정
                request.connectTimeout(2000).readTimeout(2000);
                // JSON  포맷으로 보내기  => POST 방식
                request.acceptCharset("UTF-8");
                request.acceptJson();
                request.accept(HttpRequest.CONTENT_TYPE_JSON);
                request.contentType("application/json", "UTF-8");


                // 데이터 세팅
                JSONObject params1 = new JSONObject();     params1.put("userId", id.toString());
                params1.put("className",className.toString());

                Log.d("JoinData-->", params1.toString());

                // 요청
                request.send(params1.toString());

                int responseCode = request.code();
                if (HttpURLConnection.HTTP_OK != responseCode) {
                    Log.e("HTTP fail-->", "Http Response Fail:" + responseCode);
                    return null;
                } else {
                    Log.e("HTTPRequest-->", "정상");

                }

                //4. JSON 파싱
                Reader reader = request.bufferedReader();

                result = GSON.fromJson(reader, JSONResultString.class);
                //result1 = GSON.fromJson(reader,JSONResultString1.class);
                reader.close();

                //5. 사용하기
                Log.d("---> ResponseResult-->", result.getResult());  // "success"? or "fail"?
                Log.d("--->ResponseResult-->",result.getData());
                Log.d("--->ResponseResult",result.getData2());

                codenum = result.getData();
                classNo= result.getData2();
                Log.d("Code",codenum);
                Log.d("Code",classNo);
                status  = result.getResult();
                Log.d("결과",status);

                if(status !=null && status.equals("success")) {
                    datalist = new ArrayList<String>();
                    datalist.add(id);
                    datalist.add(timer);
                    datalist.add(codenum);
                    datalist.add(className);
                    datalist.add(classNo);
                    datalist.add(position);


                    Intent intent = new Intent(getBaseContext(), checkNowActivity.class);
                    Bundle bundleData = new Bundle();
                    bundleData.putStringArrayList("DATA_LIST", datalist);
                    intent.putExtra("DATA", bundleData);
                    Log.d("데이터리스트", datalist.toString());
                    startActivity(intent);
                    finish();

                }else
                    Log.d("오류:","정확하게 입력하세요");

                return result.getData()+result.getData2();

            } catch (Exception e3) {
                e3.printStackTrace();
            }
            return null;
        }

        private class JSONResultString extends JSONResult<String> {}

    }
    private void refresh( String lesson_list ) {
        adapter.add( lesson_list ) ;
        adapter.notifyDataSetChanged() ;
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