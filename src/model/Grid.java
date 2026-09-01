package model;

/*
    Grilla o cuadricula de fila x columna (usada en la mision 1- "Rescatar a Nina")
    El objetivo de esta clase es tener una referencia geometrica del mapa (dimensiones y bombas)
*/

import Exceptions.EFueraRango;
import Exceptions.ENumeroNegativo;

public final class Grid {

    //Atributos
    private final int fila;
    private final int columna;
    private final boolean [][] bombas; //Cuidadoooo ☣️🐈


    //Constructor
    public Grid(int fila,int columna)throws ENumeroNegativo {
        if(fila<=0 || columna <=0){throw new ENumeroNegativo("filas y columnas deben ser positivas");}

        this.fila=fila;
        this.columna=columna;
        this.bombas=new boolean[fila][columna];
    }

    public int getFilas(){return fila;}

    public int getColumnas(){return columna;}

    // Marcar la celda (fila,columna) como ocupada por una bomba

    public void setBombas(int fila,int col){
        valido(fila,col);
        bombas[fila][col]=true;
    }

    //Validar si la celda (fila,columna)
    public boolean hayBomba(int fila,int col){
            valido(fila,col);
            return bombas[fila][col];
        }

    //Metodo con llamada recursiva para saber si hay bomba
    public boolean hayBomba(Punto p){
        return hayBomba(p.getColumna(),p.getColumna());
    }

    // Metodo para saber si el punto esta dentro de los limites
    public boolean limite(int row,int col){
        return row >=0 && row <fila && col >=0 && col < columna;
    }

    // Si la celda existe y ademas es transitable (dentro de limites y bombas)
    public boolean esCaminable(int fila, int col){
        return limite(fila,col) && !bombas[fila][col];
    }

    public void valido(int row,int col){
        if(!limite(row,col)){
        throw new EFueraRango("Celda fuera de rango: ("+row+", "+col+") en grilla "+fila+"x"+col);
        }
    }

}
