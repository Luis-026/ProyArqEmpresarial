package plazavea.proyarq.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "operarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Operario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String puesto;

    private Integer pedidosAsignados;

    private Double eficiencia;

    @Column(nullable = false)
    private String estado;

    private String ultimoIngreso;

    @JsonIgnore
    @OneToMany(mappedBy = "operario", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();
}
