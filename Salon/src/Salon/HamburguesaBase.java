/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Salon;

/**
 *
 * @author dario
 */
public class HamburguesaBase extends Hamburguesa {
    
    public HamburguesaBase(){
        this.ingredientes.add("Pan");
        this.ingredientes.add("Torta");
        this.ingredientes.add("Pan");
    }
    
    /**
     *
     * @return
     */
    @Override
    public String getType(){
        return "Hamburguea base";
    }
}
