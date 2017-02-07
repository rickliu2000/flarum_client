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
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
 * Created by Rickliu on 2/6/17.
 */

public class forum_main extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {
    private GetDetail GetDetailTask;
    Context context=this;
    String isLogedin="";
    String username="";
    String email="";
    String avatar_path="";
    String imageUrl="";
    URL myFileUrl = null;
    Bitmap bitmap = null;
    ImageView imView;
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
        if(!isLogedin.equals("true")){
            startActivity(new Intent(forum_main.this,MainActivity.class));
        }else{
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
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.avatar_bar) {
            Toast.makeText(forum_main.this, "login", Toast.LENGTH_SHORT).show();
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
}
