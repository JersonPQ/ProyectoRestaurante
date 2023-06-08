/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Salon;

import javax.swing.JButton;
import javax.swing.JLabel;

/**
 *
 * @author jerson
 */
public class Mesa extends javax.swing.JPanel {
    
    private boolean libre;
    static protected int contadorIdMesas = 1;
    private int id;
    private Pedido pedido;
    
    public boolean isLibre(){
        return libre;
    }
    
    public void liberarMesa(){
        this.pedido = null;
        this.libre = true;
    }
    
    public void setPedido(Pedido pedidoMesa){
        this.pedido = pedidoMesa;
        this.pedido.setIdMesa(id);
        this.libre = false;
    }

    public JButton getBoton(){
        return this.botonRealizarPedido;
    }
    
    public int getIdMesa(){
        return id;
    }
    
    public JLabel getLabelMesa(){
        return labelImagenMesa;
    }
    
    /**
     * Creates new form Mesa1
     */
    public Mesa() {
        this.id = contadorIdMesas;
        contadorIdMesas++;
        this.libre = true;
        initComponents();
        
        this.labelNumMesa.setText("Mesa " + this.id);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelImagenMesa = new javax.swing.JLabel();
        botonRealizarPedido = new javax.swing.JButton();
        labelNumMesa = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(0, 0, 0));

        labelImagenMesa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelImagenMesa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/Mesa.png"))); // NOI18N

        botonRealizarPedido.setText("Realizar pedido");

        labelNumMesa.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        labelNumMesa.setForeground(new java.awt.Color(0, 0, 0));
        labelNumMesa.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelNumMesa.setText("Mesa");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelNumMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelImagenMesa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonRealizarPedido, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
                .addContainerGap(81, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelNumMesa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelImagenMesa, javax.swing.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(botonRealizarPedido)
                .addGap(12, 12, 12))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonRealizarPedido;
    private javax.swing.JLabel labelImagenMesa;
    private javax.swing.JLabel labelNumMesa;
    // End of variables declaration//GEN-END:variables
}
