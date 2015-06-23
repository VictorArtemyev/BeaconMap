package com.vitman.rxRealm.altbeacon_map.app.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.vitman.rxRealm.altbeacon_map.app.R;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.CustomerMarker;
import com.vitman.rxRealm.altbeacon_map.app.entity.DataBase;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;
import com.vitman.rxRealm.altbeacon_map.app.layout.TouchRelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.util.LayoutBuilder;
import com.vitman.rxRealm.altbeacon_map.app.util.MapService;
import com.vitman.rxRealm.altbeacon_map.app.util.ViewBuilder;
import rx.Subscriber;
import rx.Subscription;

import java.util.*;

/**
 * Created by Victor Artemjev on 18.06.2015.
 */
public class MapActivity extends AppCompatActivity {

    private static final String LOG_TAG = MapActivity.class.getSimpleName();

    private ProgressDialog mProgressDialog;

    private DataBase mDataBase;
    private MapService mMapService;

    private TouchRelativeLayout mHolderLayout;
    private RelativeLayout mMapLayout;
    private ImageView mMapImageView;

    // list of all customers on floor
    private List<Customer> mCustomers;

    // list of customer layouts which contains all customers divided into zones by beacons
    private List<GridLayout> mBeaconZoneLayouts = new ArrayList<>();

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

