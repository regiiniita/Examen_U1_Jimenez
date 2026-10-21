/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
 
import com.toedter.calendar.JDateChooser;
import controlador.ControlReservacion;
import dtos.ClienteDTO;
import dtos.ComprobanteDTO;
import dtos.HabitacionDTO;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modeloPresentacion.IObservadorReservas;
 
/**
 * 
 * @author regin
 */
public class FrmGestionReservas extends JFrame implements IObservadorReservas {
    private static final Color ROJO = new Color(0xFF, 0x5A, 0x5F);
    private static final Color GRIS_ENCABEZADO = new Color(0xD9, 0xD9, 0xD9);
    private static final Color GRIS_COMPROBANTE = new Color(0xA6, 0xA6, 0xA6);
    private static final Color BORDE = new Color(0x55, 0x55, 0x55);
    private static final Color TEXTO_SECUNDARIO = new Color(0x44, 0x44, 0x44);
    private static final Font F_NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    private static final Font F_CHICA = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
    private static final Font F_NEGRITA = new Font(Font.SANS_SERIF, Font.BOLD, 13);
 
    private static final DateTimeFormatter FORMATO_LARGO =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-MX"));

    private enum Paso { ESTANCIA, HABITACIONES, CLIENTE, COMPROBANTE }
 
    private final ControlReservacion control;
    private Paso paso;
    private boolean actualizando = false;

    private final JPanel contenido = new JPanel(null);
 
    // Datos de la estancia
    private final JPanel tarjetaEstancia = tarjeta("NUEVA RESERVACIÓN", "Datos de la estancia");
    private final JLabel lblEntrada = etiqueta("Fecha de entrada");
    private final JLabel lblSalida = etiqueta("Fecha de salida");
    private final JLabel lblHuespedes = etiqueta("Cantidad de huéspedes");
    private final JDateChooser txtEntrada = crearSelectorFecha();
    private final JDateChooser txtSalida = crearSelectorFecha();
    private final JSpinner spnHuespedes = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private final JButton btnSiguiente = botonPrincipal("Siguiente");
 
