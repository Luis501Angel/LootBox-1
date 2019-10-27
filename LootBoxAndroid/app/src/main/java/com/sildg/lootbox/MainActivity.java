package com.sildg.lootbox;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity {

    private static final int REQUEST_PERMISSIONS = 100;
    private static final String TAG = "MainActivity";
    boolean boolean_permission;

    TextView latIn, lonIn, ciudadIn, estadoIn, ubicacionIn;

    Double lat, lon;
    Geocoder geocoder;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        askPermission();
        startLocationTracking();
        start();

        HttpUrl url = new HttpUrl();

        url.access("mnm", 10, 10);
        geocoder = new Geocoder(this, Locale.getDefault());
    }

    private void initWidgets() {
        latIn = (TextView) findViewById(R.id.latIn);
        lonIn = (TextView) findViewById(R.id.lonIn);
        ciudadIn = (TextView) findViewById(R.id.ciudadIn);
        estadoIn = (TextView) findViewById(R.id.estadoIn);
        ubicacionIn = (TextView) findViewById(R.id.ubicacionIn);
    }

    private void start() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = "InstanceID Token: " + token;
                        Log.d(TAG, "--------->" + msg);
                    }
                });

        FirebaseMessaging.getInstance().subscribeToTopic("ofertas")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed to weather topic";
                        if (!task.isSuccessful()) {
                            msg = "Failed to subscribe";
                        }
                        Log.d(TAG, "------------------>" + msg);
                    }
                });
    }

    private void startLocationTracking() {
        if (boolean_permission) {
            intent = new Intent(getApplicationContext(), GoogleService.class);
            startService(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Please enable the gps", LENGTH_SHORT).show();
        }
    }

    private void askPermission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            } else {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS
                );
            }
        } else {
            boolean_permission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    boolean_permission = true;
                } else {
                    Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lat = Double.valueOf(intent.getStringExtra("latutide"));
            lon = Double.valueOf(intent.getStringExtra("lon"));

            Address address = null;

            try {
                address = geocoder.getFromLocation(lat, lon, 1).get(0);

                //for(Address address: addresses) Toast.makeText(getApplicationContext(), address.toString(), Toast.LENGTH_LONG).show();

            } catch (IOException e1) {
                e1.printStackTrace();
            }

            latIn.setText(lat.toString());
            lonIn.setText(lon.toString());
            ciudadIn.setText(address.getLocality());
            estadoIn.setText(address.getAdminArea());
            if(checkInsideLocation(new LatLng(19.309959, -99.177147), 10, lat, lon))
            {
                ubicacionIn.setText("Adentro del establecimiento");
            }
            else if(checkOutsideLocation(new LatLng(19.309959, -99.177147), 25, lat, lon))
            {
                ubicacionIn.setText("Alrededor del establecimiento");
            }
            else
            {
                ubicacionIn.setText("No se encuentra el establecimiento");
            }
        }
    };

    private boolean checkInsideLocation(LatLng inside, double radious, double lat, double lon) {
        float[] distance = new float[2];

        Location.distanceBetween(lat, lon, inside.latitude, inside.longitude, distance);

        if(distance[0] > radious ){
            //return "Afuera del establecimiento";
            return false;
        } else {
            //return "Adentro del establecimiento";
            return true;
        }
    }

    private boolean checkOutsideLocation(LatLng outside, double radious, double lat, double lon) {
        float[] distance = new float[2];

        Location.distanceBetween(lat, lon, outside.latitude, outside.longitude, distance);

        if (distance[0] > radious) {
            //return "No se encuentra en el area";
            return false;
        } else {
            //return "Alrededor del establecimiento";
            return true;
        }
    }


}
