package ImagenLIst.demo.entidades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "puntuacion")
@AllArgsConstructor
@NoArgsConstructor
public class Puntuacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "Id_Usuario")
    @JsonIgnoreProperties({"nombre", "apellido","password","rol","ciudad"})
    private Usuario usuario;

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "Id_Producto")
    @JsonIgnoreProperties({"descripcion", "imagenes","ciudad","caracteristicas","politicas","latitud","longitud","direccion"})
    private Producto producto;

    @Column
    private Long puntuacion;
}


