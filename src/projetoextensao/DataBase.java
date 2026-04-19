package projetoextensao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DataBase {

	// Chamando a conexao do banco de dados
	private final Conexao con = new Conexao();

	public void listarDados() throws SQLException {

		// Monta a tabela completa para exibicao rapida em popup
		DefaultTableModel modelo = montarModeloTabelaCompleta();
		JTable tabela = new JTable(modelo);
		JScrollPane scroll = new JScrollPane(tabela);

		JOptionPane.showMessageDialog(null, scroll, "Relatorio de Monitoramento de Represas", JOptionPane.PLAIN_MESSAGE);
	}

	public void totalDados() throws SQLException {

		// SQL para contar os registros de monitoramento
		String sql = "SELECT COUNT(*) AS TOTAL FROM MONITORAMENTO;";

		try (Connection minhaConexao = abrirConexao(); var consulta = minhaConexao.prepareStatement(sql);
				var resultado = consulta.executeQuery()) {

			if (resultado.next()) {
				int total = resultado.getInt("TOTAL");
				JOptionPane.showMessageDialog(null, "Total de registros no sistema: " + total);
			}
		}
	}

	public void mediaDados() throws SQLException {

		// SQL para calcular a media historica de chuva
		String sql = "SELECT AVG(chuva_mm) AS media FROM MONITORAMENTO;";

		try (Connection minhaConexao = abrirConexao(); var consulta = minhaConexao.prepareStatement(sql);
				var resultado = consulta.executeQuery()) {

			if (resultado.next()) {
				double media = resultado.getDouble("media");
				JOptionPane.showMessageDialog(null,
						"Media historica de chuva registrada: " + String.format("%.2f", media) + " mm");
			}
		}
	}

	public void menorRegistro() throws SQLException {

		// SQL para trazer a menor medicao de volume util
		String sql = "SELECT " + "    MT.id, " + "    RP.nome, " + "    DATE_FORMAT(MT.data, '%d/%m/%Y') AS data, "
				+ "    MT.volume_util_percent AS volume " + "FROM MONITORAMENTO MT "
				+ "LEFT JOIN REPRESA RP ON RP.id = MT.id_represa " + "ORDER BY MT.volume_util_percent ASC "
				+ "LIMIT 1;";

		try (Connection minhaConexao = abrirConexao(); var consulta = minhaConexao.prepareStatement(sql);
				var resultado = consulta.executeQuery()) {

			if (resultado.next()) {
				String nome = resultado.getString("nome");
				String data = resultado.getString("data");
				double volume = resultado.getDouble("volume");

				JOptionPane.showMessageDialog(null,
						"Menor volume registrado no periodo:\n" + "Represa: " + nome + "\n" + "Data: " + data + "\n"
								+ "Volume: " + String.format("%.2f", volume) + "%");
			}
		}
	}

	public List<MonitoramentoRegistro> buscarMonitoramentoCompleto() throws SQLException {

		// Lista que recebe todos os registros retornados pelo SQL
		List<MonitoramentoRegistro> registros = new ArrayList<>();

		// Script SQL principal para alimentar dashboard e tabela
		String sql = "SELECT " + "    MT.id, " + "    RP.nome AS represa, " + "    MT.data, " + "    MT.chuva_mm, "
				+ "    MT.volume_util_percent, " + "    MT.chuva_acumulada_mes_mm " + "FROM MONITORAMENTO MT "
				+ "LEFT JOIN REPRESA RP ON RP.id = MT.id_represa " + "ORDER BY MT.data ASC;";

		try (Connection minhaConexao = abrirConexao(); var consulta = minhaConexao.prepareStatement(sql);
				var resultado = consulta.executeQuery()) {

			// Percorre cada linha e monta o objeto de dominio
			while (resultado.next()) {
				LocalDate data = resultado.getDate("data").toLocalDate();

				MonitoramentoRegistro registro = new MonitoramentoRegistro(resultado.getInt("id"),
						resultado.getString("represa"), data, resultado.getDouble("chuva_mm"),
						resultado.getDouble("volume_util_percent"), resultado.getDouble("chuva_acumulada_mes_mm"));

				registros.add(registro);
			}
		}

		return registros;
	}

	public List<String> buscarRepresas() throws SQLException {

		// Lista para alimentar o filtro de represas da tela
		List<String> represas = new ArrayList<>();

		// SQL para trazer somente o nome das represas
		String sql = "SELECT nome FROM REPRESA ORDER BY nome;";

		try (Connection minhaConexao = abrirConexao(); var consulta = minhaConexao.prepareStatement(sql);
				var resultado = consulta.executeQuery()) {

			while (resultado.next()) {
				represas.add(resultado.getString("nome"));
			}
		}

		return represas;
	}

	private DefaultTableModel montarModeloTabelaCompleta() throws SQLException {

		// Definicao das colunas da tabela
		String[] colunas = { "Id", "Represa", "Data", "Chuva (mm)", "Volume (%)", "Chuva Acumulada - Mes (mm)" };
		DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

		// Aproveita o mesmo metodo que abastece o dashboard
		for (MonitoramentoRegistro registro : buscarMonitoramentoCompleto()) {
			Object[] linha = { registro.getId(), registro.getRepresa(), registro.getData(), String.format("%.2f", registro.getChuvaMm()),
					String.format("%.2f", registro.getVolumeUtilPercent()),
					String.format("%.2f", registro.getChuvaAcumuladaMesMm()) };

			modelo.addRow(linha);
		}

		return modelo;
	}

	private Connection abrirConexao() throws SQLException {

		// Garante que o restante da classe nao rode com conexao nula
		Connection minhaConexao = con.getConect();
		if (minhaConexao == null) {
			throw new SQLException("Nao foi possivel conectar ao banco de dados.");
		}
		return minhaConexao;
	}
}
