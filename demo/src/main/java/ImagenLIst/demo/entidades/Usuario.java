package ImagenLIst.demo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=2 , max=45)
    @NotBlank
    @Column(name="Nombre")
    private String nombre;

    @Size(min=2 , max=45)
    @NotBlank
    @Column(name="Apellido")
    private String apellido;

    @Size(min=2 , max=45)
    @NotBlank
    @Email
    @Column(name="Email")
    private String email;

    @Size(min=2 , max=500)
    @NotBlank
    @Column(name="contraseña")
    private String password;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "ciudad_id")
    private Ciudad ciudad;

    public Usuario(String nombre, String apellido, String email, String password, Rol rol, Ciudad ciudad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.ciudad = ciudad;
    }
}
