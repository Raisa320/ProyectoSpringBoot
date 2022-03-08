package pds.wabbit.Enumeraciones;


public enum Rol {
        DISENIADORUX("Diseñador UX/UI"), WEBDESIGNER("Maquetador Web"), 
    FRONTEND("Desarrollador Frontend"), BACKEND("Desarrollador Backend"), ADMINBD("Admin. BD");
    
    private final String value;
    
    Rol(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }
    
}
