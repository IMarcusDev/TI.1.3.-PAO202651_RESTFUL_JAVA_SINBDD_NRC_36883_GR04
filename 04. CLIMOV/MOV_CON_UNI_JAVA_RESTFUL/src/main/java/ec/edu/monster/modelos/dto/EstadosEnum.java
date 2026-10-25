package ec.edu.monster.modelos.dto;

public enum EstadosEnum {
    OK,
    ERROR;

    public static EstadosEnum desdeTexto(String texto) {
        if (texto == null) {
            return null;
        }
        for (EstadosEnum estado : values()) {
            if (estado.name().equals(texto.trim())) {
                return estado;
            }
        }
        return null;
    }
}
