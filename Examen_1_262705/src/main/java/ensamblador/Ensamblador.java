/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ensamblador;

import controlador.ControlReservacion;
import modeloNegocios.FachadaModeloNegocios;
import modeloNegocios.IModeloNegocios;
import modeloPresentacion.FachadaModeloPresentacion;
import modeloPresentacion.IOperacionesModeloPresentacion;
import vista.FrmGestionReservas;

/**
 *
 * @author regin
 */
public class Ensamblador {

    public FrmGestionReservas ensamblar() {
        IModeloNegocios negocio = new FachadaModeloNegocios();
        IOperacionesModeloPresentacion modelo = new FachadaModeloPresentacion(negocio);
        ControlReservacion control = new ControlReservacion(modelo);
        FrmGestionReservas vista = new FrmGestionReservas(control);
        modelo.agregarObservador(vista);
        return vista;
    }
}