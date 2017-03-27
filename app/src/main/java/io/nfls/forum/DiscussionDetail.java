package io.nfls.forum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rickliu on 3/26/17.
 */

public class DiscussionDetail extends AppCompatActivity{
    private GetDiscussionDetail GetDiscussionDetailTask;
    String json="";
    String attributes="";
    String User="";
    String UserId="";
    String startUser="";
    String relationships="";
    String UserData="";
    String value="";
    final ArrayList<String> list = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_detail);
        Intent intent = getIntent();
        value = intent.getStringExtra("DISCUSSION_ID");
        Toast.makeText(getApplicationContext(), "Loading discussion ID:"+value,
                Toast.LENGTH_SHORT).show();
        GetDiscussionDetailTask= new GetDiscussionDetail();
        GetDiscussionDetailTask.execute();




    }
    public void setlist(){
        final ArrayAdapter adapter = new ArrayAdapter(this,R.layout.discussion_detail_list,list);
        ListView listView = (ListView) findViewById(R.id.discussionDetail_List);
        listView.setAdapter(adapter);
        /*
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
        */

    }

    private class GetDiscussionDetail extends AsyncTask<Integer, String, Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {


            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet("https://forum.nfls.io/api/discussions/"+value+"?page%5Bnear");

            try {
                HttpResponse response = httpclient.execute(httppost);
                json = EntityUtils.toString(response.getEntity());
            } catch (ClientProtocolException e) {
                System.out.println("Protool Error");
            } catch (IOException e) {
                System.out.println("IO Error");
            }
            //System.out.println(json);
            //System.out.println(2);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                //System.out.println(jsonObject);
            } catch (JSONException ex) {
                System.out.println("Server Error");
            }
            //System.out.println(status);
            try {
                String discussion=jsonObject.getString("data");
                JSONObject discussion_json=new JSONObject(discussion);
                attributes=discussion_json.getString("attributes");
                JSONObject attributes_json=new JSONObject(attributes);

                int commentsCount=Integer.valueOf(attributes_json.getString("lastPostNumber"));
                int participantsCount=Integer.valueOf(attributes_json.getString("participantsCount"));
                System.out.println(commentsCount+" "+participantsCount);
                //int num=discussionList.length();
                JSONArray allList=jsonObject.getJSONArray("included");
                //int Usernum=UserList.length();
                String[] comment=new String[commentsCount];
                String[] commentUSERID=new String[commentsCount];
                String[] participantsID=new String[participantsCount];
                String[] participants=new String[participantsCount];
                int usercount=0;
                int postcount=0;
                for (int i=0;i<commentsCount+participantsCount;i++){
                    JSONObject allInfo=allList.getJSONObject(i);
                    String type=allInfo.getString("type");
                    //System.out.println(type);

                    if(type.equals("posts")) {
                        String posts_attributes = allInfo.getString("attributes");
                        JSONObject posts_atttributes_json = new JSONObject(posts_attributes);
                        String commenttype = posts_atttributes_json.getString("contentType");
                        relationships = allInfo.getString("relationships");
                        JSONObject relationship_json = new JSONObject(relationships);
                        User = relationship_json.getString("user");
                        JSONObject User_json = new JSONObject(User);
                        UserData = User_json.getString("data");
                        JSONObject UserData_json = new JSONObject(UserData);
                        UserId = UserData_json.getString("id");
                        System.out.println(User+" "+" "+UserData+" "+UserId);
                        if (commenttype.equals("discussionStickied")) {
                            comment[postcount] = commenttype;
                            commentUSERID[postcount] = UserId;
                        } else if(commenttype.equals("discussionTagged")){
                            comment[postcount] = commenttype;
                            commentUSERID[postcount] = UserId;
                        }else{
                            comment[postcount] = posts_atttributes_json.getString("contentHtml");
                            commentUSERID[postcount] = UserId;
                        }
                        postcount++;
                        System.out.println(postcount);
                    }else if (type.equals("users")){

                        //System.out.println(usercount);
                        String name_attributes=allInfo.getString("attributes");
                        //System.out.println(name_attributes);
                        String participants_ID=allInfo.getString("id");
                        //System.out.println(participants_ID);
                        JSONObject username_json=new JSONObject(name_attributes);
                        System.out.println(participants_ID+" "+username_json.getString("username"));
                        participants[usercount]=username_json.getString("username");
                        participantsID[usercount]=participants_ID;
                        usercount++;
                    }

                    //System.out.println(UserName[i]+" "+UserID[i]);

                }
                for(int i=0;i<postcount;i++){

                    for (int j=0;j<usercount;j++){
                        System.out.println(i+" "+j);
                        if (comment[i]!=null) {
                            if (commentUSERID[i].equals(participantsID[j])) {
                                //System.out.println(j+" "+UserID[j]+" "+UserName[j]);
                                if (comment[i].equals("discussionStickied")) {
                                    list.add("Discussion Sticked By" + participants[j]);
                                    System.out.println("Discussion Sticked By" + participants[j]);
                                } else if(comment[i].equals("discussionTagged")){
                                    list.add("Discussion Tagged By" + participants[j]);
                                    System.out.println("Discussion Tagged By" + participants[j]);
                                } else{
                                    String html=participants[j] + ":" + comment[i];
                                    list.add(String.valueOf(Html.fromHtml(html, null, new SizeLabel(20))));
                                    System.out.println(participants[j] + ":" + comment[i]);
                                }
                            }
                        }

                    }
                    //System.out.println(i+" "+id+" "+type+" "+title+" author:"+startUserName+" Last reply:"+lastUserName +" post time:"+startTime+" reply:"+commentsCount);
                    //list.add(title+" author:"+startUserName+" Last reply:"+lastUserName+" post time:"+startTime+" reply:"+commentsCount);

                    //discussionID[i]=Integer.valueOf(id);
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

