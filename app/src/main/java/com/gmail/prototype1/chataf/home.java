package com.gmail.prototype1.chataf;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class home extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    FloatingActionButton myfab;
    static String receiverAddr = null;
    AddressUtility addressUtility = new AddressUtility();
    Intent intent,serverIntent;
    int i=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewpager(viewPager);

        intent = new Intent(this, WifiChatActivity.class);
        serverIntent = new Intent(this, ChatService.class);

        serverIntent = new Intent(this, ChatService.class);
        registerReceiver(broadcastReceiver, new IntentFilter(ChatService.BROADCAST_ACTION));
        startService(new Intent(getApplicationContext(), ChatService.class));

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        myfab = (FloatingActionButton) findViewById(R.id.fab);
        myfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(i != 0)
                {Intent intent = new Intent(home.this, WifiChatActivity.class);
                startActivity(intent);
                }
                else if (i==0)
                {
                 addressUtility.scanAddresses(home.this);
                 i=1;
                }

            }
        });


    }
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.v("msg", "received");
            String message = intent.getStringExtra("message");
            String code = message.substring(0,3);
            String msg = null;
            if(message.length()>3) msg = message.substring(3,message.length());
            if(code.equals("isf"))
            {
                if(receiverAddr != null)
                {
                    Log.v("idf", "received");
                    WifiChatActivity.addUser(msg, intent.getStringExtra("senderAddr"));
                    Toast toast = Toast.makeText(home.this, msg+"entered chat room", Toast.LENGTH_SHORT);
                    toast.show();
                    sendMessage(receiverAddr, "irf"+WifiChatActivity.userName, false);
                }}
                else if(code.equals("irf"))
                {
                    Log.v("irf", "received");
                    WifiChatActivity.addUser(msg, intent.getStringExtra("senderAddr"));
                    Toast toast = Toast.makeText(home.this, msg+" entered chat room", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.v("user added", msg + " " + intent.getStringExtra("senderAddr"));
                }
            }

    };
    private void sendMessage(String address, String msg, boolean bcast){

        ClientThread cThread;
        try {
            cThread = new ClientThread(address, 5555, msg, bcast);
            cThread.start();
        } catch(IOException e) {
            e.printStackTrace();
        }

    }

    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(ChatService.BROADCAST_ACTION));
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    public boolean onCreateOptionsMenu(Menu menu)   {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.android_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(home.this);
        dialog.setCancelable(false);
        dialog.setTitle("Exit");
        dialog.setMessage("Are you sure you want to exit the app?");
        dialog.setPositiveButton(Html.fromHtml("<font color='#000000'>Yes</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        })
                .setNegativeButton(Html.fromHtml("<font color='#000000'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = dialog.create();
        alert.show();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())   {
            case R.id.action_settings:
                Intent intent = new Intent(home.this,Settings.class);
                startActivity(intent);
                return true;
            case R.id.action_help:
                {i=1;
                addressUtility.scanAddresses(home.this);}
                return true;
            case R.id.action_Placeholder:
                showDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupViewpager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(),"BLUETOOTH BEACON");
        adapter.addFragment(new TwoFragment(),"WIFI P2P");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)    {
            super(manager);
        }
        @Override
        public Fragment getItem(int position)   {
            return mFragmentList.get(position);
            }
        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    void showDialog() {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);

        alert.setTitle("Profile Info");
        alert.setTitle("Profile Info");
        alert.setMessage("Nick Name");

        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                WifiChatActivity.userName = input.getText().toString();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }



}

