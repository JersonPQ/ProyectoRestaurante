/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Cocina;

/**
 *
 * @author dario
 */

public class Personalizada extends Hamburguesa {
    //Se tiene que hacer un arrayLista de ingredientes
    String[] baseIngredientes = {"Cebolla", "Tocino", "Tomate", "Huevo"};
    
    //Constructores
    public Personalizada(String s){
        Hamburguesa burguer = new HamburguesaBase();
        this.ingredientes = burguer.ingredientes;
        personalizar(s);
    }

    public Personalizada(String s, Hamburguesa hamburguer){
        this.ingredientes = hamburguer.ingredientes;
        personalizar(s);
    }
    
    private void personalizar(String personalizado){
        int index;
        for(int i = 0; i < personalizado.length(); i++){
            index = Integer.parseInt(personalizado.substring(i, i+1));
            ingredientes.add(1, baseIngredientes[index]);
        }
    }
}
