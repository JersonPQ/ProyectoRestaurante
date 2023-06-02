/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulacion;
import Salon.Pedido;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;

/**
 *
 * @author dario
 */
public class SimulacionClass {
    //Atributos para establecer conexion con Salon
    private Socket socketDevolver;
    private ObjectOutputStream outputPedidoMandar;
    private Pedido pedidoMandar;
    
    //Metodo para conectar
    public void conectar(){
        try{
            socketDevolver = new Socket("127.0.0.1", 6666);
            outputPedidoMandar = new ObjectOutputStream(socketDevolver.getOutputStream());
        }
        catch(IOException ex){
            System.out.println(ex + " LINE: 38");
        }
    }
    
    public SimulacionClass(){
        conectar();
    }
}