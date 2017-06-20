package club.mrtunnel.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserLogin extends AppCompatActivity {
    private Login_Flarum mTask;
   // private TokenCHK CHKTask;
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
    String isLogedin="";
    String cookiefinal="";
    String UserId="";
    String cookie="";
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
            //new AlertDialog.Builder(UserLogin.this)
                    //.setTitle("Login")
                   // .setMessage("呦～等一会吧")
                   // .show();
           // CHKTask = new TokenCHK();
           // CHKTask.execute();
            startOtherActivity();
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

        mTask = new Login_Flarum();
        mTask.execute();

    }
    private void startOtherActivity()
    {
        startActivity(new Intent(UserLogin.this,DetailedUserInformation.class));
       finish();



    }



   private class Login_Flarum extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://forum.mrtunnel.club/login");

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header h : headers) {
                    System.out.println(h.getValue().toString());
                    cookie=cookie+h.getValue().toString()+"; ";
                }

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
                //Toast.makeText(UserLogin.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("Server Error");
            }


            try {
                status=jsonObject
                        .getString("status");
            }catch (JSONException ex) {
                //Toast.makeText(UserLogin.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("JSON fucked");

            }
            System.out.println(status);

            if (status.equals("success")){

                try {
                    System.out.println("dealing with json");

                    token = jsonObject
                            .getString("token");
                    UserId = jsonObject
                            .getString("userId");
                    success=true;
                } catch (JSONException ex) {

                    System.out.println("JSON fucked");

                }


            }else {
                try {


                    message = jsonObject
                            .getString("message");

                } catch (JSONException ex) {
                    System.out.println("JSON fucked");

                }
            }


            runOnUiThread(new Runnable() {
                public void run() {
                   if(success){
                       SharedPreferences.Editor editor = getSharedPreferences("lock", MODE_PRIVATE).edit();
                       editor.putString("token", token);
                       editor.putString("cookie", cookie);
                       editor.putString("id", UserId);
                       editor.commit();
                       startOtherActivity();
                   }else if (status.equals("error")){
                       Toast.makeText(UserLogin.this, message, Toast.LENGTH_SHORT).show();
                   }
                }
            });

            return null;

        }

    }

    /*
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
                //Toast.makeText(UserLogin.this, "Server Error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UserLogin.this, "Welcome back "+ status, Toast.LENGTH_SHORT).show();
                        startOtherActivity();
                    }
                });


            }



            return null;

        }

    }
*/
}

