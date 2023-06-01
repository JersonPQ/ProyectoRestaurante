/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cocina;

/**
 *
 * @author dario
 */
public class HamburguesaJPQ extends Hamburguesa {
    public HamburguesaJPQ(){
        this.ingredientes.add("Pan");
        this.ingredientes.add("Torta");
        this.ingredientes.add("Lechuga");
        this.ingredientes.add("Pepinillos");
        this.ingredientes.add("Salsa tomate");
        this.ingredientes.add("Tomate");
        this.ingredientes.add("Pan");
    }
    
     /**
     *
     * @return
     */
    @Override
    public String getType(){
        return "Hamburguea JPQ";
    }
}
