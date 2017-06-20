package club.mrtunnel.forum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rickliu on 2017/1/20.
 */
public class DetailedUserInformation extends AppCompatActivity {
    private GetDetail GetDetailTask;
    String tokenExisted = "";
    String firstLogin="true";
    String json="";
    String status="";
    String username="";
    String email="";
    String avatar_path="";
    String join_time="";
    String id="";
    String info_full="";
    String imageUrl ="";
    String Userid="";
    ImageView imView;
    URL myFileUrl = null;
    Bitmap bitmap = null;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_detailed);
        initView();

    }
    /*@Override
    public void onBackPressed() {

    }*/


    private void initView(){
        SharedPreferences read = getSharedPreferences("lock",MODE_PRIVATE);
        tokenExisted = read.getString("token", "");
        Userid = read.getString("id", "");
        firstLogin = read.getString("firstLogin", "");
        if(firstLogin.equals("")){
            firstLogin="true";
            System.out.println("Will perform a restart");
        }
        //System.out.println(tokenExisted);
        //TextView TokenView=(TextView)findViewById(R.id.token);
        //TokenView.setText(tokenExisted);
        GetDetailTask= new GetDetail();
        GetDetailTask.execute();



    }
    private void startOtherActivity()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(i);

    }


    private class GetDetail extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet("https://forum.mrtunnel.club/api/users/"+Userid);

            try {
                HttpResponse response = httpclient.execute(httpget);
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
            }
/*
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
                    String UserInfoStr=jsonObject.getString("data");
                    JSONObject UserInfo = new JSONObject(UserInfoStr);
                    id = UserInfo.getString("id");
                    String name_attributes = UserInfo.getString("attributes");
                    JSONObject username_json = new JSONObject(name_attributes);
                    username = username_json.getString("username");
                    avatar_path = username_json.getString("avatarUrl");
                    System.out.println(username);
                    System.out.println(id);
                    email = username_json
                            .getString("email");
                    System.out.println(email);
                    join_time = username_json
                            .getString("join_time");
                    System.out.println(join_time);
                    System.out.println(avatar_path);
                    //System.out.println("FUCKING JSON");
                    info_full="Username:"+username +"\n"
                    +"id:"+id+"\n"
                    +"email:"+email+"\n"
                    +"join time:"+join_time+"\n";

                } catch (JSONException ex) {
                    //Toast.makeText(UserLogin.this, "Server Error", Toast.LENGTH_SHORT).show();
                    System.out.println("JSON fucked");

                }
                //Toast.makeText(UserLogin.this, token + id, Toast.LENGTH_LONG).show();
                //System.out.println(token+"--"+id);
                imageUrl="https://forum.nfls.io/assets/avatars/"+avatar_path;

                try {
                    myFileUrl = new URL(imageUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(DetailedUserInformation.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

*/
            try {
                System.out.println("dealing with json");
                String UserInfoStr=jsonObject.getString("data");
                JSONObject UserInfo = new JSONObject(UserInfoStr);
                id = UserInfo.getString("id");
                String name_attributes = UserInfo.getString("attributes");
                JSONObject username_json = new JSONObject(name_attributes);
                username = username_json.getString("username");
                avatar_path = username_json.getString("avatarUrl");
                System.out.println(username);
                System.out.println(id);
               // email = username_json
                      //  .getString("email");
                //System.out.println(email);
                join_time = username_json
                        .getString("joinTime");
                System.out.println(join_time);
                System.out.println(avatar_path);
                //System.out.println("FUCKING JSON");
                info_full="Username:"+username +"\n"
                        +"id:"+id+"\n"
                        +"email:"+email+"\n"
                        +"join time:"+join_time+"\n";

            } catch (JSONException ex) {
                //Toast.makeText(UserLogin.this, "Server Error", Toast.LENGTH_SHORT).show();
                System.out.println("JSON fucked");

            }
            //Toast.makeText(UserLogin.this, token + id, Toast.LENGTH_LONG).show();
            //System.out.println(token+"--"+id);
            imageUrl="https://forum.nfls.io/assets/avatars/"+avatar_path;
            if (avatar_path.equals(null)){
                imageUrl="https://forum.nfls.io/assets/avatars/"+"forum.png";

            }

            try {
                myFileUrl = new URL(imageUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl
                        .openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                    public void run() {
                        TextView FullView=(TextView)findViewById(R.id.info_full);
                        FullView.setText(info_full);
                        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                                (Context.LAYOUT_INFLATER_SERVICE);
                        View barView = inflater.inflate(R.layout.nav_header_main, null);
                        TextView Username=(TextView)barView.findViewById(R.id.username_bar);
                        Username.setText(username);
                        TextView Email=(TextView)barView.findViewById(R.id.email_bar);
                        Email.setText(email);

                        if (avatar_path.equals(null)){
                            Toast.makeText(DetailedUserInformation.this, "No Avatar", Toast.LENGTH_LONG).show();

                        }
                        else {
                            imView = (ImageView) findViewById(R.id.avatar);
                            imView.setImageBitmap(bitmap);
                        }
                        SharedPreferences.Editor editor = getSharedPreferences("lock", MODE_PRIVATE).edit();
                        editor.putString("isLogin", "true");
                        editor.putString("Username", username);
                        editor.putString("Email", email);
                        editor.putString("Avatar_addr", avatar_path);
                        editor.putString("firstLogin", "false");
                        editor.commit();
                        if (firstLogin.equals("true")){
                            startOtherActivity();

                        }
                        System.out.println("true");



                    }
                });





            return null;

        }

    }


}
