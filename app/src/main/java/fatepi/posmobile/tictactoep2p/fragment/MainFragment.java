package fatepi.posmobile.tictactoep2p.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Collections;
import java.util.concurrent.Callable;

import fatepi.posmobile.tictactoep2p.R;
import fatepi.posmobile.tictactoep2p.adapter.WifiP2pDevicesAdapter;
import fatepi.posmobile.tictactoep2p.client.manager.ClientManager;
import fatepi.posmobile.tictactoep2p.client.manager.PlayerManager;
import fatepi.posmobile.tictactoep2p.component.HandlerDefault;
import fatepi.posmobile.tictactoep2p.listener.WiFiDirectListener;
import fatepi.posmobile.tictactoep2p.activity.MainActivity;
import fatepi.posmobile.tictactoep2p.component.Dialog;
import fatepi.posmobile.tictactoep2p.listener.WiFiP2pDeviceListener;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.server.manager.GameManager;
import fatepi.posmobile.tictactoep2p.server.manager.ServerManager;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements WiFiDirectListener, WifiP2pManager.PeerListListener, WiFiP2pDeviceListener{

    public static final String TAG = "MAIN_FRAGMENT";
    private MainActivity mainActivity;
    private WifiP2pManager mManager;
    private RecyclerView listPeers;
    private WifiP2pDevicesAdapter adapter;

    private ServerManager serverManager;
    private PlayerManager playerManager;
    private boolean isServer = false;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        mManager = mainActivity.getManager();
        adapter = new WifiP2pDevicesAdapter(Collections.<WifiP2pDevice>emptyList(), this);

        mManager.discoverPeers(mainActivity.getChannel(), new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(Constantes.LOG_TAG, "onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(Constantes.LOG_TAG, "onFailure ("+reasonCode+")");
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);
        this.listPeers = (RecyclerView) v.findViewById(R.id.listPeers);
        this.listPeers.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        this.listPeers.setLayoutManager(mLayoutManager);

        this.listPeers.setAdapter(this.adapter);

        mainActivity.setTitle("Dispositivos");

        return v;
    }

    @Override
    public void onStateChanged(int state) {
        Log.d(Constantes.LOG_TAG, "MainFragment.onStateChanged: "+state);

        if (state != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            Dialog.alert(getActivity(), "Atenção", "Wi-Fi P2P não está ativado!");
            mainActivity.setConected(false);
        }else{
            mainActivity.setConected(true);
        }

    }

    @Override
    public void onPeersChanged() {
        Log.d(Constantes.LOG_TAG, "MainFragment.onPeersChanged: ");
        mManager.requestPeers(mainActivity.getChannel(), this);

    }

    @Override
    public void onConnectionChanged() {
        Log.d(Constantes.LOG_TAG, "MainFragment.onConnectionChanged: ");
    }

    @Override
    public void onThisDeviceChanged(WifiP2pDevice device) {
        Log.d(Constantes.LOG_TAG, "MainFragment.onThisDeviceChanged: ");

        Player p = PlayerManager.getInstance().getMyPlayer();
        if(p == null)
            p = new Player();
        p.setNome(device.deviceName);

        PlayerManager.getInstance().setMyPlayer(p);
    }

    @Override
    public void onPeerConnect(String host) {
        Log.d(Constantes.LOG_TAG, "MainFragment.onPeerConnect: "+host);
        Log.d(Constantes.LOG_TAG, "MainFragment.isServer: "+isServer);

        if(!isServer && host != null && !host.isEmpty()){

            Handler handler = new HandlerDefault(getActivity()){
                @Override
                public void onGameUpdate() {
                    Log.d(Constantes.LOG_TAG, "onGameUpdate: ");

                }
            };

            PlayerManager.getInstance().getMyPlayer().setPontuacao(0);
            ClientManager clientManager = new ClientManager(getContext(), handler, PlayerManager.getInstance().getMyPlayer(), host);
            clientManager.start();
        }

        mainActivity.replaceContent(GamePrepareFragment.newInstance(), GamePrepareFragment.TAG);
    }

    @Override
    public void onPeerDisconnect() {
        Log.d(Constantes.LOG_TAG, "MainFragment.onPeerDisconnect: ");
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        adapter.setList(peers.getDeviceList());
        adapter.notifyDataSetChanged();
        Log.d(Constantes.LOG_TAG, "MainFragment.onPeersAvailable: "+adapter.getItemCount());
    }

    @Override
    public void onPeerClick(View v, int position, final WifiP2pDevice device) {
        //obtain a peer from the WifiP2pDeviceList

        isServer = true;

        //if(mainActivity.getGame() == null){
            openServer();
        //}else{
        //    Dialog.alert(getActivity(), "Atenção", "Você já está conectado à um jogo!");
        //}

        final ProgressDialog progressDialog = Dialog.indeterminateProgress(getActivity(), "Aguarde", "Conectando...");

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mainActivity.getChannel(), config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                progressDialog.dismiss();

                GameManager gameManager = GameManager.getInstance();
                Player o = new Player();
                o.setNome(device.deviceName);
                gameManager.getGame().setOpponent(o);
                gameManager.sendGame();

                //mainActivity.setPeerDevice(device);
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                progressDialog.dismiss();
                Dialog.alert(MainFragment.this.getActivity(), "Erro", "Erro ao conectar no dispositivo ("+reason+")");
            }
        });


    }

    public void openServer(){

        try{

            if(!mainActivity.isConected()){
                Dialog.alert(getActivity(), "Atenção", "Wi-Fi P2P não está ativado!");
                return;
            }

            Handler handler = new HandlerDefault(getActivity()){
                @Override
                public void onGameUpdate() {
                    //mainActivity.replaceContent(GamePrepareFragment.newInstance(), GamePrepareFragment.TAG);
                }
            };

            if(serverManager == null)
                serverManager = new ServerManager(this.getContext(), PlayerManager.getInstance().getMyPlayer(), handler);

            if(!serverManager.isRunning()){
                serverManager.start();
                PlayerManager.getInstance().setIsServer(true);
                PlayerManager.getInstance().getMyPlayer().setConectado(true);
                PlayerManager.getInstance().getMyPlayer().setPontuacao(0);
                PlayerManager.getInstance().getMyPlayer().setPodeJogar(true);
                /*btServidor.setText(getString(R.string.fechar_servidor));
                btAbrirJogo.setVisibility(View.VISIBLE);
                btClienteConnect.setEnabled(false);
                btStandalone.setEnabled(false);*/

            }else {
                //closeServer();
            }

        }catch (Exception ex){
            Log.e(Constantes.LOG_TAG, "btCriarServidor: "+ex.getMessage());
            //Dialog.alert(this, getString(R.string.erro), getString(R.string.erro_criar_servidor)+"\n"+ex.getMessage());
        }

    }

    private void closeServer(){

        Callable call = new Callable() {
            @Override
            public Object call() throws Exception {
                //btServidor.setText(getString(R.string.criar_servidor));

                fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().disconnectGame(true);
                fatepi.posmobile.tictactoep2p.server.manager.GameManager.clearInstance();
                serverManager.cancel(false);
                serverManager = null;
                PlayerManager.getInstance().setIsServer(false);
                PlayerManager.getInstance().getMyPlayer().setConectado(false);
                /*btAbrirJogo.setVisibility(View.GONE);
                btClienteConnect.setEnabled(true);
                btStandalone.setEnabled(true);*/
                return null;
            }
        };

        //Dialog.handleActivityAlert(this, getString(R.string.info), getString(R.string.info_fechar_servidor), call);
    }
}
