package com.example.ultrademo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PtrFrameLayout ptrFrameLayout = (PtrFrameLayout) findViewById(R.id.ptrframe);
        //StoreHouseHeader header = new StoreHouseHeader(this);
        CourierHeader header = new CourierHeader(this);
        //MaterialHeader header=new MaterialHeader(this);
        //PtrClassicDefaultHeader header=new PtrClassicDefaultHeader(this);
        // header.setPadding(0,10, 0, 10);
        // header.initWithString("Ultra PTR");

        //PtrTensionIndicator ptrTensionIndicator=new PtrTensionIndicator();

        //ptrFrameLayout.setPtrIndicator(ptrTensionIndicator);
        ptrFrameLayout.setDurationToCloseHeader(200);
        ptrFrameLayout.setEnabledNextPtrAtOnce(true);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1500);
            }
        });


    }


}
