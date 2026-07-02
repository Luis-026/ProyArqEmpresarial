package plazavea.proyarq.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import plazavea.proyarq.entity.Operario;

public interface OperarioRepository extends JpaRepository<Operario, Long> {
    List<Operario> findByEstado(String estado);
}
