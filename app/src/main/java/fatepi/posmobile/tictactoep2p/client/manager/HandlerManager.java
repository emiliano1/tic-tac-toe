package fatepi.posmobile.tictactoep2p.client.manager;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by magno on 17/02/16.
 */
public class HandlerManager {

    public static final int COD_LOG                     = 1;
    public static final int COD_ALERT                   = 2;
    public static final int COD_MY_PLAYER_CONNECT       = 3;
    public static final int COD_MY_PLAYER_DISCONNECT    = 4;
    public static final int COD_GAME_UPDATE             = 5;
    public static final int COD_BOARD_UPDATE            = 6;
    public static final int COD_NEW_CHALLENGE           = 7;
    public static final int COD_SERVER_DISCONNECT       = 8;

    public static Message msgDefault(int what, Bundle bundle){
        Message message = Message.obtain();
        message.what = what;
        message.setData(bundle);
        return message;
    }

    public static Message msgDefault(int what){
        return msgDefault(what, null);
    }

    public static Message msgAlert(String title, String msg){
        Message message = Message.obtain();
        message.what = COD_ALERT;

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("msg", msg);
        bundle.putBoolean("closeProgress", true);

        message.setData(bundle);

        return message;

    }

    public static Message msgLog(String msg){
        Message message = Message.obtain();
        message.what = COD_LOG;

        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);

        message.setData(bundle);

        return message;

    }

    public static Message msgMyPlayerConnect(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("closeProgress", true);
        return msgDefault(COD_MY_PLAYER_CONNECT, bundle);
    }

    public static Message msgMyPlayerDisconnect(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("closeProgress", true);
        return msgDefault(COD_MY_PLAYER_DISCONNECT, bundle);
    }

    public static Message msgGameUpdate(){
        return msgDefault(COD_GAME_UPDATE);
    }

    public static Message msgBoardUpdate(){
        return msgDefault(COD_BOARD_UPDATE);
    }

    public static Message msgNewChallenge(){
        return msgDefault(COD_NEW_CHALLENGE);
    }

    public static Message msgServerDisconnect(){
        return msgDefault(COD_SERVER_DISCONNECT);
    }

}

