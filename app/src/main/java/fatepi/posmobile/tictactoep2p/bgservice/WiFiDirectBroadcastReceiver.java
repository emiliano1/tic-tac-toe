package fatepi.posmobile.tictactoep2p.bgservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import fatepi.posmobile.tictactoep2p.listener.WiFiDirectListener;

/**
 * Created by magno on 05/07/16.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiDirectListener wiFiDirectListener;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WiFiDirectListener wiFiDirectListener) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.wiFiDirectListener = wiFiDirectListener;
    }

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(wiFiDirectListener == null) return;

        //Log.d(Constantes.LOG_TAG, "onReceive: "+action);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            wiFiDirectListener.onStateChanged(intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1));
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            wiFiDirectListener.onPeersChanged();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // We are connected with the other device, request connection
                // info to find group owner IP

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        if(info != null)
                            wiFiDirectListener.onPeerConnect(info.groupOwnerAddress.getHostAddress());
                        else
                            wiFiDirectListener.onPeerDisconnect();

                    }
                });
            }else{
                wiFiDirectListener.onPeerDisconnect();
            }

            wiFiDirectListener.onConnectionChanged();
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            wiFiDirectListener.onThisDeviceChanged((WifiP2pDevice) intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }
}
