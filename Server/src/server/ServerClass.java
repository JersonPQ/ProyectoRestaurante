/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

import Salon.Pedido;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author jerson
 */
public class ServerClass implements Runnable{
    
    // server socket
    ServerSocket serverSalon;
    ServerSocket serverCocina;

    
    // socket para salon
    Socket socketRecibirSalon;
    ObjectInputStream inputSalon;
    ObjectOutputStream outputSalon;
    
    // socket para cocina
    Socket socketRecibirCocina;
    ObjectInputStream inputCocina;
    ObjectOutputStream outputCocina;
    
    // array que contiene los pedidos pendientes y pedidos listos
    ArrayList<ArrayList<Pedido>> pedidos;
    
    // array lists
    ArrayList<Pedido> pedidosPendientes;
    ArrayList<Pedido> pedidosListos;
    
    // pedido enviado desde salon
    Pedido pedidoRecibidoSalon;
    
    // pedido enviado desde cocina
    Pedido pedidoRecibidoCocina;
    
    // boolean para saber si metodo de abrir conexion con salon ya fue abierto
    boolean conexionSalonAbierta;
    
    public ServerClass(){
        
        // inicializa arraylists
        pedidos = new ArrayList<>();
        
        pedidosPendientes = new ArrayList<>();
        pedidosListos = new ArrayList<>();
        
        // añade los arraylist de pedidos al arraylist de pedido
        pedidos.add(pedidosPendientes);
        pedidos.add(pedidosListos);
        
        // inicializa server
        try {
            serverSalon = new ServerSocket(4444);
            serverCocina = new ServerSocket(5555);
        } catch (IOException ex) {
            System.out.println(ex + "    LINE: 69");
        }
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
    }
    
    // SALON
    
    public void abrirConexionSalon(){
        try {
            socketRecibirSalon = serverSalon.accept();
            outputSalon = new ObjectOutputStream(socketRecibirSalon.getOutputStream());
            inputSalon = new ObjectInputStream(socketRecibirSalon.getInputStream());

        } catch (Exception e) {
            System.out.println(e + "    LINE: 95");
        }
        
        recibirPedidoSalon();
    }
    
    public void recibirPedidoSalon(){
//        while (true) {            
            try {
//                socketRecibirSalon = serverSalon.accept();
                while (true) {                    
                    // lee objeto que recibe
                    pedidoRecibidoSalon = (Pedido) inputSalon.readObject();

                    // añade pedido a pedidos pendientes
                    pedidosPendientes.add(pedidoRecibidoSalon);
                    
                    // envia copia actualizada de el array de pedidos pendientes
                    outputCocina.writeObject(new ArrayList<>(pedidosPendientes));
                    outputCocina.flush();
                    
                    System.out.println("Recibe pedido");
                }

//                inputSalon.close();
//                socketRecibirSalon.close();
            } catch (Exception e) {
                System.out.println(e + "    LINE: 117");
            }            
//        }
    }
    
    // COCINA
    
    public void abrirConexionCocina(){
        try {
            socketRecibirCocina = serverCocina.accept();
            outputCocina = new ObjectOutputStream(socketRecibirCocina.getOutputStream());
            inputCocina = new ObjectInputStream(socketRecibirCocina.getInputStream());
        } catch (Exception e) {
            System.out.println(e + "    LINE: 132");
        }
        
        recibirPedidoCocina();
    }
    
    public void recibirPedidoCocina(){
        try {
            while (true) {                    
                // lee objeto que recibe
                pedidoRecibidoCocina = (Pedido) inputCocina.readObject();

                // llama metodo para actualizar el array de los pedidos pendientes y listos
                actualizarPedidos(pedidoRecibidoSalon);

                // copia actualizada del objeto de pedidos
                ArrayList<ArrayList<Pedido>> copiaPedidos = new ArrayList<>();
                copiaPedidos.add(new ArrayList<>(pedidosPendientes));
                copiaPedidos.add(new ArrayList<>(pedidosListos));

                // enviar la copia actualizada de los pedidos
                outputSalon.writeObject(copiaPedidos);
                outputSalon.flush();

                System.out.println("Pedido devuelto");
            }
        } catch (Exception e) {
            System.out.println(e + "    LINE: 156");
        }

    }

    // RUN
    
    @Override
    public void run() {
        abrirConexionSalon();
    }
}
