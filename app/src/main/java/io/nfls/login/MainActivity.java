package io.nfls.login;

import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private LoginIO mTask;
    private TokenCHK CHKTask;
    private TextView textView;
    String username="";
    String password="";
    String contentUser = "";
    String contentPass ="";
    String contentEnd="";
    String contentTerrible="";
    int id =0;
    int code=0;
    String token = "";
    String tokenExisted = "";
    String status = "";
    String message = "";
    boolean success=false;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary =  "WebKitFormBoundary7MA4YWxkTrZu0gW";
    String json="";
    //private List<NameValuePair> data = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

    }

    private void initView(){
        SharedPreferences read = getSharedPreferences("lock", MODE_PRIVATE);
        tokenExisted = read.getString("token", "");
        System.out.println(tokenExisted);
        if (!tokenExisted.equals("")){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Login")
                    .setMessage("呦～等一会吧")
                    .show();
            CHKTask = new TokenCHK();
            CHKTask.execute();
        } else {
            setContentView(R.layout.activity_main);
            findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    EditText editTextUsername = (EditText) findViewById(R.id.Username);
                    username = editTextUsername.getText().toString();
                    EditText editTextPassword = (EditText) findViewById(R.id.Password);
                    password = editTextPassword.getText().toString();
                    login(username, password);

                }

            });
        }
    }
    public void login(String username, String password) {
        //Toast.makeText(MainActivity.this, username + password, Toast.LENGTH_SHORT).show();
        /*
        try {
            // 首先最外层是{}，是创建一个对象
            JSONObject UserLogin = new JSONObject();
            // 第一个键phone的值是数组，所以需要创建数组对象
            //JSONArray phone = new JSONArray();
            //phone.put("12345678").put("87654321");
            //UserLogin.put("phone", phone);

            UserLogin.put("username", username);
            UserLogin.put("password", password);
            content = String.valueOf(UserLogin);
            System.out.println(content);
        } catch (JSONException ex) {
            // 键为null或使用json不支持的数字格式(NaN, infinities)
            throw new RuntimeException(ex);
        }
        */
        // Add your data
        //writer = new BufferedWriter(new OutputStreamWriter(dataOutputStream, "UTF-8"));
        //new AlertDialog.Builder(this).setTitle("登陆").setMessage("登陆中").show();
        //System.out.println(content);
        mTask = new LoginIO();
        mTask.execute();
        //System.out.println("ok");
    }
    private void startOtherActivity()
    {
        startActivity(new Intent(MainActivity.this,DetailedUserInformation.class));
       finish();

    }


   private class LoginIO extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://app.nfls.io/API/User/User.php?action=UserLogin");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                //System.out.println(4);
                json = EntityUtils.toString(response.getEntity());

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                System.out.println("Protool Error");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.println("IO Error");
            }
            System.out.println(json);
            System.out.println(1);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                //.getJSONObject("");
                System.out.println(jsonObject);
            } catch (JSONException ex) {
                //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("Server Error");
            }


            try {
                status=jsonObject
                        .getString("status");
            }catch (JSONException ex) {
                //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("JSON fucked");

            }
            System.out.println(status);

            if (status.equals("success")){

                try {
                    System.out.println("dealing with json");

                    token = jsonObject
                            .getString("token");
                    //System.out.println(token);
                    // id = jsonObject
                    //   .optInt("userId");
                    //System.out.println("FUCKING JSON");
                    success=true;
                } catch (JSONException ex) {
                    //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    System.out.println("JSON fucked");

                }
                //Toast.makeText(MainActivity.this, token + id, Toast.LENGTH_LONG).show();
                //System.out.println(token+"--"+id);




            }else {
                try {


                    message = jsonObject
                            .getString("message");
                    //System.out.println(token);
                    // id = jsonObject
                    //   .optInt("userId");
                    //System.out.println("FUCKING JSON");
                    //success=true;
                } catch (JSONException ex) {
                    //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    System.out.println("JSON fucked");

                }
            }




            /*String urlPath = "https://app.nfls.io/API/User/User.php?action=UserLogin";
            URL url = null;
            try {
                url = new URL(urlPath);
            } catch (MalformedURLException ex) {
                //System.out.println("666");
              // Toast.makeText(MainActivity.this, "666", Toast.LENGTH_SHORT).show();
            }
            //System.out.println("fuck");
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
            } catch (IOException ex) {

            }
            conn.setConnectTimeout(5000);
            try {
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
            } catch (ProtocolException ex) {
               // Toast.makeText(MainActivity.this, "ProtocolException", Toast.LENGTH_SHORT).show();
                System.out.println("bad protocol");
            }
            //conn.setRequestProperty("ser-Agent", "Fiddler");
            //conn.setRequestProperty("Content-Type", "multipart/form-data");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=--" + boundary);
            //conn.setRequestProperty("username",username);
            //conn.setRequestProperty("password",password);
            contentPass="Content-Disposition: form-data; name\"=password\"" + lineEnd+ password +lineEnd;
            contentUser="Content-Disposition: form-data; name=\"username\"" + lineEnd+ username +lineEnd;
            contentEnd=twoHyphens + boundary + lineEnd;
            contentTerrible=contentEnd+contentUser+contentEnd+contentPass;

            try {
                OutputStream os = conn.getOutputStream();

               // System.out.println(0);
                os.write(contentTerrible.getBytes());

                os.write(contentUser.getBytes());

                os.write(contentEnd.getBytes());
                os.write(contentPass.getBytes());

                os.flush();
               //System.out.println(1);
                os.close();
                code = conn.getResponseCode();
                //System.out.println(code);
                //System.out.println(3);

                //InputStream is = conn.getInputStream();
                //System.out.println(4);
                //String json = NetUtils.readString(is);
               // System.out.println(json);





            } catch (IOException ex) {
                //Toast.makeText(MainActivity.this, "IO Error", Toast.LENGTH_SHORT).show();
                //System.out.println("IO fucked");
            }*/



            runOnUiThread(new Runnable() {
                public void run() {
                   if(success){
                       SharedPreferences.Editor editor = getSharedPreferences("lock", MODE_PRIVATE).edit();
                       editor.putString("token", token);
                       editor.commit();
                       startOtherActivity();
                   }else if (status.equals("error")){
                       Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                   }
                }
            });

            return null;

        }

    }
    private class TokenCHK extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://app.nfls.io/API/User/User.php?action=GetUsernameByToken");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("token",tokenExisted));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                json = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                System.out.println("Protool Error");
            } catch (IOException e) {
                System.out.println("IO Error");
            }
            System.out.println(json);
            System.out.println(2);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                System.out.println(jsonObject);
            } catch (JSONException ex) {
                System.out.println("Server Error");
                status="1";
            }
            try {
                status=jsonObject
                        .getString("username");
            }catch (JSONException ex) {
                //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("JSON fucked");

            }
            System.out.println(status);
            System.out.println(status);
            if (status.equals("1")){

                runOnUiThread(new Runnable() {
                    public void run() {
                        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                EditText editTextUsername =(EditText)findViewById(R.id.Username);
                                username=editTextUsername.getText().toString();
                                EditText editTextPassword =(EditText)findViewById(R.id.Password);
                                password=editTextPassword.getText().toString();
                                login(username,password);

                            }

                        });
                    }
                });

            }else {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Welcome back "+ status, Toast.LENGTH_SHORT).show();
                        startOtherActivity();
                    }
                });


            }



            return null;

        }

    }

}

