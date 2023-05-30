
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package Salon;

/**
 *
 * @author jerson
 */
public class AppSalon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SalonClass salon = new SalonClass();
        Pedido pedido = new Pedido();
        salon.enviarPedido(pedido);
    }
    
}
