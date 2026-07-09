package plazavea.proyarq.controller;

import jakarta.servlet.http.HttpSession;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import plazavea.proyarq.entity.Pedido;
import plazavea.proyarq.entity.Producto;
import plazavea.proyarq.entity.Operario;
import plazavea.proyarq.service.PedidoService;
import plazavea.proyarq.service.ProductoService;
import plazavea.proyarq.service.OperarioService;

@Controller
public class DashboardController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final OperarioService operarioService;

    public DashboardController(
            ProductoService productoService,
            PedidoService pedidoService,
            OperarioService operarioService) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.operarioService = operarioService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAllowed(session, "Administrador")) {
            return "redirect:/login";
        }

        List<Producto> productos = productoService.findAll();
        List<Pedido> pedidos = pedidoService.findAll();
        List<Operario> operarios = operarioService.findAll();

        long stockCritico = productos.stream().filter(p -> p.getStock() != null && p.getStock() <= 10).count();
        long pedidosActivos = pedidos.stream().filter(p -> !"Entregado".equalsIgnoreCase(p.getEstado())).count();
        long pedidosDespachados = pedidos.stream().filter(p -> "Despachado".equalsIgnoreCase(p.getEstado())).count();
        long cancelaciones = pedidos.stream().filter(p -> "Cancelado".equalsIgnoreCase(p.getEstado())).count();

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        model.addAttribute("productos", productos);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("operarios", operarios);
        model.addAttribute("pedidosActivos", pedidosActivos);
        model.addAttribute("stockCritico", stockCritico);
        model.addAttribute("cancelaciones", cancelaciones);
        model.addAttribute("pedidosDespachados", pedidosDespachados);
        model.addAttribute("pedidosRecientes", pedidos.stream().limit(4).toList());
        return "dashboard";
    }

    @GetMapping("/inventario")
    public String inventario(HttpSession session, Model model) {
        if (!isAllowed(session, "Administrador", "Jefe de Tienda")) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        model.addAttribute("productos", productoService.findAll());
        return "inventario";
    }

    @GetMapping("/pedidos")
    public String pedidos(HttpSession session, Model model) {
        if (!isAllowed(session, "Administrador", "Operario")) {
            return "redirect:/login";
        }

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        model.addAttribute("pedidos", pedidoService.findAll());
        return "pedidos";
    }

    @GetMapping("/operarios")
    public String operarios(HttpSession session, Model model) {
        if (!isAllowed(session, "Administrador", "Jefe de Tienda")) {
            return "redirect:/login";
        }

        List<Operario> operarios = operarioService.findAll();
        int operariosActivos = (int) operarios.stream()
                .filter(operario -> "Activo".equalsIgnoreCase(operario.getEstado()))
                .count();

        int tiempoMedio = 18;
        boolean hayTiempo = operarios.stream().anyMatch(operario -> operario.getPedidosAsignados() != null);
        if (hayTiempo) {
            double promedioPedidos = operarios.stream()
                    .mapToInt(operario -> operario.getPedidosAsignados() != null ? operario.getPedidosAsignados() : 0)
                    .average()
                    .orElse(0);
            tiempoMedio = promedioPedidos > 0 ? (int) Math.round(promedioPedidos * 2.5) : 18;
            tiempoMedio = Math.max(12, Math.min(45, tiempoMedio));
        }

        int satisfaccion = 92;
        boolean haySatisfaccion = operarios.stream().anyMatch(operario -> operario.getEficiencia() != null);
        if (haySatisfaccion) {
            double promedioEficiencia = operarios.stream()
                    .mapToDouble(operario -> operario.getEficiencia() != null ? operario.getEficiencia() : 0)
                    .average()
                    .orElse(0);
            satisfaccion = promedioEficiencia > 0 ? (int) Math.round(promedioEficiencia) : 92;
            satisfaccion = Math.max(70, Math.min(99, satisfaccion));
        }

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        model.addAttribute("operarios", operarios);
        model.addAttribute("operariosActivos", operarios.isEmpty() ? 24 : operariosActivos);
        model.addAttribute("tiempoMedio", operarios.isEmpty() ? 18 : tiempoMedio);
        model.addAttribute("satisfaccion", operarios.isEmpty() ? 92 : satisfaccion);
        return "operarios";
    }

    @GetMapping("/reportes")
    public String reportes(HttpSession session, Model model) {
        if (!isAllowed(session, "Administrador", "Jefe de Tienda")) {
            return "redirect:/login";
        }

        List<Producto> productos = productoService.findAll();
        List<Pedido> pedidos = pedidoService.findAll();
        List<Operario> operarios = operarioService.findAll();

        int totalProductos = productos.size();
        int stockCritico = (int) productos.stream().filter(p -> p.getStock() != null && p.getStock() <= 10).count();
        int pedidosPendientes = (int) pedidos.stream().filter(p -> "Pendiente".equalsIgnoreCase(p.getEstado())).count();
        int pedidosPreparacion = (int) pedidos.stream().filter(p -> "En preparación".equalsIgnoreCase(p.getEstado())).count();
        int pedidosDespachados = (int) pedidos.stream().filter(p -> "Despachado".equalsIgnoreCase(p.getEstado())).count();
        int pedidosEntregados = (int) pedidos.stream().filter(p -> "Entregado".equalsIgnoreCase(p.getEstado())).count();
        int pedidosCancelados = (int) pedidos.stream().filter(p -> "Cancelado".equalsIgnoreCase(p.getEstado())).count();
        int totalPedidos = pedidos.size();
        int totalProductosInventario = productos.stream().mapToInt(p -> p.getStock() != null ? p.getStock() : 0).sum();
        int promedioStock = totalProductos == 0 ? 0 : totalProductosInventario / totalProductos;
        int unidadesVendidas = pedidos.stream().mapToInt(p -> p.getTotalProductos() != null ? p.getTotalProductos() : 0).sum();
        int promedioPorPedido = totalPedidos == 0 ? 0 : Math.round(unidadesVendidas / (float) totalPedidos);

        double tasaCancelacion = totalPedidos == 0 ? 0 : (pedidosCancelados * 100.0) / totalPedidos;
        double rotacionInventario = totalProductos == 0 ? 0 : (totalProductosInventario / (double) Math.max(totalProductos, 1));

        Map<LocalDate, Integer> pedidosPorDia = new LinkedHashMap<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate dia = LocalDate.now().minusDays(i);
            int valor = pedidos.stream()
                    .filter(p -> p.getFechaPedido() != null && p.getFechaPedido().toLocalDate().equals(dia))
                    .mapToInt(p -> p.getTotalProductos() != null ? p.getTotalProductos() : 0)
                    .sum();
            pedidosPorDia.put(dia, valor);
        }

        List<String> salesLabels = new ArrayList<>();
        List<Integer> salesValues = new ArrayList<>();
        pedidosPorDia.forEach((dia, valor) -> {
            salesLabels.add(dia.format(DateTimeFormatter.ofPattern("dd/MM")));
            salesValues.add(valor);
        });

        Map<String, Integer> pedidosPorFranja = new LinkedHashMap<>();
        pedidosPorFranja.put("00-05", 0);
        pedidosPorFranja.put("06-11", 0);
        pedidosPorFranja.put("12-17", 0);
        pedidosPorFranja.put("18-23", 0);
        pedidos.forEach(pedido -> {
            if (pedido.getFechaPedido() == null) {
                return;
            }
            int hora = pedido.getFechaPedido().getHour();
            if (hora < 6) {
                pedidosPorFranja.merge("00-05", 1, Integer::sum);
            } else if (hora < 12) {
                pedidosPorFranja.merge("06-11", 1, Integer::sum);
            } else if (hora < 18) {
                pedidosPorFranja.merge("12-17", 1, Integer::sum);
            } else {
                pedidosPorFranja.merge("18-23", 1, Integer::sum);
            }
        });

        List<String> hourlyChartLabels = new ArrayList<>(pedidosPorFranja.keySet());
        List<Integer> hourlyChartValues = hourlyChartLabels.stream().map(pedidosPorFranja::get).toList();

        Map<String, Integer> stockPorCategoria = new LinkedHashMap<>();
        productos.forEach(producto -> {
            String categoria = producto.getCategoria() != null ? producto.getCategoria() : "Sin categoría";
            stockPorCategoria.merge(categoria, producto.getStock() != null ? producto.getStock() : 0, Integer::sum);
        });

        List<String> rotationLabels = new ArrayList<>(stockPorCategoria.keySet());
        List<Integer> rotationValues = rotationLabels.stream()
                .map(stockPorCategoria::get)
                .toList();

        List<String> cancellationLabels = new ArrayList<>();
        List<Integer> cancellationValues = new ArrayList<>();
        for (int i = 3; i >= 0; i--) {
            LocalDate inicioSemana = LocalDate.now().minusWeeks(i).with(DayOfWeek.MONDAY);
            LocalDate finSemana = inicioSemana.plusDays(6);
            int pedidosSemana = (int) pedidos.stream()
                    .filter(p -> p.getFechaPedido() != null
                            && !p.getFechaPedido().toLocalDate().isBefore(inicioSemana)
                            && !p.getFechaPedido().toLocalDate().isAfter(finSemana))
                    .count();
            int canceladosSemana = (int) pedidos.stream()
                    .filter(p -> p.getFechaPedido() != null
                            && !p.getFechaPedido().toLocalDate().isBefore(inicioSemana)
                            && !p.getFechaPedido().toLocalDate().isAfter(finSemana)
                            && "Cancelado".equalsIgnoreCase(p.getEstado()))
                    .count();
            int tasaSemana = pedidosSemana == 0 ? 0 : (int) Math.round((canceladosSemana * 100.0) / pedidosSemana);
            cancellationLabels.add("Sem " + (4 - i));
            cancellationValues.add(Math.min(100, Math.max(0, tasaSemana)));
        }

        List<Map<String, Object>> operariosResumen = operarios.stream().map(operario -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nombre", operario.getNombre());
            item.put("pedidosAsignados", operario.getPedidosAsignados() != null ? operario.getPedidosAsignados() : 0);
            item.put("completados", operario.getPedidos() != null
                    ? operario.getPedidos().stream().filter(p -> "Entregado".equalsIgnoreCase(p.getEstado()) || "Despachado".equalsIgnoreCase(p.getEstado())).count()
                    : 0);
            item.put("eficiencia", operario.getEficiencia() != null
                    ? String.format(Locale.US, "%.1f%%", operario.getEficiencia())
                    : "0.0%");
            return item;
        }).toList();

        model.addAttribute("usuarioNombre", session.getAttribute("usuarioNombre"));
        model.addAttribute("usuarioRol", session.getAttribute("usuarioRol"));
        model.addAttribute("productos", productos);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("operarios", operarios);
        model.addAttribute("ventasTotales", unidadesVendidas * 15);
        model.addAttribute("ventasTotalesTexto", String.format(Locale.US, "S/ %,d", unidadesVendidas * 15));
        model.addAttribute("promedioPorPedido", promedioPorPedido);
        model.addAttribute("promedioPorPedidoTexto", promedioPorPedido + " uds/pedido");
        model.addAttribute("rotacionInventario", rotacionInventario);
        model.addAttribute("rotacionInventarioTexto", String.format(Locale.US, "%.1f", rotacionInventario));
        model.addAttribute("tasaCancelacion", tasaCancelacion);
        model.addAttribute("tasaCancelacionTexto", String.format(Locale.US, "%.1f", tasaCancelacion));
        model.addAttribute("salesChartLabels", salesLabels);
        model.addAttribute("salesChartValues", salesValues);
        model.addAttribute("rotationChartLabels", rotationLabels);
        model.addAttribute("rotationChartValues", rotationValues);
        model.addAttribute("cancellationChartLabels", cancellationLabels);
        model.addAttribute("cancellationChartValues", cancellationValues);
        model.addAttribute("hourlyChartLabels", hourlyChartLabels);
        model.addAttribute("hourlyChartValues", hourlyChartValues);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosPreparacion", pedidosPreparacion);
        model.addAttribute("pedidosDespachados", pedidosDespachados);
        model.addAttribute("pedidosEntregados", pedidosEntregados);
        model.addAttribute("stockCritico", stockCritico);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalProductosInventario", totalProductosInventario);
        model.addAttribute("operariosResumen", operariosResumen);
        return "reportes";
    }

    private boolean isLogged(HttpSession session) {
        return session.getAttribute("usuarioNombre") != null;
    }

    private boolean isAllowed(HttpSession session, String... allowedRoles) {
        if (!isLogged(session)) {
            return false;
        }
        Object rolObj = session.getAttribute("usuarioRol");
        if (!(rolObj instanceof String)) {
            return false;
        }
        String rol = ((String) rolObj).trim();
        for (String allowed : allowedRoles) {
            if (rol.equalsIgnoreCase(allowed.trim())) {
                return true;
            }
        }
        return false;
    }
}
