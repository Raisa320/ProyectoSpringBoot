package pds.wabbit.Entidades;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
public class Recurso {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @ManyToOne
    private Proyecto proyecto;
    
    private String titulo;
    
    @ManyToOne
    private Usuario usuario;
    
    private String urlWeb;
    
    private String nameFile;
    
   
}
