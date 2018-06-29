package fatepi.posmobile.tictactoep2p.client.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import fatepi.posmobile.tictactoep2p.activity.MainActivity;
import fatepi.posmobile.tictactoep2p.model.AbstractModel;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 16/02/16.
 */
public class ClientManager extends Thread{

    private Handler handler;
    private Context context;

    private String serverAddress;
    private Socket sckCliente;

    private ObjectOutputStream output;
    private PlayerManager playerManager;

    public ClientManager(Context context, Handler handler, Player owner, String serverAddress) {
        this.context = context;
        this.handler = handler;
        playerManager = PlayerManager.getInstance();
        playerManager.setMyPlayer(owner);
        this.serverAddress = serverAddress;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public Socket getSckCliente() {
        return sckCliente;
    }

    public boolean hasConnect(){
        return sckCliente != null;
    }

    public boolean isConnected(){
        return sckCliente != null && sckCliente.isConnected() && getPlayer() != null && getPlayer().isConectado();
    }

    public void run(){

        try {

            sckCliente = new Socket();
            sckCliente.bind(null);
            sckCliente.connect((new InetSocketAddress(serverAddress, Constantes.PORT)), 500);

            init();
            getPlayer().setConectado(true);
            ReceiverManager r = new ReceiverManager(this);
            new Thread(r,"Recebedor do Cliente").start();

        } catch (IOException connectException) {

            Log.e(Constantes.LOG_TAG, "connectException: " +connectException.getMessage());
            // Unable to connect; close the socket and get out
            try {
                disconnect(false);
            } catch (IOException closeException) { }

            handler.sendMessage(HandlerManager.msgAlert("Erro", "Erro ao conectar no dispositivo"));
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
    }

    public synchronized void disconnect(boolean redirect, boolean alert) throws IOException {



        getPlayer().setConectado(false);
        if(this.sckCliente != null){
            this.sckCliente.close();
            this.sckCliente = null;
        }
        else
            return; //ja foi encerrado

        Log.d(Constantes.LOG_TAG, "client disconnect");

        if(context != null && redirect){
            Intent i = new Intent(context, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if(alert){
                i.putExtra("cod", HandlerManager.COD_MY_PLAYER_DISCONNECT);
                //i.putExtra("msg", context.getString(R.string.erro_cliente_desconectou));
            }
            context.startActivity(i);
        }

        //broadcastDisconnect();

    }

    public synchronized void disconnect(boolean redirect) throws IOException {
        this.disconnect(redirect, false);
    }

    public void broadcastDisconnect(){
        Intent intent = new Intent(Constantes.IT_MSG_DISCONNECT);
        // add data
        //intent.putExtra("message", "data");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public synchronized void init() throws IOException {
        Player player = getPlayer();
        output = new ObjectOutputStream(sckCliente.getOutputStream());
        Log.d(Constantes.LOG_TAG, "ClientManager:" + player.toString());
        sendObjet(player);
    }

    public synchronized void sendObjet(AbstractModel s){
        try {
            output.writeObject(s.getPkg());
            resetFluxo(output);
        } catch (IOException ex) {
            Log.e(Constantes.LOG_TAG, "Erro ao inicializar Cliente: "+ex);
        }
    }

    public synchronized void sendPlayer(){
        if(getPlayer().isConectado())
            sendObjet(getPlayer());
    }

    public synchronized void resetFluxo(ObjectOutputStream oos) throws IOException {
        oos.reset();
        oos.flush();
    }

    public synchronized Player getPlayer() {
        return this.playerManager.getMyPlayer();
    }

    public void setPlayer(Player player, boolean jogando) {
        Log.d(Constantes.LOG_TAG, "Atualizando Cliente:" + player);
        this.playerManager.setMyPlayer(player);
        handler.sendMessage(HandlerManager.msgMyPlayerConnect());
    }

    public synchronized void setPodeJogar(boolean podeJogar){
        this.getPlayer().setPodeJogar(podeJogar);
    }


    public synchronized Player searchPlayer(List<Player> players, Player cli){
        for (Player c : players) {
            if(c.getId() == cli.getId())
                return c;
        }
        return null;
    }

    public synchronized void updatePlayer(List<Player> players){
        for (Player c : players) {
            if(c.getId() == getPlayer().getId())
                setPlayer(c, true);
        }
    }

    public String getMsg(int id){
        if(context == null) return "";
        return context.getString(id);
    }
}
