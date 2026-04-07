package projetoextensao;

// Bibliotecas para a conexão com o banco
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
	
	// Configurações do Banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/monitoramento_represa";
    private static final String USER = "root";
    private static final String PASSWORD = "";	

    public Connection getConect() {
        try {
            // Ele tenta criar e retornar a conexão viva
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
        	// Imprime o erro real (debug)
            System.err.println("Erro na conexão: " + e.getMessage());
            return null;
        }
    }

}
