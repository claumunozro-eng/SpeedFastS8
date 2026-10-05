package speedfast.concurrencia;

/**
 * Tres repartidores atienden una cola
 * compartida de forma concurrente.
 */
public final class SimulacionConcurrente {
    private SimulacionConcurrente(){}

    public static void ejecutar() {
        ZonaDeCarga zona=new ZonaDeCarga();
        zona.agregarPedido(new PedidoConcurrente(101,"Av. Apoquindo 4500, Las Condes"));
        zona.agregarPedido(new PedidoConcurrente(102,"San Diego 850, Santiago"));
        zona.agregarPedido(new PedidoConcurrente(103,"Vicuña Mackenna 2200, Ñuñoa"));
        zona.agregarPedido(new PedidoConcurrente(104,"Av. Providencia 1300, Providencia"));
        zona.agregarPedido(new PedidoConcurrente(105,"Gran Avenida 5200, San Miguel"));

        Thread ana=new Thread(new RepartidorConcurrente("Ana",zona,500),"Hilo-Ana");
        Thread bruno=new Thread(new RepartidorConcurrente("Bruno",zona,700),"Hilo-Bruno");
        Thread carla=new Thread(new RepartidorConcurrente("Carla",zona,900),"Hilo-Carla");

        System.out.println("\n--- Inicio de despachos concurrentes ---");
        ana.start();bruno.start();carla.start();

        try{
            ana.join();bruno.join();carla.join();
            System.out.println("\nTodos los pedidos concurrentes han sido procesados.");
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            System.out.println("La simulación fue interrumpida.");
        }
    }
}
