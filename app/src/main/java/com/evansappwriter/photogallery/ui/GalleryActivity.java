package com.evansappwriter.photogallery.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import com.evansappwriter.photogallery.R;
import com.evansappwriter.photogallery.util.Utils;

public class GalleryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            initUI();
        }
    }

    private void initUI() {
        Bundle b = getIntent().getExtras();

        int code = (getClass().getName() + "GalleryFragment").hashCode();
        Utils.printLogInfo("FRAG", "id: ", code);

        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("f" + code) == null) {
            Utils.printLogInfo("FRAG", 'f', code);
            Fragment f = GalleryFragment.newInstance(b);

            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_holder, f, "f" + code);
            ft.commit();
        }
    }
}
