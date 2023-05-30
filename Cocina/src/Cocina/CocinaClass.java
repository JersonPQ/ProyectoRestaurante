/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cocina;

import java.util.ArrayList;
import Salon.Pedido;
import java.net.*;
import java.io.*;


/**
 *
 * @author jerson
 */
public class CocinaClass {
    //Atributos para recibir pedido
    private Socket SocketRecibir;
    private ServerSocket server;
    private ObjectInputStream input;
    
    // atributos para devolver pedido a salon
    private Socket SocketPedidoDevolver;
    private ObjectOutputStream outputPedido;    
    
    //Atributo para realizar el casting
    private Pedido pedidoEntrante;
    
    private ArrayList<Pedido> pedidosPendientes;
    
    public void devolverPedido(Pedido pedidoDevolver){
        try {
            SocketPedidoDevolver = new Socket("127.0.0.1", 5555);
            outputPedido = new ObjectOutputStream(SocketPedidoDevolver.getOutputStream());
            outputPedido.writeObject(pedidoDevolver);
            outputPedido.flush();
            outputPedido.close();
            SocketPedidoDevolver.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
