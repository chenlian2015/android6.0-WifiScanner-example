package com.yunhuayuan.chenlian.testwifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity  {
    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView)findViewById(R.id.listView);

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        }
        else {


            wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            wifiReciever = new WifiScanReceiver();
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();



        }

        initView();

        initChannelName();

    }

    String password = "xinyuan1997";

    private void connectOneOfScanedResult(String wifiName, String passWord)
    {

        beforeOpenDoorWifi = wifi.getConnectionInfo().getNetworkId();
        wifi.setWifiEnabled(true);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();

        List lst = wifi.getConfiguredNetworks();


        wc.SSID = "\""+wifiName+"\"";
        wc.preSharedKey = "\""+passWord+"\"";

        wc.status = WifiConfiguration.Status.DISABLED;
        wc.priority = 40;
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        // connect to and enable the connection
        int netId = wifi.addNetwork(wc);

        boolean bEnabledNet = wifi.enableNetwork(netId, true);
        boolean bConnect =  wifi.reconnect();
        wifi.setWifiEnabled(true);
    }

    public void initChannelName()
    {

        try {
            ApplicationInfo ai = this.getPackageManager().getApplicationInfo("com.yunhuayuan.chenlian.testwifi", PackageManager.GET_META_DATA);
            String channelName = ai.metaData.getString("UMENG_CHANNEL");
            TextView tv = (TextView) findViewById(R.id.id_tv_channel_name);
            tv.setText(channelName);
        }catch (Exception e)
        {
            Log.e("MainA", e.toString());
        }
    }

    protected void onPause() {
      //  unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
            wifiReciever = new WifiScanReceiver();
            registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifi.startScan();
        }
    }

    private int beforeOpenDoorWifi = -1;
    public void initView()
    {


        findViewById(R.id.id_bt_connect_wifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText etName = (EditText) findViewById(R.id.id_et_name);
                final String wifiName = etName.getText().toString();
                EditText etPassword = (EditText) findViewById(R.id.id_et_password);
                final String wifiPassword = etPassword.getText().toString();

                MainActivity.this.connectOneOfScanedResult(wifiName, wifiPassword);
            }
        });

        findViewById(R.id.enable_last_wift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                wifi.enableNetwork(beforeOpenDoorWifi, true);
            }
        });
    }
    private class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            wifis = new String[wifiScanList.size()];


            for(int i = 0; i < wifiScanList.size(); i++){
                wifis[i] = ((wifiScanList.get(i)).toString());
                ScanResult sr = wifiScanList.get(i);


            }

            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
        }
    }
}