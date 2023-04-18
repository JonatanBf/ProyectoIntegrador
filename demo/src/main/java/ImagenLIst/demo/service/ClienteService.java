package ImagenLIst.demo.service;

import ImagenLIst.demo.entidades.Cliente;
import ImagenLIst.demo.repository.ClienteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClienteService {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;

    public Cliente agregar(Long idUsuario) {
        var usuario = usuarioService.buscarPorId(idUsuario).get();
        Cliente cliente = new Cliente(usuario.getNombre(), usuario.getApellido(), usuario.getEmail(), usuario.getPassword(), usuario.getRol(),usuario.getCiudad());
        cliente.setReservas(new ArrayList<>()); // Inicializar lista de reservas vacía
        return clienteRepository.save(cliente);
    }

    public void modificar(Cliente cliente, Long id) {

        var clienteGuardada = clienteRepository.findById(id);

        if (clienteGuardada.isPresent() ) {

            var usuario = usuarioService.buscarPorId(id).get();

            var correoRepetido = clienteRepository.findByEmail(usuario.getEmail());
            if (correoRepetido.isEmpty()){
                Cliente clienteUpdate = new Cliente(usuario.getNombre(), usuario.getApellido(), usuario.getEmail(), usuario.getPassword(), usuario.getRol(), usuario.getCiudad());
                clienteUpdate.setReservas(new ArrayList<>());
                clienteRepository.save(clienteUpdate);
            }
            }
    }


    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> findByEmail(String email){
        return clienteRepository.findByEmail(email);
    }

    public void eliminar(Long  id) {
        clienteRepository.deleteById(id);
    }

}
