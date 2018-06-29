package fatepi.posmobile.tictactoep2p.component;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import fatepi.posmobile.tictactoep2p.client.manager.HandlerManager;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 19/02/16.
 */
public class HandlerDefault extends Handler {

    private Activity act;

    public HandlerDefault(Activity act) {
        this.act = act;
    }

    @Override
    public void handleMessage(Message msg) {

        Log.d(Constantes.LOG_TAG, act.getClass().getName()+": msg recebida handler");

        switch (msg.what){
            case HandlerManager.COD_ALERT:
                onWhatAlert(msg.getData().getString("title"), msg.getData().getString("msg"));
                break;
            case HandlerManager.COD_LOG:
                onWhatLog(msg.getData().getString("msg"));
                break;
            case HandlerManager.COD_MY_PLAYER_CONNECT:
                onMyPlayerConnect();
                break;
            case HandlerManager.COD_MY_PLAYER_DISCONNECT:
                onMyPlayerDisconnect();
                break;
            case HandlerManager.COD_GAME_UPDATE:
                onGameUpdate();
                break;
            case HandlerManager.COD_BOARD_UPDATE:
                onBoardUpdate();
                break;
            case HandlerManager.COD_NEW_CHALLENGE:
                onNewChallenge();
                break;
            case HandlerManager.COD_SERVER_DISCONNECT:
                onServerDisconnect();
                break;
        }


    }

    public void onWhatAlert(String title, String msg){
        Dialog.alert(act, title, msg);
    }

    public void onWhatLog(String msg){
        Log.d(Constantes.LOG_TAG, msg);
    }

    public void onMyPlayerConnect(){}

    public void onMyPlayerDisconnect(){}

    public void onGameUpdate(){}

    public void onBoardUpdate(){}

    public void onNewChallenge(){}

    public void onServerDisconnect(){}
}
