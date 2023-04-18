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
@Table(name = "categoria")
@AllArgsConstructor
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min=2 , max=45)
    @NotBlank
    @Column(unique = true)
    private String titulo;

    @Size(min=2 , max=250)
    @NotBlank
    @Column
    private String descripcion;

    @Size(min=2 , max=250)
    @NotBlank
    @Column
    private String url;

}
