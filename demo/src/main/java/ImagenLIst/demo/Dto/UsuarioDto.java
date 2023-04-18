package ImagenLIst.demo.Dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDto {

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
    @Column(name="Email", unique = true)
    private String email;

    @Size(min=2 , max=500)
    @NotBlank
    @Column(name="contraseña")
    private String password;


    @NotNull
    private Long rolId;

    @NotNull
    private Long id_Ciudad;
}
