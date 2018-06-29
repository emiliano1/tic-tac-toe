package fatepi.posmobile.tictactoep2p.model;

import fatepi.posmobile.tictactoep2p.util.Constantes;

/**
 * Created by magno on 05/07/16.
 */
public class Move extends AbstractModel {

    private int posX;
    private int posY;
    private int value;
    private boolean fromOwner;

    public Move() {

    }

    public Move(int posX, int posY, int value, boolean fromOwner) {
        this.posX = posX;
        this.posY = posY;
        this.value = value;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public int getValue() {
        return value;
    }

    public String getValueString() {
        if(value != Constantes.COD_X && value != Constantes.COD_0)
            return "";
        return value == Constantes.COD_X ? "X" : "0";
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isFromOwner() {
        return fromOwner;
    }

    public void setFromOwner(boolean fromOwner) {
        this.fromOwner = fromOwner;
    }

    @Override
    public int getCod() {
        return Constantes.COD_MOVE_MODEL;
    }

    @Override
    public String beanToString() {

        String posXString = String.valueOf(posX);
        String posYString = String.valueOf(posY);
        String valueString = String.valueOf(value);
        String valFromOwner = fromOwner ? "1" : "0";

        return posXString+posYString+valueString+valFromOwner;
    }

    @Override
    public void stringToModel(String pkg) {
        posX = new Integer(pkg.substring( 0, 1 ));
        posY = new Integer(pkg.substring( 1, 2 ));
        value = new Integer(pkg.substring( 2, 3 ));
        fromOwner = new Integer(pkg.substring( 3, 4 )).intValue() == 1;
    }
}
