package com.xunye.zhibott.acitvity;

import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xunye.zhibott.R;
import com.xunye.zhibott.fragment.BaseFragment;
import com.xunye.zhibott.fragment.DevFragment;
import com.xunye.zhibott.fragment.MineFragment;
import com.xunye.zhibott.fragment.OnFragmentInteractionListener;
import com.xunye.zhibott.fragment.ScanDevFragment;
import com.xunye.zhibott.fragment.ShopFragment;
import com.xunye.zhibott.helper.PreferenceUtil;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener,OnFragmentInteractionListener {

    private ExecutorService threadPool= Executors.newCachedThreadPool();
    PreferenceUtil mPreferenceUtil;
    ViewPager mViewPager;
    TextView mTvDev;
    TextView mTvShop;
    TextView mTvMine;
    MyPagerAdapter mMyPagerAdapter;

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        mPreferenceUtil=new PreferenceUtil(this);
        initview();
    }

    private void initview() {
        mTvDev=findViewById(R.id.tv_dev);
        mTvShop=findViewById(R.id.tv_shop);
        mTvMine=findViewById(R.id.tv_mine);
        mTvDev.setOnClickListener(this);
        mTvShop.setOnClickListener(this);
        mTvMine.setOnClickListener(this);
        mViewPager=findViewById(R.id.viewpager);
        mMyPagerAdapter=new MyPagerAdapter(getSupportFragmentManager());
        mMyPagerAdapter.addItem(DevFragment.newInstance("1","2"));
//        mMyPagerAdapter.addItem(ShopFragment.newInstance("1","2"));
//        mMyPagerAdapter.addItem(MineFragment.newInstance("1","2"));
        mViewPager.setAdapter(mMyPagerAdapter);
//        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_dev:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.tv_shop:
                mViewPager.setCurrentItem(1);
                break;
            case R.id.tv_mine:
                mViewPager.setCurrentItem(2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        threadPool.shutdown();
    }

    public void startScanDev(){
        Fragment fragment = ScanDevFragment.actionInstance(this, null);
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public PreferenceUtil getPreferenceUtil() {
        return mPreferenceUtil;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<BaseFragment> fragments=new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addItem(BaseFragment fragment){
            fragments.add(fragment);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

    }
}
