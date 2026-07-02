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
import plazavea.proyarq.entity.Operario;
import plazavea.proyarq.service.OperarioService;

@Controller
@RequestMapping("/api/operarios")
public class OperarioController {

    private final OperarioService operarioService;

    public OperarioController(OperarioService operarioService) {
        this.operarioService = operarioService;
    }

    @GetMapping
    @ResponseBody
    public List<Operario> findAll() {
        return operarioService.findAll();
    }

    @GetMapping("/estado/{estado}")
    @ResponseBody
    public List<Operario> findByEstado(@PathVariable String estado) {
        return operarioService.findByEstado(estado);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Operario> create(@RequestBody Operario operario) {
        return ResponseEntity.ok(operarioService.save(operario));
    }

    @PutMapping
    @ResponseBody
    public ResponseEntity<Operario> update(@RequestBody Operario operario) {
        return ResponseEntity.ok(operarioService.update(operario));
    }
}
