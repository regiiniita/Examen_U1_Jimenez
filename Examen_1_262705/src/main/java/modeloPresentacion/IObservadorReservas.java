/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modeloPresentacion;

import dtos.ClienteDTO;
import dtos.ComprobanteDTO;
import dtos.HabitacionDTO;
import java.util.List;

/**
 *
 * @author regin
 */
public interface IObservadorReservas {
    void mostrarHabitacionesDisponibles(List<HabitacionDTO> habitaciones);

    void mostrarDetallesHabitacion(HabitacionDTO habitacion, double importeTotal);

    void mostrarClientesCoincidentes(List<ClienteDTO> clientes);

    void mostrarDatosCliente(ClienteDTO cliente);

    void mostrarComprobante(ComprobanteDTO comprobante);

    void mostrarMensajeError(String mensaje);
}
