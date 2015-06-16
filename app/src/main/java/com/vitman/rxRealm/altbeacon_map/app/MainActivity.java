package com.vitman.rxRealm.altbeacon_map.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.*;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.DataBase;
import com.vitman.rxRealm.altbeacon_map.app.entity.PandaBeacon;
import com.vitman.rxRealm.altbeacon_map.app.layout.TouchRelativeLayout;
import com.vitman.rxRealm.altbeacon_map.app.util.BitmapHelper;
import com.vitman.rxRealm.altbeacon_map.app.util.LayoutBuilder;
import com.vitman.rxRealm.altbeacon_map.app.util.ViewBuilder;
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

    private int startPositionX;
    private int startPositionY;

    private float mOriginalMapWidth;
    private float mOriginalMapHeight;
    private float mMapWidth;
    private float mMapHeight;

    private BeaconManager mBeaconManager;
    private DataBase mDataBase;
    private LayoutBuilder mLayoutBuilder;
    private ViewBuilder mViewBuilder;

    private List<Customer> mCustomers;
    private Map<Integer, PandaBeacon> mBeacons;
    //    private List<GridLayout> mCustomersLayouts = new ArrayList<>();
    private List<LinearLayout> mCustomersLayouts = new ArrayList<>();
    private Map<String, ImageView> mCustomerMarkers = new HashMap<>();

    private BitmapHelper mBitmapHelper;

    private float mScale;
    private int mMarkerWidth;
    private int mMarkerHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataBase = DataBase.getInstance();
        mBitmapHelper = new BitmapHelper();
        mCustomers = mDataBase.getCustomers();
        mBeacons = mDataBase.getPandaBeacons();
        mBeaconManager = BeaconManager.getInstanceForApplication(MainActivity.this);
        mBeaconManager.bind(MainActivity.this);

        mMarkerWidth = (int) getResources()
                .getDimension(R.dimen.indoor_map_marker_width);
        mMarkerHeight = (int) getResources()
                .getDimension(R.dimen.indoor_map_marker_width);

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
            }
        };
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

                                        Bitmap greyBitmap = mBitmapHelper.getGrayscaleBitmap(bitmap);
                                        ImageView marker = mViewBuilder
                                                .getMarker(greyBitmap, customer.getUserId(), mMarkerWidth);
                                        marker.setTag(customer.getBeaconId());

                                        return new Pair(customer.getUserId(), marker);
                                    }
                                });
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(customerMarkersResultSubscriber());
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

    //Setup customers layouts on map at appropriate position

    private void setupCustomerLayoutsOnMap() {
        createCustomerLayoutObservable()
                .map(setupCustomerMarkersOnLayoutOperation())
                .doOnNext(saveCustomerLayoutsOperation())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(customerLayoutResultSubscriber());
    }

    private Observable<GridLayout> createCustomerLayoutObservable() {
        return Observable.create(new Observable.OnSubscribe<GridLayout>() {
            @Override
            public void call(Subscriber<? super GridLayout> subscriber) {
                Log.e(LOG_TAG, "createCustomerLayoutObservable " + Thread.currentThread().getName());

                mCustomersLayouts.clear();

                for (Map.Entry<Integer, PandaBeacon> entry : mDataBase.getPandaBeacons().entrySet()) {
                    PandaBeacon beacon = entry.getValue();
                    Point positionPoint = getPointPositionByBeacon(beacon);
                    double strength = beacon.getStrenght();
//                    GridLayout gridLayout = getGridLayoutWithRelativeLayoutParams(positionPoint, strength);
                    GridLayout gridLayout = mLayoutBuilder.getGridLayoutWithLinearLayoutParams(strength);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridLayout.getLayoutParams();

                    int columnCount = params.width / mMarkerWidth;
                    int rowCount = params.height / mMarkerHeight;
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

    private Func1<GridLayout, LinearLayout> setupCustomerMarkersOnLayoutOperation() {
        return new Func1<GridLayout, LinearLayout>() {
            @Override
            public LinearLayout call(GridLayout gridLayout) {

                Log.e(LOG_TAG, "setupCustomerMarkersOnLayoutOperation");

                int beaconIdTag = (int) gridLayout.getTag();

                List<Customer> customers = new ArrayList<>();
                for (Customer customer : mCustomers) {
                    if (customer.getBeaconId() == beaconIdTag) {
                        customers.add(customer);
                        if (customer.getUserId().equals("current_user_id")) {
                            Collections.swap(customers, 0, customers.size() - 1);
                        }
                    }
                }

//                RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) gridLayout.getLayoutParams();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gridLayout.getLayoutParams();

                float widthMargin = params.width - mMarkerWidth * gridLayout.getColumnCount();
                widthMargin /= gridLayout.getColumnCount() + 1;

                float heightMargin = params.height - mMarkerHeight * gridLayout.getRowCount();
                heightMargin /= gridLayout.getRowCount() + 1;

                LinearLayout linearLayout = mLayoutBuilder.getLinearLayout();

                TextView zoneTitle = mViewBuilder.getZoneTitleTextView(customers.get(0).getBeaconId());

                linearLayout.addView(zoneTitle);

                for (int row = 0; row < gridLayout.getRowCount(); row++) {
                    for (int column = 0; column < gridLayout.getColumnCount(); column++) {
                        int customerRemains = customers.size() - gridLayout.getChildCount();
                        if (row + 1 == gridLayout.getRowCount() && customerRemains > 0) {
                            TextView textView = mViewBuilder.getCustomerRemainsTextView(customerRemains);

                            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                            param.height = GridLayout.LayoutParams.MATCH_PARENT;
                            param.width = GridLayout.LayoutParams.MATCH_PARENT;
param.setMargins(20, 20, 20 , 20);
                            param.setGravity(Gravity.CENTER);
                            param.columnSpec = GridLayout.spec(column, gridLayout.getColumnCount());
                            param.rowSpec = GridLayout.spec(row);
                            textView.setLayoutParams(param);

                            gridLayout.addView(textView);
                            break;
                        }

                        int item = row * gridLayout.getRowCount() + column;
                        if (customers.size() > item) {
                            Customer customer = customers.get(item);
                            ImageView marker = mCustomerMarkers.get(customer.getUserId());
                            if (marker != null) {
                                GridLayout.LayoutParams markerParams = mLayoutBuilder
                                        .getGridLayoutParamForMarker(mMarkerWidth, mMarkerHeight, column, row);
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
                linearLayout.addView(gridLayout);
                int remainsCustomers = customers.size() - gridLayout.getChildCount();
                if (remainsCustomers > 0) {
                    TextView customerRemainsTextView = mViewBuilder.getCustomerRemainsTextView
                            (remainsCustomers);
                    linearLayout.addView(customerRemainsTextView);
                }

                linearLayout.setTag(beaconIdTag);
                return linearLayout;
            }
        };
    }

    private Action1<LinearLayout> saveCustomerLayoutsOperation() {
        return new Action1<LinearLayout>() {
            @Override
            public void call(LinearLayout gridLayout) {
                mCustomersLayouts.add(gridLayout);
            }
        };
    }

    private Subscriber<LinearLayout> customerLayoutResultSubscriber() {
        Subscriber<LinearLayout> result = new Subscriber<LinearLayout>() {
            @Override
            public void onCompleted() {
                Log.e(LOG_TAG, "On completed " + Thread.currentThread().getName());
                for (LinearLayout linearLayout : mCustomersLayouts) {
                    final LinearLayout layout = linearLayout;
                    linearLayout.getViewTreeObserver().addOnGlobalLayoutListener
                            (new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    int beaconId = (int) layout.getTag();
                                    PandaBeacon beacon = mBeacons.get(beaconId);
                                    Point positionPoint = getPointPositionByBeacon(beacon);
                                    layout.setTranslationX(positionPoint.x - layout.getWidth() / 2);
                                    layout.setTranslationY(positionPoint.y - layout.getHeight() / 2);
                                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "On Error - ", e);
            }

            @Override
            public void onNext(LinearLayout linearLayout) {
                Log.e(LOG_TAG, "On next");
                mMapLayout.addView(linearLayout);
            }
        };
        return result;
    }

    private Point getPointPositionByBeacon(PandaBeacon beacon) {
        Point point = new Point();
        point.x = (int) convertXValueToView(beacon.getX());
        point.y = (int) convertYValueToView(beacon.getY());
        return point;
    }

    private void initMapSize(Bitmap bitmap) {
        mMapWidth = bitmap.getWidth();
        mMapHeight = bitmap.getHeight();
        mScale = mMapWidth / mOriginalMapWidth;
        if (mScale < mMapHeight / mOriginalMapHeight) {
            mScale = mMapHeight / mOriginalMapHeight;
        }

        //TODO: do some refactor
        mMarkerWidth = (int) mMapWidth / 14;
        mMarkerHeight = mMarkerWidth;

        mLayoutBuilder = new LayoutBuilder(MainActivity.this, mScale);
        mViewBuilder = new ViewBuilder(MainActivity.this);
    }

    private double convertValueToView(double value) {
        return (value * mScale * 100); //Floor scale
    }

    private double convertXValueToView(double value) {
        return startPositionX + convertValueToView(value);
    }

    private double convertYValueToView(double value) {
        return startPositionY + convertValueToView(value);
    }

    private void initOriginalMapSize(Bitmap bitmap) {
        mOriginalMapWidth = bitmap.getWidth();
        mOriginalMapHeight = bitmap.getHeight();
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
