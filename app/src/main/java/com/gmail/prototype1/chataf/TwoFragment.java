package com.gmail.prototype1.chataf;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class TwoFragment extends Fragment{
    static ArrayList<CharSequence> users = new ArrayList();
    static ArrayList<String> userAddress = new ArrayList();
    static String userName = "Random";
    int receiverInd = -1;
    //static String receiverAddr = null;
    static String senderAddr = null;
    String selectedReceiver = null;
    static String receiverAddr = null;


    public TwoFragment(){
        //constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)   {
        return inflater.inflate(R.layout.fragment_two, container, false);
    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
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
                    Log.v("idf", "received");
                    WifiChatActivity.addUser(msg, intent.getStringExtra("senderAddr"));
                  //  Toast toast = Toast.makeText(MainActivity.this, msg+" entered chat room", 1000);
                   // toast.show();
                    sendMessage(receiverAddr, "irf"+WifiChatActivity.userName, false);
                }
            }
            else if(code.equals("irf"))
            {
                Log.v("irf", "received");
                WifiChatActivity.addUser(msg, intent.getStringExtra("senderAddr"));
                //Toast toast = Toast.makeText(TwoFragment.this, msg+" entered chat room", Toast.LENGTH_SHORT);
                //toast.show();
                Log.v("user added", msg + " " + intent.getStringExtra("senderAddr"));
            }
        }
    };
    private void sendMessage(String address, String msg,  boolean bcast)    {
        ClientThread cThread;
        try{
            cThread = new ClientThread(address, 5555, msg, bcast);
            cThread.start();
        }catch(IOException e)   {
            e.printStackTrace();
        }
    }
    /*public void onResume()  {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ChatService.BROADCAST_ACTION));
    }
    public void onPause()   {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }*/

}

