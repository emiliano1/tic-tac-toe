package fatepi.posmobile.tictactoep2p.fragment;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import fatepi.posmobile.tictactoep2p.R;
import fatepi.posmobile.tictactoep2p.activity.MainActivity;
import fatepi.posmobile.tictactoep2p.client.manager.PlayerManager;
import fatepi.posmobile.tictactoep2p.component.Dialog;
import fatepi.posmobile.tictactoep2p.component.HandlerDefault;
import fatepi.posmobile.tictactoep2p.model.Game;
import fatepi.posmobile.tictactoep2p.model.Player;
import fatepi.posmobile.tictactoep2p.util.Constantes;

public class GamePrepareFragment extends Fragment {

    public static final String TAG = "GAME_PREPARE_FRAGMENT";
    private MainActivity mainActivity;
    private Handler handler;
    private Button btJogar;
    private TextView tvOwnerNome;
    private TextView tvOwnerPontos;
    private TextView tvOpponentNome;
    private TextView tvOpponentPontos;

    public static GamePrepareFragment newInstance() {
        GamePrepareFragment fragment = new GamePrepareFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_game_prepare, container, false);

        btJogar = (Button) v.findViewById(R.id.btJogar);
        tvOwnerNome = (TextView) v.findViewById(R.id.tvOwnerNome);
        tvOwnerPontos = (TextView) v.findViewById(R.id.tvOwnerPontos);
        tvOpponentNome = (TextView) v.findViewById(R.id.tvOpponentNome);
        tvOpponentPontos = (TextView) v.findViewById(R.id.tvOpponentPontos);

        btJogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mainActivity.isServer()){
                    try {
                        fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().initGame();
                    } catch (Exception e) {
                        Dialog.alert(GamePrepareFragment.this.getActivity(), "Erro", e.getMessage());
                    }

                }


            }
        });

        mainActivity.setTitle("Aguardando nova rodada");

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        try{

            setupHandler();
            if(mainActivity.isServer()){
                fatepi.posmobile.tictactoep2p.server.manager.GameManager.getInstance().getServerManager().setHandler(handler);
                btJogar.setVisibility(View.VISIBLE);
            }else{
                fatepi.posmobile.tictactoep2p.client.manager.GameManager.getInstance().getClientManager().setHandler(handler);
                btJogar.setVisibility(View.GONE);
                //btFecharGame.hide();
            }
            populatePlayers();

        }catch (Exception ex){
            Log.e(Constantes.LOG_TAG, "GamePrepareFragment.onStart: "+ ex.getMessage());
            mainActivity.getManager().cancelConnect(mainActivity.getChannel(), new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.d(Constantes.LOG_TAG, "cancelConnect.onSuccess: ");
                    mainActivity.replaceContent(MainFragment.newInstance(), MainFragment.TAG);
                }

                @Override
                public void onFailure(int reason) {
                    Log.e(Constantes.LOG_TAG, "cancelConnect.onFailure: ");
                    mainActivity.replaceContent(MainFragment.newInstance(), MainFragment.TAG);
                }
            });
        }



    }

    public void populatePlayers(){
        Game g = mainActivity.getGame();

        if(g == null) return;

        Log.d(Constantes.LOG_TAG, "populatePlayers: "+g);

        if(g.getOwner() != null){
            tvOwnerNome.setText(g.getOwner().getNome());
            tvOwnerPontos.setText(g.getOwner().getPontuacao()+"");
        }

        if(g.getOpponent() != null){
            tvOpponentNome.setText(g.getOpponent().getNome());
            tvOpponentPontos.setText(g.getOpponent().getPontuacao()+"");
            btJogar.setEnabled(true);
        }else{
            tvOpponentNome.setText("Aguardando...");
            btJogar.setEnabled(false);
        }

    }

    private void setupHandler(){
        Log.d(Constantes.LOG_TAG, "GamePrepareActivity: setupHandler");
        handler = new HandlerDefault(this.getActivity()){
            @Override
            public void onGameUpdate() {

                Log.d(Constantes.LOG_TAG, "GamePrepareActivity: COD_GAME_UPDATE ");

                Game g = mainActivity.getGame();
                if(g.getStatus() == Constantes.ST_INICIADO){
                    mainActivity.replaceContent(GamePlayFragment.newInstance(), GamePlayFragment.TAG);
                }else{
                    populatePlayers();
                }


            }

        };
    }

}
