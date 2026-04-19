package projetoextensao;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Principal {

	public static void main(String[] args) {

		// Inicializa a interface no padrao visual do sistema operacional
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Falha ao aplicar tema do sistema: " + e.getMessage());
		}

		// Abre a tela principal no fluxo grafico do Swing
		SwingUtilities.invokeLater(() -> {
			try {
				Tela tela = new Tela(new DataBase());
				tela.setVisible(true);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Nao foi possivel iniciar o sistema.\n" + e.getMessage(),
						"Erro de inicializacao", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