    public void onClick(View view) {
        setupCustomerMarkersOnMap();
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

    private void clearBeaconZoneLayouts() {
        for (GridLayout layout : mBeaconZoneLayouts) {
            layout.removeAllViews();
//            layout.invalidate();
        }
    }

    private void setupMap() {
        showProgressDialog();
        Subscription mapSubscription = mMapService
                .createMapBitmapObservable(R.drawable.devabit_map, mMapSubscriber);

    }

    private void produceCustomerMarkers() {
        Subscription markersSubscribtion = mMapService
                .createCustomerMarkerProducerObservable(mCustomers, mCustomerMarkersSubscriber);
    }

    private void setupBeaconZoneOnMap() {
        Subscription zoneSubscription = mMapService
                .createBeaconZoneObservable(mMapImageView, mBeacons, mBeaconZoneSubscriber);
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(MapActivity.this);
        mProgressDialog.setMessage("initialization map...");
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void setupCustomerMarkersOnMap() {
        clearBeaconZoneLayouts();
        for (GridLayout gridLayout : mBeaconZoneLayouts) {
            Log.e(LOG_TAG, "setupCustomerMarkersOnLayoutOperation");

            int beaconIdTag = (int) gridLayout.getTag();

            List<Customer> customersByZone = new ArrayList<>();
            for (Customer customer : mCustomers) {
                if (customer.getBeaconId() == beaconIdTag) {
                    customersByZone.add(customer);
                    if (customer.getUserId().equals("current_user_id")) {
                        Collections.swap(customersByZone, 0, customersByZone.size() - 1);
                    }
                }
            }
            int mMarkerSize = (int) (mMapService.getCurrentMapWidth() / 14);
            int layoutWidth = gridLayout.getWidth();
            int layoutHeight = gridLayout.getHeight();

            int columnCount = layoutWidth / mMarkerSize;
            int rowCount = layoutHeight / mMarkerSize;

            gridLayout.setColumnCount(columnCount);
            gridLayout.setRowCount(rowCount);

            if (gridLayout.getColumnCount() > customersByZone.size()) {
                gridLayout.setColumnCount(customersByZone.size());
                gridLayout.setRowCount(2);
            }

            if (gridLayout.getColumnCount() != 0 &&
                    gridLayout.getRowCount() > customersByZone.size() / gridLayout.getColumnCount()) {
                rowCount = (int) Math.ceil((double) customersByZone.size() / gridLayout.getColumnCount());
                gridLayout.setRowCount(rowCount);
            }

            float widthMargin = layoutWidth - mMarkerSize * gridLayout.getColumnCount();
            widthMargin /= gridLayout.getColumnCount() + 1;

            float heightMargin = layoutHeight - mMarkerSize * gridLayout.getRowCount();
            heightMargin /= gridLayout.getRowCount() + 1;

            int customerCapacity = gridLayout.getRowCount() * gridLayout.getColumnCount();
            int customerRemains = 0;
            if (customersByZone.size() > customerCapacity) {
                customerRemains = customersByZone.size() - customerCapacity;
                customerRemains += gridLayout.getColumnCount();
            }

            Log.e(LOG_TAG, "tag - " + gridLayout.getTag()
                    + " capacity - " + customerCapacity + " customer remains - " + customerRemains);

            for (int row = 0; row < gridLayout.getRowCount(); row++) {
                if (row == gridLayout.getRowCount() - 1 && customerRemains > 0) {
                    TextView textView = new ViewBuilder(MapActivity.this)
                            .getCustomerRemainsTextView(customerRemains);
                    GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                    param.height = GridLayout.LayoutParams.MATCH_PARENT;
                    param.width = GridLayout.LayoutParams.MATCH_PARENT;
                    param.setGravity(Gravity.CENTER);
                    param.columnSpec = GridLayout.spec(0, gridLayout.getColumnCount());
                    param.rowSpec = GridLayout.spec(row);
                    textView.setLayoutParams(param);
                    gridLayout.addView(textView);
                    break;
                }
                for (int column = 0; column < gridLayout.getColumnCount(); column++) {
                    int item = (row) * gridLayout.getColumnCount() + column;
                    Log.e("User", item + " " + gridLayout.getTag());
                    if (customersByZone.size() > item) {
                        Customer customer = customersByZone.get(item);

                        ImageView marker = mCustomerMarkers.get(customer.getUserId()).getMarker();
                        if (marker != null) {
                            GridLayout.LayoutParams markerParams = LayoutBuilder
                                    .getGridLayoutParamForMarker(mMarkerSize,
                                            mMarkerSize, column, row);
                            markerParams.leftMargin = column > 0 ?
                                    (int) widthMargin / 2 : (int) widthMargin;

                            markerParams.rightMargin = column < gridLayout.getColumnCount() - 1 ?
                                    (int) widthMargin / 2 : (int) widthMargin;

                            markerParams.topMargin = row > 0 ?
                                    (int) heightMargin / 2 : (int) heightMargin;

                            markerParams.bottomMargin = row < gridLayout.getRowCount() - 1 ?
                                    (int) heightMargin / 2 : (int) heightMargin;
                            marker.setLayoutParams(markerParams);
                            gridLayout.addView(marker);
                        }
                    }
                }
            }
            gridLayout.invalidate();
        }
    }

    private Subscriber<Bitmap> mMapSubscriber = new Subscriber<Bitmap>() {
        @Override
        public void onCompleted() {
            mMapImageView.getViewTreeObserver().addOnGlobalLayoutListener
                    (new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            setupBeaconZoneOnMap();
                            mMapImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Map Subscriber - " + e);
        }

        @Override
        public void onNext(Bitmap bitmap) {
            mHolderLayout.setMapWidth(bitmap.getWidth());
            mHolderLayout.setMapHeight(bitmap.getHeight());
            mMapImageView.setImageBitmap(bitmap);
        }
    };

    //     
    private Subscriber<CustomerMarker> mCustomerMarkersSubscriber = new Subscriber<CustomerMarker>() {
        @Override
        public void onCompleted() {
            Log.e(LOG_TAG, mCustomerMarkers.toString());
//            setupBeaconZoneOnMap();
            hideProgressDialog();
            setupCustomerMarkersOnMap();
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Customer Markers Subscriber - " + e);
        }

        @Override
        public void onNext(CustomerMarker marker) {
            String customerId = marker.getCustomerId();
            mCustomerMarkers.put(customerId, marker);
        }
    };

    private Subscriber<LinearLayout> mBeaconZoneSubscriber = new Subscriber<LinearLayout>() {
        @Override
        public void onCompleted() {
//            setupCustomerMarkersOnMap();
            produceCustomerMarkers();
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, "Beacon Zone Subscriber - " + e);
            e.printStackTrace();
        }

        @Override
        public void onNext(LinearLayout linearLayout) {
            GridLayout gridLayout = (GridLayout) linearLayout.getChildAt(1);
            mBeaconZoneLayouts.add(gridLayout);
            mMapLayout.addView(linearLayout);
        }
    };

    private Subscriber<GridLayout> mMarkersOnMapSubscriber = new Subscriber<GridLayout>() {
        @Override
        public void onCompleted() {
            Log.e(LOG_TAG, "on completed - mMarkersOnMapSubscriber");
            Log.e(LOG_TAG, mBeaconZoneLayouts.size() + "");
            for (GridLayout layout : mBeaconZoneLayouts) {
                if (layout != null) {
                    Log.e(LOG_TAG, layout.getChildCount() + "");
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            Log.e(LOG_TAG, " Markers On Map Subscriber - " + e);
            e.printStackTrace();
        }

        @Override
        public void onNext(GridLayout gridLayout) {
            gridLayout.invalidate();
            Log.e(LOG_TAG, "invalidate");
        }
    };
}
