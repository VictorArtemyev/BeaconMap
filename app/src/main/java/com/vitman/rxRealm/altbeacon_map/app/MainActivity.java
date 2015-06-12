package com.vitman.rxRealm.altbeacon_map.app;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.DataBase;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;
import com.vitman.rxRealm.altbeacon_map.app.layout.TouchRelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.util.BitmapHelper;
import net.grobas.view.PolygonImageView;
import org.altbeacon.beacon.*;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import java.util.*;

public class MainActivity extends Activity implements BeaconConsumer {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private TouchRelativeLayout mHolderLayout;
    private RelativeLayout mMapLayout;
    private ImageView mMapImageView;
    private ImageView mMarkerImageView;

    private int startPositionX;
    private int startPositionY;

    private float mOriginalMapWidth;
    private float mOriginalMapHeight;
    private float mMapWidth;
    private float mMapHeight;

    private BeaconManager mBeaconManager;
    private DataBase mDataBase;

    private List<Customer> mCustomers;
    private List<GridLayout> mCustomersGrids = new ArrayList<>();
    private Map<String, ImageView> mCustomerMarkers = new HashMap<>();

    private BitmapHelper mBitmapHelper;

    float mScale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataBase = DataBase.getInstance();
        mBitmapHelper = new BitmapHelper();
        mCustomers = mDataBase.getCustomers();
        mBeaconManager = BeaconManager.getInstanceForApplication(MainActivity.this);
        mBeaconManager.bind(MainActivity.this);
        initViews();
        setupMap();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeaconManager.isBound(this)) mBeaconManager.setBackgroundMode(false);
    }

    private void initViews() {
        mHolderLayout = (TouchRelativeLayout) findViewById(R.id.holder_layout);
        mMapLayout = (RelativeLayout) findViewById(R.id.map_layout);
        mMarkerImageView = (ImageView) findViewById(R.id.marker_imageView);
        mMapImageView = (ImageView) findViewById(R.id.map_imageView);
    }

    // Setting up map on layout

    private void setupMap() {
        createImageBitmapObservable()
                .map(createScaleImageBitmapOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createMapResultSubscriber());
    }

    private Observable<Bitmap> createImageBitmapObservable() {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
                Bitmap originalMapBitmap = BitmapFactory
                        .decodeResource(getResources(), R.drawable.map, options);
                subscriber.onNext(originalMapBitmap);
                subscriber.onCompleted();
            }
        });
    }

    private Action1<Bitmap> createMapResultSubscriber() {
        return new Action1<Bitmap>() {
            @Override
            public void call(Bitmap bitmap) {
                setupImageOnLayout(bitmap);
                initMapSize(bitmap);
                customerMarkersObservable();
//                createUserMarkerObservable();
            }
        };
    }

    private void createUserMarkerObservable() {
        Observable.create(createUserMarkerSubscribe())
                .map(createGrayscaleBitmapOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        setupUserMarkerOnMap(bitmap);
                    }
                });
    }

    // Fetch customers markers
    private void customerMarkersObservable() {
        Observable.from(mCustomers)
                .flatMap(new Func1<Customer, Observable<Pair>>() {
                    @Override
                    public Observable<Pair> call(Customer customer) {
                        Log.e(LOG_TAG, customer.getAvatarUrl());
                        Bitmap bitmap = mBitmapHelper.getBitmapFromURL(customer.getAvatarUrl());
                        return Observable.zip(Observable.just(bitmap),
                                Observable.just(customer),
                                new Func2<Bitmap, Customer, Pair>() {
                                    @Override
                                    public Pair call(Bitmap bitmap, Customer customer) {
                                        ImageView marker = getCustomerMarker(bitmap, customer);
                                        return new Pair(customer.getUserId(), marker);
                                    }
                                });
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(customerMarkersResultSubscriber());
    }

    private ImageView getCustomerMarker(Bitmap bitmap, Customer customer) {
        PolygonImageView marker = new PolygonImageView(MainActivity.this);
        Bitmap greyBitmap = mBitmapHelper.getGrayscaleBitmap(bitmap);
        marker.setBorder(true);
        if (customer.getUserId().equals("current_user_id")) {
            marker.setBorderColorResource(R.color.pink);
        } else {
            marker.setBorderColorResource(R.color.black);
        }
        marker.setBorderWidth(8f);
        marker.setCornerRadius(5f);
        marker.setVertices(0);
        marker.setImageBitmap(greyBitmap);
        marker.setTag(customer.getBeaconId());
        return marker;
    }

    private Subscriber customerMarkersResultSubscriber() {
        return new Subscriber<Pair>() {
            @Override
            public void onCompleted() {
                initStartPositionOnMap();
                setupCustomerLayoutsOnMap();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                Log.e(LOG_TAG, e.toString());
            }

            @Override
            public void onNext(Pair pair) {
                mCustomerMarkers.put((String) pair.first, (ImageView) pair.second);
            }
        };
    }

    private Observable<GridLayout> createCustomerLayoutObservable() {
        return Observable.create(new Observable.OnSubscribe<GridLayout>() {
            @Override
            public void call(Subscriber<? super GridLayout> subscriber) {
                Log.e(LOG_TAG, "createCustomerLayoutObservable " + Thread.currentThread().getName());

                mCustomersGrids.clear();

                for (Map.Entry<Integer, PandaBeacon> entry : mDataBase.getPandaBeacons().entrySet()) {
                    GridLayout gridLayout = getGridLayout(getGridLayoutPositionPoint(entry.getValue()), entry.getValue().getStrenght());
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)gridLayout.getLayoutParams();

                    int columnCount = params.width / 80;
                    int rowCount = params.height / 80;
                    int beaconId = entry.getKey();
                    gridLayout.setTag(beaconId);
                    gridLayout.setColumnCount(columnCount);
                    gridLayout.setRowCount(rowCount);
                    subscriber.onNext(gridLayout);
                }
                subscriber.onCompleted();
            }
        });
    }

    //Setup customers layouts on map at appropriate position

    private void setupCustomerLayoutsOnMap() {
        createCustomerLayoutObservable()
                .map(setupCustomerMarkersOnLayoutOperation())
                .doOnNext(saveCustomerLayoutsOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(customerLayoutResultSubscriber());
    }

    private Func1<GridLayout, GridLayout> setupCustomerMarkersOnLayoutOperation() {
        return new Func1<GridLayout, GridLayout>() {
            @Override
            public GridLayout call(GridLayout gridLayout) {

                //todo:
                Log.e(LOG_TAG, "setupCustomerMarkersOnLayoutOperation");

                int beaconIdTag = (int) gridLayout.getTag();
                int columnNumber = 0;
                int rowNumber = 0;
                int guestsNumber = mCustomerMarkers.size();
                Log.e(LOG_TAG, "" + mCustomerMarkers.size());
                if (mCustomerMarkers.containsKey("current_user_id")) {
                    ImageView userMarker = mCustomerMarkers.get("current_user_id");
                    if ((int) userMarker.getTag() == beaconIdTag) {
                        userMarker.setLayoutParams(getGridLayoutParamForCustomerMarker(columnNumber, rowNumber));
                        gridLayout.addView(userMarker);
                        columnNumber++;
                        mCustomerMarkers.remove("current_user_id");
                    }
                }
//
//                gridLayout.removeAllViews();


                List<Customer>customers = new ArrayList<>();
                for (Customer customer : mCustomers) {
                    if (customer.getBeaconId() == beaconIdTag)
                        customers.add(customer);
                }


                for (int i = 0; i < gridLayout.getRowCount(); i++){
                    for (int j = 0; j < gridLayout.getColumnCount(); j++){
                        if (customers.size() > i * gridLayout.getRowCount() +j) {
                            Customer customer = customers.get(i * gridLayout.getRowCount() + j);
                            ImageView marker = mCustomerMarkers.get(customer.getUserId());
                            if (marker != null) {
                                marker.setLayoutParams(getGridLayoutParamForCustomerMarker(j, i));
                                gridLayout.addView(marker);
                            }
                        }
                    }
                }
                

//                for (Map.Entry<String, ImageView> entry : mCustomerMarkers.entrySet()) {
//                    ImageView marker = entry.getValue();
//                    if ((int) marker.getTag() == beaconIdTag) {
//                        if (gridLayout.getColumnCount() == columnNumber) {
//                            columnNumber = 0;
//                            rowNumber++;
//                        }
//
//                        if (rowNumber + 1 == gridLayout.getRowCount()) {
//                            View textView = getTextView(guestsNumber - 6);
//                            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//                            params.width = GridLayout.LayoutParams.MATCH_PARENT;
//                            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
//                            params.setGravity(Gravity.CENTER);
//                            params.columnSpec = GridLayout.spec(columnNumber, 3);
//                            params.rowSpec = GridLayout.spec(rowNumber);
//                            textView.setLayoutParams(params);
//                            gridLayout.addView(textView);
//                            break;
//                        }
//                        marker.setLayoutParams(getGridLayoutParamForCustomerMarker(columnNumber++, rowNumber));
//                        gridLayout.addView(marker);
//                    }
//                }
                return gridLayout;
            }
        };
    }

    private Action1<GridLayout> saveCustomerLayoutsOperation() {
        return new Action1<GridLayout>() {
            @Override
            public void call(GridLayout gridLayout) {
                mCustomersGrids.add(gridLayout);
            }
        };
    }

    private Subscriber<GridLayout> customerLayoutResultSubscriber() {
        Subscriber<GridLayout> result = new Subscriber<GridLayout>() {
            @Override
            public void onCompleted() {
                Log.e(LOG_TAG, "On completed " + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "On Error - ", e);
            }

            @Override
            public void onNext(GridLayout gridLayout) {
                Log.e(LOG_TAG, "On next");
                mMapLayout.addView(gridLayout);
            }
        };
        return result;
    }

    // Grid layout

    private GridLayout getGridLayout(Point point, double strength) {
        GridLayout gridLayout = new GridLayout(MainActivity.this);

//        int width = (int) getResources().getDimension(R.dimen.guest_layout_width);
//        int height = (int) getResources().getDimension(R.dimen.guest_layout_height);
        int width = (int)converValueToView(strength);
        int height = (int)converValueToView(strength);


        width = (int)(width * Resources.getSystem().getDisplayMetrics().density);
        height = (int)(height * Resources.getSystem().getDisplayMetrics().density);
        RelativeLayout.LayoutParams params = new RelativeLayout
                .LayoutParams(width, height);
        params.leftMargin = point.x - width / 2;
        params.topMargin = point.y - height / 2;
        gridLayout.setLayoutParams(params);

        gridLayout.setBackgroundColor(Color.argb(100,0,0,255));
        return gridLayout;
    }

    private GridLayout.LayoutParams getGridLayoutParamForCustomerMarker(int columnSpec, int rowSpec) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 80;
        params.height = 80;
        params.setGravity(Gravity.CENTER);
        params.columnSpec = GridLayout.spec(columnSpec);
        params.rowSpec = GridLayout.spec(rowSpec);
        return params;
    }

    private TextView getTextView(int customerNumber) {
        TextView textView = new TextView(MainActivity.this);
        textView.setTextColor(Color.BLACK);
        textView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        textView.setText("+" + customerNumber + " more");
        textView.setBackgroundColor(Color.BLUE);
        textView.setTextSize(getResources().getDimension(R.dimen.text_size));
        return textView;
    }

    private Point getGridLayoutPositionPoint(PandaBeacon beacon) {
        Point point = new Point();
        point.x = (int)convertXValueToView(beacon.getX());
        point.y = (int)convertYValueToView(beacon.getY());
        return point;
    }

    private void initMapSize(Bitmap bitmap) {
        mMapWidth = bitmap.getWidth();
        mMapHeight = bitmap.getHeight();
        mScale = mMapWidth / mOriginalMapWidth;
        if (mScale < mMapHeight / mOriginalMapHeight)
        {
            mScale = mMapHeight / mOriginalMapHeight;
        }
    }

    private double converValueToView(double value){
        return value * mScale * 100; //Floor scale
    }

    private double convertXValueToView(double value) {
        return startPositionX + converValueToView(value);
    }

    private double convertYValueToView(double value) {
        return startPositionY + converValueToView(value);
    }

    private void initOriginalMapSize(Bitmap bitmap) {
        mOriginalMapWidth = bitmap.getWidth();
        mOriginalMapHeight = bitmap.getHeight();
    }

    private void setupUserMarkerOnMap(Bitmap bitmap) {
        mMarkerImageView.setVisibility(View.VISIBLE);
        mMarkerImageView.setImageBitmap(bitmap);
        translateMarker(startPositionX, startPositionY);

        mMarkerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (GridLayout gridLayout : mCustomersGrids) {
                    Log.e(LOG_TAG, "W - " + gridLayout.getWidth() + " H - " + gridLayout.getHeight() + " Tag - " + gridLayout.getTag());
                    gridLayout.removeAllViews();
                    mMapLayout.removeView(gridLayout);
                }
                customerMarkersObservable();
            }
        });
    }

    private void translateMarker(int x, int y) {
        Point markerCenter = new Point(mMarkerImageView.getWidth() / 2, mMarkerImageView.getHeight() / 2);
        mMarkerImageView.setTranslationX(x - markerCenter.x);
        mMarkerImageView.setTranslationY(y - markerCenter.y);
    }

    private void initStartPositionOnMap() {
        int x = mMapImageView.getWidth();
        int y = mMapImageView.getHeight();
        startPositionX = (int) ((x - mMapWidth) / 2);
        startPositionY = (int) ((y - mMapHeight) / 2);
    }

    private void setupImageOnLayout(Bitmap bitmap) {
        mHolderLayout.setMapWidth(bitmap.getWidth());
        mHolderLayout.setMapHeight(bitmap.getHeight());
        mMapImageView.setImageBitmap(bitmap);
    }

    private Func1<Bitmap, Bitmap> createGrayscaleBitmapOperation() {
        return new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                return mBitmapHelper.getGrayscaleBitmap(bitmap);
            }
        };
    }

    private Observable.OnSubscribe<Bitmap> createUserMarkerSubscribe() {
        return new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(final Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = mBitmapHelper
                        .getBitmapFromURL("https://pbs.twimg.com/media/CGkmwmHU8AAl1IC.png");
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }
        };
    }

    // ===========================  BEACONS ============================

    private Observable<Beacon> createBeaconObservable() {
        return Observable.create(new Observable.OnSubscribe<Beacon>() {
            @Override
            public void call(final Subscriber<? super Beacon> subscriber) {

                mBeaconManager.setRangeNotifier(new RangeNotifier() {
                    @Override
                    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//                        Log.e(LOG_TAG, "Beacon size - " + beacons.size());
                        if (beacons.size() > 0) {
                            Beacon firstBeacon = beacons.iterator().next();
                            subscriber.onNext(firstBeacon);
                        } else {

                            try {
                                mBeaconManager.startRangingBeaconsInRegion
                                        (new Region("myRangingUniqueId", null, null, null));
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                try {
                    mBeaconManager.startRangingBeaconsInRegion
                            (new Region("myRangingUniqueId", null, null, null));
                } catch (RemoteException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {
        createBeaconObservable()
                .filter(new Func1<Beacon, Boolean>() {
                    @Override
                    public Boolean call(Beacon beacon) {
                        return beacon.getDistance() < 1.0;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Beacon>() {
                    @Override
                    public void call(Beacon beacon) {
                        Toast.makeText(MainActivity.this, beacon.toString(), Toast.LENGTH_LONG).show();
                        for (Map.Entry<Integer, PandaBeacon> entry : mDataBase.getPandaBeacons().entrySet()) {
                            if (String.valueOf(entry.getKey()).equals(beacon.getId2().toString())) {
                                int x = getMarkerCurrentPositionX(entry.getValue());
                                int y = getMarkerCurrentPositionY(entry.getValue());
                                Log.e(LOG_TAG, "X - " + x + " Y - " + y);
                                translateMarker(x, y);
                            }
                        }
                    }
                });
    }

    private int getMarkerCurrentPositionX(PandaBeacon beacon) {
        int x = startPositionX + (int) ((beacon.getX() * (mMapLayout.getWidth() / mMapWidth)) * 100);
        Log.e(LOG_TAG, "getMarkerCurrentPositionX - " + x);
        return x;
    }

    private int getMarkerCurrentPositionY(PandaBeacon beacon) {
        int y = startPositionY + (int) ((beacon.getY() * (mMapLayout.getHeight() / mMapHeight)) * 100);
        Log.e(LOG_TAG, "getMarkerCurrentPositionY - " + y);
        return y;
    }

    private Func1<Bitmap, Bitmap> createScaleImageBitmapOperation() {
        return new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                initOriginalMapSize(bitmap);
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int displayWidth = size.x;
                Bitmap scaledMapBitmap = mBitmapHelper.scaleToFitWidth(bitmap, displayWidth);
                return scaledMapBitmap;
            }
        };
    }
}
