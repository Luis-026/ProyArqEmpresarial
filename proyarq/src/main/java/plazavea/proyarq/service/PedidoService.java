package plazavea.proyarq.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Operario;
import plazavea.proyarq.entity.Pedido;
import plazavea.proyarq.repository.OperarioRepository;
import plazavea.proyarq.repository.PedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final OperarioRepository operarioRepository;

    public PedidoService(PedidoRepository pedidoRepository, OperarioRepository operarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.operarioRepository = operarioRepository;
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
        if (pedido.getOperario() == null) {
            asignarOperarioAutomatico(pedido);
        }
        return pedidoRepository.save(pedido);
    }

    private void asignarOperarioAutomatico(Pedido pedido) {
        List<Operario> operarios = operarioRepository.findAll().stream()
                .filter(operario -> operario != null && "Activo".equalsIgnoreCase(operario.getEstado()))
                .sorted(Comparator.comparing(Operario::getId))
                .toList();

        if (operarios.isEmpty()) {
            return;
        }

        long totalPedidos = pedidoRepository.count();
        int indice = (int) (totalPedidos % operarios.size());
        Operario operarioAsignado = operarios.get(indice);

        if (operarioAsignado.getPedidosAsignados() == null) {
            operarioAsignado.setPedidosAsignados(0);
        }
        operarioAsignado.setPedidosAsignados(operarioAsignado.getPedidosAsignados() + 1);
        pedido.setOperario(operarioAsignado);
        operarioRepository.save(operarioAsignado);
    }

    public Pedido update(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }
}
