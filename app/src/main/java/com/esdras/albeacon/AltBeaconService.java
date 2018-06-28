package com.esdras.albeacon;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.UUID;

/**
 * Created by esdras on 21/10/17.
 */

public class AltBeaconService extends Application implements BootstrapNotifier {

    private static final String TAG = "AltBeaconService";
    private static final String UUID = "FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
    private static final String layoutAltBeacon = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final String layoutIBeacon = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private RegionBootstrap regionBootstrap;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"App Started");

        //Beacon Setup
        //Older Layout "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(layoutIBeacon));
        Region region = new Region("beacon-esdras", Identifier.fromUuid(java.util.UUID.fromString(UUID)),Identifier.fromInt(2712),null);
        regionBootstrap = new RegionBootstrap(this,region);
    }

    @Override
    public void didEnterRegion(Region region) {
        Log.i(TAG,"Entrou em uma regiao com beacon.\nBeacon: "+region.getId1());
        regionBootstrap.disable();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }
}
