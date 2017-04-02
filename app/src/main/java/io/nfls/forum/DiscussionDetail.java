package io.nfls.forum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import static android.R.attr.value;

/**
 * Created by Rickliu on 4/2/17.
 */

public class DiscussionDetail extends AppCompatActivity {
    private WebView discussionDetailView;
    String value="";
    String cookie="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.discussion_detail_web);
        SharedPreferences read = getSharedPreferences("lock",MODE_PRIVATE);
        cookie = read.getString("cookie", "");
        System.out.println(cookie);
        Intent intent = getIntent();
        value = intent.getStringExtra("DISCUSSION_ID");
        CookieManager.getInstance().setCookie("nfls.io", cookie);
        Toast.makeText(getApplicationContext(), "Loading discussion ID:"+value,
                Toast.LENGTH_SHORT).show();
        discussionDetailView=(WebView)findViewById(R.id.discussion_view);
        discussionDetailView.getSettings().setJavaScriptEnabled(true);
        discussionDetailView.getSettings().setDomStorageEnabled(true);
        discussionDetailView.setWebChromeClient(new WebChromeClient());
        //discussionDetailView.setWebViewClient(new WebViewClient());
        discussionDetailView.setWebViewClient(new WebViewClient() {


            @Override

            public void onLoadResource(WebView view, String url) {
                //System.out.println("666");
                //System.out.println(url);

                if (url.equals("https://forum.nfls.io/api/discussions?include=startUser%2ClastUser%2CstartPost%2Ctags&&")) {
                    onBackPressed();

                    return ;
                }
            }



        });
        discussionDetailView.loadUrl("https://forum.nfls.io/d/"+value);









    }

}
