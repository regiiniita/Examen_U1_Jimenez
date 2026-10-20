/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
 
import java.time.LocalDate;
import modeloPresentacion.IOperacionesModeloPresentacion;
 
/**
 * 
 * @author regin
 */
public class ControlReservacion {
 
    private IOperacionesModeloPresentacion modelo;
 
    public ControlReservacion(IOperacionesModeloPresentacion modelo) {
        this.modelo = modelo;
    }
 
    public void ingresarDatosEstancia(LocalDate entrada, LocalDate salida, int huespedes) {
        modelo.buscarHabitacionesDisponibles(entrada, salida, huespedes);
    }
 
    public void seleccionarHabitacion(int numero) {
        modelo.seleccionarHabitacion(numero);
    }
 
    public void ingresarIdCliente(String criterio) {
        modelo.buscarClientes(criterio);
    }
 
    public void seleccionarCliente(String id) {
        modelo.seleccionarCliente(id);
    }
 
    public void confirmar() {
        modelo.confirmarReservacion();
    }
}