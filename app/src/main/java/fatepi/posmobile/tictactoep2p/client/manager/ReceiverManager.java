package fatepi.posmobile.tictactoep2p.client.manager;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;

import fatepi.posmobile.tictactoep2p.model.AbstractModel;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 15/02/16.
 */
public class ReceiverManager implements Runnable{

    private GameManager gameManager;
    private ClientManager clientManager;
    private ObjectInputStream entrada;


    public ReceiverManager(ClientManager clientManager) {
        this.clientManager = clientManager;
        initManagers();
    }

    private void initManagers(){
        gameManager = GameManager.getInstance();
        gameManager.setClientManager(clientManager);
    }

    @Override
    public void run() {
        Object obj;
        try {

            this.entrada = new ObjectInputStream(clientManager.getSckCliente().getInputStream());
            do {
                obj = entrada.readObject();
                Log.d(Constantes.LOG_TAG, "Recebi: "+obj);
                obj = checkModel((String) obj);

                if(obj!= null)
                    useObj(obj);

            } while (clientManager.isConnected());
            Log.d(Constantes.LOG_TAG, "ReceiverManagerClient: finish");
        } catch (ClassNotFoundException ex) {
            Log.e(Constantes.LOG_TAG, "Erro no Recebedor: " + ex.getMessage());
        } catch (IOException ex) {
            try {
                clientManager.disconnect(true, true);
            } catch (IOException closeException) { }
            Log.e(Constantes.LOG_TAG, "IOException: " +ex.getMessage());
        }catch (Exception ex) {
            Log.e(Constantes.LOG_TAG, "Erro no Cliente: " + ex.getMessage());
            clientManager.getHandler().sendMessage(HandlerManager.msgAlert("Erro", ex.getMessage()));
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

        }

        if(obj != null){
            obj.stringToModel(pkg.substring(2));
        }

        return obj;
    }

    public synchronized void useObj(Object obj) throws Exception{

        if(obj instanceof Game){
            gameManager.setGame((Game) obj);
            clientManager.getHandler().sendMessage(HandlerManager.msgGameUpdate());
        }
        else if(obj instanceof Player){
            //trata Cliente
            Player p = (Player) obj;
            clientManager.setPlayer(p, false);
        }

    }
}
