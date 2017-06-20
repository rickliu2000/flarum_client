package club.mrtunnel.forum;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

        final ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
        boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        System.out.println(wifi+" "+internet);
        if(wifi|internet) {
            if (!ping()) {
                Toast.makeText(getApplicationContext(), "Our server lost his girlfriend 😭He is now hanging around",
                        Toast.LENGTH_SHORT).show();
                finish();
            }

        } else {
            System.out.println("No internet");
            Toast.makeText(getApplicationContext(), "Where do you think you are？On Mars？ Where's your connection",
                    Toast.LENGTH_SHORT).show();
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


    public static boolean ping() {
        String result = null;
        try {
            String ip = "ss-hk.nfls.io";
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            //Log.i("TTT", "result content : " + stringBuffer.toString());
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Log.i("TTT", "result = " + result);
        }
        return false;
    }


}

