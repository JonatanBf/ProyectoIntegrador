package ImagenLIst.demo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="Nombre")
    private String nombre;

    @Column(name="Descripcion")
    private String descripcion;

    private String latitud;

    private String longitud;

    @NotBlank
    @Size(min=2 , max=300)
    private String direccion;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "imagen_id")
    private List<Imagen> imagenes;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "ciudad_id")
    private Ciudad ciudad;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinTable(
            name = "producto_caracteristica",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "caracteristica_id"))
    private List<Caracteristicas> caracteristicas = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "politicas_id")
    private List<Politicas> politicas = new ArrayList<>();

}
