/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Salon;

/**
 *
 * @author dario
 */
public class FactoryHamburguesa {
    
    
    public static Hamburguesa crearHamburguesa(int id){
        return switch (id) {
            case 1 -> new HamburguesaPoo();
            case 2 -> new HamburguesaJPQ();
            default -> new HamburguesaBase();
        };   
    }
    
    
    public static Hamburguesa crearHamburguesa(int id, String ingredientes){
        //Esta es para retornar una hamburguesa personalizada.
        //Con la hamburguesa base del 
        return switch (id) {
            case 1 -> new Personalizada(ingredientes, new HamburguesaPoo());
            case 2 -> new Personalizada(ingredientes, new HamburguesaJPQ());
            default -> new Personalizada(ingredientes);
        }; 
    }
}
