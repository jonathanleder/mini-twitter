
package unrn.repositorios;

import java.util.List;

import unrn.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> buscarPorUserName(String userName);
    void agregar(Usuario usuario);
    void eliminar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
}
