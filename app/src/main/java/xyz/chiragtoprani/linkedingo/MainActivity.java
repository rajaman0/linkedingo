package xyz.chiragtoprani.linkedingo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//login
        WebView browser = (WebView) findViewById(R.id.webView);
        browser.setWebViewClient(new WebViewClient());

        browser.loadUrl("https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=7582bxcf5jzu7o&state=FWL99DTKw_NQVQtC&redirect_uri=http%3A%2F%2Flocalhost%3A3000%2Flinkedin%2Fdone&scope=r_basicprofile");

        moveToCamera(this);

    }

    public void moveToCamera(MainActivity context){
        Intent intent = new Intent(context, CameraActivity.class);
        startActivity(intent);
    }
}
