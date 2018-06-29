package fatepi.posmobile.tictactoep2p.model;

import android.content.Context;

import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by cti on 15/02/16.
 */
public class Player extends AbstractModel {

    private int id;
    private boolean conectado;
    private boolean podeJogar;
    private int pontuacao = 0;
    private int codImagem;
    private String nome;

    public Player() {
    }

    public Player(int id, boolean conectado, boolean podeJogar, int pontuacao, int codImagem, String nome) {
        this.id = id;
        this.conectado = conectado;
        this.podeJogar = podeJogar;
        this.pontuacao = pontuacao;
        this.codImagem = codImagem;
        this.nome = nome;
    }

    public Player(boolean conectado, boolean podeJogar, int pontuacao, String nome) {
        this.id = id;
        this.conectado = conectado;
        this.podeJogar = podeJogar;
        this.pontuacao = pontuacao;
        this.codImagem = codImagem;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }

    public boolean isPodeJogar() {
        return podeJogar;
    }

    public void setPodeJogar(boolean podeJogar) {
        this.podeJogar = podeJogar;
    }

    public void pontuacaoIncrement() {
        this.pontuacao++;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getCodImagem() {
        return codImagem;
    }

    public void setCodImagem(int codImagem) {
        this.codImagem = codImagem;
    }

    public int getImgResource(Context r, boolean mini){

        int img = codImagem+0;

        if(img <= 0)
            img = 1;

        String imgString = img+"";
        if(img < 10)
            imgString = "0"+imgString;

        String nome = "avatar";
        if(mini){
            nome += "_mini";
        }

        nome += imgString;

        return r.getResources().getIdentifier(nome, "drawable", r.getPackageName());
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Player[id:"+this.getId()+", nome:"+this.nome+
                ", conectado:"+this.isConectado()+", pontuacao:"+this.pontuacao+", codImagem:"+this.codImagem+"]";
    }

    public boolean equals(Object o) {

        if(o != null && o instanceof Player){
            Player x = (Player) o;
            return x.getId() == this.getId();
        }

        return false;
    }

    @Override
    public int getCod() {
        return Constantes.COD_PLAYER_MODEL;
    }

    @Override
    /**
     * Posições:
     * 0..1 : id
     * 2..3 : pontuação
     * 4..5
     * 6    : conectado
     * 7    : podeJogar
     * 7..8: tamanho do nome
     * 9..  : nome
     * ..   : tamanho de Pedras
     */
    public String beanToString() throws IllegalArgumentException{
        String pkg = "";

        String idString = String.valueOf(this.getId());
        String pontuacaoString = String.valueOf(this.getPontuacao());
        String codImagemString = String.valueOf(this.getCodImagem());
        String conectadoString = "";
        String podeJogarString = "";
        String nomeString = getNome();

        int numPedras = 0;

        if(pontuacaoString.length() > 2 || idString.length() > 2 || nomeString.length() > 99)
            throw new IllegalArgumentException("Os atributos desse objeto não podem ter valores maiores que 99");

        if (idString.length() < 2)
            pkg += "0";
        pkg += idString;

        if (pontuacaoString.length() < 2)
            pkg += "0";
        pkg += pontuacaoString;

        if (codImagemString.length() < 2)
            pkg += "0";
        pkg += codImagemString;

        if(isConectado())
            conectadoString = "1";
        else
            conectadoString = "0";

        pkg += conectadoString;

        if(isPodeJogar())
            podeJogarString = "1";
        else
            podeJogarString = "0";


        pkg += podeJogarString;

        if (nomeString.length() < 10)
            pkg += "0";
        pkg += nomeString.length();

        pkg += nomeString;

        return pkg;
    }

    @Override
    public void stringToModel(String pkg){

        int idBean = new Integer(pkg.substring( 0, 2 ));
        int pontuacaoBean = new Integer(pkg.substring( 2, 4 ));
        int codImagemBean = new Integer(pkg.substring( 4, 6 ));
        boolean conectadoBean;
        boolean podeJogarBean;
        int tamanhoNome = new Integer(pkg.substring( 8, 10 ));
        int index = 0;

        if (Integer.valueOf(String.valueOf(pkg.charAt(6))) == 1) {
            conectadoBean = true;
        } else {
            conectadoBean = false;
        }

        if (Integer.valueOf(String.valueOf(pkg.charAt(7))) == 1) {
            podeJogarBean = true;
        } else {
            podeJogarBean = false;
        }

        String nomeBean = "";
        index = 10;
        for (int i = 0; i < tamanhoNome; i++) {
            nomeBean += pkg.charAt(index);
            index++;
        }

        setId(idBean);
        setPontuacao(pontuacaoBean);
        setCodImagem(codImagemBean);
        setConectado(conectadoBean);
        setPodeJogar(podeJogarBean);
        setNome(nomeBean);

    }

    public static Player newModel(String pkg){
        Player w = new Player();
        w.stringToModel(pkg);
        return w;
    }

    public static void main(String[] args) {
        System.out.println(Player.newModel("0100251006Yasmim"));
    }
}
