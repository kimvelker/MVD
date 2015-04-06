package com.trencadis.mvd;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import nl.littlerobots.bean.Bean;
import nl.littlerobots.bean.BeanDiscoveryListener;
import nl.littlerobots.bean.BeanListener;
import nl.littlerobots.bean.BeanManager;
import nl.littlerobots.bean.message.Acceleration;
import nl.littlerobots.bean.message.Callback;
import nl.littlerobots.bean.message.Led;


public class MainActivity extends Activity {

    private Button searchForLBBs, readTemp, resetText, readAcceleration, readLED, setLED, resetLED, sendMessage;

    private ScrollView scrollView;

    private ListView listView;

    private TextView textView;

    private ArrayAdapter<String> adapter;

    private ArrayList<Bean> beansList = new ArrayList<>();

    private final BeanListener beanListener = new BeanListener() {
        @Override
        public void onConnected() {
            addText("Connected");
            showTextView();
        }

        @Override
        public void onConnectionFailed() {
            Toast.makeText(MainActivity.this, "Ceva n-o mers", Toast.LENGTH_SHORT).show();
            connectedBean = null;
        }

        @Override
        public void onDisconnected() {
            connectedBean = null;
        }

        @Override
        public void onSerialMessageReceived(byte[] bytes) {
            addText("On serial message received" + " " + byteArrayToString(bytes));
        }

        @Override
        public void onScratchValueChanged(int i, byte[] bytes) {
            addText("On scratch value changed" + " " + i + " " + byteArrayToString(bytes));
        }
    };

    private final BeanDiscoveryListener beanDiscoveryListener = new BeanDiscoveryListener() {
        @Override
        public void onBeanDiscovered(Bean bean) {
            addBeanToList(bean);
        }

        @Override
        public void onDiscoveryComplete() {

        }
    };

    private BeanManager beanManager;

    private Bean connectedBean = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        findViews();

        setClicks();

        setAdapter();
    }

    private void setAdapter() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        listView.setAdapter(adapter);
    }

    private void addBeanToList(Bean bean) {
        adapter.add(bean.getDevice().getName() + "\n" + bean.getDevice().getAddress());
        adapter.notifyDataSetChanged();

        beansList.add(bean);
    }

    private void setClicks() {
        searchForLBBs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForLBBs();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onBeanClicked(beansList.get(position));
            }
        });

        resetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText("");
            }
        });

        readTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readTemperature();
            }
        });

        readAcceleration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readAcceleration();
            }
        });

        readLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readLED();
            }
        });

        setLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomLEDColor();
            }
        });

        resetLED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetLED();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageDialog();
            }
        });
    }

    private void sendMessageDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog);
        dialog.setCancelable(true);

        final EditText editText = (EditText) dialog.findViewById(R.id.text);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        Button send = (Button) dialog.findViewById(R.id.send);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();

                if(text.length() > 0){
                    sendMessage(text);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();
    }

    private void sendMessage(String text) {

        if(connectedBean != null){
            connectedBean.sendSerialMessage(text);
            addText("Sending message : " + text);
        }else{
            addText("Connected bean is null");
        }

    }

    private void resetLED() {
        if(connectedBean != null){
            connectedBean.setLed(0, 0, 0);
            addText("Setting color to 0, 0, 0");
        }else{
            addText("Connected bean is null");
        }
    }

    private void setRandomLEDColor() {
        if(connectedBean != null) {
            Random random = new Random();
            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);

            addText("Setting color to " + red + ", " + green + ", " + blue);

            connectedBean.setLed(red, green, blue);
        }else{
            addText("Connected bean is null");
        }


    }

    private void readLED() {
        if(connectedBean != null){
            connectedBean.readLed(new Callback<Led>() {
                @Override
                public void onResult(Led led) {
                    addText("red = " + led.red());
                    addText("green = " + led.green());
                    addText("blue = " + led.blue());
                }
            });
        }else{
            addText("Connected bean is null");
        }
    }

    private void readAcceleration() {
        if(connectedBean != null){
            connectedBean.readAcceleration(new Callback<Acceleration>() {
                @Override
                public void onResult(Acceleration acceleration) {
                    addText("x acceleration = " + acceleration.x());
                    addText("y acceleration = " + acceleration.y());
                    addText("z acceleration = " + acceleration.z());
                }
            });
        }else{
            addText("Connected bean is null");
        }
    }

    private void readTemperature() {
        if(connectedBean != null){
            connectedBean.readTemperature(new Callback<Integer>() {
                @Override
                public void onResult(Integer integer) {
                    addText("Temperature = " + integer);
                }
            });
        }else{
            addText("Connected bean is null");
        }
    }

    private void onBeanClicked(Bean bean) {
        System.out.println(bean.getDevice().getName());

        beanManager.cancelDiscovery();

        connectedBean = bean;

        bean.connect(this, beanListener);
    }

    private void searchForLBBs() {
        if(connectedBean != null){
            connectedBean.disconnect();
            connectedBean = null;
        }
        showDevices();
        beansList.clear();
        setAdapter();
        beanManager = BeanManager.getInstance();
        beanManager.startDiscovery(beanDiscoveryListener);
    }

    private void findViews() {
        searchForLBBs = (Button) findViewById(R.id.search);

        listView = (ListView) findViewById(R.id.list);

        textView = (TextView) findViewById(R.id.text);

        scrollView = (ScrollView) findViewById(R.id.scrollview);

        readTemp = (Button) findViewById(R.id.read_temp);
        resetText = (Button) findViewById(R.id.reset_text);
        readAcceleration = (Button) findViewById(R.id.read_acceleration);
        readLED = (Button) findViewById(R.id.read_led);

        setLED = (Button) findViewById(R.id.set_led);
        resetLED = (Button) findViewById(R.id.reset_led);
        sendMessage = (Button) findViewById(R.id.send_message);
    }

    private void showDevices(){
        listView.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        textView.setText("");
    }

    private void showTextView(){
        listView.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
    }

    private void addText(String newText){
        textView.setText(textView.getText().toString() + "\n" + newText);
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

    @Override
    public void onBackPressed() {
        if(connectedBean == null) {
            super.onBackPressed();
        }else{
            connectedBean.disconnect();
            connectedBean = null;
            searchForLBBs();
        }
    }
}
