package com.coswind.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

/**
 * Created by coswind on 11/28/13.
 */
public class HomeActivity extends SherlockFragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mListView;
    private ActionBar mActionBar;
    private SherlockActionBarDrawerToggle mDrawerToggle;

    public static final String[] TITLES = {
        "ONE",
        "TWO",
        "THR"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        initView();
        initEvent();
    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mListView = (ListView) findViewById(R.id.left_drawer);

        mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TITLES));
        mListView.setCacheColorHint(0);
        mListView.setScrollingCacheEnabled(false);
        mListView.setScrollContainer(false);
        mListView.setFastScrollEnabled(true);
        mListView.setSmoothScrollbarEnabled(true);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer_light, R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();

        Fragment fragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.content_fragment, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		/*
		 * The action bar home/up action should open or close the drawer.
		 * mDrawerToggle will take care of this.
		 */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initEvent() {
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {
                mDrawerToggle.onDrawerSlide(view, v);
            }

            @Override
            public void onDrawerOpened(View view) {
                mDrawerToggle.onDrawerOpened(view);
            }

            @Override
            public void onDrawerClosed(View view) {
                mDrawerToggle.onDrawerClosed(view);
            }

            @Override
            public void onDrawerStateChanged(int i) {
                mDrawerToggle.onDrawerStateChanged(i);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mDrawerLayout.closeDrawer(mListView);
            }
        });
    }
}
