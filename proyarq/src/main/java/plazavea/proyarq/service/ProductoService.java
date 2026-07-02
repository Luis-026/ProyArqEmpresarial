package plazavea.proyarq.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import plazavea.proyarq.entity.Producto;
import plazavea.proyarq.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> findByRfid(String rfid) {
        return productoRepository.findByRfid(rfid);
    }

    public Producto save(Producto producto) {
        if (producto.getCategoria() != null && !producto.getCategoria().isBlank()) {
            categoriaService.getOrCreate(producto.getCategoria());
        }
        producto.setEstado(computeEstado(producto.getStock()));
        return productoRepository.save(producto);
    }

    public Producto update(Long id, Producto producto) {
        Producto actual = productoRepository.findById(id).orElseThrow();
        actual.setNombre(producto.getNombre());
        actual.setCategoria(producto.getCategoria());
        actual.setStock(producto.getStock());
        actual.setUbicacion(producto.getUbicacion());
        actual.setRfid(producto.getRfid());
        actual.setEstado(computeEstado(producto.getStock()));
        if (producto.getCategoria() != null && !producto.getCategoria().isBlank()) {
            categoriaService.getOrCreate(producto.getCategoria());
        }
        return productoRepository.save(actual);
    }

    public void deleteById(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto updateStock(Long id, Integer stock) {
        Producto producto = productoRepository.findById(id).orElseThrow();
        producto.setStock(stock);
        producto.setEstado(computeEstado(stock));
        return productoRepository.save(producto);
    }

    private String computeEstado(Integer stock) {
        if (stock == null) {
            return "Desconocido";
        }
        if (stock <= 10) {
            return "Crítico";
        }
        if (stock <= 25) {
            return "Bajo stock";
        }
        return "Disponible";
    }
}
