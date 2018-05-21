package com.xj.customloadingview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private LoadingView mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLoadingView = LoadingView.attach((ViewGroup) findViewById(R.id.root));
    }

    public void start(View view) {
        startActivity(new Intent(this,MainActivity.class));
        mLoadingView.setVisibility(View.GONE);
    }

}
