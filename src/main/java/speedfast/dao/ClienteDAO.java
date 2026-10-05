package speedfast.dao;

/**
 * Adaptador de compatibilidad con la pauta de la actividad, que menciona
 * "ClienteDAO". El caso funcional y el esquema entregado por la asignatura
 * trabajan con la entidad repartidores, por lo que la implementación real
 * y utilizada por la interfaz es RepartidorDAO.
 */
@Deprecated
public class ClienteDAO extends RepartidorDAO {
}
