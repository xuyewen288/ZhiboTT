package com.xunye.zhibott.acitvity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.xunye.zhibott.R;
import com.xunye.zhibott.fragment.ModeOrderFragment;

public class ModeChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_choose);
        findViewById(R.id.add_new_dev).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ModeChooseActivity.this,CmsSetupActivity.class));
            }
        });
        findViewById(R.id.add_by_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrderMode();
            }
        });
    }

    public void startOrderMode(){
        Fragment fragment = ModeOrderFragment.newInstance(null, null);
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.choose_framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
