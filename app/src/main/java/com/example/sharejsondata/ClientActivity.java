package com.example.sharejsondata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText getIp;
    private TextView showJson;
    private Button receiveData;
    private String serverIpAddress="";
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        getIp = findViewById(R.id.getIp);
        showJson = findViewById(R.id.showJson);
        receiveData = findViewById(R.id.receiveClient);

        turnOnWifi(this);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);


        receiveData.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.receiveClient:
                Toast.makeText(getApplicationContext(),"Mesage Received...",Toast.LENGTH_SHORT).show();
                serverIpAddress = getIp.getText().toString().trim();
                new ClientThread().execute();
                break;
            default:
                break;
        }
    }

    private static void turnOnWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled())
        {
            wifiManager.setWifiEnabled(true);
        }
    }


    public class ClientThread extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try{

                InetAddress serverAddr =InetAddress.getByName(serverIpAddress);
                Socket socket = new Socket(serverAddr, 8888);

                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                String jsonData = dataInputStream.readLine();

                Log.d("rough","JSON Data: "+jsonData);

                return jsonData;
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (!TextUtils.isEmpty(jsonData)){
                Toast.makeText(getApplicationContext(),"Data Posted...",Toast.LENGTH_SHORT).show();
                showJson.setText(jsonData);
            }
        }
    }
}
