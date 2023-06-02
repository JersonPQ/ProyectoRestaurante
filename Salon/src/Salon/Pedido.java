
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Salon;
import Cocina.Hamburguesa;

/**
 *
 * @author jerson
 */

import java.util.ArrayList;
//import Cocina.Hamburguesa;
import java.io.Serializable;

public class Pedido implements Serializable{
    
    //private ArrayList<Hamburguesa> listaPedido;
    private boolean pedidoCompletado;
    private int idMesa;

    protected static int contadorPedido = 0;

    private ArrayList<Hamburguesa> burguers;

    private int id;
    
    public Pedido(int _idMesa){
        id = contadorPedido;
        contadorPedido++;
        this.idMesa = _idMesa;
    }
    
    public int getID(){
        return id;
    }
    
    public int getIdMesa(){
        return idMesa;
    }
    
    
}
