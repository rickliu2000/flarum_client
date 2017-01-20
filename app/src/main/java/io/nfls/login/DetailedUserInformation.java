package io.nfls.login;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
    String json="";
    String status="";
    String username="";
    String email="";
    String avatar_path="";
    String join_time="";
    String id="";
    String info_full="";
    String imageUrl ="";
    private long exitTime = 0;
    ImageView imView;
    URL myFileUrl = null;
    Bitmap bitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_detailed);
        initView();

    }
    /*@Override
    public void onBackPressed() {

    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 200) {
            Toast.makeText(getApplicationContext(), "连按两次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            //finish();
            System.exit(0);
        }
    }

    private void initView(){
        SharedPreferences read = getSharedPreferences("lock",MODE_PRIVATE);
        tokenExisted = read.getString("token", "");
        //System.out.println(tokenExisted);
        //TextView TokenView=(TextView)findViewById(R.id.token);
        //TokenView.setText(tokenExisted);
        GetDetailTask= new GetDetail();
        GetDetailTask.execute();


    }



    private class GetDetail extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://app.nfls.io/API/User/User.php?action=GetPersonalGeneralInfoByToken");

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

                    username = jsonObject
                            .getString("username");

                    System.out.println(username);
                     id = jsonObject
                        .getString("id");
                    System.out.println(id);
                    email = jsonObject
                            .getString("email");
                    System.out.println(email);
                    join_time = jsonObject
                            .getString("join_time");
                    System.out.println(join_time);
                    avatar_path = jsonObject
                            .getString("avatar_path");
                    System.out.println(avatar_path);
                    //System.out.println("FUCKING JSON");
                    info_full="Username:"+username +"\n"
                    +"id:"+id+"\n"
                    +"email:"+email+"\n"
                    +"join time:"+join_time+"\n";

                } catch (JSONException ex) {
                    //Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    System.out.println("JSON fucked");

                }
                //Toast.makeText(MainActivity.this, token + id, Toast.LENGTH_LONG).show();
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


            runOnUiThread(new Runnable() {
                    public void run() {
                        TextView FullView=(TextView)findViewById(R.id.info_full);
                        FullView.setText(info_full);
                        imView = (ImageView) findViewById(R.id.avatar);
                        imView.setImageBitmap(bitmap);


                    }
                });





            return null;

        }

    }


}
