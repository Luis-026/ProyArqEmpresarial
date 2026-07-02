package plazavea.proyarq.service;

import java.util.List;
import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Pedido;
import plazavea.proyarq.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public List<Pedido> findByEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findByCanal(String canal) {
        return pedidoRepository.findByCanal(canal);
    }

    public Pedido save(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido update(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}
