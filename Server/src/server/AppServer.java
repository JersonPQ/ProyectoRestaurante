/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package server;

/**
 *
 * @author jerson
 */
public class AppServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerClass server = new ServerClass();
        Thread hiloConexionSalon = new Thread(server);
        
        // inicia hilo para conexion de salon
        hiloConexionSalon.start();
        
        // llama a run de nuevo para iniciar conexion de cocina
        server.abrirConexionCocina();
    }
    
}
