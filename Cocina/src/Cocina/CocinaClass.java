/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cocina;

import java.util.ArrayList;
import Salon.Pedido;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    private ObjectInputStream inputArrayPedidos;
    
    //Atributo para realizar el casting
    private Pedido pedidoEntrante;
    
    private ArrayList<Pedido> pedidosPendientes;
    
    public CocinaClass(){
        pedidosPendientes = new ArrayList<>();
        
        conectar();
    }
    
    public void conectar(){
        try {
            SocketPedidoDevolver = new Socket("127.0.0.1", 5555);
            outputPedido = new ObjectOutputStream(SocketPedidoDevolver.getOutputStream());
            inputArrayPedidos = new ObjectInputStream(SocketPedidoDevolver.getInputStream());
        } catch (IOException ex) {
            System.out.println(ex + " LINE: 38");
        }
    }
    
    public void recibirListaPedidosPendientes(){
        try {
            while (true) {                
                System.out.println("Cantidad pendientes antes: " + pedidosPendientes.size());
                
                // pedidos pendientes toma el valor del array enviado por ele servidor
                pedidosPendientes = (ArrayList<Pedido>) inputArrayPedidos.readObject();
                
                System.out.println("Cantidad pendientes despues: " + pedidosPendientes.size());
                System.out.println("Recibe pedido pendiente de servidor");
            }
        } catch (Exception e) {
            System.out.println(e + " LINE: 52");
        }
    }
    
    public void devolverPedido(Pedido pedidoDevolver){
        try {
//            SocketPedidoDevolver = new Socket("127.0.0.1", 5555);
//            outputPedido = new ObjectOutputStream(SocketPedidoDevolver.getOutputStream());
            outputPedido.writeObject(pedidoDevolver);
            outputPedido.flush();
            
//            outputPedido.close();
//            SocketPedidoDevolver.close();
        } catch (Exception e) {
            System.out.println(e + " LINE: 68");
        }
    }
}
