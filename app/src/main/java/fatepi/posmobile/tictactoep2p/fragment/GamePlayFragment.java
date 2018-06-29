package fatepi.posmobile.tictactoep2p.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import fatepi.posmobile.tictactoep2p.R;
import fatepi.posmobile.tictactoep2p.activity.MainActivity;
import fatepi.posmobile.tictactoep2p.client.manager.PlayerManager;
import fatepi.posmobile.tictactoep2p.component.HandlerDefault;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Move;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * A simple {@link Fragment} subclass.
 */
public class GamePlayFragment extends Fragment {

    public static final String TAG = "GAME_PREPARE_FRAGMENT";
    private MainActivity mainActivity;
    private Handler handler;
    private Button[][] buttons;
    private boolean callInitRound = false;

    public static GamePlayFragment newInstance() {
        GamePlayFragment fragment = new GamePlayFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        callInitRound = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_play, container, false);

        buttons = new Button[3][3];
        buttons[0][0] = (Button)v.findViewById(R.id.bt1);
        buttons[0][1] = (Button)v.findViewById(R.id.bt2);
        buttons[0][2] = (Button)v.findViewById(R.id.bt3);
        buttons[1][0] = (Button)v.findViewById(R.id.bt4);
        buttons[1][1] = (Button)v.findViewById(R.id.bt5);
        buttons[1][2] = (Button)v.findViewById(R.id.bt6);
        buttons[2][0] = (Button)v.findViewById(R.id.bt7);
        buttons[2][1] = (Button)v.findViewById(R.id.bt8);
        buttons[2][2] = (Button)v.findViewById(R.id.bt9);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                final Button bt = buttons[i][j];
                final int finalI = i;
                final int finalJ = j;
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickButton(bt, finalI, finalJ);
                    }
                });

            }
        }

        setTitlePlayer();
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        setupHandler();
        if(mainActivity.isServer()){
            fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().getServerManager().setHandler(handler);
        }else{
            fatepi.posmobile.tictactoep2p.client.manager.GameManager.getInstance().getClientManager().setHandler(handler);
        }

        populateBoard();

    }

    @Override
    public void onStop() {
        super.onStop();
        mainActivity.setSubtitleBar("");
    }

    public void setTitlePlayer(){
        String title = PlayerManager.getInstance().getMyPlayer().isPodeJogar() ? "(Sua Vez)" : "(Vez do oponente)";
        mainActivity.setTitle("Jogar "+title);
        mainActivity.setSubtitleBar("Rodada "+mainActivity.getGame().getRoundCurrent());
    }

    public void clickButton(Button bt, int x, int y){
        Log.d(Constantes.LOG_TAG, "clickButton: "+x+","+y);
        if(!PlayerManager.getInstance().getMyPlayer().isPodeJogar()
                || !bt.getText().toString().isEmpty()
                || mainActivity.getGame().getStatus() != Constantes.ST_INICIADO)
            return;

        Move move = new Move(x, y,
                mainActivity.isServer() ? Constantes.COD_X : Constantes.COD_0,
                mainActivity.isServer()
                );

        bt.setText(move.getValueString());

        if(mainActivity.isServer()){
            fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().execMove(move, true);
        }else{
            fatepi.posmobile.tictactoep2p.client.manager.GameManager.getInstance().execMove(move);
        }

        PlayerManager.getInstance().getMyPlayer().setPodeJogar(false);
        setTitlePlayer();
    }

    private void setupHandler(){
        Log.d(Constantes.LOG_TAG, "GamePlayActivity: setupHandler");

        handler = new HandlerDefault(this.getActivity()){
            @Override
            public void onGameUpdate() {
                Log.d(Constantes.LOG_TAG, "GamePlayActivity: COD_GAME_UPDATE ");
                Game g = mainActivity.getGame();
                if(g.getStatus() == Constantes.ST_INICIADO) {
                    PlayerManager.getInstance().getMyPlayer().setPodeJogar(!PlayerManager.getInstance().getMyPlayer().isPodeJogar());
                    setTitlePlayer();
                    populateBoard();
                }
                else if(g.getStatus() == Constantes.ST_FINALIZADO) {

                    populateBoard();
                    PlayerManager.getInstance().getMyPlayer().setPodeJogar(false);

                    mainActivity.setTitle("Fim de jogo");

                    String msg = "Jogo finalizado sem vencedor!";
                    switch (g.getWiner()){
                        case Constantes.COD_X:
                            msg = "Vitória de "+g.getOwner().getNome();
                            if(mainActivity.isServer()){
                                PlayerManager.getInstance().setMyPlayer(g.getOwner());
                                PlayerManager.getInstance().getMyPlayer().setPodeJogar(true);
                                PlayerManager.getInstance().setIsServer(true);
                            }
                            break;
                        case Constantes.COD_0:
                            msg = "Vitória de "+g.getOpponent().getNome();
                            if(!mainActivity.isServer()){
                                PlayerManager.getInstance().setMyPlayer(g.getOpponent());
                                PlayerManager.getInstance().getMyPlayer().setPodeJogar(true);
                                PlayerManager.getInstance().setIsServer(false);
                            }
                            break;
                        default:
                            if(mainActivity.isServer())
                                PlayerManager.getInstance().getMyPlayer().setPodeJogar(true);
                            break;

                    }

                    PlayerManager.getInstance().getMyPlayer().setConectado(true);

                    Toast.makeText(GamePlayFragment.this.getContext(), msg, Toast.LENGTH_SHORT).show();
                    if(mainActivity.isServer() && !callInitRound){
                        callInitRound = true;
                        this.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().initRound();
                            }
                        }, SystemClock.uptimeMillis() + 4000);
                    }

                }

                else if(g.getStatus() == Constantes.ST_AGUARDANDO) {
                    mainActivity.replaceContent(GamePrepareFragment.newInstance(), GamePrepareFragment.TAG);
                }

            }

            @Override
            public void onBoardUpdate() {
                Log.d(Constantes.LOG_TAG, "GamePlayActivity: COD_BOARD_UPDATE ");
            }
        };
    }

    public void populateBoard(){
        int[][] board = mainActivity.getGame().getBoard();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                int val = board[i][j];
                Button bt = buttons[i][j];
                switch (val){
                    case 0:
                        bt.setText("");
                        bt.setTextColor(ContextCompat.getColor(getContext(), R.color.colorIconsDark));
                        break;
                    case Constantes.COD_X:
                        bt.setText("X");
                        bt.setTextColor(ContextCompat.getColor(getContext(), R.color.colorX));
                        break;
                    case Constantes.COD_0:
                        bt.setText("0");
                        bt.setTextColor(ContextCompat.getColor(getContext(), R.color.color0));
                        break;
                }



            }
        }
    }
}
