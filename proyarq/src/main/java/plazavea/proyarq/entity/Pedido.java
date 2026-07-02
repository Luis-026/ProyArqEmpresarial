package plazavea.proyarq.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String cliente;

    @Column(nullable = false)
    private Integer totalProductos;

    @Column(nullable = false)
    private LocalDateTime fechaPedido;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String canal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operario_id")
    @JsonIgnoreProperties({"pedidos"})
    private Operario operario;
}
