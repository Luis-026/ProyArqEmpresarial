package plazavea.proyarq.service;

import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Categoria;
import plazavea.proyarq.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Categoria getOrCreate(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }

        String valor = nombre.trim();
        return categoriaRepository.findByNombre(valor)
                .orElseGet(() -> categoriaRepository.save(Categoria.builder().nombre(valor).build()));
    }
}
