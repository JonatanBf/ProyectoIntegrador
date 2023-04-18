package ImagenLIst.demo.Dto;

import ImagenLIst.demo.entidades.Imagen;
import ImagenLIst.demo.entidades.Politicas;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductoDto {

    @NotBlank
    @Size(min=2 , max=250)
    private String nombre;

    @NotBlank
    @Size(min=2 , max=500)
    private String descripcion;

    private String latitud;

    private String longitud;

    @NotBlank
    @Size(min=2 , max=300)
    private String direccion;

    @NotNull
    @Size(min = 1)
    private List<Imagen> imagenes;

    @NotNull
    @Size(min = 1)
    private List<Long> caracteristicas;

    @NotNull
    private Long id_Ciudad;

    @NotNull
    private Long id_Categoria;

    @NotNull
    @Size(min = 1)
    private List<Politicas> politicas;

}
