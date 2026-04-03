package projetoextensao;

import java.sql.SQLException;

import javax.swing.JOptionPane;

public class Principal {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub

		JOptionPane.showMessageDialog(null, "Iniciando a Extensão");
		
		DataBase db = new DataBase();
		
		db.listarDados();
	}

}
