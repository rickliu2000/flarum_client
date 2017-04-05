package io.nfls.forum;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.R.attr.value;


/**
 * Created by Rickliu on 4/2/17.
 */

public class DiscussionDetail extends Activity {
    private WebView discussionDetailView;
    String value="";
    String cookie="";
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;


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

       // Toast.makeText(getApplicationContext(), "Loading discussion ID:"+value, Toast.LENGTH_SHORT).show();

        discussionDetailView=(WebView)findViewById(R.id.discussion_view);
        discussionDetailView.getSettings().setJavaScriptEnabled(true);
        discussionDetailView.getSettings().setDomStorageEnabled(true);
        discussionDetailView.setWebChromeClient(new WebChromeClient() {

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();
                return true;
            }
        });
        //discussionDetailView.setWebViewClient(new WebViewClient());
        discussionDetailView.setWebViewClient(new WebViewClient() {


            @Override

            public void onLoadResource(WebView view, String url) {
                //System.out.println("666");
                System.out.println(url);

                if (url.equals("https://forum.nfls.io/api/discussions?include=startUser%2ClastUser%2CstartPost%2Ctags&&")) {
                    onBackPressed();

                    return ;
                } else if(url.equals("https://login.nfls.io/operation/")){
                    startActivity(new Intent(DiscussionDetail.this, UserLogin.class));
                }
            }



        });
        discussionDetailView.loadUrl("https://forum.nfls.io/d/"+value);









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
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

}
