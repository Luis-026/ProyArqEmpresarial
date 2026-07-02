package plazavea.proyarq;

import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import plazavea.proyarq.entity.Operario;
import plazavea.proyarq.entity.Pedido;
import plazavea.proyarq.entity.Producto;
import plazavea.proyarq.entity.Usuario;
import plazavea.proyarq.service.OperarioService;
import plazavea.proyarq.service.PedidoService;
import plazavea.proyarq.service.ProductoService;
import plazavea.proyarq.service.UsuarioService;

@SpringBootApplication
public class ProyarqApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyarqApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(
			UsuarioService usuarioService,
			ProductoService productoService,
			OperarioService operarioService,
			PedidoService pedidoService) {

		return args -> {
			if (usuarioService.findByEmail("admin@plazavea.com").isEmpty()) {
				usuarioService.save(Usuario.builder()
						.email("admin@plazavea.com")
						.password("admin123")
						.nombre("Administrador")
						.rol("Administrador")
						.activo(true)
						.build());
			}

            if (usuarioService.findByEmail("c26luis@gmail.com").isEmpty()) {
                usuarioService.save(Usuario.builder()
                        .email("c26luis@gmail.com")
                        .password("luis123")
                        .nombre("Luis")
                        .rol("Administrador")
                        .activo(true)
                        .build());
            }

            if (usuarioService.findByEmail("operariocarlos@gmail.com").isEmpty()) {
                usuarioService.save(Usuario.builder()
                        .email("operariocarlos@gmail.com")
                        .password("carlos123")
                        .nombre("Carlos")
                        .rol("Operario")
                        .activo(true)
                        .build());
            }

            if (usuarioService.findByEmail("jefesamuel@gmail.com").isEmpty()) {
                usuarioService.save(Usuario.builder()
                        .email("jefesamuel@gmail.com")
                        .password("samuel123")
                        .nombre("Samuel")
                        .rol("Jefe de Tienda")
                        .activo(true)
                        .build());
            }

            if (productoService.findAll().isEmpty()) {
                productoService.save(Producto.builder()
                        .nombre("Arroz Costeño 5kg")
                        .categoria("Abarrotes")
                        .stock(120)
                        .ubicacion("Pasillo 2 - Estante A")
                        .rfid("RFID-0012")
                        .build());
                productoService.save(Producto.builder()
                        .nombre("Leche Gloria 1L")
                        .categoria("Lácteos")
                        .stock(18)
                        .ubicacion("Pasillo 4 - Estante C")
                        .rfid("RFID-0045")
                        .build());
                productoService.save(Producto.builder()
                        .nombre("Aceite Primor 1L")
                        .categoria("Abarrotes")
                        .stock(8)
                        .ubicacion("Pasillo 3 - Estante B")
                        .rfid("RFID-0089")
                        .build());
            }

			if (operarioService.findAll().isEmpty()) {
				Operario ana = operarioService.save(Operario.builder()
						.nombre("Ana Torres")
						.puesto("Supervisor")
						.pedidosAsignados(24)
						.eficiencia(92.0)
						.estado("Activo")
						.ultimoIngreso("Hoy 08:12")
						.build());

				Operario carlos = operarioService.save(Operario.builder()
						.nombre("Carlos Rojas")
						.puesto("Repartidor")
						.pedidosAsignados(20)
						.eficiencia(90.0)
						.estado("Activo")
						.ultimoIngreso("Hoy 08:05")
						.build());

				pedidoService.save(Pedido.builder()
						.codigo("P000129")
						.cliente("María López")
						.totalProductos(5)
						.fechaPedido(LocalDateTime.now().minusHours(2))
						.estado("Pendiente")
						.canal("Web")
						.operario(null)
						.build());

				pedidoService.save(Pedido.builder()
						.codigo("P000128")
						.cliente("Pedro García")
						.totalProductos(8)
						.fechaPedido(LocalDateTime.now().minusHours(3))
						.estado("En preparación")
						.canal("App móvil")
						.operario(ana)
						.build());

				pedidoService.save(Pedido.builder()
						.codigo("P000126")
						.cliente("Carlos Díaz")
						.totalProductos(6)
						.fechaPedido(LocalDateTime.now().minusHours(4))
						.estado("Despachado")
						.canal("Tienda física")
						.operario(ana)
						.build());
			}
		};
	}
}
