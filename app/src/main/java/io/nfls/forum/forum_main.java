package io.nfls.forum;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    String startUserAvatarUrl="";
    String UserData="";
    String tags="";
    String id="";
    List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
    Context context=this;
    String isLogedin="";
    String username="";
    String email="";
    String avatar_path="";
    String imageUrl="";
    URL myFileUrl = null;
    Bitmap bitmap = null;
    Bitmap[] userBitMap=new Bitmap[40];
    ImageView imView;
    private long exitTime = 0;
    int[] discussionID=new int[5000];
    String[] slug=new String[5000];
    String[] UserID=new String[40];
    String[] UserAvatarPath=new String[40];
    String[] UserName=new String[40];
    String[] discussionTitle=new String[40];
    String[] discussionInfo=new String[40];
    String[] UserAvatarPathOrdered=new String[40];
    boolean isUser=false;
    int pageCount=0;
    int postCount=-1;
    int lastPageFlag=-1;
    int newPostFlag=-1;
    int curItem=0;
    int Usernum=0;
    int num=0;
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
                newPostFlag=0;
                Intent intent = new Intent(forum_main.this, NewDiscussion.class);

                startActivity(intent);

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
                    finish();
                }

            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();






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
            GetDetailTask.execute(0);


       }
        GetDiscussionTask= new GetDiscussion();
        GetDiscussionTask.execute();





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

        if (id == R.id.nav_about) {
            startActivity(new Intent(forum_main.this, About.class));
        } else if (id == R.id.nav_settings) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Always call the superclass method first
        if (newPostFlag!=-1) {
            discussionID=new int[5000];
            slug=new String[5000];
            UserID=new String[40];
            UserAvatarPath=new String[40];
            UserName=new String[40];
            discussionTitle=new String[40];
            discussionInfo=new String[40];
            UserAvatarPathOrdered=new String[40];
            bitmap=null;
            //isUser=false;
            pageCount=0;
            postCount=-1;
            lastPageFlag=-1;
            newPostFlag=-1;
            curItem=0;
            GetDiscussionTask = new GetDiscussion();
            GetDiscussionTask.execute();
            final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            ListView listView = (ListView) findViewById(R.id.discussion_List);
            listView.setAdapter(null);
            list.clear();
        }

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
        isUser=true;
        for(int i=0; i<20;i++){
            GetDetailTask= new GetDetail();
            GetDetailTask.execute(i);
        }

        for(int i=0; i<20;i++){
            if(discussionInfo[i]!=null) {
                while (userBitMap[i]==null){
                    System.out.println("Stop");
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println("Value i "+i);
                if(lastPageFlag==-1) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    userBitMap[i].compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] b = baos.toByteArray();
                    String temp = Base64.encodeToString(b, Base64.DEFAULT);
                    map.put("image", temp);
                    map.put("title", discussionTitle[i]);
                    map.put("info", discussionInfo[i]);
                    list.add(map);
                }
            }

        }


        final ArrayAdapter adapter = new ArrayAdapter(this,R.layout.discussion_list,list);
        final ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
        final ListView listView = (ListView) findViewById(R.id.discussion_List);
        listView.setAdapter(new DiscussionListAdapter(this, list));
        listView.setSelection(curItem);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            System.out.println("last");
                            final ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
                            boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                            boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                            System.out.println(wifi+" "+internet);
                            if(wifi|internet){
                                TextView status = (TextView) findViewById(R.id.title);
                                status.setText("Welcome to NFLS Community!");
                                if (!ping()) {

                                    Toast.makeText(getApplicationContext(), "Our server lost his girlfriend 😭He is now hanging around",
                                            Toast.LENGTH_SHORT).show();
                                    status.setText("Hangin’ around, Nothing to do but frown.");
                                }else {
                                    if (lastPageFlag == -1) {
                                        pageCount++;
                                        UserAvatarPath = new String[40];
                                        UserName = new String[40];
                                        discussionTitle = new String[40];
                                        discussionInfo = new String[40];
                                        UserAvatarPathOrdered = new String[40];
                                        userBitMap=new Bitmap[40];
                                        UserID=new String[40];
                                        bitmap=null;
                                        GetDiscussionTask = new GetDiscussion();
                                        GetDiscussionTask.execute();
                                        bar.setVisibility(View.VISIBLE);
                                    }
                                }
                            }else{
                                System.out.println("No internet");
                                Toast.makeText(getApplicationContext(), "Where do you think you are？On Mars？ Where's your connection",
                                        Toast.LENGTH_SHORT).show();
                                TextView status=(TextView)findViewById(R.id.title);
                                status.setText("It's suggested that you go back to the Earth.No internet :(");

                            }



                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                curItem = firstVisibleItem+2;

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Object string=adapter.getItem(position);
                Long sss =adapter.getItemId(position);
                int numcount=sss.intValue();
                System.out.println("**********"+ Integer.toString(discussionID[numcount])+"-"+slug[numcount]);
                Intent intent = new Intent(forum_main.this, DiscussionDetail.class);

                intent.putExtra("DISCUSSION_ID",Integer.toString(discussionID[numcount])+"-"+slug[numcount]);
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


    private class GetDetail extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {
            System.out.println("???");
             int i=params[0];
            //System.out.println("Value i "+i);
                if (isUser){

                    imageUrl = UserAvatarPathOrdered[i];
                } else {
                    imageUrl = "https://forum.nfls.io/assets/avatars/" + avatar_path;
                }
                if((isUser)||(!isUser&&avatar_path!=null)) {
                    try {
                        myFileUrl = new URL(imageUrl);
                        System.out.println(myFileUrl);
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
                }
            if(bitmap==null){

            }else {
                Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                        .getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                final int color = 0xff424242;
                final Paint paint = new Paint();
                final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                final RectF rectF = new RectF(rect);
                final float roundPx = 96;

                paint.setAntiAlias(true);
                canvas.drawARGB(0, 0, 0, 0);
                paint.setColor(color);
                canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                canvas.drawBitmap(bitmap, rect, rect, paint);
                bitmap=output;

            }


            if(isUser){
                if (UserAvatarPathOrdered[i]==null) {
                    bitmap=null;
                } else{
                    userBitMap[i]=bitmap;
                    System.out.println(i+" sent");
                }

            }else {
                runOnUiThread(new Runnable() {
                    public void run() {

                        if (avatar_path.isEmpty()) {
                            Toast.makeText(forum_main.this, "No Avatar", Toast.LENGTH_LONG).show();
                        } else {
                            showAvatar();
                        }
                    }
                });
            }
            return null;
        }
    }


    private class GetDiscussion extends AsyncTask<Integer, String, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {

            runOnUiThread(new Runnable() {
                public void run() {
                    final ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
                    boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
                    boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
                    System.out.println(wifi+" "+internet);
                    if(wifi|internet){
                        if (!ping()) {
                            Toast.makeText(getApplicationContext(), "Our server lost his girlfriend 😭He is now hanging around",
                                    Toast.LENGTH_SHORT).show();
                            TextView status = (TextView) findViewById(R.id.title);
                            status.setText("Hangin’ around, Nothing to do but frown.");
                        }
                        TextView status = (TextView) findViewById(R.id.title);
                        status.setText("Welcome to NFLS Community!");
                    }else{
                        System.out.println("No internet");
                        Toast.makeText(getApplicationContext(), "Where do you think you are？On Mars？ Where's your connection",
                                Toast.LENGTH_SHORT).show();
                        TextView status=(TextView)findViewById(R.id.title);
                        status.setText("It's suggested that you go back to the Earth.No internet :(");

                    }

                }
            });


            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet("https://forum.nfls.io/api/discussions?include=startUser%2ClastUser&page%5Boffset%5D="+pageCount*20);

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
            if(jsonObject!=null) {//System.out.println(status);
                try {
                    JSONArray discussionList = jsonObject.getJSONArray("data");
                    num = discussionList.length();
                    if (num == 0) {
                        System.out.println("No more posts");
                        lastPageFlag = 0;
                    }
                    JSONArray UserList = jsonObject.getJSONArray("included");
                    Usernum = UserList.length();
                    for (int i = 0; i < Usernum; i++) {
                        JSONObject UserInfo = UserList.getJSONObject(i);
                        String UserId = UserInfo.getString("id");
                        String name_attributes = UserInfo.getString("attributes");
                        JSONObject username_json = new JSONObject(name_attributes);
                        UserName[i] = username_json.getString("username");
                        UserAvatarPath[i] = username_json.getString("avatarUrl");
                        UserID[i] = UserId;
                        System.out.println(UserName[i]+" "+UserID[i]+" "+UserAvatarPath[i]);

                    }

                    for (int i = 0; i < num; i++) {
                        postCount++;
                        JSONObject discussion = discussionList.getJSONObject(i);
                        //System.out.println(i+" "+discussion);
                        id = discussion.getString("id");
                        type = discussion.getString("type");
                        attributes = discussion.getString("attributes");
                        //System.out.println(attributes);
                        //------------------start attributes------------------
                        JSONObject attributes_json = new JSONObject(attributes);
                        title = attributes_json.getString("title");
                        slug[postCount] = attributes_json.getString("slug");
                        commentsCount = attributes_json.getString("commentsCount");
                        startTime = attributes_json.getString("startTime");
                        //------------------end of attributes------------------
                        //username=attributes_json.getString("username");
                        //------------------start relationships-----------------
                        relationships = discussion.getString("relationships");
                        JSONObject relationship_json = new JSONObject(relationships);
                        //------------------start startUser-----------------
                        startUser = relationship_json.getString("startUser");
                        JSONObject startUser_json = new JSONObject(startUser);
                        UserData = startUser_json.getString("data");
                        JSONObject UserData_json = new JSONObject(UserData);
                        startUserid = UserData_json.getString("id");
                        //------------------start lastUser-----------------
                        lastUser = relationship_json.getString("lastUser");
                        JSONObject lastUser_json = new JSONObject(lastUser);
                        UserData = lastUser_json.getString("data");
                        UserData_json = new JSONObject(UserData);
                        lastUserid = UserData_json.getString("id");
                        //------------------end of relationships------------
                        for (int j = 0; j < 40; j++) {
                            if (startUserid.equals(UserID[j])) {
                                //System.out.println(j+" "+UserID[j]+" "+UserName[j]);
                                startUserName = UserName[j];
                                UserAvatarPathOrdered[i] = UserAvatarPath[j];
                            }
                            if (lastUserid.equals(UserID[j])) {
                                lastUserName = UserName[j];
                            }
                        }
                        System.out.println(i + " " + id + " " + type + " " + title + " author:" + startUserName + " Last reply:" + lastUserName + " post time:" + startTime + " reply:" + commentsCount);
                        //list.add(title+" author:"+startUserName+" Last reply:"+lastUserName+" post time:"+startTime+" reply:"+commentsCount);
                        if (UserAvatarPathOrdered[i] == "null") {
                            System.out.println(i + " lacks avatar");
                            UserAvatarPathOrdered[i] = "https://forum.nfls.io/assets/avatars/nfls_forum.png";
                        }
                        System.out.println(UserAvatarPathOrdered[i]);

                        discussionTitle[i] = title;
                        discussionInfo[i] = " Last reply:" + lastUserName;

                        discussionID[postCount] = Integer.valueOf(id);

                    }

                } catch (JSONException ex) {

                }
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
