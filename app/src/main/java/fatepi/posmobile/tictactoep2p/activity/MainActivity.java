package fatepi.posmobile.tictactoep2p.activity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import fatepi.posmobile.tictactoep2p.R;
import fatepi.posmobile.tictactoep2p.client.manager.PlayerManager;
import fatepi.posmobile.tictactoep2p.listener.WiFiDirectListener;
import fatepi.posmobile.tictactoep2p.fragment.MainFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;

import fatepi.posmobile.tictactoep2p.bgservice.WiFiDirectBroadcastReceiver;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.util.Constantes;

public class MainActivity extends AppCompatActivity {

    private WifiP2pDevice peerDevice;

    private WifiP2pManager mManager;
    private Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WiFiDirectListener wiFiDirectListener;
    private Toolbar toolbar;

    protected boolean isConected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        if (savedInstanceState == null) {
            MainFragment f = MainFragment.newInstance();
            wiFiDirectListener = f;
            this.replaceContent(f, MainFragment.TAG);
        }

        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, wiFiDirectListener);

    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0)
            getFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    private boolean checkMyPlayer(){

        PlayerManager playerManager = PlayerManager.getInstance();

        playerManager.setSharedPreferences(getSharedPreferences("PlayerInfo", 0));
        if(!playerManager.hasPlayer())
            playerManager.loadStoredUser();

        if(!playerManager.hasPlayer()){
            //openProfilePlayerActivity();
            return false;
        }
        return true;
    }

    public void replaceContent(Fragment f, boolean history, String TAG){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.replace(R.id.flContent, f, TAG);

        if(history)
            ft.addToBackStack(null);

        // or ft.add(R.id.your_placeholder, new FooFragment());
        ft.commit();

    }

    public void replaceContent(Fragment f, String TAG){
        this.replaceContent(f, true, TAG);
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mManager == null) return;
        mManager.cancelConnect(getChannel(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(Constantes.LOG_TAG, "cancelConnect.onSuccess: ");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(Constantes.LOG_TAG, "cancelConnect.onFailure: ");
            }
        });
    }

    public WifiP2pManager getManager() {
        return mManager;
    }

    public Channel getChannel() {
        return mChannel;
    }

    public WiFiDirectListener getWiFiDirectListener() {
        return wiFiDirectListener;
    }

    public WifiP2pDevice getPeerDevice() {
        return peerDevice;
    }

    public void setPeerDevice(WifiP2pDevice peerDevice) {
        this.peerDevice = peerDevice;
    }

    public boolean isConected() {
        return isConected;
    }

    public void setConected(boolean conected) {
        isConected = conected;
    }

    public Game getGame() {

        if(isServer())
            return fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().getGame();

        return fatepi.posmobile.tictactoep2p.client.manager.GameManager.getInstance().getGame();
    }

    public boolean isServer(){
        return PlayerManager.getInstance().isServer();
    }

    public void setTitleBar(String title){
        if(toolbar != null)
            toolbar.setTitle(title);
    }

    public void setSubtitleBar(String title){
        if(toolbar != null)
            toolbar.setSubtitle(title);
    }

}
