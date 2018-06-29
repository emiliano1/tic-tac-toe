package fatepi.posmobile.tictactoep2p.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 16/02/16.
 */
public class Game extends AbstractModel {

    private int id;
    private int status;
    private int time;
    private Player owner;
    private Player opponent;
    private int[][] board;
    private int winer;

    private int roundCurrent;

    public Game() {
    }

    public Game(int id, int status, int time, Player owner) {
        this.id = id;
        this.status = status;
        this.time = time;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusString(){
        switch (this.status){
            case Constantes.ST_AGUARDANDO: return "Aguardando";
            case Constantes.ST_ROUND_ANDAMENTO: return "Rodada em Andamento";
            case Constantes.ST_ROUND_FIM: return "Rodada Finalizada";
            case Constantes.ST_INICIADO: return "Iniciado";
            case Constantes.ST_FINALIZADO: return "Finalizado";
            default: return "";
        }
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getRoundCurrent() {
        return roundCurrent;
    }

    public void setRoundCurrent(int roundCurrent) {
        this.roundCurrent = roundCurrent;
    }

    public void roundIncrement() {
        this.roundCurrent++;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    @Override
    public int getCod() {
        return Constantes.COD_GAME_MODEL;
    }

    public int getWiner() {
        return winer;
    }

    public void setWiner(int winer) {
        this.winer = winer;
    }

    @Override
    /**
     * Posições:
     * 0..1 : id
     * 2..a : word
     * a..b : players
     */
    public String beanToString() {
        String pkg = "";

        String idString = String.valueOf(this.getId());
        String statusString = String.valueOf(this.getStatus());
        String winerString = String.valueOf(this.getWiner());
        String tempoString = String.valueOf(this.getTime());

        if(idString.length() > 2 || statusString.length() > 1)
            throw new IllegalArgumentException("Objeto com valores incorretos");

        if (idString.length() < 2)
            pkg += "0";
        pkg += idString;
        pkg += statusString;
        pkg += winerString;

        pkg += tempoString+getTempCod("T");
        pkg += String.valueOf(this.roundCurrent)+getTempCod("RI");

        if(this.owner != null){
            String wordString = this.owner.beanToString();
            pkg += wordString+getTempCod("OW");
        }

        if(this.opponent != null){
            String wordString = this.opponent.beanToString();
            pkg += wordString+getTempCod("OP");
        }

        if(this.board == null)
            this.board = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                pkg += this.board[i][j];
            }
        }

        pkg += getTempCod("BD");

        return pkg;
    }

    @Override
    public void stringToModel(String pkg) {
        int index = 0;
        this.id = new Integer(pkg.substring( index, index+=2 ));
        this.status = new Integer(pkg.substring( index, index+=1 ));
        this.winer = new Integer(pkg.substring( index, index+=1 ));
        String tempCod = "";

        pkg = pkg.substring(index);

        tempCod = getTempCod("T");
        index = pkg.indexOf(tempCod);
        this.time = new Integer(pkg.substring(0, index));
        pkg = pkg.substring(index+=tempCod.length());

        tempCod = getTempCod("RI");
        index = pkg.indexOf(tempCod);
        this.roundCurrent = new Integer(pkg.substring(0, index));
        pkg = pkg.substring(index+=tempCod.length());

        tempCod = getTempCod("OW");
        index = pkg.indexOf(tempCod);
        if(index >= 0){
            this.owner = Player.newModel(pkg.substring(0, index));
            pkg = pkg.substring(index+=tempCod.length());
        }

        tempCod = getTempCod("OP");
        index = pkg.indexOf(tempCod);
        if(index >= 0){
            this.opponent = Player.newModel(pkg.substring(0, index));
            pkg = pkg.substring(index+=tempCod.length());
        }

        tempCod = getTempCod("BD");
        index = pkg.indexOf(tempCod);
        if(index >= 0){
            String boardString = pkg.substring(0, index);

            if(this.board == null)
                this.board = new int[3][3];

            int ind = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    this.board[i][j] = Integer.valueOf(boardString.substring(ind, ind+1)).intValue();
                    ind++;
                }
            }

        }

    }

    @Override
    public String toString() {
        String tos = "";

        tos += "Game[\nId: "+this.id+", Status: "+this.status+", RoundCurrent: "+this.roundCurrent+", Winer: "+this.winer+
                "\nOwner: "+this.owner+"\n"+
                "\nOpponent: "+this.opponent+"\n"+
                "\nBoard: "+ printBoart()+"\n";

        tos += "]";

        return tos;
    }

    public String printBoart(){
        String s = "";
        s+=Arrays.toString(this.board[0])+", ";
        s+=Arrays.toString(this.board[1])+", ";
        s+=Arrays.toString(this.board[2]);
        return s;
    }

    public static Game newModel(String pkg){
        Game w = new Game();
        w.stringToModel(pkg);
        return w;
    }

    public static void main(String[] args) {
        System.out.println(newModel("0100060#1T#0#1RI#0000001117Yakeme Samsung J1#1OW#0000000010Magno Leal#1OP#000000000#1BD#"));
    }
}
