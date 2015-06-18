package com.vitman.rxRealm.altbeacon_map.app;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.CustomerMarker;
import com.vitman.rxRealm.altbeacon_map.app.entity.DataBase;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;
import com.vitman.rxRealm.altbeacon_map.app.layout.TouchRelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.util.BitmapHelper;
import com.vitman.rxRealm.altbeacon_map.app.util.LayoutBuilder;
import com.vitman.rxRealm.altbeacon_map.app.util.MapService;
import com.vitman.rxRealm.altbeacon_map.app.util.ViewBuilder;
import rx.Subscriber;
import rx.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Victor Artemjev on 18.06.2015.
 */
public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = MapActivity.class.getSimpleName();

    private DataBase mDataBase;
    private MapService mMapService;
    private BitmapHelper mBitmapHelper;
    private LayoutBuilder mLayoutBuilder;
    private ViewBuilder mViewBuilder;

    private TouchRelativeLayout mHolderLayout;
    private RelativeLayout mMapLayout;
    private ImageView mMapImageView;

    // list of all customers on floor
    private List<Customer> mCustomers;

    // list of customer layouts which contains all customers divided into zones by beacons
    private List<GridLayout> mCustomerLayouts = new ArrayList<>();

    // map of customer markers on map, where key - user id and value - user avatar
    private Map<String, CustomerMarker> mCustomerMarkers = new HashMap<>();

    // map of beacons, where key - beacon id and value - beacon
    private Map<Integer, PandaBeacon> mBeacons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapService = new MapService(MapActivity.this);
        initCustomerData();
        initViews();
        setupMap();
    }

    private void initCustomerData() {
        mDataBase = DataBase.getInstance();
        mCustomers = mDataBase.getCustomers();
        mBeacons = mDataBase.getPandaBeacons();
    }

    private void initViews() {
        mHolderLayout = (TouchRelativeLayout) findViewById(R.id.holder_layout);
        mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mMapImageView = (ImageView) findViewById(R.id.map_imageView);
    }


    private void setupMap() {
        Subscription mapSubscription = mMapService
                .createMapBitmapObservable(R.drawable.devabit_map, mMapSubscriber);

    }

    private void produceCustomerMarkers() {
        Subscription markersSubscribtion = mMapService
                .createCustomerMarkerProducerObservable(mCustomers, mCustomerMarkersSubscriber);
    }

    // TODO: implemented new logic
    private void setupZoneOnMapByBeacon() {
//        Subscription zoneSubscription = mMapService
//                .createBeaconZoneObservabe()
    }


    private void setupCustomerMarkersOnMap() {
//        Subscription markersOnMapSubscription = mMapService
//                .create
    }

    private Subscriber<Bitmap> mMapSubscriber = new Subscriber<Bitmap>() {
        @Override
        public void onCompleted() {
            produceCustomerMarkers();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Map Subscriber - " +  e.toString());
        }

        @Override
        public void onNext(Bitmap bitmap) {
            mHolderLayout.setMapWidth(bitmap.getWidth());
            mHolderLayout.setMapHeight(bitmap.getHeight());
            mMapImageView.setImageBitmap(bitmap);
        }
    };

    private Subscriber<CustomerMarker> mCustomerMarkersSubscriber = new Subscriber<CustomerMarker>() {
        @Override
        public void onCompleted() {
            Log.e(LOG_TAG, mCustomerMarkers.toString());
            setupZoneOnMapByBeacon();
//            setupCustomerMarkersOnMap();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Customer Markers Subscriber - " + e.toString());
        }

        @Override
        public void onNext(CustomerMarker marker) {
            String customerId = marker.getCustomerId();
            mCustomerMarkers.put(customerId, marker);
        }
    };



}
