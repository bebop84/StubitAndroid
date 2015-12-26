package com.example.bit_user.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bit_user.myapllication.core.JSONResult;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.nearby.connection.Connections;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Random;

import static com.github.kevinsawicki.http.HttpRequest.post;

public class LoginActivity extends Activity {

    public static final String TAG = "LoginActivity";
    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    public static final String KEY_SIMPLE_DATA = "data";



    Button loginButton;
    EditText Login_id;
    EditText Login_password;
    Handler handler = new Handler();
    Button joinform;
    String id;
    String password;
    String position;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        joinform=(Button) findViewById(R.id.joinform);
        loginButton = (Button) findViewById(R.id.loginbutton);
        Login_id = (EditText) findViewById(R.id.Login_id);
        Login_password = (EditText) findViewById(R.id.Login_password);

        joinform.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 버튼 누르면 회원가입 페이지로 넘기기
                Intent intent = new Intent(getBaseContext(),JoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                id = Login_id.getText().toString();
                password = Login_password.getText().toString();



                try {
                    WebTask asyncT = new WebTask();
                    asyncT.execute();
                    //Log.e("---> ", "Http Response");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("---> ", "Http Response Fail");
                }
            }
        });
    }

    public void checkSuccess(String status) {
        if(status !=null && status.equals("success")) {
            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            Bundle bundleData = new Bundle();
            bundleData.putString("ID", id);
            bundleData.putString("POSITION",position);
            intent.putExtra("ID_DATA", bundleData);
            startActivity(intent);
            finish();
        }else if(status != null && status.equals("fail")){
            Toast.makeText(getApplicationContext(), "wrong id, password",Toast.LENGTH_LONG).show();
            return;
        }
    }

    private class WebTask extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {

            try {
                //HttpClient client = new DefaultHttpClient();
                HttpRequest request = post("http://192.168.1.13:8088/bitin/api/user/loginwithusertype");
                request.connectTimeout(2000).readTimeout(2000);

                // JSON  포맷으로 보내기  => POST 방식
                request.acceptCharset("UTF-8");
                request.acceptJson();
                request.accept(HttpRequest.CONTENT_TYPE_JSON);
                request.contentType( "application/json", "UTF-8" );

                //json params
                JSONObject params1 = new JSONObject();
                params1.put("userId",id);
                params1.put("userPassword", password);
                //Log.d("--->Login :", GSON.toJson(UserVo));
                Log.d("JoinData-->", params1.toString());
                // 요청
                request.send(params1.toString());

                int responseCode = request.code();
                if (HttpURLConnection.HTTP_OK != responseCode) {
                    Log.e("--->Login :", "Http Response Fail:" + responseCode );
                    return "오류";
                }else {
                    Log.e("정상", "정상");
                }

                Reader reader = request.bufferedReader();
                JSONResultString result = GSON.fromJson(reader, JSONResultString.class);
                reader.close();

                position = result.getData();

                SharedPreferences preferences = getSharedPreferences("Setting",0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("id",id);
                editor.putString("password",password);
                editor.putString("postion",position);
                editor.commit();


                //5. 사용하기
                Log.d("---> Login --->", result.getData() );
                Log.d("---> Login --->", result.getResult() );

                status  = result.getResult();
                checkSuccess(status);
                return result.getResult();
            } catch (Exception e3) {
                e3.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // JSON 결과확인
            super.onPostExecute(result);
            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }

    private class JSONResultString extends JSONResult<String> {

    }
}
