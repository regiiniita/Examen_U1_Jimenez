/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modeloNegocios;

import dtos.ClienteDTO;
import dtos.ComprobanteDTO;
import dtos.HabitacionDTO;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author regin
 */
public interface IModeloNegocios {
    public List<HabitacionDTO>obtenerHabitacionesDisponibles(LocalDate entrada, LocalDate salida, int huespuedes);
    public HabitacionDTO obtenerHabitacion(int numero);
    public double crearReserva(int numero, LocalDate entrada, LocalDate salida, int huespedes);
    public List<ClienteDTO> buscarClientes(String id);
    public ComprobanteDTO confirmarReserva(String idCliente);
    public ClienteDTO obtenerCliente(String id);
    
}
