/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package modeloPresentacion;

import java.time.LocalDate;

/**
 *
 * @author regin
 */
public interface IOperacionesModeloPresentacion {
    void agregarObservador(IObservadorReservas observador);

    void buscarHabitacionesDisponibles(LocalDate entrada, LocalDate salida, int huespedes);

    void seleccionarHabitacion(int numero);

    void buscarClientes(String criterio);

    void seleccionarCliente(String id);

    void confirmarReservacion();
}
