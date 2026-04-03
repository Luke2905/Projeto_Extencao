package projetoextensao;

import java.sql.SQLException;

import javax.swing.JOptionPane;

public class DataBase {

	//Chamando a conexão do Banco de Dados
	Conexao con = new Conexao();
	
	
	public void listarDados() throws SQLException {
	
		//Script SQL
		String sql = "SELECT * FROM PRODUTOS;";
		
		// Metodos de conexao
		var consulta = con.getConect().prepareStatement(sql);
		
		// Execução do Script
		var resultado = consulta.executeQuery();
		
		String aux = "\nLista de Produtos\n\n";
		// resultado.next() pula para a próxima e retorna 'true' se ela existir.
	    while (resultado.next()) {
	        String n = resultado.getString("nome"); // Nome
	        String d = resultado.getString("descricao");
	        
	        aux += "Produto encontrado: " + n + " " + d + "\n";
	        
	        //System.out.println("Produto encontrado: " + n + " " + d);
	    }
	    
	    JOptionPane.showMessageDialog(null, aux);
	}
	
}
