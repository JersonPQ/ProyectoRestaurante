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
    
    //Atributo para realizar el casting
    private Pedido pedidoEntrante;
    
    private ArrayList<Pedido> pedidosPendientes;
    
    public void recibirPedido(){
        try{
            server = new ServerSocket(3333);
            SocketRecibir = server.accept();
            input = new ObjectInputStream(SocketRecibir.getInputStream());
            pedidoEntrante = (Pedido) input.readObject();
            
            System.out.println("Llego");
            
            input.close();
            SocketRecibir.close();
            server.close();
        }
        catch(Exception ex){
            System.out.println(ex);
        }
    }
}
