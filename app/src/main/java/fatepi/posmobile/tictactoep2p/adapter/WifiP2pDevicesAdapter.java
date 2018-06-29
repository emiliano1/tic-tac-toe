package fatepi.posmobile.tictactoep2p.adapter;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fatepi.posmobile.tictactoep2p.R;
import fatepi.posmobile.tictactoep2p.listener.WiFiP2pDeviceListener;

/**
 * Created by cti on 28/04/16.
 */
public class WifiP2pDevicesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<WifiP2pDevice> mList;
    private WiFiP2pDeviceListener listener;

    public WifiP2pDevicesAdapter(List<WifiP2pDevice> mList, WiFiP2pDeviceListener listener) {
        this.mList = mList;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WifiP2pDevicesAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_peer, parent, false));
    }

    public List<WifiP2pDevice> getList() {
        return mList;
    }

    public void setList(List<WifiP2pDevice> mList) {
        this.mList = mList;
    }

    public void setList(Collection<WifiP2pDevice> mList){
        this.mList = new ArrayList(mList);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hd, final int position) {


        final WifiP2pDevice rd = mList.get(position);

        final ViewHolder holder = (WifiP2pDevicesAdapter.ViewHolder) hd;

        holder.tvDeviceName.setText(rd.deviceName);

        if(listener != null){
            holder.itemView.setClickable(true);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onPeerClick(v, position, rd);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView tvDeviceName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tvDeviceName);
        }
    }
}