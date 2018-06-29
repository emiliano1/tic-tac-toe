package fatepi.posmobile.tictactoep2p.client.manager;

import android.util.Log;

import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Move;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 16/02/16.
 */
public class GameManager {
    private static GameManager ourInstance;
    private ClientManager clientManager;
    private Game game;

    public static GameManager getInstance() {
        if(ourInstance == null)
            ourInstance = new GameManager();
        return ourInstance;
    }

    public static void clearInstance() {
        ourInstance = null;
    }

    private GameManager() {
        Log.d(Constantes.LOG_TAG, "new GameManager (client)");
        clearManagers();
    }

    private void clearManagers(){
        //PlayerManager.clearInstance();
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void execMove(Move move){
        this.clientManager.sendObjet(move);
    }

}
