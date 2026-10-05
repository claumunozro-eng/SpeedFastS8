package speedfast.dao;

import speedfast.db.ConexionDB;
import speedfast.model.Entrega;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de entregas. El listado usa JOIN para mostrar información legible
 * del pedido y del repartidor.
 */
public class EntregaDAO {

    public void create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) entrega.setId(rs.getInt(1));
            }
        }
    }

    public List<Entrega> readAll() throws SQLException {
        return readAll(null, null);
    }

    public List<Entrega> readAll(Integer idPedido, Integer idRepartidor) throws SQLException {
        List<Entrega> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora,
                       p.direccion, r.nombre
                FROM entregas e
                INNER JOIN pedidos p ON p.id = e.id_pedido
                INNER JOIN repartidores r ON r.id = e.id_repartidor
                WHERE 1=1
                """);
        if (idPedido != null) sql.append(" AND e.id_pedido = ?");
        if (idRepartidor != null) sql.append(" AND e.id_repartidor = ?");
        sql.append(" ORDER BY e.id");

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            int i = 1;
            if (idPedido != null) ps.setInt(i++, idPedido);
            if (idRepartidor != null) ps.setInt(i++, idRepartidor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Entrega e = new Entrega(
                            rs.getInt("id"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_repartidor"),
                            rs.getDate("fecha").toLocalDate(),
                            rs.getTime("hora").toLocalTime()
                    );
                    e.setDireccionPedido(rs.getString("direccion"));
                    e.setNombreRepartidor(rs.getString("nombre"));
                    lista.add(e);
                }
            }
        }
        return lista;
    }

    public void update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.setInt(5, entrega.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