    // Habitaciones
    private final JPanel tarjetaHabitaciones = tarjeta(null, "Habitaciones disponibles");
    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new Object[]{"NÚMERO", "TIPO", "COSTO POR NOCHE"}, 0) {
                @Override public boolean isCellEditable(int f, int c) { return false; }
            };
    private final JTable tblHabitaciones = new JTable(modeloTabla);
    private final JScrollPane scrHabitaciones = new JScrollPane(tblHabitaciones);
    private final JLabel lblSinHabitaciones = mensajeVacio("No hay habitaciones disponibles",
            "No se encontraron habitaciones para las fechas y la capacidad indicadas. Regresa e ingresa otros datos.");
 
    // Detalles
    private final JPanel tarjetaDetalles = tarjeta("HABITACIÓN SELECCIONADA", "Detalles");
    private final JLabel valNumero = valor(), valTipo = valor(), valCapacidad = valor(),
            valServicios = valor(), valCosto = valor(), valImporte = valor();
    private final JButton btnReservar = botonPrincipal("Reservar");
 
    // Búsqueda de cliente
    private final JPanel tarjetaBusqueda = tarjeta(null, "Cliente");
    private final JTextField txtIdCliente = campoTexto("");
    private final DefaultListModel<ClienteDTO> modeloClientes = new DefaultListModel<>();
    private final JList<ClienteDTO> lstClientes = new JList<>(modeloClientes);
    private final JScrollPane scrClientes = new JScrollPane(lstClientes);
    private final JLabel lblSinClientes = mensajeVacio("No se encontró ningún cliente",
            "El ID ingresado no coincide con ningún cliente registrado en el sistema.");
 
    // Datos del cliente
    private final JPanel tarjetaCliente = tarjeta(null, "Cliente");
    private final JLabel valId = valor(), valNombre = valor(), valTelefono = valor(), valCorreo = valor();
    private final JButton btnConfirmar = botonPrincipal("Confirmar");
 
    // Comprobante (aparece en la misma zona que los datos del cliente)
    private final JPanel tarjetaComprobante = new JPanel(new BorderLayout());
    private final JLabel cId = valor(), cNombre = valor(), cTelefono = valor(), cCorreo = valor(),
            cHabitacion = valor(), cEstancia = valor(), cImporte = new JLabel();
 
    private final JButton btnRegresar = botonSecundario("Regresar");
 
    public FrmGestionReservas(ControlReservacion control) {
        this.control = control;
        setTitle("Gestión de Reservas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        contenido.setBackground(Color.WHITE);
        contenido.setPreferredSize(new Dimension(1000, 600));
        setContentPane(contenido);
        pack();
        setLocationRelativeTo(null);
 
        construirEncabezado();
        construirHabitaciones();
        construirDetalles();
        construirBusqueda();
        construirDatosCliente();
        construirComprobante();
        registrarEventos();
 
        solicitarDatosEstancia();
    }
 
    // =====================================================================
    //  Construcción de los recuadros
    // =====================================================================
 
    private void construirEncabezado() {
        JLabel sistema = new JLabel("Gestión de Reservas");
        sistema.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        sistema.setBounds(20, 12, 300, 18);
        JLabel titulo = new JLabel("Reservar habitación");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        titulo.setBounds(20, 30, 400, 28);
        JSeparator linea = new JSeparator();
        linea.setForeground(Color.BLACK);
        linea.setBounds(20, 64, 960, 2);
        contenido.add(sistema);
        contenido.add(titulo);
        contenido.add(linea);
        spnHuespedes.setFont(F_NORMAL);
    }
 
    private void construirHabitaciones() {
        tblHabitaciones.setFont(F_CHICA);
        tblHabitaciones.setRowHeight(26);
        tblHabitaciones.setShowVerticalLines(false);
        tblHabitaciones.setGridColor(BORDE);
        tblHabitaciones.setSelectionBackground(GRIS_ENCABEZADO);
        tblHabitaciones.setSelectionForeground(Color.BLACK);
        tblHabitaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblHabitaciones.getTableHeader().setFont(F_CHICA);
        tblHabitaciones.getTableHeader().setBackground(GRIS_ENCABEZADO);
        tblHabitaciones.getTableHeader().setReorderingAllowed(false);
        tblHabitaciones.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblHabitaciones.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblHabitaciones.getColumnModel().getColumn(2).setPreferredWidth(120);
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tblHabitaciones.getColumnModel().getColumn(2).setCellRenderer(derecha);
        scrHabitaciones.setBorder(BorderFactory.createEmptyBorder());
        scrHabitaciones.getViewport().setBackground(Color.WHITE);
 
        JPanel cuerpo = cuerpo(tarjetaHabitaciones);
        cuerpo.setLayout(new BorderLayout());
        cuerpo.add(scrHabitaciones, BorderLayout.CENTER);
    }
 
    private void construirDetalles() {
        JPanel cuerpo = cuerpo(tarjetaDetalles);
        cuerpo.setLayout(new GridLayout(6, 1));
        cuerpo.add(fila("Número", valNumero, false));
        cuerpo.add(fila("Tipo", valTipo, false));
        cuerpo.add(fila("Capacidad", valCapacidad, false));
        cuerpo.add(fila("Servicios incluidos", valServicios, false));
        cuerpo.add(fila("Costo por noche", valCosto, false));
        cuerpo.add(fila("Importe total", valImporte, true));
    }
 
    private void construirBusqueda() {
        JPanel cuerpo = cuerpo(tarjetaBusqueda);
        cuerpo.setLayout(null);
        JLabel lblId = etiqueta("ID del cliente");
        lblId.setFont(F_CHICA);
        lblId.setBounds(12, 6, 200, 14);
        JPanel caja = new JPanel(new BorderLayout());
        caja.setBackground(Color.WHITE);
        caja.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        txtIdCliente.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        caja.add(txtIdCliente, BorderLayout.CENTER);
        JLabel lupa = new JLabel(new IconoLupa());
        lupa.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 8));
        caja.add(lupa, BorderLayout.EAST);
        caja.setBounds(12, 22, 414, 26);
        JLabel lblCoincidencias = etiqueta("Coincidencias");
        lblCoincidencias.setFont(F_CHICA);
        lblCoincidencias.setBounds(12, 54, 200, 14);
 
        lstClientes.setFont(F_CHICA);
        lstClientes.setFixedCellHeight(22);
        lstClientes.setSelectionBackground(GRIS_ENCABEZADO);
        lstClientes.setSelectionForeground(Color.BLACK);
        lstClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstClientes.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foco) {
                ClienteDTO c = (ClienteDTO) v;
                JLabel r = (JLabel) super.getListCellRendererComponent(l,
                        String.format("%-10s   %s", c.getId(), c.getNombre()), i, sel, foco);
                r.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                        BorderFactory.createEmptyBorder(0, 8, 0, 0)));
                return r;
            }
        });
        scrClientes.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        scrClientes.setBounds(12, 70, 414, 70);
        lblSinClientes.setBounds(12, 70, 414, 70);
        lblSinClientes.setBorder(BorderFactory.createLineBorder(Color.BLACK));
 
        cuerpo.add(lblId);
        cuerpo.add(caja);
        cuerpo.add(lblCoincidencias);
        cuerpo.add(scrClientes);
        cuerpo.add(lblSinClientes);
    }
 
    private void construirDatosCliente() {
        JPanel cuerpo = cuerpo(tarjetaCliente);
        cuerpo.setLayout(new GridLayout(4, 1));
        cuerpo.add(filaIzquierda("ID", valId));
        cuerpo.add(filaIzquierda("Nombre", valNombre));
        cuerpo.add(filaIzquierda("Teléfono", valTelefono));
        cuerpo.add(filaIzquierda("Correo", valCorreo));
    }
 
    private void construirComprobante() {
        tarjetaComprobante.setBackground(Color.WHITE);
        tarjetaComprobante.setBorder(BorderFactory.createLineBorder(BORDE));
 
        JLabel encabezado = new JLabel("Comprobante de reservación");
        encabezado.setOpaque(true);
        encabezado.setBackground(GRIS_COMPROBANTE);
        encabezado.setForeground(Color.WHITE);
        encabezado.setFont(F_NEGRITA);
        encabezado.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
 
        JPanel cuerpo = new JPanel(new GridLayout(3, 1));
        cuerpo.setBackground(Color.WHITE);
        cuerpo.add(filaDoble("ID", cId, "Nombre", cNombre));
        cuerpo.add(filaDoble("Teléfono", cTelefono, "Correo", cCorreo));
        cuerpo.add(filaDoble("Habitación", cHabitacion, "Estancia", cEstancia));
 
        JPanel pie = new JPanel(new BorderLayout());
        pie.setBackground(GRIS_COMPROBANTE);
        pie.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JLabel lblTotal = new JLabel("Importe total");
        lblTotal.setForeground(Color.WHITE);
        lblTotal.setFont(F_NEGRITA);
        cImporte.setForeground(Color.WHITE);
        cImporte.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        pie.add(lblTotal, BorderLayout.WEST);
        pie.add(cImporte, BorderLayout.EAST);
 
        tarjetaComprobante.add(encabezado, BorderLayout.NORTH);
        tarjetaComprobante.add(cuerpo, BorderLayout.CENTER);
        tarjetaComprobante.add(pie, BorderLayout.SOUTH);
    }
 
 
    private void registrarEventos() {
        btnSiguiente.addActionListener(e -> {
            if (txtEntrada.getDate() == null || txtSalida.getDate() == null) {
                mostrarMensajeError("Selecciona la fecha de entrada y la fecha de salida.");
                return;
            }
            LocalDate entrada = aLocalDate(txtEntrada.getDate());
            LocalDate salida = aLocalDate(txtSalida.getDate());
            control.ingresarDatosEstancia(entrada, salida, (Integer) spnHuespedes.getValue());
        });
 
        tblHabitaciones.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblHabitaciones.getSelectedRow();
            if (!actualizando && !e.getValueIsAdjusting() && fila >= 0 && paso == Paso.HABITACIONES) {
                control.seleccionarHabitacion((Integer) modeloTabla.getValueAt(fila, 0));
            }
        });
 
        btnReservar.addActionListener(e -> solicitarIdCliente());
 
        txtIdCliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { buscarCliente(); }
            @Override public void removeUpdate(DocumentEvent e) { buscarCliente(); }
            @Override public void changedUpdate(DocumentEvent e) { buscarCliente(); }
        });
 
        lstClientes.addListSelectionListener(e -> {
            ClienteDTO c = lstClientes.getSelectedValue();
            if (!actualizando && !e.getValueIsAdjusting() && c != null) {
                control.seleccionarCliente(c.getId());
            }
        });
 
        btnConfirmar.addActionListener(e -> control.confirmar());
 
        btnRegresar.addActionListener(e -> {
            if (paso == Paso.CLIENTE) {
                regresarAHabitaciones();
            } else {
                solicitarDatosEstancia();
            }
        });
    }

    private void solicitarDatosEstancia() {
        paso = Paso.ESTANCIA;
        limpiarPantalla();
        modeloTabla.setRowCount(0);
 
        mostrar(tarjetaEstancia, 280, 115, 440, 200);
        JPanel cuerpo = cuerpo(tarjetaEstancia);
        cuerpo.setLayout(null);
        colocar(cuerpo, lblEntrada, 28, 18, 140, 16);
        colocar(cuerpo, txtEntrada, 28, 36, 130, 28);
        colocar(cuerpo, lblSalida, 178, 18, 140, 16);
        colocar(cuerpo, txtSalida, 178, 36, 130, 28);
        colocar(cuerpo, lblHuespedes, 28, 76, 200, 16);
        colocar(cuerpo, spnHuespedes, 28, 94, 120, 28);
        mostrar(btnSiguiente, 600, 335, 120, 34);
        habilitarEstancia(true);
        refrescar();
        txtEntrada.requestFocusInWindow();
    }

    private void regresarAHabitaciones() {
        paso = Paso.HABITACIONES;
        contenido.remove(tarjetaBusqueda);
        contenido.remove(tarjetaCliente);
        contenido.remove(btnConfirmar);
        mostrar(btnReservar, 860, 330, 120, 34);
        refrescar();
    }

    private void solicitarIdCliente() {
        paso = Paso.CLIENTE;
        contenido.remove(btnReservar);
        actualizando = true;
        txtIdCliente.setText("");
        modeloClientes.clear();
        actualizando = false;
        scrClientes.setVisible(true);
        lblSinClientes.setVisible(false);
        mostrar(tarjetaBusqueda, 20, 345, 440, 190);
        mostrar(btnRegresar, 20, 552, 110, 34);
        refrescar();
        txtIdCliente.requestFocusInWindow();
    }
 
    private void buscarCliente() {
        if (actualizando) {
            return;
        }
        String criterio = txtIdCliente.getText().trim();
        contenido.remove(tarjetaCliente);
        contenido.remove(btnConfirmar);
        if (criterio.isEmpty()) {
            modeloClientes.clear();
            scrClientes.setVisible(true);
            lblSinClientes.setVisible(false);
            refrescar();
            return;
        }
        control.ingresarIdCliente(criterio);
    }

    private void subirCamposEstancia() {
        contenido.remove(tarjetaEstancia);
        contenido.remove(btnSiguiente);
        colocar(contenido, lblEntrada, 20, 78, 140, 16);
        colocar(contenido, txtEntrada, 20, 96, 130, 28);
        colocar(contenido, lblSalida, 165, 78, 140, 16);
        colocar(contenido, txtSalida, 165, 96, 130, 28);
        colocar(contenido, lblHuespedes, 310, 78, 160, 16);
        colocar(contenido, spnHuespedes, 310, 96, 110, 28);
        habilitarEstancia(false);
    }
 
 
    @Override
    public void mostrarHabitacionesDisponibles(List<HabitacionDTO> habitaciones) {
        paso = Paso.HABITACIONES;
        limpiarPantalla();
        subirCamposEstancia();
 
        actualizando = true;
        modeloTabla.setRowCount(0);
        for (HabitacionDTO h : habitaciones) {
            modeloTabla.addRow(new Object[]{h.getNumero(), h.getTipo(), moneda(h.getCostoPorNoche())});
        }
        tblHabitaciones.clearSelection();
        actualizando = false;
 
        JPanel cuerpo = cuerpo(tarjetaHabitaciones);
        cuerpo.removeAll();
        cuerpo.add(habitaciones.isEmpty() ? lblSinHabitaciones : scrHabitaciones, BorderLayout.CENTER);
        int alto = habitaciones.isEmpty() ? 150 : 56 + 26 * Math.min(habitaciones.size(), 6);
        mostrar(tarjetaHabitaciones, 20, 140, 440, alto);
        mostrar(btnRegresar, 20, 552, 110, 34);
        refrescar();
    }
 
    @Override
    public void mostrarDetallesHabitacion(HabitacionDTO h, double importeTotal) {
        valNumero.setText(String.valueOf(h.getNumero()));
        valTipo.setText(h.getTipo());
        valCapacidad.setText(h.getCapacidad() + " personas");
        valServicios.setText("<html><div style='text-align:right;width:230px'>"
                + String.join(", ", h.getServicios()) + ".</div></html>");
        valCosto.setText(moneda(h.getCostoPorNoche()));
        valImporte.setText(moneda(importeTotal));
        mostrar(tarjetaDetalles, 490, 78, 490, 240);
        mostrar(btnReservar, 860, 330, 120, 34);
        refrescar();
    }
 
    @Override
    public void mostrarClientesCoincidentes(List<ClienteDTO> clientes) {
        actualizando = true;
        modeloClientes.clear();
        for (ClienteDTO c : clientes) {
            modeloClientes.addElement(c);
        }
        actualizando = false;
        scrClientes.setVisible(!clientes.isEmpty());
        lblSinClientes.setVisible(clientes.isEmpty());
        refrescar();
    }
 
    @Override
    public void mostrarDatosCliente(ClienteDTO c) {
        valId.setText(c.getId());
        valNombre.setText(c.getNombre());
        valTelefono.setText(c.getTelefono());
        valCorreo.setText(c.getCorreo());
        mostrar(tarjetaCliente, 490, 345, 490, 150);
        mostrar(btnConfirmar, 860, 552, 120, 34);
        refrescar();
    }
 
    @Override
    public void mostrarComprobante(ComprobanteDTO comp) {
        paso = Paso.COMPROBANTE;
        ClienteDTO c = comp.getCliente();
        cId.setText(c.getId());
        cNombre.setText(c.getNombre());
        cTelefono.setText(c.getTelefono());
        cCorreo.setText(c.getCorreo());
        cHabitacion.setText(comp.getTipoHabitacion() + " #" + comp.getNumeroHabitacion());
        cEstancia.setText(textoEstancia(comp.getFechaEntrada(), comp.getFechaSalida()));
        cImporte.setText(moneda(comp.getImporteTotal()));
 
        contenido.remove(tarjetaCliente);
        contenido.remove(btnConfirmar);
        contenido.remove(btnRegresar);
        txtIdCliente.setEnabled(false);
        lstClientes.setEnabled(false);
        mostrar(tarjetaComprobante, 490, 345, 490, 160);
        refrescar();
    }
 
    @Override
    public void mostrarMensajeError(String mensaje) {
        JDialog dialogo = new JDialog(this, "Revisa la información", true);
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel aviso = new JLabel("REVISA LA INFORMACIÓN");
        aviso.setFont(F_CHICA);
        aviso.setBounds(20, 14, 340, 14);
        JLabel texto = new JLabel("<html><b style='font-size:11px'>" + mensaje + "</b></html>");
        texto.setVerticalAlignment(SwingConstants.TOP);
        texto.setBounds(20, 32, 340, 56);
        JButton aceptar = botonPrincipal("Aceptar");
        aceptar.setBounds(130, 96, 120, 32);
        aceptar.addActionListener(e -> dialogo.dispose());
        panel.add(aviso);
        panel.add(texto);
        panel.add(aceptar);
        panel.setPreferredSize(new Dimension(380, 145));
        dialogo.setContentPane(panel);
        dialogo.setUndecorated(true);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.getRootPane().setDefaultButton(aceptar);
        dialogo.setVisible(true);
    }
 

    private void limpiarPantalla() {
        for (JComponent c : new JComponent[]{tarjetaEstancia, btnSiguiente, tarjetaHabitaciones,
                tarjetaDetalles, btnReservar, tarjetaBusqueda, tarjetaCliente, btnConfirmar,
                tarjetaComprobante, btnRegresar, lblEntrada, txtEntrada, lblSalida, txtSalida,
                lblHuespedes, spnHuespedes}) {
            if (c.getParent() == contenido) {
                contenido.remove(c);
            }
        }
        txtIdCliente.setEnabled(true);
        lstClientes.setEnabled(true);
    }
 
    private void mostrar(JComponent c, int x, int y, int w, int h) {
        colocar(contenido, c, x, y, w, h);
    }
 
    private static void colocar(JPanel padre, JComponent c, int x, int y, int w, int h) {
        c.setBounds(x, y, w, h);
        padre.add(c);
    }
 
    private void refrescar() {
        contenido.revalidate();
        contenido.repaint();
    }
 
    private void habilitarEstancia(boolean habilitar) {
        txtEntrada.setEnabled(habilitar);
        txtSalida.setEnabled(habilitar);
        spnHuespedes.setEnabled(habilitar);
    }

    private static JPanel tarjeta(String subtitulo, String titulo) {
        JPanel t = new JPanel(new BorderLayout());
        t.setBackground(Color.WHITE);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 3, new Color(0xBB, 0xBB, 0xBB)),
                BorderFactory.createLineBorder(BORDE)));
        JPanel cabecera = new JPanel(new GridLayout(subtitulo == null ? 1 : 2, 1));
        cabecera.setBackground(Color.WHITE);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        if (subtitulo != null) {
            JLabel s = new JLabel(subtitulo);
            s.setFont(F_CHICA);
            cabecera.add(s);
        }
        JLabel tt = new JLabel(titulo);
        tt.setFont(F_NEGRITA);
        cabecera.add(tt);
        JPanel cuerpo = new JPanel();
        cuerpo.setBackground(Color.WHITE);
        t.add(cabecera, BorderLayout.NORTH);
        t.add(cuerpo, BorderLayout.CENTER);
        return t;
    }
 
    private static JPanel cuerpo(JPanel tarjeta) {
        return (JPanel) ((BorderLayout) tarjeta.getLayout()).getLayoutComponent(BorderLayout.CENTER);
    }
 
    private static JPanel fila(String nombre, JLabel valor, boolean resaltada) {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(resaltada ? GRIS_ENCABEZADO : Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        JLabel n = new JLabel(nombre);
        n.setFont(F_CHICA);
        valor.setHorizontalAlignment(SwingConstants.RIGHT);
        f.add(n, BorderLayout.WEST);
        f.add(valor, BorderLayout.EAST);
        return f;
    }
 
    private static JPanel filaIzquierda(String nombre, JLabel valor) {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)));
        JLabel n = new JLabel(nombre);
        n.setFont(F_CHICA);
        n.setPreferredSize(new Dimension(70, 20));
        f.add(n, BorderLayout.WEST);
        f.add(valor, BorderLayout.CENTER);
        return f;
    }
 
    private static JPanel filaDoble(String n1, JLabel v1, String n2, JLabel v2) {
        JPanel f = new JPanel(new BorderLayout());
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        JPanel izquierda = filaIzquierda(n1, v1);
        JPanel derecha = filaIzquierda(n2, v2);
        izquierda.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
        derecha.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 10));
        izquierda.getComponent(0).setPreferredSize(new Dimension(58, 20));
        derecha.getComponent(0).setPreferredSize(new Dimension(52, 20));
        izquierda.setPreferredSize(new Dimension(190, 30));
        f.add(izquierda, BorderLayout.WEST);
        f.add(derecha, BorderLayout.CENTER);
        return f;
    }
 
    private static String textoEstancia(LocalDate entrada, LocalDate salida) {
        if (entrada.getMonth() == salida.getMonth() && entrada.getYear() == salida.getYear()) {
            return "Del " + entrada.getDayOfMonth() + " al " + salida.format(FORMATO_LARGO);
        }
        return "Del " + entrada.format(FORMATO_LARGO) + " al " + salida.format(FORMATO_LARGO);
    }
 
    private static JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(F_NORMAL);
        l.setForeground(TEXTO_SECUNDARIO);
        return l;
    }
 
    private static JLabel valor() {
        JLabel l = new JLabel();
        l.setFont(F_CHICA);
        return l;
    }
 
    private static JLabel mensajeVacio(String titulo, String detalle) {
        JLabel l = new JLabel("<html><div style='text-align:center;width:280px'><b>" + titulo
                + "</b><br><span style='font-size:9px'>" + detalle + "</span></div></html>",
                SwingConstants.CENTER);
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        return l;
    }
 
    private static JTextField campoTexto(String sugerencia) {
        JTextField t = new JTextField();
        t.setFont(F_NORMAL);
        t.setToolTipText(sugerencia);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        return t;
    }
 
    private static JButton botonPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(F_NORMAL);
        b.setBackground(ROJO);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private static JButton botonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(F_NORMAL);
        b.setBackground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 3, new Color(0xCC, 0xCC, 0xCC)),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private static JDateChooser crearSelectorFecha() {
        JDateChooser selector = new JDateChooser();
        selector.setLocale(Locale.forLanguageTag("es-MX"));
        selector.setDateFormatString("dd/MM/yyyy");
        selector.setFont(F_NORMAL);
        return selector;
    }

    private static LocalDate aLocalDate(Date fecha) {
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
 
    private static String moneda(double cantidad) {
        return cantidad == Math.floor(cantidad)
                ? String.format("$%,.0f", cantidad)
                : String.format("$%,.2f", cantidad);
    }
 
    private static class IconoLupa implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.6f));
            g2.drawOval(x + 1, y + 1, 10, 10);
            g2.drawLine(x + 10, y + 10, x + 14, y + 14);
            g2.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    }
}