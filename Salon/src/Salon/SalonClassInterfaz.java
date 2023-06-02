/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Salon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author jerson
 */
public class SalonClassInterfaz extends javax.swing.JFrame implements Runnable{
    
    //Atributos para mandar información a las cocina
    Socket PedidoSocket;
    ObjectOutputStream output;
    
    // atributos para recibir pedido
    private Socket SocketRecibir;
    private ServerSocket server;
    private ObjectInputStream inputArrayPedidos;
    private Pedido pedidoDevuelto;
    
    private ArrayList<Mesa> mesas;
    
    ArrayList<ArrayList<Pedido>> pedidos;
    
    // copia de servidor pedidos pendientes
    ArrayList<Pedido> pedidosListos;
    
    // copia de servidor pedidos listos
    ArrayList<Pedido> pedidosPendientes;
    
    // GENERAL
    
    // método actualiza los arrays de los pedidos listos y los pendientes
    // añade el pedido a los pedidos listos y lo elimina de pedidos 
    public void actualizarPedidos(Pedido pedidoListo){
        
        // elimina el pedido listo en el array de pedidos pendientes
        for (int i = 0; i < pedidosPendientes.size(); i++) {
            if (pedidoListo.getID() == pedidosPendientes.get(i).getID()) {
                pedidosPendientes.remove(i);
                break;
            }
        }
        
        pedidosListos.add(pedidoListo);
        
//        for (Pedido pedido : pedidosListos) {
//            System.out.println(pedido.getID());
//        }
    }
    
    // CONEXIONES
    
    public void conectar(){
        try {
            PedidoSocket = new Socket("127.0.0.1", 4444);
            output = new ObjectOutputStream(PedidoSocket.getOutputStream());
            inputArrayPedidos = new ObjectInputStream(PedidoSocket.getInputStream());

        } catch (IOException ex) {
            System.out.println(ex + "    LINE: 50");
        }
    }
    
    public void recibirListaPedidos(){      
        try {
            while (true) {                    
                pedidos = (ArrayList<ArrayList<Pedido>>) inputArrayPedidos.readObject();

                System.out.println("Antes listos: " + pedidosListos.size());

                // actualiza array de pedidos pendientes
                pedidosPendientes = pedidos.get(0);

                // actualiza array de pedidos listos
                pedidosListos = pedidos.get(1);

                System.out.println("Despues listos: " + pedidosListos.size());
            }
        } catch (Exception e) {
            System.out.println(e + "    LINE: 67");
        }

        System.out.println("Pedidos pendientes: " + pedidosPendientes.size() + " Pedidos listos: " + pedidosListos.size());
    }
    
    public void enviarPedido(Pedido pedido){
        try{
            output.writeObject(pedido);
            output.flush();
        }
        catch(Exception ex){
            System.out.println(ex + "    LINE: 85");
        }
    }

    /**
     * Creates new form SalonClassInterfaz
     */
    public SalonClassInterfaz() {
        // incializar arrays de pedidos
        pedidos = new ArrayList<>();
        
        pedidosListos = new ArrayList<>();
        pedidosPendientes = new ArrayList<>();
        
        conectar();
        
        initComponents();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Hacer pedido");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Mesa1");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jButton1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jLabel1)))
                .addContainerGap(491, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(330, Short.MAX_VALUE))
        );

        jButton1.getAccessibleContext().setAccessibleName("botonHacerPedido1");
        jLabel1.getAccessibleContext().setAccessibleName("mesa1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        // envia nuevo pedido a servidor
        enviarPedido(new Pedido(1));
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SalonClassInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SalonClassInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SalonClassInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SalonClassInterfaz.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        ///////////////////////////////////////////////////////////////
        
        SalonClassInterfaz saloninterfaz = new SalonClassInterfaz();
        
        Thread hiloRecibirServidor = new Thread(saloninterfaz);
        
        // inicia hilo con metodo run que es para que tenga un ciclo true para recibir 
        // de servidor
        hiloRecibirServidor.start();
        
        saloninterfaz.setVisible(true);
        
        ///////////////////////////////////////////////////////////////
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        // empieza metodo para recibir de servidor
        recibirListaPedidos();
    }
}
