package com.example.sharejsondata;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class ServerActivity extends AppCompatActivity implements View.OnClickListener {

    private Button send;
    private EditText firstName, lastName;
    private String strFirstName="", strLastName="";
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        send = findViewById(R.id.sendServer);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        status = findViewById(R.id.status);

        status.setText(getIpAddress());

        send.setOnClickListener(this);
    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: " + inetAddress.getHostAddress() + "\n";
                    }
                }
            }
            return ip;
        } catch (Exception e) {
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendServer:
                Toast.makeText(getApplicationContext(),"Mesage Sent...",Toast.LENGTH_SHORT).show();

                strFirstName = firstName.getText()+"";
                strLastName = lastName.getText()+"";
                try {
                    JSONObject sendData = new JSONObject();
                    sendData.put("first",strFirstName);
                    sendData.put("last",strLastName);
                    new ServerThread().execute(sendData.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    public class ServerThread extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... jsonString) {
            try{
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();

                DataOutputStream dataOutputStream = new DataOutputStream(client.getOutputStream());

                dataOutputStream.writeBytes(jsonString[0]);
                dataOutputStream.flush();
                dataOutputStream.close();

                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (status){
                Toast.makeText(getApplicationContext(),"Data Posted...",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
