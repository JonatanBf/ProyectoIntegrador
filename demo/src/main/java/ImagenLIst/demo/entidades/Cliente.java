package ImagenLIst.demo.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("Cliente")
@NoArgsConstructor
public class Cliente extends Usuario{

    // Atributos adicionales de la clase Cliente
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Reserva> reservas;

    public Cliente( String nombre, String apellido, String email, String password, Rol rol, Ciudad ciudad) {
        super(nombre, apellido, email, password, rol, ciudad);
        this.reservas = new ArrayList<>();
    }

}
