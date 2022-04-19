
package pds.wabbit.Entidades;

import java.util.Calendar;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import pds.wabbit.Enumeraciones.EstadosProyecto;
import pds.wabbit.Enumeraciones.Temas;

@Data
@Entity
public class Proyecto {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @NotEmpty(message = "Debe Colocar un nombre")
    private String nombre;
    
    @Enumerated(EnumType.STRING)
    private Temas tema;
    
    @ManyToOne
    private Usuario usuarioCreador;
    
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Usuario> participantes;
    
    @Lob
    @Type(type = "text")
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "text")  //LINEA QUE SIRVE PARA GUARDAR EL TIPO DE DATO TEXT EN LA BD Y PUEDA RECIBIR UN COMENTARIO LARGO
    private String descripcion;
    
    @Column(columnDefinition = "boolean default false")
    private Boolean eliminado = false;
    
    @Column
    @ElementCollection(targetClass=String.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> lenguajes;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fechaCreacion;
    
    private Integer cupos;
    
    private Integer votos=0;
    
    @Enumerated(EnumType.STRING)
    private EstadosProyecto estadosProyecto;
    
    private String url;
    
    
    public String verificarParticipante(String idUsuario){
        String unido="false";
        if(this.participantes.stream().filter(user->user.getId().equals(idUsuario)).findFirst().isPresent()){
           unido="true";
        }
        return unido;
    }
    
    public String verificarPostulado(List<String> listado){
        String postula="false";
        if (listado.stream().filter(idP->idP.equalsIgnoreCase(this.id)).findFirst().isPresent()) {
            postula="true";
        }
        return postula;
    }
    
    public String verificarVoto(List<String> listado){
        String voto="false";
        if (listado.stream().filter(idP->idP.equalsIgnoreCase(this.id)).findFirst().isPresent()) {
            voto="true";
        }
        return voto;
    }
    
}
