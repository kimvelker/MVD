package com.trencadis.mvd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.trencadis.mvd.global.DataBase;
import com.trencadis.mvd.global.Entry;
import com.trencadis.mvd.global.Global;
import com.trencadis.mvd.internet.ConnectionDetector;
import com.trencadis.mvd.internet.Parser;

import java.util.ArrayList;

/**
 * Created by Kimv on 4/8/2015.
 */
public class Main extends Activity {

    private static final int BLUETOOTH_INTENT_CODE = 100;
    private static final long DELAY_BETWEEN_CONNECTIONS = 1000 * 60 * 5; // 5 minutes

    private static final String METHOD = "METHOD";
    private static final String METHOD_VALUE = "storeData";
    private static final String API_KEY = "API_KEY";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String ID_LBB = "ID_LBB";
    private static final String ID_SENZOR = "ID_SENZOR";
    private static final String TIP_SENZOR = "TIP_SENZOR";
    private static final String VALUE_FROM = "VALUE_FROM";
    private static final String VALUE_TO = "VALUE_TO";
    private static final String LAST_MESSAGE = "LAST_MESSAGE";

    private Button lbbController, succesiveConnections;

    private ArrayList<Entry> entries;
    private int nextEntryIndex;

    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        startBluetooth();

        findViews();

        setClicks();

        startWebAPI();

    }

    private void startWebAPI() {
        db = DataBase.getInstance(this);
        entries = db.getEntries();

        if(ConnectionDetector.isConnected(this)){

            nextEntryIndex = 0;
            if(entries.size() > 0) {
                sendEntry(entries.get(nextEntryIndex));
            }else{
                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startWebAPI();
                    }
                }, DELAY_BETWEEN_CONNECTIONS);
            }
        }
    }

    private void sendEntry(final Entry entry) {

        System.out.println("Sending entry...");

        if(!entry.mustSend()){
            sendNextEntry();
            return;
        }

        Parser parser = new Parser(this, Parser.OBJECT){

            @Override
            protected void onPostExecute(Void result) {

                db.setEntrySent(entry);

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendNextEntry();
                    }
                }, 2000);
            }
        };

        parser.addParam(METHOD, METHOD_VALUE);
        parser.addParam(API_KEY, Global.API_KEY);
        parser.addParam(TIMESTAMP, entry.getTimestamp());
        parser.addParam(ID_LBB, entry.getLbbId());
        parser.addParam(ID_SENZOR, entry.getSensorId());
        parser.addParam(TIP_SENZOR, entry.getType());
        parser.addParam(VALUE_FROM, entry.getValueFrom());
        parser.addParam(VALUE_TO, entry.getValueTo());
        parser.addParam(LAST_MESSAGE, entry.getLastMessage());

        parser.execute();

    }

    private void sendNextEntry() {
        nextEntryIndex++;

        if(nextEntryIndex >= entries.size()){
            Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startWebAPI();
                }
            }, DELAY_BETWEEN_CONNECTIONS);
        }else{
            sendEntry(entries.get(nextEntryIndex));
        }

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
