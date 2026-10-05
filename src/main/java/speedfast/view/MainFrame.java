package speedfast.view;

import speedfast.dao.EntregaDAO;
import speedfast.dao.PedidoDAO;
import speedfast.dao.RepartidorDAO;
import speedfast.db.ConexionDB;
import speedfast.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Ventana principal Swing. La vista solo recoge/valida datos y delega
 * la persistencia en los DAO.
 */
public class MainFrame extends JFrame {

    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    private final DefaultTableModel modeloRepartidores = modeloNoEditable("ID", "Nombre");
    private final DefaultTableModel modeloPedidos = modeloNoEditable("ID", "Dirección", "Tipo", "Estado");
    private final DefaultTableModel modeloEntregas =
            modeloNoEditable("ID", "Pedido", "Dirección", "Repartidor", "Fecha", "Hora");

    private final JTable tablaRepartidores = new JTable(modeloRepartidores);
    private final JTable tablaPedidos = new JTable(modeloPedidos);
    private final JTable tablaEntregas = new JTable(modeloEntregas);

    private final JTextField txtRepId = new JTextField();
    private final JTextField txtRepNombre = new JTextField();
    private final JTextField txtPedId = new JTextField();
    private final JTextField txtPedDireccion = new JTextField();
    private final JTextField txtEntId = new JTextField();
    private final JTextField txtEntFecha = new JTextField(LocalDate.now().toString());
    private final JTextField txtEntHora = new JTextField(LocalTime.now().withNano(0).toString());

    private final JComboBox<TipoPedido> cboTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> cboEstado = new JComboBox<>(EstadoPedido.values());
    private final JComboBox<EstadoPedido> cboFiltroEstado = new JComboBox<>();
    private final JComboBox<TipoPedido> cboFiltroTipo = new JComboBox<>();
    private final JComboBox<Pedido> cboPedidoEntrega = new JComboBox<>();
    private final JComboBox<Repartidor> cboRepartidorEntrega = new JComboBox<>();
    private final JComboBox<Pedido> cboFiltroPedidoEntrega = new JComboBox<>();
    private final JComboBox<Repartidor> cboFiltroRepartidorEntrega = new JComboBox<>();

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss");

    public MainFrame() {
        setTitle("SpeedFast - Gestión de pedidos y entregas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.add(crearEncabezado(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Repartidores", crearPanelRepartidores());
        tabs.addTab("Pedidos", crearPanelPedidos());
        tabs.addTab("Entregas", crearPanelEntregas());
        root.add(tabs, BorderLayout.CENTER);

        setContentPane(root);

        tablaRepartidores.getSelectionModel().addListSelectionListener(e -> cargarRepartidorSeleccionado());
        tablaPedidos.getSelectionModel().addListSelectionListener(e -> cargarPedidoSeleccionado());
        tablaEntregas.getSelectionModel().addListSelectionListener(e -> cargarEntregaSeleccionada());

        cargarTodo();
    }

    private JPanel crearEncabezado() {
        JPanel p = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("SpeedFast | Gestión CRUD con JDBC");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel estado = new JLabel("  Base de datos: " + ConexionDB.getUrl());
        estado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        p.add(titulo, BorderLayout.WEST);
        p.add(estado, BorderLayout.SOUTH);
        return p;
    }

