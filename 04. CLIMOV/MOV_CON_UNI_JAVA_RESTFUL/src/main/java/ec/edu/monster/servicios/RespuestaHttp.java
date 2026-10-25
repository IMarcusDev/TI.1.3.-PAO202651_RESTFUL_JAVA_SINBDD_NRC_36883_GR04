package ec.edu.monster.servicios;

public class RespuestaHttp {
    private final int codigo;
    private final String razon;
    private final String cuerpo;

    public RespuestaHttp(int codigo, String razon, String cuerpo) {
        this.codigo = codigo;
        this.razon = razon;
        this.cuerpo = cuerpo != null ? cuerpo : "";
    }

    public int getCodigo() {
        return codigo;
    }

    public String getRazon() {
        return razon;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public boolean esExitosa() {
        return codigo >= 200 && codigo <= 299;
    }
}
