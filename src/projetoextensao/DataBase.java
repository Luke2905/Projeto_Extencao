package projetoextensao;

import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel; // → Biblioteca de tabela

public class DataBase {

	//Chamando a conexão do Banco de Dados
	Conexao con = new Conexao();
	
	
	public void listarDados() throws SQLException {
		
		// Definição das colunas da tabela
		String[] colunas = {"Id", "Represa", "Data", "Chuva (mm)", "% Volume", " % Chuva Acumulada - Mês"};
		
		//Chamada da classe do modelo para tabela
		DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
	
		//Script SQL
		String sql = "SELECT\r\n"
				+ "	MT.id,\r\n"
				+ "    RP.nome,\r\n"
				+ "    date_format(MT.data, '%d/%m/%Y') AS data,\r\n"
				+ "    MT.chuva_mm,\r\n"
				+ "    MT.volume_util_percent,\r\n"
				+ "    MT.chuva_acumulada_mes_mm\r\n"
				+ "FROM MONITORAMENTO MT\r\n"
				+ "LEFT JOIN REPRESA RP ON RP.id = MT.id_represa";
		
		// Metodos de conexao
		var consulta = con.getConect().prepareStatement(sql);
		
		// Execução do Script
		var resultado = consulta.executeQuery();
		
		//String aux = "\nLista de Produtos\n\n";
		// resultado.next() pula para a próxima e retorna 'true' se ela existir.
	    while (resultado.next()) {
	    	
	    	// Objeto para a composição de linhas da tablea
	    	Object[] linha = {
	    			resultado.getInt("id"),
	    			resultado.getString("nome"),
	    			resultado.getString("data"),
	    			resultado.getDouble("chuva_mm"),
	    			resultado.getDouble("volume_util_percent"),
	    			resultado.getDouble("chuva_acumulada_mes_mm")
	    	};
	    	
	    	// Adiciona linha na tabela
	    	modelo.addRow(linha);
	    	/*
	        String n = resultado.getString("nome"); // Nome
	        String d = resultado.getString("descricao");
	       
	        aux += "Produto encontrado: " + n + " " + d + "\n";
	       */ 
	        //System.out.println("Produto encontrado: " + n + " " + d);
	    }
	    
	    // chamada da classe java para tabela e Scroll
	    JTable tabela = new JTable(modelo);
	    JScrollPane scroll = new JScrollPane(tabela);
	    
	    JOptionPane.showMessageDialog(null, scroll);
	}
	
}
