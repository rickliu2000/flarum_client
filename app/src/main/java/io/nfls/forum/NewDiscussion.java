package io.nfls.forum;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rickliu on 4/4/17.
 */

public class NewDiscussion extends AppCompatActivity {
    String isLogedin="";
    String title="";
    String contents="";
    String cookie="";
    String json="";
    String jsonSent="";
    private sendNewDiscussion NewDiscussionTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_discussion);
        SharedPreferences read = getSharedPreferences("lock",MODE_PRIVATE);
        isLogedin = read.getString("isLogin", "");
        cookie = read.getString("cookie", "");
        if(!isLogedin.equals("true")){
            Toast.makeText(getApplicationContext(), "Please login first",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(NewDiscussion.this, UserLogin.class));
            finish();

        }
        Button submit;
        submit = (Button) findViewById(R.id.btn_new_discussion);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText inputTitle =(EditText)findViewById(R.id.Title);
                title=inputTitle.getText().toString();
                EditText inputContent =(EditText)findViewById(R.id.discussion_content);
                contents=inputContent.getText().toString();
                System.out.println(title+" "+contents);
                if ((!title.equals(""))&&(!contents.equals(""))){
                    jsonSent="{\"data\":{\"type\":\"discussions\",\"attributes\":{\"title\":\""+title+"\",\"content\":\""+contents+"\"},\"relationships\":{\"tags\":{\"data\":[]}}}}";
                    NewDiscussionTask= new sendNewDiscussion();
                    NewDiscussionTask.execute();
                }else{
                    Toast.makeText(getApplicationContext(), "Please check your post and try again",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private class sendNewDiscussion extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://forum.nfls.io/api/discussions");

            try {
                // Add your data
                httppost.addHeader(new BasicHeader("Cookie",cookie));
                JSONObject jsonParam = new JSONObject(jsonSent);
                StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httppost.setEntity(entity);

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
            } catch(JSONException e){

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
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), "Post Success!",
                            Toast.LENGTH_SHORT).show();
                    finish();


                }
            });

            return null;
        }
    }


}

