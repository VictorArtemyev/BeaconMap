package com.vitman.rxRealm.altbeacon_map.app;

import android.app.Application;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

/**
 * Created by Victor Artemjev on 02.06.2015.
 */
public class BeaconMapApp extends Application implements BootstrapNotifier {

    private static final String LOG_TAG = BeaconMapApp.class.getCanonicalName();

    private static final String BEACON_ESTIMOTE_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private RegionBootstrap mRegionBootstrap;
    private BackgroundPowerSaver mBackgroundPowerSaver;
    private boolean mHaveDetectedBeaconsSinceBoot = false;

    @Override
    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = BeaconManager
                .getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BEACON_ESTIMOTE_LAYOUT));

        Region region = new Region("backgroundRegion",
                null, null, null);
        mRegionBootstrap = new RegionBootstrap(this, region);
        mBackgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    @Override
    public void didEnterRegion(Region region) {

    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
