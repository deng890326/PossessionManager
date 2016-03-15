package com.example.wei.possessionmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.util.Log;

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
