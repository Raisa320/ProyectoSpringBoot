package pds.wabbit.Enumeraciones;

public enum Temas {
    Web("Web","Consisten en el desarrollo de un software complejo que requiere la utilización de uno o más lenguajes de programación y conocer la estructura de la programación web"),
    Mobile("Mobile","Se refiere al desarrollo de aplicaciones para teléfonos celulares. Existen dos grandes ramas ‒de nuevo con las divisiones‒ que dependen del sistema operativo del celular: IOS de Apple y Android de Google."),
    MachineLearning("Machine Learning","Rama de la inteligencia artificial (IA) que se ocupa del aprendizaje automático a partir del suministro de grandes volúmenes de datos"), 
    Videojuegos("Videojuegos","El desarrollo de videojuegos se compone de diversos perfiles, la programación es solo una pequeña parte. El proceso se compone del diseño, la creación de escenarios y personajes, storytellers, etcétera."), 
    Embebida("Programación Embebida","Se trata de programas sencillos que están incorporados a una placa electrónica o chip, de allí su nombre «embebidos». Por lo general, se encuentran  instalados en electrodomésticos."), 
    Desktop("Programación Desktop","Se llama «desktop» o de escritorio porque son sistemas que requieren ser instalados en el sistema operativo de la computadora de la persona."), 
    SO("Sistemas Operativos","Se trata del desarrollo y/o mantenimiento de sistemas operativos. Ya hemos mencionado algunos: Windows, Linux, IOS, Android, etcétera. Se trata del software que más cerca está del hardware."), 
    SI("Seguridad informática","Se enfoca en la protección de la infraestructura computacional y todo lo vinculado con la misma, y especialmente la información contenida en una computadora o en la red.");
    
    private final String value;
    private final String descrip;
    
    Temas(String value,String descrip){
        this.value=value;
        this.descrip=descrip;
    }

    public String getValue() {
        return value;
    }
    
    public String getDescrip() {
        return descrip;
    }
}
