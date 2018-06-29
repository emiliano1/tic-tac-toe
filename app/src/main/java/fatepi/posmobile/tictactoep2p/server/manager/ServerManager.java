package fatepi.posmobile.tictactoep2p.server.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import fatepi.posmobile.tictactoep2p.activity.MainActivity;
import fatepi.posmobile.tictactoep2p.client.manager.HandlerManager;
import fatepi.posmobile.tictactoep2p.model.AbstractModel;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 15/02/16.
 */
public class ServerManager extends Thread {

    private Context context;
    private Handler handler;
    private int lastIdClient;
    private ServerSocket serverSck;

    private Socket opponentSck;
    private ObjectOutputStream opponentStream;

    //private PlayerManager playerManager;
    private GameManager gameManager;

    private boolean running = false;

    public ServerManager(Context context, Player owner, Handler handler) throws Exception {

        this.context = context;

        if(owner == null)
            throw new Exception("Criador do jogo não encontrado!");

        this.handler = handler;

        this.lastIdClient = 0;
        inicializaManager();

        this.gameManager.reset();
        this.gameManager.setOwner(owner);
        //this.playerManager.addPlayerList(owner);

    }

    public boolean isRunning() {
        return serverSck != null && !serverSck.isClosed();
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ServerSocket getServerSck() {
        return serverSck;
    }

    public int getLastIdClient() {
        return lastIdClient;
    }

    public int incrementClient(){
        return lastIdClient++;
    }

    public void inicializaManager(){

        this.gameManager = GameManager.getInstance();
        gameManager.setServerManager(this);

    }

    /**
     * Roda o Servidor:
     *  - Aguarda Player
     *  - Inicia a thread q irão receber as mensagens vindas do Player
     * @throws IOException
     */
    public void run() {

        try {

            serverSck = new ServerSocket(Constantes.PORT);

            Log.d(Constantes.LOG_TAG, "Ip do Servidor: " + InetAddress.getLocalHost().getHostAddress());
            Log.d(Constantes.LOG_TAG, "Porta " + Constantes.PORT + " Aberta");
            running = true;

            //new Loop("loopServer").start();

            //while (isConnected()) {

                opponentSck = serverSck.accept();
                Log.d(Constantes.LOG_TAG, "Player conectou: " + opponentSck.getInetAddress().getHostName());
                opponentStream = new ObjectOutputStream(opponentSck.getOutputStream());

                ReceiverManager rec = new ReceiverManager(this, opponentSck);
                new Thread(rec, "ReceiverManager: "+opponentSck.getInetAddress().getAddress()).start();

            //}

        } catch (IOException e) {
            this.cancel(false);
        }

    }

    public boolean isConnected(){
        //return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        return true;
    }

    /**Envia objeto para o player
     *
     * @param o saida Servidor - Player
     * @param s - Objeto
     * @throws IOException - entrada/saida
     */
    public synchronized void sendObject(ObjectOutputStream o, AbstractModel s) {
        try {

            if(o != null)
            {
                o.writeObject(s.getPkg());
                o.reset();
                o.flush();
            }else{
                Log.e(Constantes.LOG_TAG, "Erro, saida nao encontrada ");
            }
        } catch (IOException ex) {
            Log.e(Constantes.LOG_TAG, "Erro ao enviar objeto para o Player: " + ex);
        }
    }

    public synchronized void sendObject(Player cli, AbstractModel s) {

        Player owner = this.gameManager.getGame().getOwner();
        if(cli != null && owner != null && owner.getId() == cli.getId()){
            Log.d(Constantes.LOG_TAG, "Ignorando envio de dados para o player servidor: " + cli.getNome());
            return;
        }

        //ObjectOutputStream o = searchOutput(cli);
        ObjectOutputStream o = opponentStream;
        if (o != null) {
            sendObject(o, s);
        } else {
            Log.e(Constantes.LOG_TAG, "Erro, saida nao encontrada para o Player: " + cli.getNome());
        }
    }

    /**
     * Distribui algum tipo de objeto genÃ©rico para todos os players
     * Ex.: Ao iniciar o Player envia seu Usuario para o servidor, que
     * por sua vez atualiza a lista de contatos e distribui para todos os que
     * estÃ£o online.
     * @param obj
     */
    public synchronized void distributeObject(AbstractModel obj) { //para todos

        if(opponentStream == null) return;

        Log.d(Constantes.LOG_TAG, "SendManager.distributeObject: "+obj.toString());
        synchronized (opponentStream) {
            sendObject(opponentStream, obj);
        }
    }

    public void cancel(boolean redirect, boolean alert) {

        try {
            running = false;
            if(serverSck != null)
                serverSck.close();
            Log.d(Constantes.LOG_TAG, "Server closed!");

            if(context != null && redirect){
                Intent i = new Intent(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if(alert){
                    i.putExtra("cod", HandlerManager.COD_MY_PLAYER_DISCONNECT);
                    //i.putExtra("msg", context.getString(R.string.erro_servidor_desconectou));
                }
                context.startActivity(i);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(boolean redirect) {
        this.cancel(redirect, false);
    }

    public String getMsg(int id){
        if(context == null) return "";
        return context.getString(id);
    }

}
