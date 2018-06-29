package fatepi.posmobile.tictactoep2p.server.manager;

import android.util.Log;

import fatepi.posmobile.tictactoep2p.client.manager.HandlerManager;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Move;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 16/02/16.
 */
public class GameManager {

    private static GameManager ourInstance;
    private ServerManager serverManager;

    private Game game;

    public static GameManager getInstance() {
        if(ourInstance == null)
            ourInstance = new GameManager();
        return ourInstance;
    }

    public static void clearInstance() {
        ourInstance = null;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    private GameManager() {
        Log.d(Constantes.LOG_TAG, "new GameManager (server)");
        clearManagers();
        this.initManagers();
        this.reset();
    }

    public Game getGame() {
        return game;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public void nextRound() throws Exception{

        /*if(game.getRounds() == null)
            throw new Exception(serverManager.getMsg(R.string.erro_sem_rodadas));

        if(this.game.getRoundCurrent() < game.getRounds().size()) { //ultimo
            this.game.roundIncrement();
            game.setStatus(Constantes.ST_INICIADO);
        }*/

        this.game.roundIncrement();
        game.setStatus(Constantes.ST_INICIADO);
        this.sendGame();

    }

    private void initManagers(){

    }

    private void clearManagers(){
        /*ChallengeManager.clearInstance();
        PlayerManager.clearInstance();
        RoundManager.clearInstance();
        WordManager.clearInstance();*/
    }

    //public void setWord(String nome, String dica){
    //    this.game.setWord(new Word(0, nome, dica));
    //}

    public void setTime(int time){
        this.game.setTime(time);
    }

    public void setTimeDefault(){
        this.setTime(Constantes.TIME_DEFAULT);
    }

    public void setOwner(Player owner){
        this.game.setOwner(owner);
    }

    public void reset(){
        this.game = new Game();
        this.setTimeDefault();
        this.game.setStatus(Constantes.ST_AGUARDANDO);
        this.game.setRoundCurrent(1);
        this.game.setBoard(new int[3][3]);
        this.game.setWiner(0);
        this.sendGame();
    }

    public void initGame() throws Exception {

        Log.d(Constantes.LOG_TAG, "GameManager.initGame");
        if(this.game.getOpponent() == null)
            throw new Exception("Oponente não encontrado!");

        this.game.setWiner(0);
        this.game.setStatus(Constantes.ST_INICIADO);
        this.sendGame();
    }

    public void initRound(){
        Log.d(Constantes.LOG_TAG, "GameManager.initRound "+this.game.getRoundCurrent());
        this.game.setStatus(Constantes.ST_AGUARDANDO);
        this.game.roundIncrement();
        this.game.setBoard(new int[3][3]);
        this.game.setWiner(0);
        this.sendGame();
    }

    public void sendGame(){
        if(this.serverManager != null) {
            this.serverManager.distributeObject(this.game);
            this.serverManager.getHandler().sendMessage(HandlerManager.msgGameUpdate());
            Log.d(Constantes.LOG_TAG, "GameManager: HandlerManager.msgGameUpdate");
        }else{
            Log.e(Constantes.LOG_TAG, "GameManager: serverManager is null");
        }
    }

    public void disconnectGame(boolean sendGame){
        game.setStatus(Constantes.ST_DISCONECTADO);
        game.getOwner().setConectado(false);
        if(sendGame)
            sendGame();
    }

    public void sendMove(Move move){

    }

    public void execMove(Move move, boolean fromServer){
        try {
            int[][] board = this.game.getBoard();
            if(board[move.getPosX()][move.getPosY()] != 0)
                return;

            if(move.getValue() != Constantes.COD_X || move.getValue() != Constantes.COD_0){
                board[move.getPosX()][move.getPosY()] = move.getValue();
                this.game.setBoard(board);
                if(fromServer)
                    this.serverManager.distributeObject(game);
                else
                    this.serverManager.getHandler().sendMessage(HandlerManager.msgGameUpdate());
            }

            int winer = checkFinsish();
            if(winer != 0){
                this.game.setStatus(Constantes.ST_FINALIZADO);
                this.game.setWiner(winer);

                if(winer == Constantes.COD_X)
                    this.game.getOwner().pontuacaoIncrement();
                else if(winer == Constantes.COD_0)
                    this.game.getOpponent().pontuacaoIncrement();

                this.serverManager.distributeObject(game);
                this.serverManager.getHandler().sendMessage(HandlerManager.msgGameUpdate());
            }

        }catch (Exception ex){

        }
    }

    public int checkFinsish(){
        int c = checaLinhas();
        if(c != 0) return c;

        c = checaColunas();
        if(c != 0) return c;

        c = checaDiagonais();
        if(c != 0) return c;

        if(boardCompleto()) return 3;

        return c;
    }

    public int checaLinhas(){

        int[][] board = this.game.getBoard();

        for(int l=0 ; l < 3 ; l++){

            if(board[l][0] == Constantes.COD_X && board[l][1] == Constantes.COD_X && board[l][2] == Constantes.COD_X)
                return Constantes.COD_X;
            if(board[l][0] == Constantes.COD_0 && board[l][1] == Constantes.COD_0 && board[l][2] == Constantes.COD_0)
                return Constantes.COD_0;
        }

        return 0;

    }

    private int checaColunas(){

        int[][] board = this.game.getBoard();
        for(int c=0 ; c<3 ; c++){

            if(board[0][c] == Constantes.COD_X && board[1][c] == Constantes.COD_X &&  board[2][c] == Constantes.COD_X)
                return Constantes.COD_X;
            if(board[0][c] == Constantes.COD_0 && board[1][c] == Constantes.COD_0 &&  board[2][c] == Constantes.COD_0)
                return Constantes.COD_0;
        }

        return 0;

    }

    private int checaDiagonais(){

        int[][] board = this.game.getBoard();

        if(board[0][0] == Constantes.COD_X && board[1][1] == Constantes.COD_X && board[2][2] == Constantes.COD_X)
            return Constantes.COD_X;
        if(board[0][0] == Constantes.COD_0 && board[1][1] == Constantes.COD_0 && board[2][2] == Constantes.COD_0)
            return Constantes.COD_0;

        if(board[0][2] == Constantes.COD_X && board[1][1] == Constantes.COD_X && board[2][0] == Constantes.COD_X)
            return Constantes.COD_X;
        if(board[0][2] == Constantes.COD_0 && board[1][1] == Constantes.COD_0 && board[2][0] == Constantes.COD_0)
            return Constantes.COD_0;

        return 0;
    }

    public boolean boardCompleto(){
        int[][] board = this.game.getBoard();
        for(int linha=0 ; linha<3 ; linha++)
            for(int coluna=0 ; coluna<3 ; coluna++)
                if( board[linha][coluna] == 0 )
                    return false;
        return true;
    }

}
