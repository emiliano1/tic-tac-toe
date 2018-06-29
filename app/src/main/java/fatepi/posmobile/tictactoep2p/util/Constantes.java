package fatepi.posmobile.tictactoep2p.util;

/**
 * Created by magno on 05/07/16.
 */
public class Constantes {
    public static final String LOG_TAG = "TicTacToeP2P";
    public static final int PORT = 3579;

    /* Constantes referente ao JOGO */

    public static final int ST_AGUARDANDO       = 0;
    public static final int ST_ROUND_ANDAMENTO  = 1;
    public static final int ST_ROUND_FIM        = 2;
    public static final int ST_INICIADO         = 3;
    public static final int ST_FINALIZADO       = 4;
    public static final int ST_DISCONECTADO     = 5;

    /* Constantes referentes ao Codigo de Cada Model */
    public static final int COD_GAME_MODEL      = 1;
    public static final int COD_PLAYER_MODEL    = 2;
    public static final int COD_MOVE_MODEL      = 3;

    public static final int TIME_DEFAULT = 60; //em seg

    public static final String IT_MSG_DISCONNECT = "disconnect";

    public static final int COD_X = 1;
    public static final int COD_0 = 2;

}

