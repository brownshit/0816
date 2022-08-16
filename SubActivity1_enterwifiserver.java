package com.practice.wpsactivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SubActivity1_enterwifiserver extends AppCompatActivity {

    //SubActivity1에서 버튼을 누르면 이쪽으로 넘어온다.
    /**
     * 해당 javafile에서 와이파이에 연결하고
     * 와이파이 서버에 연결하는데까지 구성하자.
     * client로서의 역할을 수행하기 위해 소켓을 구성하고
     * 해당내용을 받아오는것까지 구성한다. 받아오게 되면
     * 다음 java파일로 넘어가서 xml파일로 정보가 전달되고
     * 전달된 정보에서 수량 추가하여 이벤트 버스로 전송한다.
     * <p>
     * needs : 서버 구축을 해야함
     * 1. Wifi connect
     * WPA2 Wifi에 연결하는 것을 aim
     * 2. server belongs to Wifi connect _by TCP/IP
     **/


    //아래의 내용 전부 수정할 것.
    //동적이긴 한데 sub_1하고 리사이클 뷰에서 클릭하면 넘어가는 창 과의 intent

    //아래부터는 와이파이 연결하는 자바 코딩

    Context context;

    static final int PERMISSIONS_REQUEST = 0x0000001;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;


    private WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    // 현제 SSID 를 받는 코드
    public String getWiFiSSID() {
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        String sSSID = connectionInfo.getSSID();
        String s = sSSID.substring(1, sSSID.length() - 1);
        return s;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview_item_sub);

        /**
         OnCheckPermission(); // 권한 요청하기 1
         checkSystemPermission(); // 권한 요청하기 2
         * **/
        context = this;

        Button btnConnect = (Button) findViewById(R.id.btn_connect);
        Button btnDisconnect = (Button) findViewById(R.id.btn_disconnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connectWifi();
                }
            });


        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });

    }

    void connectWifi() {
        try {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder();
                builder.setSsid("WIFI 이름"); // 연결하고자 하는 SSID
                builder.setWpa2Passphrase("비밀번호"); // 비밀번호

                WifiNetworkSpecifier wifiNetworkSpecifier = builder.build();

                final NetworkRequest.Builder networkRequestBuilder1 = new NetworkRequest.Builder();
                networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);
                networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier);

                NetworkRequest networkRequest = networkRequestBuilder1.build();
                networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                        connectivityManager.bindProcessToNetwork(network);
                        Toast.makeText(getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
                    }
                };

                connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
                connectivityManager.requestNetwork(networkRequest, networkCallback);

            } else {
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                wifiConfiguration.SSID = String.format("\"%s\"", "wifi 이름"); // 연결하고자 하는 SSID
                wifiConfiguration.preSharedKey = String.format("\"%s\"", "비밂번호"); // 비밀번호
                int wifiId = wifiManager.addNetwork(wifiConfiguration);
                wifiManager.enableNetwork(wifiId, true);
                Toast.makeText(getApplicationContext(), "연결됨", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "연결 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    void Disconnect() {
        try {
            if (wifiManager.isWifiEnabled()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    connectivityManager.unregisterNetworkCallback(networkCallback);
                    Toast.makeText(getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();

                } else {
                    if (wifiManager.getConnectionInfo().getNetworkId() == -1) {
                        Toast.makeText(getApplicationContext(), "연결", Toast.LENGTH_SHORT).show();

                    } else {
                        int networkId = wifiManager.getConnectionInfo().getNetworkId();
                        wifiManager.removeNetwork(networkId);
                        wifiManager.saveConfiguration();
                        wifiManager.disconnect();
                        Toast.makeText(getApplicationContext(), "연결 끊김", Toast.LENGTH_SHORT).show();
                    }
                }

            } else
                Toast.makeText(getApplicationContext(), "Wifi 꺼짐", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "연결 해제 예외 : " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
    /*
    * public void OnCheckPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ssid_now.setText(String.valueOf(getWiFiSSID()));
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 취소 되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean checkSystemPermission() {

        boolean permission = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   //23버전 이상
            permission = Settings.System.canWrite(this);
            if (permission) {
            } else {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 2127);
                permission = false;
            }
        } else {

        }

        return permission;
    }
    *
    *
    *
    * */
//ssid_now
