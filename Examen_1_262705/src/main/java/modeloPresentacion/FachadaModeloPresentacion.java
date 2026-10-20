/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modeloPresentacion;

import java.time.LocalDate;
import modeloNegocios.IModeloNegocios;

public class FachadaModeloPresentacion implements IOperacionesModeloPresentacion {

    private ControlModeloPresentacion control;

    public FachadaModeloPresentacion(IModeloNegocios negocio) {
        this.control = new ControlModeloPresentacion(negocio);
    }

    @Override
    public void agregarObservador(IObservadorReservas observador) {
        control.agregarObservador(observador);
    }

    @Override
    public void buscarHabitacionesDisponibles(LocalDate entrada, LocalDate salida, int huespedes) {
        control.buscarHabitacionesDisponibles(entrada, salida, huespedes);
    }

    @Override
    public void seleccionarHabitacion(int numero) {
        control.seleccionarHabitacion(numero);
    }

    @Override
    public void buscarClientes(String criterio) {
        control.buscarClientes(criterio);
    }

    @Override
    public void seleccionarCliente(String id) {
        control.seleccionarCliente(id);
    }

    @Override
    public void confirmarReservacion() {
        control.confirmarReservacion();
    }
}