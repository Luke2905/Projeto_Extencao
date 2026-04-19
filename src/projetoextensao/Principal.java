package projetoextensao;
// import java.lang.classfile.instruction.SwitchCase;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Principal {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub

		JOptionPane.showMessageDialog(null, "Bem Vindo ao Painel de Monitoramento de Represas 2020 - 2026");
		
		DataBase db = new DataBase();
		
		String opcao = ""; 
		
		while (!opcao.equals("0")) {
			
			opcao = JOptionPane.showInputDialog(null,"			Menu \n"
					+ "Selecione a informação que quer visualizar\n"
					+ "1 - Tabela de Dados\n"
					+ "2 - Total de Dados Registrados\n"
					+ "3 - Média Histórica\n"
					+ "4 - Menor Volume Registrado\n"
					+ "5 - Gráfico Chuva x Volume\n"
					+ "0 - Sair");
			
			switch (opcao) {
			case "1": {
				
				// Listar dados
				db.listarDados();
				
				break;
			}
			case "2": {
				
				// Exibir Total
				db.totalDados();
				
				break;
			}
			case "3": {
				
				// Exibir Média
				db.mediaDados();
				
				break;
			}
			case "4": {
				
				//Exibir Menor Volume
				db.menorRegistro();
				
				break;
			}
			case "5": {
				
				// Exibir Gráfico
				try {
					DadosGraficoChuvaVolume dadosGrafico = db.buscarDadosGraficoChuvaVolume();
					TelaGraficoChuvaVolume.exibir(dadosGrafico);
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(null, "Erro ao carregar gráfico: " + e.getMessage());
				}
				
				break;
			}
			case "0": {
				//Fechando o programa
				JOptionPane.showMessageDialog(null, "Encerrando o Programa!!");
				System.exit(0);
			}
			default:
				JOptionPane.showMessageDialog(null, "Escolha as opções do menu.");
			}
			
			
		}
		
		
	}

}
