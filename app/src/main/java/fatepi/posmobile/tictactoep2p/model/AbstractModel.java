package fatepi.posmobile.tictactoep2p.model;

/**
 * Created by cti on 15/02/16.
 */
public abstract class AbstractModel {

    public abstract int getCod();
    public abstract String beanToString();
    public abstract void stringToModel(String pkg);

    protected String padNum(int num){
        String retorno = "";
        if(num < 10)
            retorno += "0";
        retorno += String.valueOf(num);
        return retorno;
    }

    public String getCodString(){
        return this.padNum(getCod());
    }

    public String getPkg(){
        return getCodString()+beanToString();
    }

    protected String getTempCod(String cod){
        return "#"+getCod()+cod+"#";
    }

}
