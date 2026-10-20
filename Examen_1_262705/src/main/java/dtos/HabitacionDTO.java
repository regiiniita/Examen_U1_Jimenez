/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dtos;

import java.util.List;

/**
 *
 * @author regin
 */
public class HabitacionDTO {
    private int numero;
    private String tipo;
    private int capacidad;
    private List<String> servicios;
    private double costoPorNoche;

    public HabitacionDTO() {
    }

    
    public HabitacionDTO(int numero, String tipo, int capacidad, List<String> servicios, double costoPorNoche) {
        this.numero = numero;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.servicios = servicios;
        this.costoPorNoche = costoPorNoche;
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
    
    
    
}
