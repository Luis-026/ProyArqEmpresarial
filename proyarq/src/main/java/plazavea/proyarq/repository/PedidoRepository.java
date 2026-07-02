package plazavea.proyarq.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import plazavea.proyarq.entity.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByEstado(String estado);
    List<Pedido> findByCanal(String canal);
}
