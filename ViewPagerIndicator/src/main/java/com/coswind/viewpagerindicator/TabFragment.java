package com.coswind.viewpagerindicator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.Options;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by coswind on 11/29/13.
 */
public class TabFragment extends Fragment implements OnRefreshListener {
    private CustomPullToRefreshLayout mCustomPullToRefreshLayout;

    public TabFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mCustomPullToRefreshLayout = (CustomPullToRefreshLayout) rootView.findViewById(R.id.ptr_layout);

        ActionBarPullToRefresh.from(getActivity())
                .options(Options.create()
                        .headerLayout(R.layout.custom_pullrefresh_header)
                        .headerTransformer(new CustomHeaderTransformer()).build())
                .allChildrenArePullable()
                .listener(this)
                .setup(mCustomPullToRefreshLayout);

        return rootView;
    }

    @Override
    public void onRefreshStarted(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // Notify PullToRefreshLayout that the refresh has finished
                mCustomPullToRefreshLayout.setRefreshComplete();
            }
        }.execute();
    }
}
