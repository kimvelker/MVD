package com.trencadis.mvd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

import java.util.ArrayList;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanListener;
import nl.littlerobots.bean.BeanManager;

/**
 * Created by Kimv on 4/6/2015.
 */
public class SuccesiveConnections extends Activity implements BeanDiscoveryListener, BeanListener{

    private ArrayList<Bean> foundBeans = new ArrayList<>();

    private Bean connectedBean = null;

    private int indexOfConnectedBean = 0;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.second_activity);
        foundBeans = new ArrayList<>();
        indexOfConnectedBean = 0;
        findViews();

        BeanManager beanManager = BeanManager.getInstance();
        beanManager.startDiscovery(this);

    }

    private void goToNextBean() {
        indexOfConnectedBean++;

        if(indexOfConnectedBean == foundBeans.size()){
            indexOfConnectedBean = 0;
            return;
            /*
            Intent intent = new Intent(this, SuccesiveConnections.class);
            startActivity(intent);
            finish();

            addText("Waiting for 5 seconds");
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectToBean();
                }
            }, 5000);
            */
        }else {
            connectToBean();
        }
    }

    private void startConnections() {
        addText("Starting to connect");
        connectToBean();
    }

    private void connectToBean() {

        for(int i = 0; i < foundBeans.size(); i++){
            if(foundBeans.get(i).isConnected()){
                addText("Device still connected to : " + foundBeans.get(i).getDevice().getName());
            }
        }
        try {
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
        startConnections();
    }

    @Override
    public void onConnected() {
        addText("Connected to : " + connectedBean.getDevice().getName());

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                connectedBean.disconnect();
                goToNextBean();
            }
        }, 10000);
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
        String result = byteArrayToString(bytes);
        addText("On serial message received" + " " + result);
        handleResult(result);
    }

    private void handleResult(String result) {
        // TODO get all sensors from string
    }

    @Override
    public void onScratchValueChanged(int i, byte[] bytes) {

    }

    private String byteArrayToString(byte[] bytes){
        /*
        String x = "";
        for(int i = 0; i < bytes.length; i++){

            x += bytes[i];

        }
        return x;
        */
        return new String(bytes);
    }
}
