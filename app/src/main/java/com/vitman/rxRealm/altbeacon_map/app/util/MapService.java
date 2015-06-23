package com.vitman.rxRealm.altbeacon_map.app.util;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.*;
import com.vitman.rxRealm.altbeacon_map.app.R;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.CustomerMarker;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Victor Artemjev on 18.06.2015.
 */
public class MapService {

    private static final String LOG_TAG = MapService.class.getCanonicalName();
    private static final int MARKER_RELATIVE_SIZE = 14;

    private Activity mActivity;

    private float mOriginalMapWidth;
    private float mOriginalMapHeight;
    private float mCurrentMapWidth;
    private float mCurrentMapHeight;
    private float mScale;
    private int mMarkerSize;

    private Point mMapStartPoint = new Point();

    private BitmapHelper mBitmapHelper;
    private ViewBuilder mViewBuilder;
    private LayoutBuilder mLayoutBuilder;



    public MapService(Activity activity) {
        mActivity = activity;
        mViewBuilder = new ViewBuilder(activity);
        mBitmapHelper = new BitmapHelper();
    }

    public float getOriginalMapWidth() {
        return mOriginalMapWidth;
    }

    public void setOriginalMapWidth(float originalMapWidth) {
        mOriginalMapWidth = originalMapWidth;
    }

    public float getOriginalMapHeight() {
        return mOriginalMapHeight;
    }

    public void setOriginalMapHeight(float originalMapHeight) {
        mOriginalMapHeight = originalMapHeight;
    }

    public float getCurrentMapWidth() {
        return mCurrentMapWidth;
    }

    public void setCurrentMapWidth(float currentMapWidth) {
        mCurrentMapWidth = currentMapWidth;
    }

    public float getCurrentMapHeight() {
        return mCurrentMapHeight;
    }

    public void setCurrentMapHeight(float currentMapHeight) {
        mCurrentMapHeight = currentMapHeight;
    }

    public Point getMapStartPoint() {
        return mMapStartPoint;
    }

    public void setMapStartPoint(Point mapStartPoint) {
        mMapStartPoint = mapStartPoint;
    }

    public int getMarkerSize() {
        return mMarkerSize;
    }

    public void setMarkerSize(int markerSize) {
        mMarkerSize = markerSize;
    }

    private void initOriginalMapSize(Bitmap originalMap) {
        mOriginalMapWidth = originalMap.getWidth();
        mOriginalMapHeight = originalMap.getHeight();
    }

    private void initCurrentMapSizeAndScale(Bitmap bitmap) {
        mCurrentMapWidth = bitmap.getWidth();
        mCurrentMapHeight = bitmap.getHeight();

        mScale = mCurrentMapWidth / mOriginalMapWidth;
        if (mScale < mCurrentMapHeight / mOriginalMapHeight) {
            mScale = mCurrentMapHeight / mOriginalMapHeight;
        }
    }

    private void initMarkerSize() {
        mMarkerSize = (int) mCurrentMapWidth / MARKER_RELATIVE_SIZE;
    }

    private void initMapStartPoint(ImageView map) {
        int x = map.getWidth();
        int y = map.getHeight();
        mMapStartPoint.x = (int) ((x - mCurrentMapWidth) / 2);
        mMapStartPoint.y = (int) ((y - mCurrentMapHeight) / 2);
    }

    private void initLayoutBuilder() {
        mLayoutBuilder = new LayoutBuilder(mActivity, mScale);
    }



    // ===================== create and setup map =============================

    public Subscription createMapBitmapObservable(int resourceId,
                                                  Subscriber<Bitmap> subscriber) {
        return Observable.create(getMapBitmapOnSubscribe(resourceId))
                .map(getScaleMapBitmapOperation())
                .map(getMapSizeOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private Observable.OnSubscribe<Bitmap> getMapBitmapOnSubscribe(final int resourceId) {
        return new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
                Bitmap originalMapBitmap = BitmapFactory
                        .decodeResource(mActivity.getResources(), resourceId, options);
                subscriber.onNext(originalMapBitmap);
                subscriber.onCompleted();
            }
        };
    }

