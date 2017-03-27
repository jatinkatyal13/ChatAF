package com.gmail.prototype1.chataf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Akshit on 3/14/2017.
 */

public class WifiChatActivity extends AppCompatActivity {
    Button but,sel;
    EditText textBox;
    TextView chatArea;
    ScrollView scrollView;
    Intent intent;
    View root;
    //AddressUtility addressUtility = new AddressUtility();
    static ArrayList<CharSequence> users = new ArrayList();
    static ArrayList<String> userAddress = new ArrayList();
    static String userName = "Placeholder";
    public static int receiverInd = -1;
    static String receiverAddr = null;
    static String senderAddr = null;
    public static String selectedReceiver = null;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);
        //addressUtility.setdomain(this);
        //sel1 = (Button) findViewById(R.id.button1);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff' style='bold'>CHAT BOX</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        but = (Button) findViewById(R.id.Button);
        textBox = (EditText) findViewById(R.id.txt);
        scrollView = (ScrollView) findViewById(R.id.scrollView1);
        chatArea = (TextView) findViewById(R.id.ChatArea);
        chatArea.setMovementMethod(new ScrollingMovementMethod());
        sel= (Button) findViewById(R.id.button);
        intent = new Intent(this, ChatService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(ChatService.BROADCAST_ACTION));
        startService(new Intent(getApplicationContext(),ChatService.class));
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!textBox.getText().equals("")) {
                    if (selectedReceiver != null) {
                        chatArea.append(Html.fromHtml("<font color='black' style='bold'>" + userName + ": </font>"));
                        chatArea.append(Html.fromHtml("<font color='black' >" + textBox.getText() + ": </font> <br/>"));
                        sendMessage(selectedReceiver, "msg" + userName + ": " + textBox.getText().toString(), false);
                        textBox.setText("");
                    } else {
                        Toast toast = Toast.makeText(WifiChatActivity.this, "Please choose a Recepient", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            }
        });
        /*sel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressUtility.scanAddresses(WifiChatActivity.this);
            }
        });*/
        sel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence[] temp = new CharSequence[users.size()];
                for(int i=0;i<users.size();i++) temp[i] = users.get(i);
                final AlertDialog.Builder builder = new AlertDialog.Builder(WifiChatActivity.this);
                builder.setTitle("Choose Recepient");
                builder.setSingleChoiceItems(temp, receiverInd, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        receiverInd = which;
                        selectedReceiver = userAddress.get(which);
                        Log.v("receiver addr set to", selectedReceiver);
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }

        });

        setUsers();
    }
    private void setUsers() {
        final Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                sendMessage("255.255.255.255", "idf"+userName, true);
            }

        });

        t.start();

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        intent = new Intent(this, home.class);
        startActivity(intent);


        return true;
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("msg", "received");
            String message = intent.getStringExtra("message");
            String code = message.substring(0, 3);
            String msg = null;
            if(message.length()>3) msg = message.substring(3, message.length());
            if(code.equals("idf"))
            {
                if(receiverAddr != null)
                {
                    addUser(msg, intent.getStringExtra("senderAddr"));
                    Toast toast = Toast.makeText(WifiChatActivity.this, msg+" entered chat room",Toast.LENGTH_SHORT);
                    toast.show();
                    sendMessage(receiverAddr, "irf"+userName, false);
                }
            }
            else if(code.equals("irf"))
            {
                addUser(msg, intent.getStringExtra("senderAddr"));
                Toast toast = Toast.makeText(WifiChatActivity.this, msg+" entered chat room", Toast.LENGTH_SHORT);
                toast.show();
                Log.v("user added", msg + " " + intent.getStringExtra("senderAddr"));
            }
            else if(code.equals("msg"))
            {
                int temp = 0;
                while(msg.charAt(temp)!=' ') temp++;
                String sender;
                String message1;
                sender = msg.substring(0, temp);
                message1 = msg.substring(temp, msg.length());

                chatArea.append(Html.fromHtml("<font color='red'>"+sender+" </font>"));
                chatArea.append(Html.fromHtml("<font color='black'>"+message1+"</font> <br/>"));

                scrollView.post(new Runnable() {
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });

            }
        }
    };

    private void sendMessage(String address, String msg, boolean bcast) {
        ClientThread cThread;
        try {
            cThread = new ClientThread(address, 5555, msg, bcast);
            cThread.start();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String user, String addr) {
        for(int i=0;i<userAddress.size();i++) if(userAddress.get(i).equals(addr)) return;
        users.add(user);
        userAddress.add(addr);
    }

    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ChatService.BROADCAST_ACTION));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public void onDestroy() {
        super.onDestroy();
    }


}
