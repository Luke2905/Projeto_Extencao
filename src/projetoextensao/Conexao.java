package projetoextensao;

// Bibliotecas para a conexão com o banco
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
	
	// Configurações do Banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/monitoramento_represa";
    private static final String USER = "root";
    private static final String PASSWORD = "root";	

  public Connection getConect() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);

    } catch (ClassNotFoundException e) {
        System.err.println("Driver MySQL não encontrado: " + e.getMessage());

    } catch (SQLException e) {
        System.err.println("Erro na conexão: " + e.getMessage());
    }

    return null;
}

}
