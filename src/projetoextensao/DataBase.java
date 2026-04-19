package projetoextensao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
				+ "LEFT JOIN REPRESA RP ON RP.id = MT.id_represa\r\n"
				+ "ORDER BY MT.data DESC;";
			
			// Abre a conexão e garante que que ela seja fechada 
			try (Connection minhaConexao = con.getConect();
				// Metodos de conexao
			     var consulta = minhaConexao.prepareStatement(sql);
				// Execução do Script
			     var resultado = consulta.executeQuery()) {
			 
					// resultado.next() pula para a próxima e retorna 'true' se ela existir.
				    while (resultado.next()) {
				    	
				    	// Objeto para a composição de linhas da tablea
				    	Object[] linha = {
				    			resultado.getInt("id"),
				    			resultado.getString("nome"),
				    			resultado.getString("data"),
				    			String.format("%.2f", resultado.getDouble("chuva_mm")), // Formata para 2 casas decimais 
				    		    String.format("%.2f", resultado.getDouble("volume_util_percent")),
				    		    String.format("%.2f", resultado.getDouble("chuva_acumulada_mes_mm"))
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
				    
				    JOptionPane.showMessageDialog(null, scroll, "Relatório de Monitoramento de Represas", JOptionPane.PLAIN_MESSAGE);

				    //Chama a ela
				    //Tela tela = new Tela(modelo);
				   // tela.setVisible(true);
		
			} catch (SQLException e) {
					 
				        JOptionPane.showMessageDialog(null, "Erro ao carregar tabela: " + e.getMessage());
				 }
	
		
		}
		
		public void totalDados() throws SQLException {
			
			//Total de Registros
			String sql = "SELECT COUNT(*) AS TOTAL FROM MONITORAMENTO;";
			
			
			Connection minhaConexao = con.getConect();
			
			// Metodos de conexao
		     var consulta = minhaConexao.prepareStatement(sql);
			// Execução do Script
		     var resultado = consulta.executeQuery();
		     
		     //Exibe resultado
		     if (resultado.next()) {
		    	    int total = resultado.getInt("TOTAL");
		    	    JOptionPane.showMessageDialog(null, "Total de registros no sistema: " + total);
		    }
	
		    	// Fecha a conexão
		    	resultado.close();
		    	consulta.close();
		    	minhaConexao.close();
			
		}
		
		public void mediaDados() throws SQLException {
			
			//Total de Registros
			String sql = "SELECT AVG(chuva_mm) AS media FROM MONITORAMENTO;";
			
			
			Connection minhaConexao = con.getConect();
			
			// Metodos de conexao
		     var consulta = minhaConexao.prepareStatement(sql);
			// Execução do Script
		     var resultado = consulta.executeQuery();
		     
		     //Exibe resultado
		     if (resultado.next()) {
		    	    int total = resultado.getInt("media");
		    	    JOptionPane.showMessageDialog(null, "Média Histórica do volume de Chuva em mm: " + total);
		    }
	
		    	// Fecha a conexão
		    	resultado.close();
		    	consulta.close();
		    	minhaConexao.close();
			
		}
		
		public void menorRegistro() throws SQLException {
			
			//Total de Registros
			String sql = "SELECT \r\n"
					+ "    MT.id,\r\n"
					+ "    RP.nome,\r\n"
					+ "    DATE_FORMAT(MT.data, '%d/%m/%Y') AS data,\r\n"
					+ "    MT.volume_util_percent AS volume \r\n"
					+ "FROM MONITORAMENTO MT\r\n"
					+ "LEFT JOIN REPRESA RP ON RP.id = MT.id_represa\r\n"
					+ "ORDER BY MT.volume_util_percent ASC  -- ASC = Menor para o Maior\r\n"
					+ "LIMIT 1;";
			
			
			Connection minhaConexao = con.getConect();
			
			// Metodos de conexao
		     var consulta = minhaConexao.prepareStatement(sql);
			// Execução do Script
		     var resultado = consulta.executeQuery();
		     
		     //Exibe resultado
		     if (resultado.next()) {
		    	 	String nome = resultado.getString("nome");
		    	 	String data = resultado.getString("data");
		    	    int total = resultado.getInt("volume");
		    	    JOptionPane.showMessageDialog(null, "Menor Volume Registrado no Periodo: \n"
		    	    		+ "Represa: " + nome + "\n"
		    	    		+ "Data: " + data + " Volume Registrado: " + total );
		    }
	
		    	// Fecha a conexão
		    	resultado.close();
		    	consulta.close();
		    	minhaConexao.close();
			
		}

		public DadosGraficoChuvaVolume buscarDadosGraficoChuvaVolume() throws SQLException {
			
			String sql = "SELECT "
					+ "DATE_FORMAT(MT.data, '%d/%m/%Y') AS data, "
					+ "AVG(MT.chuva_mm) AS chuva_mm, "
					+ "AVG(MT.volume_util_percent) AS volume_util_percent "
					+ "FROM MONITORAMENTO MT "
					+ "GROUP BY MT.data "
					+ "ORDER BY MT.data;";
			
			List<String> datas = new ArrayList<>();
			List<Double> chuvas = new ArrayList<>();
			List<Double> volumes = new ArrayList<>();
			
			Connection minhaConexao = con.getConect();
			
			if (minhaConexao == null) {
				throw new SQLException("Não foi possível abrir conexão para montar o gráfico.");
			}
			
			try (minhaConexao;
			     var consulta = minhaConexao.prepareStatement(sql);
			     var resultado = consulta.executeQuery()) {
				
				while (resultado.next()) {
					datas.add(resultado.getString("data"));
					chuvas.add(resultado.getDouble("chuva_mm"));
					volumes.add(resultado.getDouble("volume_util_percent"));
				}
				
				return new DadosGraficoChuvaVolume(datas, chuvas, volumes);
			}
			
		}
	
}