    private Func1<Bitmap, Bitmap> getScaleMapBitmapOperation() {
        return new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                initOriginalMapSize(bitmap);
                Display display = mActivity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int displayWidth = size.x;
                Bitmap scaledMapBitmap = mBitmapHelper.scaleToFitWidth(bitmap, displayWidth);
                return scaledMapBitmap;
            }
        };
    }

    private Func1<Bitmap, Bitmap> getMapSizeOperation() {
        return new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                initCurrentMapSizeAndScale(bitmap);
                initMarkerSize();
                return bitmap;
            }
        };
    }

    // ==================== produce customer markers ======================================

    public Subscription createCustomerMarkerProducerObservable(List<Customer> customers,
                                                               Subscriber<CustomerMarker> subscriber) {
        return Observable.from(customers)
                .flatMap(getCustomerMarkersOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private Func1<Customer, Observable<CustomerMarker>> getCustomerMarkersOperation() {
        return new Func1<Customer, Observable<CustomerMarker>>() {
            @Override
            public Observable<CustomerMarker> call(Customer customer) {
                return Observable
                        .zip(getCustomerMarkerBitmapObservable(customer),
                                Observable.just(customer),
                                getCustomerMarkerZipOperation());
            }
        };
    }

    private Observable<Bitmap> getCustomerMarkerBitmapObservable(final Customer customer) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                String customerAvatarUrl = customer.getAvatarUrl();
                Log.e(LOG_TAG, customerAvatarUrl);
                Bitmap avatarBitmap = mBitmapHelper.getBitmapFromURL(customerAvatarUrl);
                if(avatarBitmap != null) {
                    subscriber.onNext(avatarBitmap);
                }
            }
        }).map(new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                if (bitmap == null) {
                    return BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.map);
                }
                Bitmap greyAvatarBitmap = mBitmapHelper.getGrayscaleBitmap(bitmap);
                return greyAvatarBitmap;
            }
        });
    }

    private Func2<Bitmap, Customer, CustomerMarker> getCustomerMarkerZipOperation() {
        return new Func2<Bitmap, Customer, CustomerMarker>() {
            @Override
            public CustomerMarker call(Bitmap bitmap, Customer customer) {
                String customerId = customer.getUserId();
                Integer beaconId = customer.getBeaconId();
                ImageView marker = mViewBuilder
                        .getMarker(bitmap, customerId, mMarkerSize);
                marker.setTag(beaconId);
                return new CustomerMarker(customerId, beaconId, marker);
            }
        };
    }

    // ============================== setup beacon zones ======================================

    public Subscription createBeaconZoneObservable(ImageView mapImageView, Map<Integer, PandaBeacon> beacons,
//                                                   Subscriber<GridLayout> subscriber) {
                                                   Subscriber<LinearLayout> subscriber) {
        initMapStartPoint(mapImageView);
        initLayoutBuilder();
        return Observable.create(getBeaconZoneOnSubscriber(beacons))
                .map(getAddTitleToLayoutOperation())
                .map(getAddGridLayoutToLayoutOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    private Observable.OnSubscribe<LinearLayout> getBeaconZoneOnSubscriber(final Map<Integer, PandaBeacon> beaconMap) {
        return  new Observable.OnSubscribe<LinearLayout>() {
            @Override
            public void call(Subscriber<? super LinearLayout> subscriber) {
                for (Map.Entry<Integer, PandaBeacon> entry : beaconMap.entrySet()) {
                    PandaBeacon beacon = entry.getValue();
                    LinearLayout layout = mLayoutBuilder
                            .getLinearLayoutWithRelativeLayoutParams(beacon, mMapStartPoint);
                    layout.setTag(entry.getKey());
                    subscriber.onNext(layout);
                }
                subscriber.onCompleted();
            }
        };
    }

    private Func1<LinearLayout, LinearLayout> getAddTitleToLayoutOperation() {
        return new Func1<LinearLayout, LinearLayout>() {
            @Override
            public LinearLayout call(LinearLayout layout) {
                int beaconId = (int) layout.getTag();
                TextView title = mViewBuilder.getZoneTitleTextView(beaconId);
                layout.addView(title);
                return layout;
            }
        };
    }

    private Func1<LinearLayout, LinearLayout> getAddGridLayoutToLayoutOperation() {
        return new Func1<LinearLayout, LinearLayout>() {
            @Override
            public LinearLayout call(LinearLayout layout) {
                GridLayout gridLayout = mLayoutBuilder.getGridLayoutWithLinearLayoutParams();
                gridLayout.setTag(layout.getTag());
                layout.addView(gridLayout);
                return layout;
            }
        };
    }


    // ================================ setup markers on map ====================================

    public Subscription createCustomerMarkerOnMapObservable(List<Customer> customers,
                                                            Map<String, CustomerMarker> customerMarkers,
                                                            List<GridLayout> beaconZoneLayouts,
                                                            Subscriber<GridLayout> subscriber) {
        return Observable.create(getMarkerOnMapOnSubscribe(customers, customerMarkers, beaconZoneLayouts))

                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    public Observable.OnSubscribe<GridLayout> getMarkerOnMapOnSubscribe(final List<Customer> allCustomers,
                                                                        final Map<String, CustomerMarker> customerMarkers,
                                                                        final List<GridLayout> beaconZoneLayouts) {
        return new Observable.OnSubscribe<GridLayout>() {
            @Override
            public void call(Subscriber<? super GridLayout> subscriber) {
                for (GridLayout gridLayout : beaconZoneLayouts) {
                    Log.e(LOG_TAG, "setupCustomerMarkersOnLayoutOperation");

                    int beaconIdTag = (int) gridLayout.getTag();

                    List<Customer> customers = new ArrayList<>();
                    for (Customer customer : allCustomers) {
                        if (customer.getBeaconId() == beaconIdTag) {
                            customers.add(customer);
                            if (customer.getUserId().equals("current_user_id")) {
                                Collections.swap(customers, 0, customers.size() - 1);
                            }
                        }
                    }

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
                    int columnCount = params.width / mMarkerSize;
                    int rowCount = params.height / mMarkerSize;
                    gridLayout.setColumnCount(columnCount);
                    gridLayout.setRowCount(rowCount);

                    if (gridLayout.getColumnCount() > customers.size()) {
                        gridLayout.setColumnCount(customers.size());
                        gridLayout.setRowCount(1);
                    }

                    if (gridLayout.getColumnCount() != 0 &&
                            gridLayout.getRowCount() > customers.size() / gridLayout.getColumnCount()) {
                        gridLayout.setRowCount((int) Math.ceil((double) customers.size() / gridLayout.getColumnCount()));
                    }

                    float widthMargin = params.width - mMarkerSize * gridLayout.getColumnCount();
                    widthMargin /= gridLayout.getColumnCount() + 1;

                    float heightMargin = params.height - mMarkerSize * gridLayout.getRowCount();
                    heightMargin /= gridLayout.getRowCount() + 1;

//                    TextView zoneTitle = mViewBuilder.getZoneTitleTextView(beaconIdTag);
//                    GridLayout.LayoutParams param =new GridLayout.LayoutParams();
//                    param.height = GridLayout.LayoutParams.MATCH_PARENT;
//                    param.width = GridLayout.LayoutParams.MATCH_PARENT;
//                    param.setGravity(Gravity.CENTER);
//                    param.columnSpec = GridLayout.spec(0, gridLayout.getColumnCount());
//                    param.rowSpec = GridLayout.spec(0);
//                    zoneTitle.setLayoutParams(param);
//                    gridLayout.addView(zoneTitle);

                    for (int row = 0; row < gridLayout.getRowCount(); row++) {
//                        if (row == 0) {
//                            TextView zoneTitle = mViewBuilder.getZoneTitleTextView(beaconIdTag);
//                            GridLayout.LayoutParams param = new GridLayout.LayoutParams();
//                            param.height = GridLayout.LayoutParams.MATCH_PARENT;
//                            param.width = GridLayout.LayoutParams.MATCH_PARENT;
//                            param.setGravity(Gravity.CENTER);
//                            param.columnSpec = GridLayout.spec(0, gridLayout.getColumnCount());
//                            param.rowSpec = GridLayout.spec(0);
//                            zoneTitle.setLayoutParams(param);
//                            gridLayout.addView(zoneTitle);
//                        }

                        for (int column = 0; column < gridLayout.getColumnCount(); column++) {

                            int customerRemains = customers.size() - gridLayout.getChildCount();
//                        if (row + 1 == gridLayout.getRowCount() && customerRemains > 0) {
//                            TextView textView = mViewBuilder.getCustomerRemainsTextView(customerRemains);
//
//                            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
//                            param.height = GridLayout.LayoutParams.MATCH_PARENT;
//                            param.width = GridLayout.LayoutParams.MATCH_PARENT;
//                            param.setGravity(Gravity.CENTER);
//                            param.columnSpec = GridLayout.spec(column, gridLayout.getColumnCount());
//                            param.rowSpec = GridLayout.spec(row);
//                            textView.setLayoutParams(param);
//                            gridLayout.addView(textView);
//                            break;
//                        }

                            int item = row * gridLayout.getColumnCount() + column;
                            Log.e("User", item + " " + gridLayout.getTag());
                            if (customers.size() > item) {
                                Customer customer = customers.get(item);

                                ImageView marker = customerMarkers.get(customer.getUserId()).getMarker();
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
                    Log.e(LOG_TAG, "Layout child count - " + gridLayout.getChildCount());
                    subscriber.onNext(gridLayout);
                }

            }
        };
    }
}


