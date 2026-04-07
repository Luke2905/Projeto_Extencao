package projetoextensao;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class Tela extends JFrame{

	public Tela(DefaultTableModel modelo) {
		
		// Configuração basica da janela
		setTitle("Sistema de Monitoramento de Represas");
		setSize(1000, 600); // Largura e Altura
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fecha só a janela, não o app todo
        setLocationRelativeTo(null); // Centraliza na tela
        
        // Cria a tabela com o modelo que veio do banco
        JTable tabela = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabela);

        // Dimensiona a tabela na tela
        scroll.setPreferredSize(new Dimension(800, 300)); 
        add(scroll, BorderLayout.SOUTH); // Colocamos no Norte para ela "grudar" em cima

        // --- Outro Bloco da Tela---
        JPanel painelCadastro = new JPanel();
        painelCadastro.setBorder(BorderFactory.createTitledBorder("Dashboard"));
        painelCadastro.setLayout(new FlowLayout()); // Elementos um do lado do outro


        add(painelCadastro, BorderLayout.CENTER);
		
	}

}
