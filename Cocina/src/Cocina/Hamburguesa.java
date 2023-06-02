
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Cocina;

import java.util.ArrayList;

/**
 *
 * @author jerson
 */

public class Hamburguesa {
    
    protected ArrayList<String> ingredientes;
    
    public String getIngredientes(){
        String ingre = "";
        for(String i : ingredientes){
            ingre += i + "-";
        }
        ingre = ingre.substring(0, ingre.length()-1);
        return ingre;
    }
}
