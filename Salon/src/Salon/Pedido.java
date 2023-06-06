
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Salon;

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

    protected static int contadorPedido = 1;

    private ArrayList<Hamburguesa> arrayBurguer;

    private int id;
    
    public Pedido(){
        id = contadorPedido;
        contadorPedido++;
        pedidoCompletado = false;
        arrayBurguer = new ArrayList<Hamburguesa>();
    }
    
    public int getID(){
        return id;
    }
    
    public int getIdMesa(){
        return idMesa;
    }
    
    public ArrayList<Hamburguesa> getArrayHamburguesas(){
        return arrayBurguer;
    }
    
    public void setIdPedido(){
        id = contadorPedido;
        contadorPedido++;
    }
    
    public void setHamburguesa(Hamburguesa burguer){
        arrayBurguer.add(burguer);
    }
    
    public void setIdMesa(int id){
        this.idMesa = id;
    }
    
    public void setPedidoCompletado(){
        this.pedidoCompletado = true;
    }
}
