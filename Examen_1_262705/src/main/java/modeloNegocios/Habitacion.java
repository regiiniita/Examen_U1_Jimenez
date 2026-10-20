/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modeloNegocios;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author regin
 */
public class Habitacion {
    private int numero;
    private String tipo;
    private int capacidad;
    private List<String> servicios;
    private double costoPorNoche;
    private List<Reserva> reservas = new ArrayList<>();

    public Habitacion() {
        
    }

    public Habitacion(int numero, String tipo, int capacidad, List<String> servicios, double costoPorNoche) {
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.servicios = servicios;
        this.costoPorNoche = costoPorNoche;
    }
    
    public boolean estaDisponible(LocalDate entrada, LocalDate salida) {
        for (Reserva r : reservas) {
            // la nueva empieza antes de que la otra termine, y termina después de que la otra empiece
            if (entrada.isBefore(r.getFechaSalida()) && salida.isAfter(r.getFechaEntrada())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean tieneCapacidad(int numHuespedes){
        return numHuespedes <= capacidad;
    }
    
    public void agregarReserva(Reserva reserva){
        reservas.add(reserva);
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public List<String> getServicios() {
        return servicios;
    }

    public void setServicios(List<String> servicios) {
        this.servicios = servicios;
    }

    public double getCostoPorNoche() {
        return costoPorNoche;
    }

    public void setCostoPorNoche(double costoPorNoche) {
        this.costoPorNoche = costoPorNoche;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }
    
    
}
