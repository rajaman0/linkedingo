package xyz.chiragtoprani.linkedingo;

import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView browser = (WebView) findViewById(R.id.webViewMap);
        browser.setWebViewClient(new WebViewClient());

        browser.loadUrl("http://www.google.com");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_cam);
        final MapActivity me = this;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(me, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

}
