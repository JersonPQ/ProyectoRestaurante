/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Salon;

import java.io.IOException;
import java.util.ArrayList;
import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author jerson
 */
public class SalonClass {
    
    //Atributos para mandar información a las cocina
    Socket PedidoSocket;
    ObjectOutputStream output;
    
    // atributos para recibir pedido
    private Socket SocketRecibir;
    private ServerSocket server;
    private ObjectInputStream inputArrayPedidos;
    private Pedido pedidoDevuelto;
    
    private ArrayList<Mesa> mesas;
    
    ArrayList<ArrayList<Pedido>> pedidos;
    
    // copia de servidor pedidos pendientes
    ArrayList<Pedido> pedidosListos;
    
    // copia de servidor pedidos listos
    ArrayList<Pedido> pedidosPendientes;
    
    public SalonClass(){
        // incializar arrays de pedidos
        pedidos = new ArrayList<>();
        
        pedidosListos = new ArrayList<>();
        pedidosPendientes = new ArrayList<>();
        
        conectar();
    }
    
    // GENERAL
    
    // método actualiza los arrays de los pedidos listos y los pendientes
    // añade el pedido a los pedidos listos y lo elimina de pedidos 
    public void actualizarPedidos(Pedido pedidoListo){
        
        // elimina el pedido listo en el array de pedidos pendientes
        for (int i = 0; i < pedidosPendientes.size(); i++) {
            if (pedidoListo.getID() == pedidosPendientes.get(i).getID()) {
                pedidosPendientes.remove(i);
                break;
            }
        }
        
        pedidosListos.add(pedidoListo);
        
//        for (Pedido pedido : pedidosListos) {
//            System.out.println(pedido.getID());
//        }
    }
    
    // CONEXIONES
    
    public void conectar(){
        try {
            PedidoSocket = new Socket("127.0.0.1", 4444);
            output = new ObjectOutputStream(PedidoSocket.getOutputStream());
            inputArrayPedidos = new ObjectInputStream(PedidoSocket.getInputStream());

        } catch (IOException ex) {
            System.out.println(ex + "    LINE: 50");
        }
    }
    
    public void recibirListaPedidos(){      
        try {
            while (true) {                    
                pedidos = (ArrayList<ArrayList<Pedido>>) inputArrayPedidos.readObject();

                System.out.println("Antes listos: " + pedidosListos.size());

                // actualiza array de pedidos pendientes
                pedidosPendientes = pedidos.get(0);

                // actualiza array de pedidos listos
                pedidosListos = pedidos.get(1);

                System.out.println("Despues listos: " + pedidosListos.size());

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.out.println(e + "    LINE: 67");
        }

        System.out.println("Pedidos pendientes: " + pedidosPendientes.size() + " Pedidos listos: " + pedidosListos.size());
    }
    
    public void enviarPedido(Pedido pedido){
        try{
            output.writeObject(pedido);
            output.flush();
        }
        catch(Exception ex){
            System.out.println(ex + "    LINE: 85");
        }
    }
}
