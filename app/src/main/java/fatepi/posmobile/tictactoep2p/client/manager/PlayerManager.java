package fatepi.posmobile.tictactoep2p.client.manager;

import android.content.SharedPreferences;
import android.util.Log;

import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by magno on 17/02/16.
 */
public class PlayerManager {

    private static PlayerManager ourInstance;
    private Player myPlayer;
    private boolean isServer;
    private SharedPreferences sharedPreferences;

    public static PlayerManager getInstance() {
        if(ourInstance == null)
            ourInstance = new PlayerManager();
        return ourInstance;
    }

    public static void clearInstance() {
        ourInstance = null;
    }

    private PlayerManager() {
    }

    public void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void setMyPlayer(Player myPlayer) {
        Log.d(Constantes.LOG_TAG, "setMyPlayer: "+myPlayer);
        this.myPlayer = myPlayer;
        storedPlayer();
    }

    public Player getMyPlayer() {
        return myPlayer;
    }

    public boolean hasPlayer(){
        return myPlayer != null;
    }

    public void loadStoredUser() {
        if(this.sharedPreferences != null){
            String playerString = sharedPreferences.getString("my_player", null);
            if(playerString != null){
                try {
                    this.setMyPlayer(Player.newModel(playerString));
                } catch (Exception e) {
                    this.rmStoragePlayer();
                }
            }
        }
    }

    public void storedPlayer(){
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(this.myPlayer != null){

                //alguns dados nao devem ser armazenados
                Player temp = new Player();
                temp.setConectado(false);
                temp.setNome(this.myPlayer.getNome());
                temp.setCodImagem(this.myPlayer.getCodImagem());
                temp.setId(0);
                temp.setPodeJogar(true);
                temp.setPontuacao(0);

                String playerString = temp.beanToString();
                editor.putString("my_player", playerString);
                editor.commit();
            }

        }
    }

    public void rmStoragePlayer(){
        if(sharedPreferences != null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("my_player");
            editor.commit();
        }
    }

    public boolean isServer() {
        return isServer;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }
}
