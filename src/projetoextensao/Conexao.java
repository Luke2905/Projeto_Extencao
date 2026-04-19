package projetoextensao;

// Bibliotecas para a conexao com o banco
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

	// Configuracoes do banco de dados
	private static final String URL = "jdbc:mysql://localhost:3306/monitoramento_represa";
	private static final String USER = "root";
	private static final String PASSWORD = "9651luke";

	public Connection getConect() {
		try {
			// Garante carregamento do driver JDBC mesmo em ambientes antigos
			Class.forName("com.mysql.cj.jdbc.Driver");

			// Ele tenta criar e retornar a conexao viva
			return DriverManager.getConnection(URL, USER, PASSWORD);
		} catch (ClassNotFoundException e) {
			System.err.println("Driver MySQL nao encontrado: " + e.getMessage());
		} catch (SQLException e) {
			// Imprime o erro real 
			System.err.println("Erro na conexao: " + e.getMessage());
		}
		return null;
	}
}
