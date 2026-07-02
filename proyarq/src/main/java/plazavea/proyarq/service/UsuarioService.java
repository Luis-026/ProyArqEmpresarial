package plazavea.proyarq.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Usuario;
import plazavea.proyarq.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
