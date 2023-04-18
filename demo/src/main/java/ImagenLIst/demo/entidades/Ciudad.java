package ImagenLIst.demo.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ciudades")
@AllArgsConstructor
@NoArgsConstructor
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=2 , max=45)
    @NotBlank
    @Column(name="Nombre", unique = true)
    private String nombre;

    @Size(min=2 , max=45)
    @NotBlank
    @Column(name="Pais")
    private String pais;


}
