package speedfast.concurrencia;

/** Tarea ejecutada por un hilo de repartidor. */
public class RepartidorConcurrente implements Runnable {
    private final String nombre;
    private final ZonaDeCarga zona;
    private final long tiempoEntregaMs;

    public RepartidorConcurrente(String nombre, ZonaDeCarga zona, long tiempoEntregaMs) {
        this.nombre=nombre;
        this.zona=zona;
        this.tiempoEntregaMs=tiempoEntregaMs;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            PedidoConcurrente pedido=zona.retirarPedido();
            if(pedido==null){
                System.out.printf("[%s] No quedan pedidos. Fin de turno.%n",nombre);
                return;
            }
            pedido.setEstado(EstadoPedidoConcurrente.EN_REPARTO);
            System.out.printf("[%s] Retiró pedido %d para %s. Estado: %s%n",
                    nombre,pedido.getId(),pedido.getDireccion(),pedido.getEstado());
            try{
                Thread.sleep(tiempoEntregaMs);
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                System.out.printf("[%s] Pedido %d interrumpido.%n",nombre,pedido.getId());
                return;
            }
            pedido.setEstado(EstadoPedidoConcurrente.ENTREGADO);
            System.out.printf("[%s] Entregó pedido %d. Estado: %s%n",
                    nombre,pedido.getId(),pedido.getEstado());
        }
    }
}
