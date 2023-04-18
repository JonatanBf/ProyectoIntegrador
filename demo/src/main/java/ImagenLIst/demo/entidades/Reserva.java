package ImagenLIst.demo.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "reservas")
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalTime horaComienzo;

    private LocalTime horaFinal;

    @NotNull
    private LocalDate fechaInicial;

    @NotNull
    private LocalDate fechaFinal;

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "producto_id")
    @JsonIgnoreProperties({"descripcion", "imagenes","caracteristicas","politicas","latitud","longitud"})
    private Producto producto;

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"nombre", "apellido","password","rol","ciudad"})
    private Cliente cliente;
}
