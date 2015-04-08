package com.trencadis.mvd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import com.trencadis.mvd.global.DataBase;
import com.trencadis.mvd.global.Entry;
import com.trencadis.mvd.global.TempDataBase;

import java.util.ArrayList;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanListener;
import nl.littlerobots.bean.BeanManager;

/**
 * Created by Kimv on 4/6/2015.
 */
public class SuccesiveConnections extends Activity implements BeanDiscoveryListener, BeanListener{

    private static final String ENUM_COUNT = "enum";
    private static final String GET_SENSOR_ID = "get ID_SENZOR";
    private static final String GET_LBB_ID = "lbbId";

    private static final long DELAY_BEFORE_SENDING_FIRST_MESSAGE = 15000;

    private ArrayList<Bean> foundBeans = new ArrayList<>();

    private Bean connectedBean = null;

    private int indexOfConnectedBean = 0;

    private TextView textView;

    private DataBase dataBase;
    private TempDataBase tempDataBase;

    private Runnable runnable;

    // variables for getting data from LBB
    private String lbbId, sensorId, type, valueFrom, lastMessage;
    private int numberOfSensors, currentSensorIndex;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.second_activity);

        init();

        findViews();

        final BeanManager beanManager = BeanManager.getInstance();
        beanManager.startDiscovery(this);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                beanManager.cancelDiscovery();
                SuccesiveConnections.this.onDiscoveryComplete();
            }
        }, 7000);
    }

    private void init() {
        foundBeans = new ArrayList<>();
        indexOfConnectedBean = 0;

        dataBase = DataBase.getInstance(this);
        tempDataBase = TempDataBase.getInstance(this);

    }

    private void goToNextBean() {
        indexOfConnectedBean++;

        if(indexOfConnectedBean >= foundBeans.size()){
            indexOfConnectedBean = 0;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SuccesiveConnections.this, SuccesiveConnections.class);
                    startActivity(intent);
                    finish();
                }
            }, 5000);


        }else {
            connectToBean();
        }
    }

    private void startConnections() {
        addText("Starting to connect");
        connectToBean();
    }

    private void connectToBean() {
        try {

            for(int i = 0; i < foundBeans.size(); i++){
                if(foundBeans.get(i).isConnected()){
                    addText("Device still connected to : " + foundBeans.get(i).getDevice().getName());
                }
            }

            addText("Attempting connection to : " + foundBeans.get(indexOfConnectedBean).getDevice().getName());

            connectedBean = foundBeans.get(indexOfConnectedBean);
            connectedBean.connect(this, this);

        }catch (IndexOutOfBoundsException e){

        }

    }

    private void findViews() {
        textView = (TextView) findViewById(R.id.text);
    }

    private void addText(String newText){
        textView.setText(textView.getText().toString() + "\n" + newText);
    }

    @Override
    public void onBeanDiscovered(Bean bean) {
        foundBeans.add(bean);
        addText("Bean discovered : " + bean.getDevice().getName());
    }

    @Override
    public void onDiscoveryComplete() {
        addText("Discovery complete");
        if(foundBeans.size() > 0){
            startConnections();
        }else{
            BeanManager.getInstance().startDiscovery(this);
        }
    }

    @Override
    public void onConnected() {
        addText("Connected to : " + connectedBean.getDevice().getName());

        runnable = new Runnable() {
            @Override
            public void run() {
                getLBBId();
            }
        };
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendSerialMessage(GET_LBB_ID);
            }
        }, DELAY_BEFORE_SENDING_FIRST_MESSAGE);
    }

    @Override
    public void onConnectionFailed() {
        addText("Connection failed");
        goToNextBean();
    }

    @Override
    public void onDisconnected() {
        addText("Disconnected");
        goToNextBean();
    }

    @Override
    public void onSerialMessageReceived(byte[] bytes) {
        result = byteArrayToString(bytes);
        System.out.println(result);
        addText("On serial message received" + " " + result);
        Handler handler = new Handler();
        if(runnable != null) {
            handler.post(runnable);
        }else{
            throw new NullPointerException();
        }
    }

    @Override
    public void onScratchValueChanged(int i, byte[] bytes) {

    }

    private String byteArrayToString(byte[] bytes){
        String result = new String(bytes);
        if(result.length() > 0){
            return result;
        }else{
            String x = "";
            for(int i = 0; i < bytes.length; i++){

                x += bytes[i];

            }
            return x;
        }
    }

    private void getLBBId(){
        lbbId = result;
        System.out.println("LBB id = " + lbbId);
        runnable = new Runnable() {
            @Override
            public void run() {
                getNumberOfSensors();
            }
        };
        sendSerialMessage(ENUM_COUNT);
    }

    private void getNumberOfSensors() {
        currentSensorIndex = 0;
        try {
            numberOfSensors = Integer.parseInt(result);
        }catch (NumberFormatException e){
            numberOfSensors = -1;
        }
        System.out.println("No of sensors = " + numberOfSensors);

        if(numberOfSensors == -1){
            goToNextBean();
            return;
        }

        getCurrentSensor();
    }

    private void getCurrentSensor() {
        runnable = new Runnable() {
            @Override
            public void run() {
                getSensorIdAndType();
            }
        };

        sendSerialMessage(ENUM_COUNT + currentSensorIndex);
    }

    private void getSensorIdAndType() {
        String[] results = result.split(";");
        sensorId = results[0];
        type = results[1];

        checkIfDataMustBeSent(lbbId, sensorId);

        if(type.equalsIgnoreCase(Entry.READ)){
            valueFrom = results[2];
            lastMessage = results[3];
        }else{
            lastMessage = results[2];
        }

        Entry entry = new Entry(lbbId, sensorId, type, valueFrom, lastMessage);

        dataBase.addEntry(entry);
        tempDataBase.addEntry(entry);

        goToNextSensor();

    }

    private void goToNextSensor() {
        currentSensorIndex++;
        if(currentSensorIndex >= numberOfSensors){
            connectedBean.disconnect();
            goToNextBean();
        }else{
            getCurrentSensor();
        }

    }

    private void checkIfDataMustBeSent(String lbbId, String sensorId) {
        // TODO
    }

    private void sendSerialMessage(String msg){
        addText("Sending serial message : " + msg);
        connectedBean.sendSerialMessage(msg);
    }

}
