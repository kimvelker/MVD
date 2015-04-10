package com.trencadis.mvd;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.trencadis.mvd.global.DataBase;
import com.trencadis.mvd.global.Entry;
import com.trencadis.mvd.global.Global;
import com.trencadis.mvd.internet.ConnectionDetector;
import com.trencadis.mvd.internet.Parser;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Activity {

    private static final int BLUETOOTH_INTENT_CODE = 100;
    private static final long DELAY_BETWEEN_CONNECTIONS = 1000 * 60 * 5; // 5 minutes

    private static final int SUCCESS = 1;

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
    private static final String STATUS = "status";

    private Button lbbController, succesiveConnections;

    private ArrayList<Entry> entries;
    private int nextEntryIndex;

    private DataBase db;

    private String regId = null;
    private GoogleCloudMessaging gcm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        startBluetooth();

        findViews();

        setClicks();

        startWebAPI();

        registerForNotifications();

    }

    private void registerForNotifications() {

        new AsyncTask<Void, Void, Void>() {

            private boolean isOk;

            @Override
            protected Void doInBackground(Void... params) {
                isOk = true;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging
                                .getInstance(getApplicationContext());
                    }

                    regId = gcm.register(Global.GCM_PROJECT_ID);
                    Log.i("GCM", "!!!!! " + regId);

                    if (regId == null) {
                        isOk = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    isOk = false;
                }
                return null;
            }

            protected void onPostExecute(Void result) {
                if (!isOk) {
                    try {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                registerForNotifications();
                            }
                        }, 1000);
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                        isOk = false;
                    }
                } else {
                    System.out.println("regId is set");
                    setNotifications();
                    TextView textView = (TextView) findViewById(R.id.regId);
                    textView.setText(regId);

                }
            };

        }.execute();
    }

    private void setNotifications() {
        // TODO
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

                try {
                    if (getObject().getInt(STATUS) == SUCCESS) {
                        db.setEntrySent(entry);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

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
