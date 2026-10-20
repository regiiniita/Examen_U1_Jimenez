/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modeloNegocios;

import dtos.ClienteDTO;
import dtos.ComprobanteDTO;
import dtos.HabitacionDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author regin
 */
public class FachadaModeloNegocios implements IModeloNegocios {

    private List<Habitacion> habitaciones;
    private List<Cliente> clientes;
    private List<Reserva> reservas;
    private Reserva reservaEnCurso;

    public FachadaModeloNegocios() {
        this.habitaciones = new ArrayList<>();
        this.clientes = new ArrayList<>();
        this.reservas = new ArrayList<>();
        cargarDatos();
    }

    @Override
    public List<HabitacionDTO> obtenerHabitacionesDisponibles(LocalDate entrada, LocalDate salida, int huespedes) {
        List<HabitacionDTO> disponibles = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (h.estaDisponible(entrada, salida) && h.tieneCapacidad(huespedes)) {
                disponibles.add(convertirADTO(h));
            }
        }
        return disponibles;
    }

    @Override
    public HabitacionDTO obtenerHabitacion(int numero) {
        return convertirADTO(buscarHabitacionPorNumero(numero));
    }

    @Override
    public double crearReserva(int numero, LocalDate entrada, LocalDate salida, int huespedes) {
        Habitacion habitacion = buscarHabitacionPorNumero(numero);
        reservaEnCurso = new Reserva(habitacion, entrada, salida, huespedes);
        return reservaEnCurso.calcularImporteTotal();
    }

    @Override
    public List<ClienteDTO> buscarClientes(String criterio) {
        List<ClienteDTO> coincidentes = new ArrayList<>();
        for (Cliente c : clientes) {
            if (c.coincideConId(criterio)) {
                coincidentes.add(convertirADTO(c));
            }
        }
        return coincidentes;
    }

    @Override
    public ClienteDTO obtenerCliente(String id) {
        return convertirADTO(buscarClientePorId(id));
    }

    @Override
    public ComprobanteDTO confirmarReserva(String idCliente) {
        if (reservaEnCurso == null) {
            throw new IllegalStateException("No hay una reservación en curso.");
        }
        Cliente cliente = buscarClientePorId(idCliente);
        reservaEnCurso.asignarCliente(cliente);
        reservaEnCurso.getHabitacion().agregarReserva(reservaEnCurso);
        reservas.add(reservaEnCurso);

        ComprobanteDTO comprobante = generarComprobante(reservaEnCurso);
        reservaEnCurso = null;
        return comprobante;
    }

    private Habitacion buscarHabitacionPorNumero(int numero) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numero) {
                return h;
            }
        }
        throw new IllegalArgumentException("No existe la habitación " + numero);
    }

    private Cliente buscarClientePorId(String id) {
        for (Cliente c : clientes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No existe el cliente con ID " + id);
    }


    private HabitacionDTO convertirADTO(Habitacion h) {
        return new HabitacionDTO(h.getNumero(), h.getTipo(), h.getCapacidad(),
                h.getServicios(), h.getCostoPorNoche());
    }

    private ClienteDTO convertirADTO(Cliente c) {
        return new ClienteDTO(c.getId(), c.getNombre(), c.getTelefono(), c.getCorreo());
    }

    private ComprobanteDTO generarComprobante(Reserva r) {
        return new ComprobanteDTO(
                convertirADTO(r.getCliente()),
                r.getHabitacion().getNumero(),
                r.getHabitacion().getTipo(),
                r.getFechaEntrada(),
                r.getFechaSalida(),
                r.getImporteTotal());
    }

    private void cargarDatos() {
        habitaciones.add(new Habitacion(101, "Estándar Double", 2,
                List.of("Conexión a Wi-Fi", "Televisor con cable"), 100));
        habitaciones.add(new Habitacion(205, "Estándar Twin", 4,
                List.of("Cafetera", "Conexión a Wi-Fi", "Televisor con cable", "Plancha de ropa y burro"), 150));
        habitaciones.add(new Habitacion(303, "Comfort Twin", 4,
                List.of("Cafetera", "Conexión a Wi-Fi", "Televisor con cable", "Minibar"), 250));
        habitaciones.add(new Habitacion(400, "Deluxe Twin", 4,
                List.of("Cafetera", "Conexión a Wi-Fi", "Smart TV", "Minibar", "Jacuzzi"), 400));

        clientes.add(new Cliente("262705", "Regina Jiménez Meneses", "6442491867",
                "regina.jimenez262705@potros.itson.edu.mx"));
        clientes.add(new Cliente("262709", "Mariana Orduño Velazquez", "6440000001",
                "mariana.orduno@ejemplo.com"));
        clientes.add(new Cliente("262711", "Danna Sofia Piña Ibarra", "6440000002",
                "danna.pina@ejemplo.com"));
    }
}