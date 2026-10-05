package speedfast.concurrencia;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Recurso compartido de la Semana 5. Los métodos sincronizados garantizan
 * que dos hilos no retiren el mismo pedido.
 */
public class ZonaDeCarga {
    private final Queue<PedidoConcurrente> pedidosPendientes = new LinkedList<>();

    public synchronized void agregarPedido(PedidoConcurrente pedido) {
        if (pedido == null) throw new IllegalArgumentException("El pedido no puede ser nulo.");
        pedidosPendientes.offer(pedido);
        System.out.printf("[Zona de carga] Ingresó pedido %d. Pendientes: %d%n",
                pedido.getId(), pedidosPendientes.size());
    }

    public synchronized PedidoConcurrente retirarPedido() {
        return pedidosPendientes.poll();
    }

    public synchronized int cantidadPendientes() {
        return pedidosPendientes.size();
    }
}
