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
        pedidosPendientes = new ArrayList<Pedido>();
        pedidosListos = new ArrayList<Pedido>();
        
        // inicializa server
        try {
            serverSalon = new ServerSocket(4444);
            serverCocina = new ServerSocket(5555);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public void abrirConexionSalon(){
        while (true) {            
            try {
                socketRecibirSalon = serverSalon.accept();
                inputSalon = new ObjectInputStream(socketRecibirSalon.getInputStream());
                outputSalon = new ObjectOutputStream(socketRecibirSalon.getOutputStream());
                
                // lee objeto que recibe
                pedidoRecibidoSalon = (Pedido) inputSalon.readObject();

                inputSalon.close();
                socketRecibirSalon.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            
            System.out.println("Recibe pedido");
        }
    }
    
    public void abrirConexionCocina(){
        while (true) {            
            try {
                socketRecibirCocina = serverCocina.accept();
                inputCocina = new ObjectInputStream(socketRecibirCocina.getInputStream());
                outputCocina = new ObjectOutputStream(socketRecibirCocina.getOutputStream());
                
                // lee objeto que recibe
                pedidoRecibidoCocina = (Pedido) inputCocina.readObject();

                inputCocina.close();
                socketRecibirCocina.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            
            System.out.println("Pedido devuelto");
        }
    }

    @Override
    public void run() {
        abrirConexionSalon();
    }
}
