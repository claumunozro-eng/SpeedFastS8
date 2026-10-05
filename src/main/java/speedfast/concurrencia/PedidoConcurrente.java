package speedfast.concurrencia;

public class PedidoConcurrente {
    private final int id;
    private final String direccion;
    private EstadoPedidoConcurrente estado = EstadoPedidoConcurrente.PENDIENTE;

    public PedidoConcurrente(int id, String direccion) {
        this.id=id;
        this.direccion=direccion;
    }
    public int getId(){return id;}
    public String getDireccion(){return direccion;}
    public EstadoPedidoConcurrente getEstado(){return estado;}
    public void setEstado(EstadoPedidoConcurrente estado){this.estado=estado;}
}
