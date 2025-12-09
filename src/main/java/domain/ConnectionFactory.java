package domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    public Connection recuperarConexao() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/capital_cervantes?user=root&password=Lh!c5T*Z8GYVMT");
        } catch (SQLException e ) {
            throw new RuntimeException("Erro de conexão:" + e);
        }
    }
}
