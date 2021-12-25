package com.llc.exoplayerexample;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class MainActivity extends AppCompatActivity {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private final List<String> statusList = new ArrayList<>();
    private VerticalViewPager view_pager;
    private ViewPagerAdapter adapter;
    private PlayerFragment playerFragment;
    public int item = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        item++;


        Bundle bundle = new Bundle();
        bundle.putString("local", "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");

        playerFragment = new PlayerFragment(new PlayerCallback() {
            @Override
            public void onPlayingCompleted() {
                // Add Events here
                if (view_pager.getCurrentItem() < mFragmentList.size()) {
                    view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                }
            }
        });

        playerFragment.setArguments(bundle);
        adapter.addFragment(playerFragment);
        adapter.notifyDataSetChanged();
        addVideo(true);


    }

    void initView() {
        view_pager = (VerticalViewPager) findViewById(R.id.view_pager);
        view_pager.setOffscreenPageLimit(1000);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        view_pager.setAdapter(adapter);
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position >= mFragmentList.size() - 1) {
                    addVideo(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    void loadMore(String url) {
        item++;
        Bundle bundle = new Bundle();
        bundle.putString("local", url);
        PlayerFragment playerFragment = new PlayerFragment(new PlayerCallback() {
            @Override
            public void onPlayingCompleted() {
                // Add Events here
                if (view_pager.getCurrentItem() < mFragmentList.size()) {
                    view_pager.setCurrentItem(view_pager.getCurrentItem() + 1);
                }
            }
        });
        playerFragment.setArguments(bundle);
        adapter.addFragment(playerFragment);
    }

    void addVideo(boolean first){
        statusList.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
        statusList.add("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4");

        for (int i = 0; i < statusList.size(); i++) {
            loadMore(statusList.get(i));
        }
        adapter.notifyDataSetChanged();
        if(first){
            playerFragment.run();
        }
    }
}