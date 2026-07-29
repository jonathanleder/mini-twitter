package unrn.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import unrn.model.Usuario;

import java.util.Optional;

interface MongoUsuarioRepository extends MongoRepository<Usuario, Long> {
    Optional<Usuario> findByUserName(String userName);
}
