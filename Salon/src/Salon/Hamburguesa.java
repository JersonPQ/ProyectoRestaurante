
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Salon;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author jerson
 */

public class Hamburguesa implements Serializable{
    
    protected ArrayList<String> ingredientes = new ArrayList<String>();
    
    public String getIngredientes(){
        String ingre = "";
        for(String i : ingredientes){
            ingre += i + "-";
        }
        ingre = ingre.substring(0, ingre.length()-1);
        return ingre;
    }
    
    public String getType(){
        return "";
    }
}
