package speedfast.dao;

import speedfast.db.ConexionDB;
import speedfast.model.EstadoPedido;
import speedfast.model.Pedido;
import speedfast.model.TipoPedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de pedidos con CRUD y filtros opcionales por estado y tipo.
 */
public class PedidoDAO {

    public void create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) pedido.setId(rs.getInt(1));
            }
        }
    }

    public List<Pedido> readAll() throws SQLException {
        return readAll(null, null);
    }

    public List<Pedido> readAll(EstadoPedido estado, TipoPedido tipo) throws SQLException {
        List<Pedido> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT id, direccion, tipo, estado FROM pedidos WHERE 1=1");
        List<String> filtros = new ArrayList<>();

        if (estado != null) {
            sql.append(" AND estado = ?");
            filtros.add(estado.name());
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
            filtros.add(tipo.name());
        }
        sql.append(" ORDER BY id");

        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < filtros.size(); i++) ps.setString(i + 1, filtros.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pedido(
                            rs.getInt("id"),
                            rs.getString("direccion"),
                            TipoPedido.valueOf(rs.getString("tipo")),
                            EstadoPedido.valueOf(rs.getString("estado"))
                    ));
                }
            }
        }
        return lista;
    }

    public void update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection cn = ConexionDB.conectar();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
