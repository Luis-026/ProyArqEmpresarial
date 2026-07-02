package plazavea.proyarq.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import plazavea.proyarq.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByRfid(String rfid);
}
