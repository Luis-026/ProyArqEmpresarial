package plazavea.proyarq.service;

import java.util.List;
import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Operario;
import plazavea.proyarq.repository.OperarioRepository;

@Service
public class OperarioService {

    private final OperarioRepository operarioRepository;

    public OperarioService(OperarioRepository operarioRepository) {
        this.operarioRepository = operarioRepository;
    }

    public List<Operario> findAll() {
        return operarioRepository.findAll();
    }

    public List<Operario> findByEstado(String estado) {
        return operarioRepository.findByEstado(estado);
    }

    public Operario save(Operario operario) {
        return operarioRepository.save(operario);
    }

    public Operario update(Operario operario) {
        return operarioRepository.save(operario);
    }
}
