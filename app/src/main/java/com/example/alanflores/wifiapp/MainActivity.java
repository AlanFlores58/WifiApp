package com.example.alanflores.wifiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    WifiManager wifiManager;
    private BroadcastReceiverWifi broadcastReceiverWifi;
    final int REQUEST_FINE_LOCATION = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(mayRequestLocation()){
            listView = (ListView)findViewById(android.R.id.list);
            wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
            wifiManager.setWifiEnabled(true);
            broadcastReceiverWifi = new BroadcastReceiverWifi();
            estadoWifi();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mayRequestLocation())
            unregisterReceiver(broadcastReceiverWifi);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mayRequestLocation())
            registerReceiver(broadcastReceiverWifi,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    private void estadoWifi(){
        Log.v("entro","entro");
        if(wifiManager.isWifiEnabled()){
            Log.v("mas","mas");
            wifiManager.startScan();
        }
    }

    private boolean mayRequestLocation() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The requested permission is granted.
                    listView = (ListView)findViewById(android.R.id.list);
                    wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                    broadcastReceiverWifi = new BroadcastReceiverWifi();
                    estadoWifi();
                    registerReceiver(broadcastReceiverWifi,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                }
                else{
                    // The user disallowed the requested permission.
                }
                return;
            }

        }
    }

    class BroadcastReceiverWifi extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> listaEscaneo = wifiManager.getScanResults();
            String[] listaWifi = new String[listaEscaneo.size()];
            Log.v("entro","entro2");

            for(int i = 0; i < listaEscaneo.size(); i++){
                Log.v("entro2","entro2");
                    listaWifi[i] = (listaEscaneo.get(i).SSID) + "\n"
                            +listaEscaneo.get(i).frequency + "\n"
                            +listaEscaneo.get(i).capabilities + "\n"
                            +String.valueOf(listaEscaneo.get(i).timestamp);
            }
            Log.v("entro2","entro2");
            listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, listaWifi));
        }
    }
}
