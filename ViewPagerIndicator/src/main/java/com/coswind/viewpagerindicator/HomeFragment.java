package com.coswind.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.viewpagerindicator.TabPageIndicator;

/**
 * Created by coswind on 11/28/13.
 */
public class HomeFragment extends SherlockFragment {
    private View contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_main, container, false);

        initView();

        return contentView;
    }

    private void initView() {
        ViewPager viewPager = (ViewPager) contentView.findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            protected final String[] CONTENT = new String[]{"ONE", "TWO", "THR"};

            @Override
            public Fragment getItem(int i) {
                return new TabFragment();
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return CONTENT[position % CONTENT.length];
            }
        });

        TabPageIndicator pageIndicator = (TabPageIndicator) contentView.findViewById(R.id.titles);
        pageIndicator.setViewPager(viewPager);
    }
}
