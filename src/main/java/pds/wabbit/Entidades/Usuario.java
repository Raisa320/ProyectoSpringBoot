package pds.wabbit.Entidades;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pds.wabbit.Enumeraciones.Nivel;
import pds.wabbit.Enumeraciones.Rol;
import pds.wabbit.Enumeraciones.Sexo;

@Data
@Entity
public class Usuario {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @NotEmpty(message = "Debe Colocar un email")
    private String email;

    @NotEmpty(message = "Debe Colocar el nombre del Usuario")
    private String nombre;

    @NotEmpty(message = "Debe Colocar el apellido del Usuario")
    private String apellido;

    @NotEmpty(message = "Debe Colocar una clave de Usuario")
    private String clave;

    @Column(columnDefinition = "boolean default false")
    private Boolean eliminado = false;


    @Enumerated(EnumType.STRING)
    private Rol rol;

    @ElementCollection
    @MapKeyColumn(name = "lenguaje")
    @Column(name = "porcentaje")
    @CollectionTable(name = "usuario_lenguajes", joinColumns = @JoinColumn(name = "usuario_id"))
    @LazyCollection(LazyCollectionOption.FALSE)
    private Map<String, String> lenguajes = new HashMap<String, String>();

    @Column
    @ElementCollection(targetClass = String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> temas;

    @Enumerated(EnumType.STRING)
    private Nivel nivel;

    @Enumerated(EnumType.STRING)
    private Sexo sexo;

    private String avatar;

    private Integer reputacionAlta;
    private Integer reputacionMedia;
    private Integer reputacionBaja;
    
    private boolean cuentaVerificada;
    
    @Column
    @ElementCollection(targetClass = String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> proyectosGuardados;
    
}
