package apifestivos.apifestivos.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tipo")
public class Tipo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo", unique = true, length = 100)
    private String tipo;

    public Tipo() {
    }

    public Tipo(Integer id, String tipo) {
        this.id = id;
        this.tipo = tipo;
    }

}
