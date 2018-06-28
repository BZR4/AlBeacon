package com.esdras.albeacon;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BeaconConsumer{

    private static final String TAG = "MainActivity";
    private static final int COARSE_LOCATION = 1;
    private static final String UUID = "FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
    private static final String layoutAltBeacon = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final String layoutIBeacon = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static boolean inRegion = false;
    String d = "";

    BeaconManager beaconManager;
    Region mRegion;
    TextView textView, textViewStatus;
    CardView cardView;
    int color;

    Button buttonStart, buttonStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView = (TextView)findViewById(R.id.textViewDistance);
        textViewStatus = (TextView)findViewById(R.id.textViewStatusProximity);
        cardView = (CardView)findViewById(R.id.cardView);
        buttonStart = (Button)findViewById(R.id.buttonStart);
        buttonStop = (Button)findViewById(R.id.buttonStop);
        buttonStart.setEnabled(false);
        buttonStop.setEnabled(false);

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
//         beaconManager.getBeaconParsers().add(new BeaconParser().
//                setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mRegion = new Region("beacon-esdras",Identifier.parse(UUID), Identifier.fromInt(274),Identifier.fromInt(12972));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(layoutIBeacon));

        verifyPermission();

        beaconManager.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void verifyPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    message("É necessário a permissao de Localização do Beacons, por favor autorize para o aplicativo funcionar corretmente.");
                }else {
                    message("É necessário a permissao de Localização do Beacons, por favor autorize para o aplicativo funcionar corretmente.");
                }
            }else{
                beaconManager.bind(this);
            }
        }
    }

    private void message(String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(this)
                .setTitle("Permissão Necessária")
                .setMessage(message)
                .setNegativeButton("Cancelar",null)
                .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},COARSE_LOCATION);
                    }
                });
        alert.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),"Permissao Garantida, binding beacons",Toast.LENGTH_SHORT).show();
                    beaconManager.bind(this);
                }else {
                    Toast.makeText(getApplicationContext(),"Permissao Negada",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Date currentTime = Calendar.getInstance().getTime();
                Log.i("Enter region: ","  "+region.getUniqueId()+"\tDate: "+currentTime);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonStart.setEnabled(true);
                        buttonStop.setEnabled(true);
                    }
                });

                beaconNotification();

                try {
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i("Exit region: ",region.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        buttonStart.setEnabled(false);
                        buttonStop.setEnabled(false);
                    }
                });

            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.i("State region: ",i+"");
            }
        });

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> collection, Region region) {
                if (collection.size() > 0){
                    Log.i("Region: ",region.getUniqueId()+"\tBeacon: UUID: "+collection.iterator().next().getId1()
                            +"\tMajor: "+collection.iterator().next().getId2()
                            +"\tMinor: "+collection.iterator().next().getId3()
                    );
                    float distance = (float) collection.iterator().next().getDistance();

                    if (distance < 1.0){
                        Log.i("Range: ","Imediatly");
                        d = "Imediatly";
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cardView.setBackgroundColor(Color.YELLOW);
                            }
                        });
                    }else if (distance < 10){
                        Log.i("Range: ","Near");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cardView.setBackgroundColor(Color.GREEN);
                            }
                        });
                        d = "Near";
                    }else if (distance < 20){
                        Log.i("Range: ","Longe");
                        d = "Far";
                    }else {
                        Log.i("Range: ","Desconhecido");
                        d = "Unknown";
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double distance = collection.iterator().next().getDistance();
                            textView.setText(String.format("%.3f",distance));
                            textViewStatus.setText(d.toString());

                        }
                    });
                    Log.i("Range: ",collection.iterator().next().getDistance()+ " meters");
                }
            }
        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("beacon-esdras",Identifier.fromUuid(java.util.UUID.fromString(UUID)),null,null));
        } catch (RemoteException e) {
            Log.i("Exception: ",e.getLocalizedMessage());
        }
    }

    public void beaconNotification() {
        Uri notificationSoundUri =
                RingtoneManager.getDefaultUri(
                        RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Beacon detectado")
                .setContentText("Marque sua presença")
                .setSound(notificationSoundUri)
                .setLights(Color.MAGENTA, 500, 500)
                .setVibrate(new long[]{125,250,250,500,250,500,125,250});

            //Setting Action
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        //Add Button
        builder.addAction(android.R.drawable.ic_dialog_email, "Chamada",pendingIntent);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    public void iniciaChamada(View view){
        Date currentTime = Calendar.getInstance().getTime();
        Log.i("Time: ",String.valueOf(currentTime));
    }

    public void finalizaChamada(View view){
        Date currentTime = Calendar.getInstance().getTime();
        Log.i("Time: ",String.valueOf(currentTime));
    }
}
