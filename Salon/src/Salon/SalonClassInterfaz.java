/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Salon;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JDialog;

/**
 *
 * @author jerson
 */
public class SalonClassInterfaz extends javax.swing.JFrame implements Runnable{
    
    //Atributos para conectar con Simulador
    ServerSocket serverSimulador;
    Socket socketRecibirSimulador;
    ObjectInputStream inputSimulador; 
    Pedido pedidoSimulador;
    
    //Atributos para mandar información a las cocina
    private Socket PedidoSocket;
    private ObjectOutputStream output;
    
    // atributos para recibir pedido
    private ObjectInputStream inputArrayPedidos;
    private Pedido pedidoDevuelto;
    
    private ArrayList<Mesa> mesas;
    private int cantidadMesas = 6;
    
    private ArrayList<ArrayList<Pedido>> pedidos;
    
    // copia de servidor pedidos pendientes
    private ArrayList<Pedido> pedidosListos;
    
    // copia de servidor pedidos listos
    private ArrayList<Pedido> pedidosPendientes;
    
    // variable para llevar que pedido hacer
    private Pedido pedidoRealizar;
    
    // variable para que toma valor de la hamburguesa del pedido
    Hamburguesa hamburguesaPedida;
    
    // string para llevar registro de los ingreientes extra
    private String ingredientesExtra = "";
    
    // variable para saber que hamburguesa pedir por medio de codigo de numero
    private int hamburguesaPedir;
    
    private boolean recibirServidorCorriendo;
    
    // atributo para saber a que mesa hay que ponerle el esperar
    Mesa mesaHacerPedido;
    
    // valores para ingredientes extra
    private String cebolla = "Cebolla";
    private String tocino = "Tocino";
    private String huevo = "Huevo";
    private String tomate = "Tomate";
    private String salsaEspecial = "Salsa especial";
    private String lechuga = "Lechuga";
    
    
    //Conexiones con Simulador--------------------------------------------------
    
    
    //Abrir conexion para simulador
    public void abrirConexionSimulador(){
        try{
            socketRecibirSimulador = serverSimulador.accept();
            inputSimulador = new ObjectInputStream(socketRecibirSimulador.getInputStream());
        }
        catch(Exception ex){
            System.out.println(ex + "    LINE: 119");
        }
        
        recibirPedidoSimulador();
    }
    
    //Recibe el pedido enviado de Simulador
    public void recibirPedidoSimulador(){
        try{
            while(true){
                pedidoSimulador = (Pedido) inputSimulador.readObject();
                System.out.println("Recibe");
                if(existMesaLibre()){
                    for(Mesa mesa : mesas){
                        if(mesa.isLibre()){
                            pedidoSimulador.setIdPedido();
                            pedidoSimulador.setIdMesa(mesa.getIdMesa());
                            enviarPedido(pedidoSimulador);
                            
                            //Aqui se añade el Pedido a la mesa
                            //Se añade el Id de la mesa al pedido
                            mesa.setPedido(pedidoSimulador);
                            
                            // cambia texto de boton al hacer el pedido y lo deshabilita
                            mesa.getBoton().setText("Pendiente...");
                            mesa.getBoton().setEnabled(false);
                            break;
                        }
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println(e + "    LINE: 117");
        }
    }
    
    // METODOS GENERALES
    
    //Definir si hay una mesa libre
    public boolean existMesaLibre(){
        for(Mesa mesa : mesas){
            if(mesa.isLibre()){
                return true;
            }
        }
        return false;
    }
    
    private void actualizarPedidoListo(Pedido pedidoListo){
        // toma el id de la mesa que esta en el pedido para ubicarlo en el array 
        Mesa mesaPedidoListo = mesas.get(pedidoListo.getIdMesa() - 1);
        mesaPedidoListo.getBoton().setText("Pagar");
        mesaPedidoListo.getBoton().setEnabled(true);
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
                
                // envia el ultimo pedido que esta en el array de listos, es decir
                // el ultimo que ha sido enviado por el servidor
                actualizarPedidoListo(pedidosListos.get(pedidosListos.size() - 1));
            }
        } catch (Exception e) {
            System.out.println(e + "    LINE: 67");
        }
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
        mesas = new ArrayList<>();
        pedidosListos = new ArrayList<>();
        pedidosPendientes = new ArrayList<>();
        
        recibirServidorCorriendo = false;
        
        conectar();
        
        initComponents();
        
        panelSalon.setLayout(new GridLayout(2, 4));
        
        for (int i = 0; i < cantidadMesas; i++) {
            Mesa mesaCreada = new Mesa();
            
            // le asigna el action performed al boton de el panel de mesa
            mesaCreada.getBoton().addActionListener(new java.awt.event.ActionListener() {
                
                Mesa mesa = mesaCreada;
                
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    botonHacerPedido(evt, mesa);
                }
            });
            
            // anade la mesa creada al arrayList
            mesas.add(mesaCreada);
            panelSalon.add(mesaCreada);
        }
        
        //Inicializar server del Simulador
        try{
            serverSimulador = new ServerSocket(6666);
        }
        catch (IOException ex) {
            System.out.println(ex + "    LINE: 69");
        }
    }
    
