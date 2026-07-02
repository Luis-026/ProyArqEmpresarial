package plazavea.proyarq.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import plazavea.proyarq.entity.Pedido;
import plazavea.proyarq.service.PedidoService;

@Controller
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    @ResponseBody
    public List<Pedido> findAll() {
        return pedidoService.findAll();
    }

    @GetMapping("/estado/{estado}")
    @ResponseBody
    public List<Pedido> findByEstado(@PathVariable String estado) {
        return pedidoService.findByEstado(estado);
    }

    @GetMapping("/canal/{canal}")
    @ResponseBody
    public List<Pedido> findByCanal(@PathVariable String canal) {
        return pedidoService.findByCanal(canal);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Pedido> create(@RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Pedido> update(@RequestBody Pedido pedido) {
        return ResponseEntity.ok(pedidoService.update(pedido));
    }
}
