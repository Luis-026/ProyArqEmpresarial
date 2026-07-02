package plazavea.proyarq.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import plazavea.proyarq.entity.Producto;
import plazavea.proyarq.service.ProductoService;

@Controller
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    @ResponseBody
    public List<Producto> findAll() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Producto> findById(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Producto> create(@RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.save(producto));
    }

    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Producto> update(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.update(id, producto));
    }

    @PutMapping("/{id}/stock")
    @ResponseBody
    public ResponseEntity<Producto> updateStock(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.updateStock(id, producto.getStock()));
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
