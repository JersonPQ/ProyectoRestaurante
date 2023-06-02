/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Simulacion;
import Salon.Pedido;
import Cocina.FactoryHamburguesa;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.util.Random;

/**
 *
 * @author dario
 */
public class ClassSimulacion {
    private Socket socketDevolver;
    private ObjectOutputStream outputPedidoMandar;
    private Pedido pedidoMandar;
    
    Random random = new Random();
    
    //Generar string 
    public String getStringPersonalizar(){
        String banco = "012345";
        String nString = "";
        int longitud = (int) (Math.floor(Math.random()*(4-1+1)+1));
        
        for(int i = 0; i < longitud; i++){
            int indice = (int) (Math.floor(Math.random()*(banco.length()-0+1)+0));
            nString += banco.charAt(indice);
        }
        
        return nString;
    }
    
    //Generar hamburguesa
    public void generarHamburguesas(Pedido pedido){
        int cantidadCrear = (int)(Math.floor(Math.random()*(3-1+1)+1));
        for(int i = 0; i < cantidadCrear; i++){
            int idBurger = (int)(Math.floor(Math.random()*(3-1+1)+1));
            boolean typeBurguer = random.nextBoolean();
            
            if(typeBurguer){
                pedido.setHamburguesa(FactoryHamburguesa.crearHamburguesa(idBurger));
            }else{
                String s = getStringPersonalizar();
                pedido.setHamburguesa(FactoryHamburguesa.crearHamburguesa(idBurger, s));
            }
        }
    }
    
    public Pedido generarPedido(){
        Pedido pedido = new Pedido();
        generarHamburguesas(pedido);
        return pedido;
    }
    
    public void generarEnviarPedido(){
        while (true){
            int segEsperar = (int)(Math.floor(Math.random()*(20-15+1)+15));
            pedidoMandar = generarPedido();
            enviarPedido(pedidoMandar);
            for(int i = segEsperar; i > 0; i--){
                try{
                    Thread.sleep(1000);
                }
                catch(Exception ex){
                    System.out.println(ex + "   LINE:70");
                }
            }
        }
    }
    
    //Metodo para conectar
    public void conectar(){
        try{
            socketDevolver = new Socket("127.0.0.1", 6666);
            outputPedidoMandar = new ObjectOutputStream(socketDevolver.getOutputStream());
        }
        catch(IOException ex){
            System.out.println(ex + " LINE: 38");
        }
        
        System.out.println("Conecto");
    }
    
    //Enviar pedido
    public void enviarPedido(Pedido pedido){
        try{
            outputPedidoMandar.writeObject(pedido);
            outputPedidoMandar.flush();
        }
        catch(Exception ex){
            System.out.println(ex + "  Line: 37");
        }
    }
    
    //CONSTRUCTOR
    public ClassSimulacion(){
        conectar();
    }
}
