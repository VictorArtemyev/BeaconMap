package com.vitman.rxRealm.altbeacon_map.app.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.ImageView;
import com.vitman.rxRealm.altbeacon_map.app.entity.Customer;
import com.vitman.rxRealm.altbeacon_map.app.entity.CustomerMarker;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

import java.util.List;

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

    private Point mMapStartPoint;

    private BitmapHelper mBitmapHelper;
    private final ViewBuilder mViewBuilder;

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
        mMapStartPoint.x = (int) ((x - mOriginalMapWidth) / 2);
        mMapStartPoint.y = (int) ((y - mOriginalMapHeight) / 2);
    }

    // create and setup on ui club map
    public Subscription createMapBitmapObservable(int resourceId, Subscriber<Bitmap> subscriber) {
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

    //create customer markers

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
                                getCustomerIdObservable(customer),
                                getBeaconIdObservable(customer),
                                getCustomerMarkerZipOperation());
            }
        };
    }

    private Observable<Bitmap> getCustomerMarkerBitmapObservable(final Customer customer) {
        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                String customerAvatarUrl = customer.getAvatarUrl();
                Bitmap avatarBitmap = mBitmapHelper.getBitmapFromURL(customerAvatarUrl);
                subscriber.onNext(avatarBitmap);
            }
        }).map(new Func1<Bitmap, Bitmap>() {
            @Override
            public Bitmap call(Bitmap bitmap) {
                Bitmap greyAvatarBitmap = mBitmapHelper.getGrayscaleBitmap(bitmap);
                return greyAvatarBitmap;
            }
        });
    }

    private Observable<String> getCustomerIdObservable(final Customer customer) {
        return Observable.just(customer.getUserId());
    }

    private Observable<Integer> getBeaconIdObservable(final Customer customer) {
        return Observable.just(customer.getBeaconId());
    }

    private Func3<Bitmap, String, Integer, CustomerMarker> getCustomerMarkerZipOperation() {
        return new Func3<Bitmap, String, Integer, CustomerMarker>() {
            @Override
            public CustomerMarker call(Bitmap bitmap, String customerId, Integer beaconId) {
                ImageView marker = mViewBuilder
                        .getMarker(bitmap, customerId, mMarkerSize);
                marker.setTag(beaconId);
                return new CustomerMarker(customerId, beaconId, marker);
            }
        };
    }

}


