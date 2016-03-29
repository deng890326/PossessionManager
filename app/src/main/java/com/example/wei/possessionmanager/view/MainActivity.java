package com.example.wei.possessionmanager.view;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.wei.possessionmanager.utils.SingleFragmentActivity;
import com.example.wei.possessionmanager.bean.Item;

public class MainActivity extends SingleFragmentActivity implements MainFragment.OnFragmentInteractionListener {

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    public void onFragmentInteraction(Item item) {
        String uuid = item.getUUID().toString();
        Intent intent = DetailActivity.newIntent(this, uuid);
        startActivity(intent);
    }
}
