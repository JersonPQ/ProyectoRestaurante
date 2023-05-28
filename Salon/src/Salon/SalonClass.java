/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Salon;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

/**
 *
 * @author jerson
 */
public class SalonClass {
    
    //Atributos para mandar información a las cocina
    Socket PedidoSocket;
    ObjectOutputStream output;
    
    private ArrayList<Mesa> mesas;
    
    void enviarPedido(Pedido pedido){
        try{
            PedidoSocket = new Socket("127.0.0.1", 3333);
            output = new ObjectOutputStream(PedidoSocket.getOutputStream());
            output.writeObject(pedido);
            output.flush();
            output.close();
            PedidoSocket.close();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
}
