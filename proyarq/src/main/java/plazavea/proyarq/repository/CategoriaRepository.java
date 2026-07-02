package plazavea.proyarq.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import plazavea.proyarq.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNombre(String nombre);
}
