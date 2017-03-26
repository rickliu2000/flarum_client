package io.nfls.forum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
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

/**
 * Created by Rickliu on 2/6/17.
 */

public class forum_main extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private GetDetail GetDetailTask;
    private GetDiscussion GetDiscussionTask;
    String json="";
    String status="";
    String attributes="";
    String type="";
    String title="";
    String commentsCount="";
    String startTime="";
    String startUser="";
    String relationships="";
    String lastUser="";
    String startUserid="";
    String lastUserid="";
    String startUserName="";
    String lastUserName="";
    String UserData="";
    String tags="";
    String id="";
    final ArrayList<String> list = new ArrayList<String>();
    Context context=this;
    String isLogedin="";
    String username="";
    String email="";
    String avatar_path="";
    String imageUrl="";
    URL myFileUrl = null;
    Bitmap bitmap = null;
    ImageView imView;
    private long exitTime = 0;
    int[] discussionID=new int[20];
    String[] UserID=new String[40];
    String[] UserName=new String[40];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View barView = inflater.inflate(R.layout.nav_header_main, null);
        SharedPreferences read = getSharedPreferences("lock",MODE_PRIVATE);
        isLogedin = read.getString("isLogin", "");
        System.out.println(isLogedin);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        imView = (ImageView) header.findViewById(R.id.avatar_bar);
        imView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogedin.equals("true")) {

                    startActivity(new Intent(forum_main.this, DetailedUserInformation.class));
                } else{
                    startActivity(new Intent(forum_main.this, UserLogin.class));
                }

            }
        });
        if(isLogedin.equals("true")){
           username=read.getString("Username", "");
           email=read.getString("Email", "");
           avatar_path=read.getString("Avatar_addr", "");

/*View view=navigationView.inflateHeaderView(R.layout.nav_header_main);*/
           TextView Username=(TextView)header.findViewById(R.id.username_bar);
           Username.setText(username);
           TextView Email=(TextView)header.findViewById(R.id.email_bar);
           Email.setText(email);
           GetDetailTask= new GetDetail();
           GetDetailTask.execute();
           GetDiscussionTask= new GetDiscussion();
           GetDiscussionTask.execute();

       }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            exit();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 200) {
            Toast.makeText(getApplicationContext(), "连按两次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            //System.exit(0);

        }
    }

    public void setlist(){
        final ArrayAdapter adapter = new ArrayAdapter(this,R.layout.discussion_list,list);
        ListView listView = (ListView) findViewById(R.id.discussion_List);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object string=adapter.getItem(position);
                Long sss =adapter.getItemId(position);
                int numcount=sss.intValue();
                System.out.println("**********"+ discussionID[numcount]);
                Intent intent = new Intent(forum_main.this, DiscussionDetail.class);
                intent.putExtra("DISCUSSION_ID",Integer.toString(discussionID[numcount]));
                startActivity(intent);

            }
        });

    }



    private void showAvatar(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        imView = (ImageView) header.findViewById(R.id.avatar_bar);
        imView.setImageBitmap(bitmap);
    }
    private class GetDetail extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
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
            runOnUiThread(new Runnable() {
                public void run() {
                    if (avatar_path.equals(null)){
                        Toast.makeText(forum_main.this, "No Avatar", Toast.LENGTH_LONG).show();
                    }
                    else {
                       showAvatar();
                    }
                }
            });
            return null;
        }
    }


    private class GetDiscussion extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet("https://forum.nfls.io/api/discussions?include=startUser%2ClastUser");

            try {
                HttpResponse response = httpclient.execute(httppost);
                json = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                System.out.println("Protool Error");
            } catch (IOException e) {
                System.out.println("IO Error");
            }
            //System.out.println(json);
            System.out.println(2);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                System.out.println(jsonObject);
            } catch (JSONException ex) {
                System.out.println("Server Error");
            }
            //System.out.println(status);
            try {
                JSONArray discussionList=jsonObject.getJSONArray("data");
                int num=discussionList.length();
                JSONArray UserList=jsonObject.getJSONArray("included");
                int Usernum=UserList.length();
                for (int i=0;i<Usernum;i++){
                    JSONObject UserInfo=UserList.getJSONObject(i);
                    String UserId=UserInfo.getString("id");
                    String name_attributes=UserInfo.getString("attributes");
                    JSONObject username_json=new JSONObject(name_attributes);
                    UserName[i]=username_json.getString("username");
                    UserID[i]=UserId;
                    //System.out.println(UserName[i]+" "+UserID[i]);

                }
                for(int i=0;i<num;i++){
                    //System.out.println(i);
                    JSONObject discussion=discussionList.getJSONObject(i);
                    //System.out.println(i+" "+discussion);
                    id=discussion.getString("id");
                    type=discussion.getString("type");
                    attributes=discussion.getString("attributes");
                    //System.out.println(attributes);
                    //------------------start attributes------------------
                    JSONObject attributes_json=new JSONObject(attributes);
                    title=attributes_json.getString("title");
                    commentsCount=attributes_json.getString("commentsCount");
                    startTime=attributes_json.getString("startTime");
                    //------------------end of attributes------------------
                    //username=attributes_json.getString("username");
                    //------------------start relationships-----------------
                    relationships=discussion.getString("relationships");
                    JSONObject relationship_json =new JSONObject(relationships);
                    //------------------start startUser-----------------
                    startUser=relationship_json.getString("startUser");
                    JSONObject startUser_json=new JSONObject(startUser);
                    UserData=startUser_json.getString("data");
                    JSONObject UserData_json=new JSONObject(UserData);
                    startUserid=UserData_json.getString("id");
                    //------------------start lastUser-----------------
                    lastUser=relationship_json.getString("lastUser");
                    JSONObject lastUser_json=new JSONObject(lastUser);
                    UserData=lastUser_json.getString("data");
                    UserData_json=new JSONObject(UserData);
                    lastUserid=UserData_json.getString("id");
                    //------------------end of relationships------------
                    for (int j=0;j<40;j++){
                        if(startUserid.equals(UserID[j])){
                            //System.out.println(j+" "+UserID[j]+" "+UserName[j]);
                            startUserName=UserName[j];
                        }
                        if(lastUserid.equals(UserID[j])){
                            lastUserName=UserName[j];
                        }
                    }
                    System.out.println(i+" "+id+" "+type+" "+title+" author:"+startUserName+" Last reply:"+lastUserName +" post time:"+startTime+" reply:"+commentsCount);
                    list.add(title+" author:"+startUserName+" Last reply:"+lastUserName+" post time:"+startTime+" reply:"+commentsCount);

                    discussionID[i]=Integer.valueOf(id);
                }

            }catch (JSONException ex){

            }
            runOnUiThread(new Runnable() {
                public void run() {
                    setlist();
                }
            });

            return null;
        }
    }
}
