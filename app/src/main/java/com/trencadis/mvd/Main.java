package com.trencadis.mvd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Kimv on 4/8/2015.
 */
public class Main extends Activity {

    private static final int BLUETOOTH_INTENT_CODE = 100;

    private Button lbbController, succesiveConnections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        startBluetooth();

        findViews();

        setClicks();

    }

    private void setClicks() {
        lbbController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, LBBController.class);
                startActivity(intent);
            }
        });

        succesiveConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, SuccesiveConnections.class);
                startActivity(intent);
            }
        });
    }

    private void findViews() {

        lbbController = (Button) findViewById(R.id.lbb_controller);
        succesiveConnections = (Button) findViewById(R.id.succesive_connections);
    }

    private void startBluetooth() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if(!adapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intent, BLUETOOTH_INTENT_CODE);
        }

    }
}
