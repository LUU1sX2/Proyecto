/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Jose Luis
 */
public class Ficha {

    private int xPosicion;
    private int yPosicion;
    private boolean existe;

    public Ficha(int x, int y, boolean existe) {
        this.xPosicion = x;
        this.yPosicion = y;
        this.existe = existe;
    }

    public int getxPosicion() {
        return xPosicion;
    }

    public void setxPosicion(int xPosicion) {
        this.xPosicion = xPosicion;
    }

    public int getyPosicion() {
        return yPosicion;
    }

    public void setyPosicion(int yPosicion) {
        this.yPosicion = yPosicion;
    }

    public boolean isExiste() {
        return existe;
    }

    public void setExiste(boolean existe) {
        this.existe = existe;
    }

}