    private JPanel crearPanelRepartidores() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.add(formularioRepartidor(), BorderLayout.NORTH);
        p.add(new JScrollPane(tablaRepartidores), BorderLayout.CENTER);
        return p;
    }

    private JPanel formularioRepartidor() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Datos del repartidor"));
        GridBagConstraints g = gbc();

        txtRepId.setEditable(false);
        g.gridx=0; g.gridy=0; p.add(new JLabel("ID:"), g);
        g.gridx=1; g.weightx=1; p.add(txtRepId, g);
        g.gridx=2; g.weightx=0; p.add(new JLabel("Nombre:"), g);
        g.gridx=3; g.weightx=1; p.add(txtRepNombre, g);

        JButton nuevo = boton("Nuevo", e -> limpiarRepartidor());
        JButton guardar = boton("Guardar", e -> guardarRepartidor());
        JButton editar = boton("Editar", e -> editarRepartidor());
        JButton eliminar = boton("Eliminar", e -> eliminarRepartidor());
        JButton actualizar = boton("Actualizar tabla", e -> cargarRepartidores());

        g.gridy=1; g.gridx=0; p.add(nuevo,g);
        g.gridx=1; p.add(guardar,g);
        g.gridx=2; p.add(editar,g);
        g.gridx=3; p.add(eliminar,g);
        g.gridx=4; p.add(actualizar,g);
        return p;
    }

    private JPanel crearPanelPedidos() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        p.add(formularioPedido(), BorderLayout.NORTH);
        p.add(new JScrollPane(tablaPedidos), BorderLayout.CENTER);
        return p;
    }

    private JPanel formularioPedido() {
        JPanel cont = new JPanel(new BorderLayout(5,5));
        JPanel f = new JPanel(new GridBagLayout());
        f.setBorder(BorderFactory.createTitledBorder("Datos del pedido"));
        GridBagConstraints g=gbc();

        txtPedId.setEditable(false);
        g.gridx=0;g.gridy=0;f.add(new JLabel("ID:"),g);
        g.gridx=1;g.weightx=1;f.add(txtPedId,g);
        g.gridx=2;g.weightx=0;f.add(new JLabel("Dirección:"),g);
        g.gridx=3;g.weightx=1;f.add(txtPedDireccion,g);
        g.gridx=4;g.weightx=0;f.add(new JLabel("Tipo:"),g);
        g.gridx=5;g.weightx=0.7;f.add(cboTipo,g);
        g.gridx=6;g.weightx=0;f.add(new JLabel("Estado:"),g);
        g.gridx=7;g.weightx=0.7;f.add(cboEstado,g);

        JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(boton("Nuevo",e->limpiarPedido()));
        botones.add(boton("Guardar",e->guardarPedido()));
        botones.add(boton("Editar",e->editarPedido()));
        botones.add(boton("Eliminar",e->eliminarPedido()));

        JPanel filtros=new JPanel(new FlowLayout(FlowLayout.LEFT));
        cboFiltroEstado.addItem(null);
        for(EstadoPedido x:EstadoPedido.values()) cboFiltroEstado.addItem(x);
        cboFiltroTipo.addItem(null);
        for(TipoPedido x:TipoPedido.values()) cboFiltroTipo.addItem(x);
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros opcionales"));
        filtros.add(new JLabel("Estado:")); filtros.add(cboFiltroEstado);
        filtros.add(new JLabel("Tipo:")); filtros.add(cboFiltroTipo);
        filtros.add(boton("Aplicar filtros",e->cargarPedidosConFiltros()));
        filtros.add(boton("Mostrar todos",e->{cboFiltroEstado.setSelectedIndex(0);cboFiltroTipo.setSelectedIndex(0);cargarPedidos();}));

        cont.add(f,BorderLayout.NORTH);
        JPanel abajo=new JPanel(new BorderLayout());
        abajo.add(botones,BorderLayout.NORTH); abajo.add(filtros,BorderLayout.SOUTH);
        cont.add(abajo,BorderLayout.SOUTH);
        return cont;
    }

    private JPanel crearPanelEntregas() {
        JPanel p=new JPanel(new BorderLayout(8,8));
        JPanel superior = new JPanel(new BorderLayout(5, 5));
        superior.add(formularioEntrega(), BorderLayout.NORTH);
        superior.add(filtrosEntregas(), BorderLayout.SOUTH);
        p.add(superior, BorderLayout.NORTH);
        p.add(new JScrollPane(tablaEntregas),BorderLayout.CENTER);
        return p;
    }

    private JPanel filtrosEntregas() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setBorder(BorderFactory.createTitledBorder("Filtros de entregas"));
        cboFiltroPedidoEntrega.addItem(null);
        cboFiltroRepartidorEntrega.addItem(null);
        p.add(new JLabel("Pedido:"));
        p.add(cboFiltroPedidoEntrega);
        p.add(new JLabel("Repartidor:"));
        p.add(cboFiltroRepartidorEntrega);
        p.add(boton("Aplicar filtros", e -> cargarEntregasConFiltros()));
        p.add(boton("Mostrar todas", e -> {
            cboFiltroPedidoEntrega.setSelectedIndex(0);
            cboFiltroRepartidorEntrega.setSelectedIndex(0);
            cargarEntregas();
        }));
        return p;
    }

    private JPanel formularioEntrega() {
        JPanel p=new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder("Datos de la entrega"));
        GridBagConstraints g=gbc();
        txtEntId.setEditable(false);
        g.gridx=0;g.gridy=0;p.add(new JLabel("ID:"),g);
        g.gridx=1;g.weightx=.4;p.add(txtEntId,g);
        g.gridx=2;g.weightx=0;p.add(new JLabel("Pedido:"),g);
        g.gridx=3;g.weightx=1.2;p.add(cboPedidoEntrega,g);
        g.gridx=4;g.weightx=0;p.add(new JLabel("Repartidor:"),g);
        g.gridx=5;g.weightx=1.2;p.add(cboRepartidorEntrega,g);

        g.gridx=0;g.gridy=1;g.weightx=0;p.add(new JLabel("Fecha (AAAA-MM-DD):"),g);
        g.gridx=1;g.weightx=.6;p.add(txtEntFecha,g);
        g.gridx=2;g.weightx=0;p.add(new JLabel("Hora (HH:MM:SS):"),g);
        g.gridx=3;g.weightx=.6;p.add(txtEntHora,g);

        JPanel botones=new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.add(boton("Nuevo",e->limpiarEntrega()));
        botones.add(boton("Guardar",e->guardarEntrega()));
        botones.add(boton("Editar",e->editarEntrega()));
        botones.add(boton("Eliminar",e->eliminarEntrega()));
        botones.add(boton("Actualizar datos",e->{cargarCombosEntrega();cargarEntregas();}));
        g.gridx=0;g.gridy=2;g.gridwidth=6;g.weightx=1;p.add(botones,g);
        return p;
    }

    private void guardarRepartidor() {
        String nombre=txtRepNombre.getText().trim();
        if(nombre.isEmpty()){error("El nombre es obligatorio.");return;}
        try{repartidorDAO.create(new Repartidor(nombre)); exito("Repartidor registrado."); cargarRepartidores(); cargarCombosEntrega(); limpiarRepartidor();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void editarRepartidor() {
        Integer id=entero(txtRepId.getText(),"Selecciona un repartidor.");
        if(id==null)return;
        String nombre=txtRepNombre.getText().trim();
        if(nombre.isEmpty()){error("El nombre es obligatorio.");return;}
        try{repartidorDAO.update(new Repartidor(id,nombre));exito("Repartidor actualizado.");cargarRepartidores();cargarCombosEntrega();limpiarRepartidor();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void eliminarRepartidor() {
        Integer id=entero(txtRepId.getText(),"Selecciona un repartidor.");
        if(id==null)return;
        if(!confirmar("¿Eliminar el repartidor seleccionado? Si tiene entregas asociadas, MySQL puede impedirlo."))return;
        try{repartidorDAO.delete(id);exito("Repartidor eliminado.");cargarRepartidores();cargarCombosEntrega();limpiarRepartidor();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void guardarPedido() {
        String dir=txtPedDireccion.getText().trim();
        if(dir.isEmpty()){error("La dirección es obligatoria.");return;}
        try{
            pedidoDAO.create(new Pedido(dir,(TipoPedido)cboTipo.getSelectedItem(),(EstadoPedido)cboEstado.getSelectedItem()));
            exito("Pedido registrado.");cargarPedidos();cargarCombosEntrega();limpiarPedido();
        }catch(SQLException ex){errorSql(ex);}
    }

    private void editarPedido() {
        Integer id=entero(txtPedId.getText(),"Selecciona un pedido.");if(id==null)return;
        String dir=txtPedDireccion.getText().trim();
        if(dir.isEmpty()){error("La dirección es obligatoria.");return;}
        try{
            pedidoDAO.update(new Pedido(id,dir,(TipoPedido)cboTipo.getSelectedItem(),(EstadoPedido)cboEstado.getSelectedItem()));
            exito("Pedido actualizado.");cargarPedidos();cargarCombosEntrega();limpiarPedido();
        }catch(SQLException ex){errorSql(ex);}
    }

    private void eliminarPedido() {
        Integer id=entero(txtPedId.getText(),"Selecciona un pedido.");if(id==null)return;
        if(!confirmar("¿Eliminar el pedido seleccionado? Si tiene una entrega asociada, MySQL puede impedirlo."))return;
        try{pedidoDAO.delete(id);exito("Pedido eliminado.");cargarPedidos();cargarCombosEntrega();limpiarPedido();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void guardarEntrega() {
        Entrega e=leerEntrega(); if(e==null)return;
        try{entregaDAO.create(e);exito("Entrega registrada.");cargarEntregas();limpiarEntrega();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void editarEntrega() {
        Integer id=entero(txtEntId.getText(),"Selecciona una entrega.");if(id==null)return;
        Entrega e=leerEntrega();if(e==null)return;e.setId(id);
        try{entregaDAO.update(e);exito("Entrega actualizada.");cargarEntregas();limpiarEntrega();}
        catch(SQLException ex){errorSql(ex);}
    }

    private void eliminarEntrega() {
        Integer id=entero(txtEntId.getText(),"Selecciona una entrega.");if(id==null)return;
        if(!confirmar("¿Eliminar la entrega seleccionada?"))return;
        try{entregaDAO.delete(id);exito("Entrega eliminada.");cargarEntregas();limpiarEntrega();}
        catch(SQLException ex){errorSql(ex);}
    }

    private Entrega leerEntrega() {
        Pedido p=(Pedido)cboPedidoEntrega.getSelectedItem();
        Repartidor r=(Repartidor)cboRepartidorEntrega.getSelectedItem();
        if(p==null||r==null){error("Debes seleccionar un pedido y un repartidor.");return null;}
        try{
            LocalDate fecha=LocalDate.parse(txtEntFecha.getText().trim());
            LocalTime hora=LocalTime.parse(txtEntHora.getText().trim(),HORA);
            return new Entrega(0,p.getId(),r.getId(),fecha,hora);
        }catch(DateTimeParseException ex){error("Fecha u hora inválida. Usa AAAA-MM-DD y HH:MM:SS.");return null;}
    }

    private void cargarTodo(){cargarRepartidores();cargarPedidos();cargarCombosEntrega();cargarEntregas();}

    private void cargarRepartidores() {
        try{
            modeloRepartidores.setRowCount(0);
            for(Repartidor r:repartidorDAO.readAll())modeloRepartidores.addRow(new Object[]{r.getId(),r.getNombre()});
        }catch(SQLException ex){errorSql(ex);}
    }

    private void cargarPedidos(){cargarPedidos(null,null);}
    private void cargarPedidosConFiltros(){
        cargarPedidos((EstadoPedido)cboFiltroEstado.getSelectedItem(),(TipoPedido)cboFiltroTipo.getSelectedItem());
    }
    private void cargarPedidos(EstadoPedido estado,TipoPedido tipo){
        try{
            modeloPedidos.setRowCount(0);
            for(Pedido p:pedidoDAO.readAll(estado,tipo))
                modeloPedidos.addRow(new Object[]{p.getId(),p.getDireccion(),p.getTipo(),p.getEstado()});
        }catch(SQLException ex){errorSql(ex);}
    }

    private void cargarCombosEntrega() {
        try{
            int pedidoSeleccionado=selectedPedidoId();
            int repartidorSeleccionado=selectedRepartidorId();
            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();

            cboPedidoEntrega.removeAllItems();
            for(Pedido p:pedidos)cboPedidoEntrega.addItem(p);
            seleccionarPedidoCombo(pedidoSeleccionado);

            cboFiltroPedidoEntrega.removeAllItems();
            cboFiltroPedidoEntrega.addItem(null);
            for(Pedido p:pedidos)cboFiltroPedidoEntrega.addItem(p);

            cboRepartidorEntrega.removeAllItems();
            for(Repartidor r:repartidores)cboRepartidorEntrega.addItem(r);
            seleccionarRepartidorCombo(repartidorSeleccionado);

            cboFiltroRepartidorEntrega.removeAllItems();
            cboFiltroRepartidorEntrega.addItem(null);
            for(Repartidor r:repartidores)cboFiltroRepartidorEntrega.addItem(r);
        }catch(SQLException ex){errorSql(ex);}
    }

    private void cargarEntregas(){
        cargarEntregas(null, null);
    }

    private void cargarEntregasConFiltros() {
        Pedido pedido = (Pedido) cboFiltroPedidoEntrega.getSelectedItem();
        Repartidor repartidor = (Repartidor) cboFiltroRepartidorEntrega.getSelectedItem();
        cargarEntregas(pedido == null ? null : pedido.getId(),
                       repartidor == null ? null : repartidor.getId());
    }

    private void cargarEntregas(Integer idPedido, Integer idRepartidor){
        try{
            modeloEntregas.setRowCount(0);
            for(Entrega e:entregaDAO.readAll(idPedido, idRepartidor))
                modeloEntregas.addRow(new Object[]{e.getId(),e.getIdPedido(),e.getDireccionPedido(),
                        e.getNombreRepartidor(),e.getFecha(),e.getHora().format(HORA)});
        }catch(SQLException ex){errorSql(ex);}
    }

    private void cargarRepartidorSeleccionado(){
        int row=tablaRepartidores.getSelectedRow();if(row<0)return;
        txtRepId.setText(String.valueOf(modeloRepartidores.getValueAt(row,0)));
        txtRepNombre.setText(String.valueOf(modeloRepartidores.getValueAt(row,1)));
    }

    private void cargarPedidoSeleccionado(){
        int row=tablaPedidos.getSelectedRow();if(row<0)return;
        txtPedId.setText(String.valueOf(modeloPedidos.getValueAt(row,0)));
        txtPedDireccion.setText(String.valueOf(modeloPedidos.getValueAt(row,1)));
        cboTipo.setSelectedItem(modeloPedidos.getValueAt(row,2));
        cboEstado.setSelectedItem(modeloPedidos.getValueAt(row,3));
    }

    private void cargarEntregaSeleccionada(){
        int row=tablaEntregas.getSelectedRow();if(row<0)return;
        txtEntId.setText(String.valueOf(modeloEntregas.getValueAt(row,0)));
        int pedidoId=(Integer)modeloEntregas.getValueAt(row,1);
        String fecha=String.valueOf(modeloEntregas.getValueAt(row,4));
        String hora=String.valueOf(modeloEntregas.getValueAt(row,5));
        txtEntFecha.setText(fecha);txtEntHora.setText(hora);
        seleccionarPedidoCombo(pedidoId);
        String nombre=String.valueOf(modeloEntregas.getValueAt(row,3));
        for(int i=0;i<cboRepartidorEntrega.getItemCount();i++){
            if(cboRepartidorEntrega.getItemAt(i).getNombre().equals(nombre)){cboRepartidorEntrega.setSelectedIndex(i);break;}
        }
    }

    private void limpiarRepartidor(){txtRepId.setText("");txtRepNombre.setText("");tablaRepartidores.clearSelection();}
    private void limpiarPedido(){txtPedId.setText("");txtPedDireccion.setText("");cboTipo.setSelectedIndex(0);cboEstado.setSelectedItem(EstadoPedido.PENDIENTE);tablaPedidos.clearSelection();}
    private void limpiarEntrega(){txtEntId.setText("");txtEntFecha.setText(LocalDate.now().toString());txtEntHora.setText(LocalTime.now().withNano(0).format(HORA));tablaEntregas.clearSelection();}

    private int selectedPedidoId(){Pedido p=(Pedido)cboPedidoEntrega.getSelectedItem();return p==null?0:p.getId();}
    private int selectedRepartidorId(){Repartidor r=(Repartidor)cboRepartidorEntrega.getSelectedItem();return r==null?0:r.getId();}
    private void seleccionarPedidoCombo(int id){for(int i=0;i<cboPedidoEntrega.getItemCount();i++)if(cboPedidoEntrega.getItemAt(i).getId()==id){cboPedidoEntrega.setSelectedIndex(i);return;}}
    private void seleccionarRepartidorCombo(int id){for(int i=0;i<cboRepartidorEntrega.getItemCount();i++)if(cboRepartidorEntrega.getItemAt(i).getId()==id){cboRepartidorEntrega.setSelectedIndex(i);return;}}

    private static DefaultTableModel modeloNoEditable(String... cols){
        return new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
    }
    private static GridBagConstraints gbc(){
        GridBagConstraints g=new GridBagConstraints();g.insets=new Insets(4,4,4,4);g.fill=GridBagConstraints.HORIZONTAL;g.anchor=GridBagConstraints.WEST;g.weighty=0;return g;
    }
    private JButton boton(String texto, java.awt.event.ActionListener a){JButton b=new JButton(texto);b.addActionListener(a);return b;}
    private Integer entero(String texto,String msg){try{return Integer.valueOf(texto.trim());}catch(Exception e){error(msg);return null;}}
    private boolean confirmar(String msg){return JOptionPane.showConfirmDialog(this,msg,"Confirmar",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION;}
    private void exito(String msg){JOptionPane.showMessageDialog(this,msg,"Operación exitosa",JOptionPane.INFORMATION_MESSAGE);}
    private void error(String msg){JOptionPane.showMessageDialog(this,msg,"Validación",JOptionPane.WARNING_MESSAGE);}
    private void errorSql(SQLException ex){
        JOptionPane.showMessageDialog(this,
                "No fue posible completar la operación.\n\nDetalle: "+ex.getMessage(),
                "Error de base de datos",JOptionPane.ERROR_MESSAGE);
    }
}