    private int indexOf(String[] array, String element){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return i;
            }
        }
        
        return -1;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogMenu = new javax.swing.JDialog();
        panelGeneralMenu = new javax.swing.JPanel();
        PanelTituloMenu = new javax.swing.JPanel();
        LabelMenu = new javax.swing.JLabel();
        PanelHambBase9 = new javax.swing.JPanel();
        LabelImagenHambBase9 = new javax.swing.JLabel();
        LabelIngredientesHambBase9 = new javax.swing.JLabel();
        botonSeleccionarHambBase = new javax.swing.JButton();
        PanelHambPOO = new javax.swing.JPanel();
        LabelImagenHambPOO = new javax.swing.JLabel();
        LabelIngredientesHambPOO = new javax.swing.JLabel();
        botonSeleccionarHambPOO = new javax.swing.JButton();
        PanelRealizarPedido = new javax.swing.JPanel();
        botonRealizarPedido = new javax.swing.JButton();
        botonCancelarMenu = new javax.swing.JButton();
        PanelHambJPQ = new javax.swing.JPanel();
        LabelImagenHambJPQ = new javax.swing.JLabel();
        LabelIngredientesHambJPQ = new javax.swing.JLabel();
        botonSeleccionarHambJPQ = new javax.swing.JButton();
        PanelHambPersonalizada = new javax.swing.JPanel();
        LabelImagenHambPersonalizada = new javax.swing.JLabel();
        LabelIngredientesHambPerso = new javax.swing.JLabel();
        botonSeleccionarHambPerso = new javax.swing.JButton();
        dialogExtras = new javax.swing.JDialog();
        panelGeneral = new javax.swing.JPanel();
        panelTituloExtras = new javax.swing.JPanel();
        LabelExtras = new javax.swing.JLabel();
        panelCebolla = new javax.swing.JPanel();
        labelCebolla = new javax.swing.JLabel();
        botonCebolla = new javax.swing.JButton();
        panelTocino = new javax.swing.JPanel();
        labelTocino = new javax.swing.JLabel();
        botonTocino = new javax.swing.JButton();
        panelHuevo = new javax.swing.JPanel();
        labelHuevo = new javax.swing.JLabel();
        botonHuevo = new javax.swing.JButton();
        panelTomate = new javax.swing.JPanel();
        labelTomate = new javax.swing.JLabel();
        botonTomate = new javax.swing.JButton();
        panelSalsa = new javax.swing.JPanel();
        labelSalsa = new javax.swing.JLabel();
        botonSalsa = new javax.swing.JButton();
        panelLechuga = new javax.swing.JPanel();
        labelLechuga = new javax.swing.JLabel();
        botonLechuga = new javax.swing.JButton();
        panelExtrasListo = new javax.swing.JPanel();
        botonExtrasListo = new javax.swing.JButton();
        panelSalon = new javax.swing.JPanel();
        panelTituloSalon = new javax.swing.JPanel();
        labelTituloSalon = new javax.swing.JLabel();

        dialogMenu.setBackground(new java.awt.Color(255, 255, 255));
        dialogMenu.setMinimumSize(new java.awt.Dimension(987, 600));
        dialogMenu.setModal(true);
        dialogMenu.setSize(new java.awt.Dimension(1001, 569));

        panelGeneralMenu.setBackground(new java.awt.Color(255, 255, 255));

        PanelTituloMenu.setBackground(new java.awt.Color(255, 255, 255));

        LabelMenu.setFont(new java.awt.Font("Liberation Sans", 0, 36)); // NOI18N
        LabelMenu.setText("MENU");

        javax.swing.GroupLayout PanelTituloMenuLayout = new javax.swing.GroupLayout(PanelTituloMenu);
        PanelTituloMenu.setLayout(PanelTituloMenuLayout);
        PanelTituloMenuLayout.setHorizontalGroup(
            PanelTituloMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelTituloMenuLayout.createSequentialGroup()
                .addGap(436, 436, 436)
                .addComponent(LabelMenu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelTituloMenuLayout.setVerticalGroup(
            PanelTituloMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelMenu, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        PanelHambBase9.setBackground(new java.awt.Color(255, 255, 255));

        LabelImagenHambBase9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelImagenHambBase9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/HamburguesaBase.png"))); // NOI18N
        LabelImagenHambBase9.setText("Hamburguesa Base");
        LabelImagenHambBase9.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        LabelImagenHambBase9.setIconTextGap(10);
        LabelImagenHambBase9.setMaximumSize(new java.awt.Dimension(646, 512));
        LabelImagenHambBase9.setName("ImagenHambBase"); // NOI18N
        LabelImagenHambBase9.setPreferredSize(new java.awt.Dimension(150, 150));

        LabelIngredientesHambBase9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelIngredientesHambBase9.setText("<html>\nIngredientes: <br>\n\n        - Pan<br>\n        - Torta<br>\n        - Pan\n\n</html>\n");
        LabelIngredientesHambBase9.setToolTipText("");
        LabelIngredientesHambBase9.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        LabelIngredientesHambBase9.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        LabelIngredientesHambBase9.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        botonSeleccionarHambBase.setText("Seleccionar");
        botonSeleccionarHambBase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionarHambBaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelHambBase9Layout = new javax.swing.GroupLayout(PanelHambBase9);
        PanelHambBase9.setLayout(PanelHambBase9Layout);
        PanelHambBase9Layout.setHorizontalGroup(
            PanelHambBase9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambBase9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambBase9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(LabelImagenHambBase9, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                    .addComponent(botonSeleccionarHambBase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelIngredientesHambBase9, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );
        PanelHambBase9Layout.setVerticalGroup(
            PanelHambBase9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambBase9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambBase9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelHambBase9Layout.createSequentialGroup()
                        .addComponent(LabelImagenHambBase9, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonSeleccionarHambBase, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                    .addComponent(LabelIngredientesHambBase9))
                .addContainerGap())
        );

        LabelImagenHambBase9.getAccessibleContext().setAccessibleName("ImagenHambBase");

        PanelHambPOO.setBackground(new java.awt.Color(255, 255, 255));

        LabelImagenHambPOO.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelImagenHambPOO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/HamburguesaPoo.png"))); // NOI18N
        LabelImagenHambPOO.setText("Hamburguesa POO");
        LabelImagenHambPOO.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        LabelImagenHambPOO.setIconTextGap(10);
        LabelImagenHambPOO.setMaximumSize(new java.awt.Dimension(646, 512));
        LabelImagenHambPOO.setName(""); // NOI18N
        LabelImagenHambPOO.setPreferredSize(new java.awt.Dimension(150, 150));

        LabelIngredientesHambPOO.setText("<html>\nIngredientes: <br>\n\n        - Pan <br>\n        - Torta <br>\n        - Torta <br>\n        - Queso cheddar <br>\n        - Salsa especial <br>\n        - Hongos <br>\n        - Pan <br>\n</html>");
        LabelIngredientesHambPOO.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        botonSeleccionarHambPOO.setText("Seleccionar");
        botonSeleccionarHambPOO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionarHambPOOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelHambPOOLayout = new javax.swing.GroupLayout(PanelHambPOO);
        PanelHambPOO.setLayout(PanelHambPOOLayout);
        PanelHambPOOLayout.setHorizontalGroup(
            PanelHambPOOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambPOOLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambPOOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelImagenHambPOO, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonSeleccionarHambPOO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelIngredientesHambPOO, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE))
        );
        PanelHambPOOLayout.setVerticalGroup(
            PanelHambPOOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambPOOLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambPOOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelHambPOOLayout.createSequentialGroup()
                        .addComponent(LabelImagenHambPOO, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonSeleccionarHambPOO, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                    .addComponent(LabelIngredientesHambPOO, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE))
                .addContainerGap())
        );

        PanelRealizarPedido.setBackground(new java.awt.Color(255, 255, 255));

        botonRealizarPedido.setText("Realizar pedido");
        botonRealizarPedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonRealizarPedidoActionPerformed(evt);
            }
        });

        botonCancelarMenu.setText("Cancelar");
        botonCancelarMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCancelarMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelRealizarPedidoLayout = new javax.swing.GroupLayout(PanelRealizarPedido);
        PanelRealizarPedido.setLayout(PanelRealizarPedidoLayout);
        PanelRealizarPedidoLayout.setHorizontalGroup(
            PanelRealizarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRealizarPedidoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonCancelarMenu)
                .addGap(312, 312, 312)
                .addComponent(botonRealizarPedido)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelRealizarPedidoLayout.setVerticalGroup(
            PanelRealizarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRealizarPedidoLayout.createSequentialGroup()
                .addGroup(PanelRealizarPedidoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botonRealizarPedido)
                    .addComponent(botonCancelarMenu))
                .addGap(0, 16, Short.MAX_VALUE))
        );

        PanelHambJPQ.setBackground(new java.awt.Color(255, 255, 255));

        LabelImagenHambJPQ.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelImagenHambJPQ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/HamburguesaJPQ.png"))); // NOI18N
        LabelImagenHambJPQ.setText("Hamburguesa JPQ");
        LabelImagenHambJPQ.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        LabelImagenHambJPQ.setIconTextGap(10);
        LabelImagenHambJPQ.setMaximumSize(new java.awt.Dimension(646, 512));
        LabelImagenHambJPQ.setName(""); // NOI18N
        LabelImagenHambJPQ.setPreferredSize(new java.awt.Dimension(150, 150));

        LabelIngredientesHambJPQ.setText("<html>\nIngredientes: <br>\n\n        - Pan <br>\n        - Torta <br>\n        - Lechuga <br>\n        - Pepinillos <br>\n        - Salsa de tomate <br>\n        - Tomate <br>\n        - Pan\n\n</html>");
        LabelIngredientesHambJPQ.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        botonSeleccionarHambJPQ.setText("Seleccionar");
        botonSeleccionarHambJPQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionarHambJPQActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelHambJPQLayout = new javax.swing.GroupLayout(PanelHambJPQ);
        PanelHambJPQ.setLayout(PanelHambJPQLayout);
        PanelHambJPQLayout.setHorizontalGroup(
            PanelHambJPQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambJPQLayout.createSequentialGroup()
                .addGroup(PanelHambJPQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelHambJPQLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(LabelImagenHambJPQ, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(botonSeleccionarHambJPQ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelIngredientesHambJPQ, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelHambJPQLayout.setVerticalGroup(
            PanelHambJPQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambJPQLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambJPQLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelIngredientesHambJPQ)
                    .addGroup(PanelHambJPQLayout.createSequentialGroup()
                        .addComponent(LabelImagenHambJPQ, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonSeleccionarHambJPQ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        PanelHambPersonalizada.setBackground(new java.awt.Color(255, 255, 255));

        LabelImagenHambPersonalizada.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LabelImagenHambPersonalizada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/HamburguesaPersonalizada.png"))); // NOI18N
        LabelImagenHambPersonalizada.setText("Hamburguesa Personalizada");
        LabelImagenHambPersonalizada.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        LabelImagenHambPersonalizada.setIconTextGap(10);
        LabelImagenHambPersonalizada.setMaximumSize(new java.awt.Dimension(646, 512));
        LabelImagenHambPersonalizada.setName(""); // NOI18N

        LabelIngredientesHambPerso.setText("<html>\nIngredientes base: <br>\n\n        - Pan <br>\n        - Torta <br>\n        - Pan <br> \n\n</html>");
        LabelIngredientesHambPerso.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        botonSeleccionarHambPerso.setText("Seleccionar");
        botonSeleccionarHambPerso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSeleccionarHambPersoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelHambPersonalizadaLayout = new javax.swing.GroupLayout(PanelHambPersonalizada);
        PanelHambPersonalizada.setLayout(PanelHambPersonalizadaLayout);
        PanelHambPersonalizadaLayout.setHorizontalGroup(
            PanelHambPersonalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambPersonalizadaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambPersonalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelImagenHambPersonalizada, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(botonSeleccionarHambPerso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelIngredientesHambPerso, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))
        );
        PanelHambPersonalizadaLayout.setVerticalGroup(
            PanelHambPersonalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelHambPersonalizadaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelHambPersonalizadaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelHambPersonalizadaLayout.createSequentialGroup()
                        .addComponent(LabelImagenHambPersonalizada, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(botonSeleccionarHambPerso, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE))
                    .addComponent(LabelIngredientesHambPerso))
                .addContainerGap())
        );

        javax.swing.GroupLayout panelGeneralMenuLayout = new javax.swing.GroupLayout(panelGeneralMenu);
        panelGeneralMenu.setLayout(panelGeneralMenuLayout);
        panelGeneralMenuLayout.setHorizontalGroup(
            panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                        .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PanelTituloMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PanelRealizarPedido, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                        .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(PanelHambBase9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(PanelHambPOO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PanelHambJPQ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PanelHambPersonalizada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );
        panelGeneralMenuLayout.setVerticalGroup(
            panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelTituloMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                        .addComponent(PanelHambBase9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(panelGeneralMenuLayout.createSequentialGroup()
                        .addComponent(PanelHambJPQ, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(12, 12, 12)))
                .addGroup(panelGeneralMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelHambPersonalizada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelHambPOO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, Short.MAX_VALUE)
                .addComponent(PanelRealizarPedido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout dialogMenuLayout = new javax.swing.GroupLayout(dialogMenu.getContentPane());
        dialogMenu.getContentPane().setLayout(dialogMenuLayout);
        dialogMenuLayout.setHorizontalGroup(
            dialogMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogMenuLayout.createSequentialGroup()
                .addComponent(panelGeneralMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogMenuLayout.setVerticalGroup(
            dialogMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogMenuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelGeneralMenu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogExtras.setBackground(new java.awt.Color(255, 255, 255));
        dialogExtras.setMinimumSize(new java.awt.Dimension(1001, 600));
        dialogExtras.setModal(true);
        dialogExtras.setSize(new java.awt.Dimension(1001, 569));

        panelGeneral.setBackground(new java.awt.Color(255, 255, 255));

        panelTituloExtras.setBackground(new java.awt.Color(255, 255, 255));

        LabelExtras.setFont(new java.awt.Font("Liberation Sans", 0, 36)); // NOI18N
        LabelExtras.setForeground(new java.awt.Color(0, 0, 0));
        LabelExtras.setText("EXTRAS");

        javax.swing.GroupLayout panelTituloExtrasLayout = new javax.swing.GroupLayout(panelTituloExtras);
        panelTituloExtras.setLayout(panelTituloExtrasLayout);
        panelTituloExtrasLayout.setHorizontalGroup(
            panelTituloExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTituloExtrasLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(LabelExtras)
                .addGap(420, 420, 420))
        );
        panelTituloExtrasLayout.setVerticalGroup(
            panelTituloExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(LabelExtras, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        panelCebolla.setBackground(new java.awt.Color(255, 255, 255));

        labelCebolla.setForeground(new java.awt.Color(0, 0, 0));
        labelCebolla.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelCebolla.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/cebolla.png"))); // NOI18N
        labelCebolla.setText("Cebolla");
        labelCebolla.setIconTextGap(10);

        botonCebolla.setText("Añadir");
        botonCebolla.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonCebollaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelCebollaLayout = new javax.swing.GroupLayout(panelCebolla);
        panelCebolla.setLayout(panelCebollaLayout);
        panelCebollaLayout.setHorizontalGroup(
            panelCebollaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCebollaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelCebolla, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonCebolla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelCebollaLayout.setVerticalGroup(
            panelCebollaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCebollaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelCebollaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelCebolla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonCebolla, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelTocino.setBackground(new java.awt.Color(255, 255, 255));

        labelTocino.setForeground(new java.awt.Color(0, 0, 0));
        labelTocino.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/tocino.png"))); // NOI18N
        labelTocino.setText("Tocino");
        labelTocino.setIconTextGap(10);

        botonTocino.setText("Añadir");
        botonTocino.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTocinoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTocinoLayout = new javax.swing.GroupLayout(panelTocino);
        panelTocino.setLayout(panelTocinoLayout);
        panelTocinoLayout.setHorizontalGroup(
            panelTocinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTocinoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTocino, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonTocino, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );
        panelTocinoLayout.setVerticalGroup(
            panelTocinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTocinoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTocinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTocino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonTocino, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelHuevo.setBackground(new java.awt.Color(255, 255, 255));

        labelHuevo.setForeground(new java.awt.Color(0, 0, 0));
        labelHuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/huevo.png"))); // NOI18N
        labelHuevo.setText("Huevo");
        labelHuevo.setIconTextGap(10);

        botonHuevo.setText("Añadir");
        botonHuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonHuevoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelHuevoLayout = new javax.swing.GroupLayout(panelHuevo);
        panelHuevo.setLayout(panelHuevoLayout);
        panelHuevoLayout.setHorizontalGroup(
            panelHuevoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHuevoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelHuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonHuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelHuevoLayout.setVerticalGroup(
            panelHuevoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHuevoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelHuevoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelHuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonHuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelTomate.setBackground(new java.awt.Color(255, 255, 255));

        labelTomate.setForeground(new java.awt.Color(0, 0, 0));
        labelTomate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/tomate.png"))); // NOI18N
        labelTomate.setText("Tomate");
        labelTomate.setIconTextGap(10);

        botonTomate.setText("Añadir");
        botonTomate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonTomateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelTomateLayout = new javax.swing.GroupLayout(panelTomate);
        panelTomate.setLayout(panelTomateLayout);
        panelTomateLayout.setHorizontalGroup(
            panelTomateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTomateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelTomate, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonTomate, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTomateLayout.setVerticalGroup(
            panelTomateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTomateLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelTomateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTomate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonTomate, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelSalsa.setBackground(new java.awt.Color(255, 255, 255));

        labelSalsa.setForeground(new java.awt.Color(0, 0, 0));
        labelSalsa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/salsaEspecial.png"))); // NOI18N
        labelSalsa.setText("Salsa especial");
        labelSalsa.setIconTextGap(10);

        botonSalsa.setText("Añadir");
        botonSalsa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonSalsaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelSalsaLayout = new javax.swing.GroupLayout(panelSalsa);
        panelSalsa.setLayout(panelSalsaLayout);
        panelSalsaLayout.setHorizontalGroup(
            panelSalsaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalsaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelSalsa, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonSalsa, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelSalsaLayout.setVerticalGroup(
            panelSalsaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelSalsaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelSalsaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelSalsa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonSalsa, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelLechuga.setBackground(new java.awt.Color(255, 255, 255));

        labelLechuga.setForeground(new java.awt.Color(0, 0, 0));
        labelLechuga.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/lechuga.png"))); // NOI18N
        labelLechuga.setText("Lechuga");
        labelLechuga.setIconTextGap(10);

        botonLechuga.setText("Añadir");
        botonLechuga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonLechugaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLechugaLayout = new javax.swing.GroupLayout(panelLechuga);
        panelLechuga.setLayout(panelLechugaLayout);
        panelLechugaLayout.setHorizontalGroup(
            panelLechugaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLechugaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLechuga, javax.swing.GroupLayout.PREFERRED_SIZE, 270, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(botonLechuga, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelLechugaLayout.setVerticalGroup(
            panelLechugaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLechugaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLechugaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelLechuga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(botonLechuga, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panelExtrasListo.setBackground(new java.awt.Color(255, 255, 255));

        botonExtrasListo.setText("Listo");
        botonExtrasListo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonExtrasListoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelExtrasListoLayout = new javax.swing.GroupLayout(panelExtrasListo);
        panelExtrasListo.setLayout(panelExtrasListoLayout);
        panelExtrasListoLayout.setHorizontalGroup(
            panelExtrasListoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExtrasListoLayout.createSequentialGroup()
                .addGap(427, 427, 427)
                .addComponent(botonExtrasListo, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelExtrasListoLayout.setVerticalGroup(
            panelExtrasListoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExtrasListoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(botonExtrasListo, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelGeneralLayout = new javax.swing.GroupLayout(panelGeneral);
        panelGeneral.setLayout(panelGeneralLayout);
        panelGeneralLayout.setHorizontalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelTituloExtras, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panelGeneralLayout.createSequentialGroup()
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(panelSalsa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelHuevo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelCebolla, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelTocino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelTomate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(panelLechuga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(panelExtrasListo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelGeneralLayout.setVerticalGroup(
            panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelGeneralLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelTituloExtras, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelCebolla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTocino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelHuevo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelTomate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelGeneralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelSalsa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelLechuga, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelExtrasListo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dialogExtrasLayout = new javax.swing.GroupLayout(dialogExtras.getContentPane());
        dialogExtras.getContentPane().setLayout(dialogExtrasLayout);
        dialogExtrasLayout.setHorizontalGroup(
            dialogExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dialogExtrasLayout.setVerticalGroup(
            dialogExtrasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1000, 500));
        setSize(new java.awt.Dimension(700, 650));

        javax.swing.GroupLayout panelSalonLayout = new javax.swing.GroupLayout(panelSalon);
        panelSalon.setLayout(panelSalonLayout);
        panelSalonLayout.setHorizontalGroup(
            panelSalonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        panelSalonLayout.setVerticalGroup(
            panelSalonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 456, Short.MAX_VALUE)
        );

        labelTituloSalon.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        labelTituloSalon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ImagesMenu/iconoSalon.png"))); // NOI18N
        labelTituloSalon.setText("Salon");

        javax.swing.GroupLayout panelTituloSalonLayout = new javax.swing.GroupLayout(panelTituloSalon);
        panelTituloSalon.setLayout(panelTituloSalonLayout);
        panelTituloSalonLayout.setHorizontalGroup(
            panelTituloSalonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloSalonLayout.createSequentialGroup()
                .addGap(450, 450, 450)
                .addComponent(labelTituloSalon)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelTituloSalonLayout.setVerticalGroup(
            panelTituloSalonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTituloSalonLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelTituloSalon))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelSalon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelTituloSalon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(panelTituloSalon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelSalon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void botonHacerPedido(java.awt.event.ActionEvent evt, Mesa mesaClickeada){
        mesaHacerPedido = mesaClickeada;
        // en caso de que la mesa este libre abre ventana de menu
        if (mesaHacerPedido.isLibre()) {
            pedidoRealizar = new Pedido();
            pedidoRealizar.setIdMesa(mesaClickeada.getIdMesa());
            // abre menu
            dialogMenu.setVisible(true);
        } else { // caso de que no este libre es para pagar y libera la mesa
            mesaClickeada.liberarMesa();
            
            // vuelve a poner el boton como la mesa libre
            mesaClickeada.getBoton().setText("Realizar pedido");
        }
    }
    
    private void botonSeleccionarHambBaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionarHambBaseActionPerformed
        ingredientesExtra = "";
        hamburguesaPedir = 3;
        dialogExtras.setVisible(true);
    }//GEN-LAST:event_botonSeleccionarHambBaseActionPerformed

    private void botonSeleccionarHambJPQActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionarHambJPQActionPerformed
        ingredientesExtra = "";
        hamburguesaPedir = 2;
        dialogExtras.setVisible(true);
    }//GEN-LAST:event_botonSeleccionarHambJPQActionPerformed

    private void botonSeleccionarHambPOOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionarHambPOOActionPerformed
        ingredientesExtra = "";
        hamburguesaPedir = 1;
        dialogExtras.setVisible(true);
    }//GEN-LAST:event_botonSeleccionarHambPOOActionPerformed

    private void botonSeleccionarHambPersoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSeleccionarHambPersoActionPerformed
        ingredientesExtra = "";
        hamburguesaPedir = 3;
        dialogExtras.setVisible(true);
    }//GEN-LAST:event_botonSeleccionarHambPersoActionPerformed

    private void botonCebollaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCebollaActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, cebolla);
    }//GEN-LAST:event_botonCebollaActionPerformed

    private void botonTocinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTocinoActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, tocino);
    }//GEN-LAST:event_botonTocinoActionPerformed

    private void botonHuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonHuevoActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, huevo);
    }//GEN-LAST:event_botonHuevoActionPerformed

    private void botonTomateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonTomateActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, tomate);
    }//GEN-LAST:event_botonTomateActionPerformed

    private void botonSalsaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonSalsaActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, salsaEspecial);
    }//GEN-LAST:event_botonSalsaActionPerformed

    private void botonLechugaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonLechugaActionPerformed
        ingredientesExtra += indexOf(Personalizada.baseIngredientes, lechuga);
    }//GEN-LAST:event_botonLechugaActionPerformed

    private void botonExtrasListoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonExtrasListoActionPerformed
        
        if (ingredientesExtra != "") {
            hamburguesaPedida = FactoryHamburguesa.crearHamburguesa(hamburguesaPedir, ingredientesExtra);
        } else {
            hamburguesaPedida = FactoryHamburguesa.crearHamburguesa(hamburguesaPedir);
        }
        
        // anade la hamburguesa a el array del pedido
        pedidoRealizar.setHamburguesa(hamburguesaPedida);
        
        // restablece valor de hamburguesa a 0 que significa ninguna hamburguesa seleccionada
        hamburguesaPedir = 0;
        
        System.out.println("Hamburguesa creada");
        
        // cierra ventana de extras
        dialogExtras.setVisible(false);
    }//GEN-LAST:event_botonExtrasListoActionPerformed

    private void botonRealizarPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonRealizarPedidoActionPerformed
        if (!pedidoRealizar.getArrayHamburguesas().isEmpty()) {
            enviarPedido(pedidoRealizar);
            
            // coloca el pedido en la mesa y coloca la mesa como ocupada
            mesaHacerPedido.setPedido(pedidoRealizar);
            
            // cambia texto de boton al hacer el pedido y lo deshabilita
            mesaHacerPedido.getBoton().setText("Pendiente...");
            mesaHacerPedido.getBoton().setEnabled(false);
            
            dialogMenu.setVisible(false);
        }
    }//GEN-LAST:event_botonRealizarPedidoActionPerformed

    private void botonCancelarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonCancelarMenuActionPerformed
        dialogMenu.setVisible(false);
    }//GEN-LAST:event_botonCancelarMenuActionPerformed

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
        
        Thread hiloAbrirConexionSimulador = new Thread(saloninterfaz);
        
        // inicia hilo con metodo run que es para que tenga un ciclo true para recibir 
        // de servidor
        hiloRecibirServidor.start();
        
        // inicia hilo para abrir conexion con simulador en caso de querer conectar con él
        hiloAbrirConexionSimulador.start();
        
        saloninterfaz.setVisible(true);
        
        ///////////////////////////////////////////////////////////////
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelExtras;
    private javax.swing.JLabel LabelImagenHambBase9;
    private javax.swing.JLabel LabelImagenHambJPQ;
    private javax.swing.JLabel LabelImagenHambPOO;
    private javax.swing.JLabel LabelImagenHambPersonalizada;
    private javax.swing.JLabel LabelIngredientesHambBase9;
    private javax.swing.JLabel LabelIngredientesHambJPQ;
    private javax.swing.JLabel LabelIngredientesHambPOO;
    private javax.swing.JLabel LabelIngredientesHambPerso;
    private javax.swing.JLabel LabelMenu;
    private javax.swing.JPanel PanelHambBase9;
    private javax.swing.JPanel PanelHambJPQ;
    private javax.swing.JPanel PanelHambPOO;
    private javax.swing.JPanel PanelHambPersonalizada;
    private javax.swing.JPanel PanelRealizarPedido;
    private javax.swing.JPanel PanelTituloMenu;
    private javax.swing.JButton botonCancelarMenu;
    private javax.swing.JButton botonCebolla;
    private javax.swing.JButton botonExtrasListo;
    private javax.swing.JButton botonHuevo;
    private javax.swing.JButton botonLechuga;
    private javax.swing.JButton botonRealizarPedido;
    private javax.swing.JButton botonSalsa;
    private javax.swing.JButton botonSeleccionarHambBase;
    private javax.swing.JButton botonSeleccionarHambJPQ;
    private javax.swing.JButton botonSeleccionarHambPOO;
    private javax.swing.JButton botonSeleccionarHambPerso;
    private javax.swing.JButton botonTocino;
    private javax.swing.JButton botonTomate;
    private javax.swing.JDialog dialogExtras;
    private javax.swing.JDialog dialogMenu;
    private javax.swing.JLabel labelCebolla;
    private javax.swing.JLabel labelHuevo;
    private javax.swing.JLabel labelLechuga;
    private javax.swing.JLabel labelSalsa;
    private javax.swing.JLabel labelTituloSalon;
    private javax.swing.JLabel labelTocino;
    private javax.swing.JLabel labelTomate;
    private javax.swing.JPanel panelCebolla;
    private javax.swing.JPanel panelExtrasListo;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelGeneralMenu;
    private javax.swing.JPanel panelHuevo;
    private javax.swing.JPanel panelLechuga;
    private javax.swing.JPanel panelSalon;
    private javax.swing.JPanel panelSalsa;
    private javax.swing.JPanel panelTituloExtras;
    private javax.swing.JPanel panelTituloSalon;
    private javax.swing.JPanel panelTocino;
    private javax.swing.JPanel panelTomate;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        if (!recibirServidorCorriendo) {
            recibirServidorCorriendo = true;
            System.out.println("Corre servidor");
            // empieza metodo para recibir de servidor
            recibirListaPedidos();
        } else {
            System.out.println("Simulador");
            abrirConexionSimulador();
        }
    }
}
