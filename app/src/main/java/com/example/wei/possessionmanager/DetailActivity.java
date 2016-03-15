package com.example.wei.possessionmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wei on 2016/2/28 0028.
 */
public class DetailActivity extends AppCompatActivity {

    private static final String EXTRA_UUID = "com.example.wei.possessionmanager.key_uuid";

    public static Intent newIntent(Context context, String uuid) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_UUID, uuid);
        return intent;
    }

    @InjectView(R.id.view_pager)
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);

        ItemManager itemManager = ItemManager.getInstance(this);
        final List<Item> items = itemManager.getAllItems();

        FragmentManager fm = getSupportFragmentManager();
        PagerAdapter pagerAdapter = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Item item = items.get(position);
                return DetailFragment.newInstance(item.getUUID().toString());
            }

            @Override
            public int getCount() {
                return items.size();
            }
        };

        mViewPager.setAdapter(pagerAdapter);

        String uuid = getIntent().getStringExtra(EXTRA_UUID);
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (uuid.equals(item.getUUID().toString())) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
