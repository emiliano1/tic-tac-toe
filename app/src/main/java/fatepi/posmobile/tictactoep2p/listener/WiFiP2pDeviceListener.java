package fatepi.posmobile.tictactoep2p.listener;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;

/**
 * Created by magno on 05/07/16.
 */
public interface WiFiP2pDeviceListener {
    void onPeerClick(View v, int position, WifiP2pDevice device);
}
