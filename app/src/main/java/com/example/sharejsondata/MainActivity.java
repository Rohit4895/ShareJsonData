package com.example.sharejsondata;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button send, receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = findViewById(R.id.senderButton);
        receive = findViewById(R.id.receiverButton);

        send.setOnClickListener(this);
        receive.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.senderButton:
                intent = new Intent(MainActivity.this,ServerActivity.class);
                startActivity(intent);
                break;
            case R.id.receiverButton:
                intent = new Intent(MainActivity.this,ClientActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
