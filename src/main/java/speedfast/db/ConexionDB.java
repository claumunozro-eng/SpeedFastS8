package speedfast.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza la conexión JDBC con MySQL.
 * Se pueden configurar variables de entorno para no dejar credenciales
 * en el código: SPEEDFAST_DB_URL, SPEEDFAST_DB_USER y SPEEDFAST_DB_PASSWORD.
 */
public final class ConexionDB {

    private static final String URL = System.getenv().getOrDefault(
            "SPEEDFAST_DB_URL",
            "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true");
    private static final String USER = System.getenv().getOrDefault("SPEEDFAST_DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("SPEEDFAST_DB_PASSWORD", "");

    private ConexionDB() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static String getUrl() {
        return URL;
    }
}
