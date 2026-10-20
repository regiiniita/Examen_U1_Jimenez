/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modeloPresentacion;

import dtos.ClienteDTO;
import dtos.ComprobanteDTO;
import dtos.HabitacionDTO;
import java.time.LocalDate;
import java.util.List;
import modeloNegocios.IModeloNegocios;

public class ControlModeloPresentacion {

    private IModeloNegocios negocio;
    private IObservadorReservas observador;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int numHuespedes;
    private String idClienteSeleccionado;

    public ControlModeloPresentacion(IModeloNegocios negocio) {
        this.negocio = negocio;
    }

    public void agregarObservador(IObservadorReservas observador) {
        this.observador = observador;
    }

    public void buscarHabitacionesDisponibles(LocalDate entrada, LocalDate salida, int huespedes) {
        if (!validarFechas(entrada, salida)) {
            return; 
        }
        if (huespedes <= 0) {
            observador.mostrarMensajeError("La cantidad de huéspedes debe ser mayor a 0.");
            return;
        }
        this.fechaEntrada = entrada;
        this.fechaSalida = salida;
        this.numHuespedes = huespedes;

        List<HabitacionDTO> disponibles = negocio.obtenerHabitacionesDisponibles(entrada, salida, huespedes);
        if (disponibles.isEmpty()) {
            observador.mostrarMensajeError("No hay habitaciones disponibles para esas fechas y huéspedes.");
            return;
        }
        observador.mostrarHabitacionesDisponibles(disponibles);
    }

    public void seleccionarHabitacion(int numero) {
        try {
            HabitacionDTO habitacion = negocio.obtenerHabitacion(numero);
            double importe = negocio.crearReserva(numero, fechaEntrada, fechaSalida, numHuespedes);
            observador.mostrarDetallesHabitacion(habitacion, importe);
        } catch (IllegalArgumentException e) {
            observador.mostrarMensajeError(e.getMessage());
        }
    }

    public void buscarClientes(String criterio) {
        List<ClienteDTO> clientes = negocio.buscarClientes(criterio);
        observador.mostrarClientesCoincidentes(clientes);
    }

    public void seleccionarCliente(String id) {
        try {
            ClienteDTO cliente = negocio.obtenerCliente(id);
            this.idClienteSeleccionado = id;
            observador.mostrarDatosCliente(cliente);
        } catch (IllegalArgumentException e) {
            observador.mostrarMensajeError(e.getMessage());
        }
    }

    public void confirmarReservacion() {
        if (idClienteSeleccionado == null) {
            observador.mostrarMensajeError("Seleccione un cliente antes de confirmar.");
            return;
        }
        try {
            ComprobanteDTO comprobante = negocio.confirmarReserva(idClienteSeleccionado);
            idClienteSeleccionado = null;
            observador.mostrarComprobante(comprobante);
        } catch (IllegalStateException | IllegalArgumentException e) {
            observador.mostrarMensajeError(e.getMessage());
        }
    }

    private boolean validarFechas(LocalDate entrada, LocalDate salida) {
        if (entrada.isBefore(LocalDate.now())) {
            observador.mostrarMensajeError("La fecha de entrada no es válida, ya pasó.");
            return false;
        }
        if (!salida.isAfter(entrada)) {
            observador.mostrarMensajeError("La fecha de salida debe ser posterior a la fecha de entrada.");
            return false;
        }
        return true;
    }
}
