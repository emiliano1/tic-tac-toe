package fatepi.posmobile.tictactoep2p.server.manager;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import fatepi.posmobile.tictactoep2p.model.AbstractModel;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Move;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 15/02/16.
 */
public class ReceiverManager implements Runnable{

    private ServerManager serverManager;
    private GameManager gameManager;

    private Socket clientSocket;
    private Player clientPlayer;

    public ReceiverManager(ServerManager serverManager, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.serverManager = serverManager;
    }

    private void initManagers(){
        gameManager = GameManager.getInstance();
    }

    @Override
    public void run() {
        Object obj;
        try {

            initManagers();

            ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());

            do {
                obj = entrada.readObject();
                Log.d(Constantes.LOG_TAG, "Recebi: " + obj);
                obj = checkModel((String) obj);

                if(obj!= null)
                    useObj(obj);

            } while (serverManager.isRunning());
            Log.d(Constantes.LOG_TAG, "ReceiverManagerServer: finish");
        } catch (ClassNotFoundException ex) {
            Log.e(Constantes.LOG_TAG, "Erro no Recebedor: " + ex.getMessage());
        } catch (IOException ex) {
            if(clientPlayer != null) {
                clientPlayer.setConectado(false);
                //serverManager.removePlayer(clientPlayer);
            }
        }catch (Exception ex) {
            Log.e(Constantes.LOG_TAG, "Erro ao usar Objeto: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public synchronized Object checkModel(String pkg){

        int cod = Integer.valueOf(pkg.substring(0,2));
        AbstractModel obj = null;

        switch(cod){
            case Constantes.COD_GAME_MODEL: {
                obj = new Game();
                break;
            }
            case Constantes.COD_PLAYER_MODEL: {
                obj = new Player();
                break;
            }
            case Constantes.COD_MOVE_MODEL: {
                obj = new Move();
                break;
            }

        }

        if(obj != null){
            obj.stringToModel(pkg.substring(2));
        }

        return obj;
    }

    public synchronized void useObj(Object obj) throws Exception {
        if(obj instanceof Game){
            //this.wordManager.checkWinner((Challenge) obj);
        }
        else if(obj instanceof Player){
            //trata Cliente
            Player p = (Player) obj;
            this.clientPlayer = p;
            this.gameManager.getGame().setOpponent(p);
            this.gameManager.sendGame();

            //this.serverManager.newPlayer(p, new ObjectOutputStream(clientSocket.getOutputStream()));

            //playerManager.setPlayer(cli,false);

        }
        else if(obj instanceof Move){
            Move m = (Move) obj;
            this.gameManager.execMove(m, false);
        }/*
        else if(obj instanceof Round){

            Round r = (Round) obj;
            this.gameManager.addRound(r);


        }
        else if(obj instanceof Challenge){
            //this.wordManager.checkWinner((Challenge) obj);

            Challenge c = (Challenge) obj;
            this.roundManager.addChallenge(c);

        }
        else if(obj instanceof Answer){
            Answer a = (Answer) obj;
            this.challengeManager.addAnswer(a);
        }*/
    }
}
