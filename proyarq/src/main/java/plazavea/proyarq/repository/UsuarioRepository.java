package plazavea.proyarq.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import plazavea.proyarq.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}
