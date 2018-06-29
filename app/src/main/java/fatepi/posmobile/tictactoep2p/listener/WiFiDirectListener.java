package fatepi.posmobile.tictactoep2p.listener;

import android.net.wifi.p2p.WifiP2pDevice;

/**
 * Created by magno on 05/07/16.
 */
public interface WiFiDirectListener {
    void onStateChanged(int state);
    void onPeersChanged();
    void onConnectionChanged();
    void onThisDeviceChanged(WifiP2pDevice device);
    void onPeerConnect(String host);
    void onPeerDisconnect();
}
