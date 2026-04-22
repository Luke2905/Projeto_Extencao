package projetoextensao;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Tela extends JFrame {

	// Constantes visuais para manter identidade clean na tela
	private static final Color COR_FUNDO = new Color(243, 246, 251);
	private static final Color COR_PAINEL = Color.WHITE;
	private static final Color COR_MENU = new Color(23, 43, 77);
	private static final Color COR_BOTAO = new Color(0, 0, 0);
	private static final Color COR_TEXTO_MENU = new Color(235, 241, 252);
	private static final Color COR_GRAFICO_1 = new Color(51, 136, 255);
	private static final Color COR_GRAFICO_2 = new Color(33, 184, 153);
	private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");

	// Objetos de apoio para dados e formatacao
	private final DataBase db;
	private final DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private final DateTimeFormatter formatoFiltro = DateTimeFormatter.ISO_LOCAL_DATE;

	// Componentes de filtro
	private final JComboBox<String> comboRepresa = new JComboBox<>();
	private final JTextField campoDataInicial = new JTextField(10);
	private final JTextField campoDataFinal = new JTextField(10);

	// Componentes KPI
	private final JLabel lblTotalRegistros = new JLabel("0");
	private final JLabel lblMediaChuva = new JLabel("--");
	private final JLabel lblMediaVolume = new JLabel("--");
	private final JLabel lblMenorVolume = new JLabel("--");
	private final JLabel lblMaiorVolume = new JLabel("--");

	// Componentes de conteudo
	private final CardLayout cardLayout = new CardLayout();
	private final JPanel painelConteudo = new JPanel(cardLayout);
	private final JPanel painelChuvaVolume = new JPanel(new BorderLayout());
	private final BarChartPanel graficoVolumeRepresa = new BarChartPanel("Volume medio por represa (%)");
	private final LineChartPanel graficoChuvaTempo = new LineChartPanel("Chuva media por data (mm)");
	private final JTextArea areaAnalisesResumo = new JTextArea();
	private final JTextArea areaAnalisesDetalhadas = new JTextArea();

	// Modelo e tabela de dados
	private final DefaultTableModel modeloTabela = new DefaultTableModel(
			new String[] { "Id", "Represa", "Data", "Chuva (mm)", "Volume (%)", "Chuva Acumulada Mes (mm)" }, 0) {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	private final JTable tabela = new JTable(modeloTabela);

	// Listas com dados originais e filtrados
	private List<MonitoramentoRegistro> dadosBase = new ArrayList<>();
	private List<MonitoramentoRegistro> dadosFiltrados = new ArrayList<>();

	public Tela(DataBase db) {
		this.db = db;

		// Configuracao basica da janela principal
		setTitle("Painel de Monitoramento de Represas");
		setSize(1300, 760);
		setMinimumSize(new Dimension(1100, 680));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setBackground(COR_FUNDO);
		setLayout(new BorderLayout());

		// Monta as partes visuais da interface
		add(criarMenuLateral(), BorderLayout.WEST);
		add(criarPainelFiltros(), BorderLayout.NORTH);
		add(criarConteudoCentral(), BorderLayout.CENTER);

		// Carrega os dados e desenha a tela com os valores iniciais
		carregarDadosIniciais();
	}

	private JPanel criarMenuLateral() {

		// Painel esquerdo para substituir o menu de JOptionPane
		JPanel menu = new JPanel();
		menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
		menu.setBackground(COR_MENU);
		menu.setPreferredSize(new Dimension(220, 0));
		menu.setBorder(new EmptyBorder(24, 18, 24, 18));

		JLabel titulo = new JLabel("<html><center>Painel de<br>Represas</center></html>", SwingConstants.CENTER);
		titulo.setForeground(COR_TEXTO_MENU);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titulo.setAlignmentX(CENTER_ALIGNMENT);

		JLabel subtitulo = new JLabel("2020 - 2026");
		subtitulo.setForeground(new Color(178, 198, 232));
		subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		subtitulo.setAlignmentX(CENTER_ALIGNMENT);

		JButton btnDashboard = criarBotaoMenu("Dashboard");
		btnDashboard.addActionListener(e -> cardLayout.show(painelConteudo, "dashboard"));

		JButton btnTabela = criarBotaoMenu("Tabela");
		btnTabela.addActionListener(e -> cardLayout.show(painelConteudo, "tabela"));

		JButton btnAnalises = criarBotaoMenu("Analises");
		btnAnalises.addActionListener(e -> cardLayout.show(painelConteudo, "analises"));

		JButton btnChuvaVolume = criarBotaoMenu("Chuva x Volume");
		btnChuvaVolume.addActionListener(e -> cardLayout.show(painelConteudo, "chuvaVolume"));

		menu.add(titulo);
		menu.add(Box.createVerticalStrut(6));
		menu.add(subtitulo);
		menu.add(Box.createVerticalStrut(28));
		menu.add(btnDashboard);
		menu.add(Box.createVerticalStrut(10));
		menu.add(btnTabela);
		menu.add(Box.createVerticalStrut(10));
		menu.add(btnAnalises);
		menu.add(Box.createVerticalStrut(10));
		menu.add(btnChuvaVolume);
		menu.add(Box.createVerticalGlue());

		return menu;
	}

	private JButton criarBotaoMenu(String texto) {

		// Botao com estilo padrao para os atalhos do menu lateral
		JButton botao = new JButton(texto);
		botao.setFocusPainted(false);
		botao.setBackground(COR_BOTAO);
		botao.setForeground(new Color(20, 20, 20));
		botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
		botao.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
		botao.setAlignmentX(CENTER_ALIGNMENT);
		botao.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
		return botao;
	}

	private JPanel criarPainelFiltros() {

		// Filtros no topo para o usuario explorar os dados da forma que quiser
		JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
		painelFiltros.setBackground(COR_PAINEL);
		painelFiltros.setBorder(new EmptyBorder(10, 14, 10, 14));

		JLabel lblRepresa = new JLabel("Represa:");
		lblRepresa.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JLabel lblDataInicial = new JLabel("Data Inicial (yyyy-MM-dd):");
		lblDataInicial.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JLabel lblDataFinal = new JLabel("Data Final (yyyy-MM-dd):");
		lblDataFinal.setFont(new Font("Segoe UI", Font.BOLD, 13));

		JButton btnAplicar = new JButton("Aplicar");
		btnAplicar.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnAplicar.addActionListener(e -> aplicarFiltros());

		JButton btnLimpar = new JButton("Limpar");
		btnLimpar.setFont(new Font("Segoe UI", Font.BOLD, 12));
		btnLimpar.addActionListener(e -> limparFiltros());

		painelFiltros.add(lblRepresa);
		painelFiltros.add(comboRepresa);
		painelFiltros.add(lblDataInicial);
		painelFiltros.add(campoDataInicial);
		painelFiltros.add(lblDataFinal);
		painelFiltros.add(campoDataFinal);
		painelFiltros.add(btnAplicar);
		painelFiltros.add(btnLimpar);

		return painelFiltros;
	}

	private JPanel criarConteudoCentral() {

		// Area principal da tela com paginas controladas pelo menu lateral
		painelConteudo.setOpaque(false);
		painelConteudo.add(criarPaginaDashboard(), "dashboard");
		painelConteudo.add(criarPaginaTabela(), "tabela");
		painelConteudo.add(criarPaginaAnalises(), "analises");
		painelConteudo.add(criarPaginaChuvaVolume(), "chuvaVolume");

		JPanel conteudo = new JPanel(new BorderLayout());
		conteudo.setOpaque(false);
		conteudo.setBorder(new EmptyBorder(14, 14, 14, 14));
		conteudo.add(painelConteudo, BorderLayout.CENTER);
		return conteudo;
	}

	private JPanel criarPaginaDashboard() {

		// Painel principal com KPI, graficos e resumo de analises
		JPanel pagina = new JPanel(new BorderLayout(10, 10));
		pagina.setOpaque(false);

		JPanel painelKpi = new JPanel(new GridLayout(1, 5, 8, 0));
		painelKpi.setOpaque(false);
		painelKpi.add(criarCardKpi("Total Registros", lblTotalRegistros));
		painelKpi.add(criarCardKpi("Media Chuva", lblMediaChuva));
		painelKpi.add(criarCardKpi("Media Volume", lblMediaVolume));
		painelKpi.add(criarCardKpi("Menor Volume", lblMenorVolume));
		painelKpi.add(criarCardKpi("Maior Volume", lblMaiorVolume));

		JPanel painelGraficos = new JPanel(new GridLayout(1, 2, 10, 0));
		painelGraficos.setOpaque(false);
		painelGraficos.add(graficoVolumeRepresa);
		painelGraficos.add(graficoChuvaTempo);

		configurarAreaTexto(areaAnalisesResumo);
		JPanel painelResumo = criarCardGenerico("Analises Rapidas", new JScrollPane(areaAnalisesResumo));
		painelResumo.setPreferredSize(new Dimension(0, 170));

		pagina.add(painelKpi, BorderLayout.NORTH);
		pagina.add(painelGraficos, BorderLayout.CENTER);
		pagina.add(painelResumo, BorderLayout.SOUTH);

		return pagina;
	}

	private JPanel criarPaginaTabela() {

		// Pagina com tabela completa para consulta detalhada dos registros
		JPanel pagina = new JPanel(new BorderLayout(8, 8));
		pagina.setOpaque(false);

		tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		tabela.setRowHeight(24);
		tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

		JScrollPane scroll = new JScrollPane(tabela);
		scroll.setBorder(BorderFactory.createEmptyBorder());

		JPanel card = criarCardGenerico("Tabela de Monitoramento", scroll);
		pagina.add(card, BorderLayout.CENTER);
		return pagina;
	}

	private JPanel criarPaginaAnalises() {

		// Pagina dedicada para texto de analises interpretativas
		configurarAreaTexto(areaAnalisesDetalhadas);
		JScrollPane scroll = new JScrollPane(areaAnalisesDetalhadas);
		scroll.setBorder(BorderFactory.createEmptyBorder());

		JPanel pagina = new JPanel(new BorderLayout());
		pagina.setOpaque(false);
		pagina.add(criarCardGenerico("Analises Estrategicas", scroll), BorderLayout.CENTER);
		return pagina;
	}

	private JPanel criarPaginaChuvaVolume() {

		// Pagina integrada ao menu lateral para o grafico detalhado
		painelChuvaVolume.setOpaque(false);
		return painelChuvaVolume;
	}

	private JPanel criarCardKpi(String titulo, JLabel valor) {

		// Card  para mostrar um indicador numerico no topo do dashboard
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(COR_PAINEL);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 228, 238)),
				new EmptyBorder(10, 12, 10, 12)));

		JLabel lblTitulo = new JLabel(titulo);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblTitulo.setForeground(new Color(84, 92, 108));

		valor.setFont(new Font("Segoe UI", Font.BOLD, 22));
		valor.setHorizontalAlignment(SwingConstants.RIGHT);
		valor.setForeground(new Color(21, 39, 72));

		card.add(lblTitulo, BorderLayout.NORTH);
		card.add(valor, BorderLayout.CENTER);
		return card;
	}

	private JPanel criarCardGenerico(String titulo, JScrollPane conteudo) {

		// Card reutilizavel 
		JPanel card = new JPanel(new BorderLayout());
		card.setBackground(COR_PAINEL);
		card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 228, 238)),
				new EmptyBorder(10, 10, 10, 10)));

		JLabel lblTitulo = new JLabel(titulo);
		lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTitulo.setBorder(new EmptyBorder(0, 0, 8, 0));

		card.add(lblTitulo, BorderLayout.NORTH);
		card.add(conteudo, BorderLayout.CENTER);
		return card;
	}

	private void configurarAreaTexto(JTextArea area) {

		// Configura area de texto para leitura de analises sem edicao
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		area.setBackground(Color.WHITE);
		area.setBorder(new EmptyBorder(8, 8, 8, 8));
	}

	private void carregarDadosIniciais() {

		try {
			// Carrega dados brutos para o dashboard
			dadosBase = db.buscarMonitoramentoCompleto();
			dadosFiltrados = new ArrayList<>(dadosBase);

			// Preenche filtro de represas com opcoes vindas do banco
			comboRepresa.removeAllItems();
			comboRepresa.addItem("Todas");
			for (String represa : db.buscarRepresas()) {
				comboRepresa.addItem(represa);
			}

			// Atualiza todos os blocos visuais da tela
			atualizarDashboard();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Erro ao carregar dados iniciais:\n" + e.getMessage(), "Erro",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void aplicarFiltros() {

		try {
			// Leitura e validacao dos filtros informados pelo usuario
			String represaSelecionada = comboRepresa.getSelectedItem() == null ? "Todas"
					: comboRepresa.getSelectedItem().toString();
			LocalDate dataInicial = parseDataFiltro(campoDataInicial.getText().trim(), "Data inicial");
			LocalDate dataFinal = parseDataFiltro(campoDataFinal.getText().trim(), "Data final");

			if (dataInicial != null && dataFinal != null && dataInicial.isAfter(dataFinal)) {
				JOptionPane.showMessageDialog(this, "A data inicial nao pode ser maior que a data final.",
						"Filtro invalido", JOptionPane.WARNING_MESSAGE);
				return;
			}

			// Aplicacao real dos filtros nos dados em memoria
			dadosFiltrados = dadosBase.stream().filter(registro -> filtrarRepresa(registro, represaSelecionada))
					.filter(registro -> filtrarPeriodo(registro, dataInicial, dataFinal)).collect(Collectors.toList());

			// Atualiza os paineis apos o filtro
			atualizarDashboard();
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Filtro invalido", JOptionPane.WARNING_MESSAGE);
		}
	}

	private boolean filtrarRepresa(MonitoramentoRegistro registro, String represaSelecionada) {
		return "Todas".equalsIgnoreCase(represaSelecionada) || registro.getRepresa().equalsIgnoreCase(represaSelecionada);
	}

	private boolean filtrarPeriodo(MonitoramentoRegistro registro, LocalDate dataInicial, LocalDate dataFinal) {
		return (dataInicial == null || !registro.getData().isBefore(dataInicial))
				&& (dataFinal == null || !registro.getData().isAfter(dataFinal));
	}

	private LocalDate parseDataFiltro(String valorCampo, String nomeCampo) {

		// Permite campo vazio e valida somente quando houver valor digitado
		if (valorCampo == null || valorCampo.isBlank()) {
			return null;
		}

		try {
			return LocalDate.parse(valorCampo, formatoFiltro);
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException(nomeCampo + " invalida. Use o formato yyyy-MM-dd.");
		}
	}

	private void limparFiltros() {

		// Restaura filtros para o estado inicial do dashboard
		comboRepresa.setSelectedIndex(0);
		campoDataInicial.setText("");
		campoDataFinal.setText("");
		dadosFiltrados = new ArrayList<>(dadosBase);
		atualizarDashboard();
	}

	private void atualizarDashboard() {

		// Atualiza todos os componentes visuais com base nos dados atuais
		atualizarTabela();
		atualizarKpis();
		atualizarGraficos();
		atualizarAnalises();
		atualizarPaginaChuvaVolume();
	}

	private void atualizarTabela() {

		// Limpa tabela e remonta linhas usando os dados filtrados
		modeloTabela.setRowCount(0);

		List<MonitoramentoRegistro> ordenados = new ArrayList<>(dadosFiltrados);
		ordenados.sort(Comparator.comparing(MonitoramentoRegistro::getData).reversed());

		for (MonitoramentoRegistro registro : ordenados) {
			modeloTabela.addRow(new Object[] { registro.getId(), registro.getRepresa(), formatoData.format(registro.getData()),
					formatarNumero(registro.getChuvaMm()), formatarNumero(registro.getVolumeUtilPercent()),
					formatarNumero(registro.getChuvaAcumuladaMesMm()) });
		}
	}

	private void atualizarKpis() {

		// Monta os indicadores principais para leitura rapida
		int totalRegistros = dadosFiltrados.size();
		lblTotalRegistros.setText(String.valueOf(totalRegistros));

		if (totalRegistros == 0) {
			lblMediaChuva.setText("--");
			lblMediaVolume.setText("--");
			lblMenorVolume.setText("--");
			lblMaiorVolume.setText("--");
			return;
		}

		double mediaChuva = dadosFiltrados.stream().mapToDouble(MonitoramentoRegistro::getChuvaMm).average().orElse(0.0);
		double mediaVolume = dadosFiltrados.stream().mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent).average()
				.orElse(0.0);
		double menorVolume = dadosFiltrados.stream().mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent).min()
				.orElse(0.0);
		double maiorVolume = dadosFiltrados.stream().mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent).max()
				.orElse(0.0);

		lblMediaChuva.setText(formatarNumero(mediaChuva) + " mm");
		lblMediaVolume.setText(formatarNumero(mediaVolume) + "%");
		lblMenorVolume.setText(formatarNumero(menorVolume) + "%");
		lblMaiorVolume.setText(formatarNumero(maiorVolume) + "%");
	}

	private void atualizarGraficos() {

		// Agrupa dados para o grafico de barras por represa
		LinkedHashMap<String, Double> volumePorRepresa = dadosFiltrados.stream()
				.collect(Collectors.groupingBy(MonitoramentoRegistro::getRepresa, LinkedHashMap::new,
						Collectors.averagingDouble(MonitoramentoRegistro::getVolumeUtilPercent)))
				.entrySet().stream().sorted(Map.Entry.<String, Double>comparingByValue(Comparator.reverseOrder()))
				.limit(8).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

		// Agrupa dados para o grafico de linha por data
		NavigableMap<LocalDate, Double> chuvaPorData = dadosFiltrados.stream().collect(Collectors.groupingBy(
				MonitoramentoRegistro::getData, TreeMap::new, Collectors.averagingDouble(MonitoramentoRegistro::getChuvaMm)));

		graficoVolumeRepresa.setDados(volumePorRepresa);
		graficoChuvaTempo.setDados(chuvaPorData);
	}

	private void atualizarAnalises() {

		// Cria texto de analise com insights automaticos sobre o periodo filtrado
		if (dadosFiltrados.isEmpty()) {
			String textoVazio = "Nenhum dado encontrado para os filtros atuais.";
			areaAnalisesResumo.setText(textoVazio);
			areaAnalisesDetalhadas.setText(textoVazio);
			return;
		}

		Map<String, Double> volumeMedioRepresa = dadosFiltrados.stream()
				.collect(Collectors.groupingBy(MonitoramentoRegistro::getRepresa,
						Collectors.averagingDouble(MonitoramentoRegistro::getVolumeUtilPercent)));
		Map<String, Double> chuvaMediaRepresa = dadosFiltrados.stream().collect(Collectors.groupingBy(
				MonitoramentoRegistro::getRepresa, Collectors.averagingDouble(MonitoramentoRegistro::getChuvaMm)));

		Map.Entry<String, Double> piorVolume = volumeMedioRepresa.entrySet().stream().min(Map.Entry.comparingByValue())
				.orElse(null);
		Map.Entry<String, Double> melhorVolume = volumeMedioRepresa.entrySet().stream().max(Map.Entry.comparingByValue())
				.orElse(null);
		Map.Entry<String, Double> maiorChuva = chuvaMediaRepresa.entrySet().stream().max(Map.Entry.comparingByValue())
				.orElse(null);

		long diasCriticos = dadosFiltrados.stream().filter(r -> r.getVolumeUtilPercent() < 30.0).count();
		double chuvaTotal = dadosFiltrados.stream().mapToDouble(MonitoramentoRegistro::getChuvaMm).sum();
		String tendencia = calcularTendenciaVolume();

		StringBuilder texto = new StringBuilder();
		texto.append("Panorama do periodo filtrado\n\n");
		texto.append("1. Represa com menor volume medio: ")
				.append(piorVolume == null ? "--" : piorVolume.getKey() + " (" + formatarNumero(piorVolume.getValue()) + "%)")
				.append("\n");
		texto.append("2. Represa com melhor volume medio: ")
				.append(melhorVolume == null
						? "--"
						: melhorVolume.getKey() + " (" + formatarNumero(melhorVolume.getValue()) + "%)")
				.append("\n");
		texto.append("3. Represa com maior media de chuva: ")
				.append(maiorChuva == null ? "--" : maiorChuva.getKey() + " (" + formatarNumero(maiorChuva.getValue()) + " mm)")
				.append("\n");
		texto.append("4. Registros em zona de alerta (volume < 30%): ").append(diasCriticos).append("\n");
		texto.append("5. Volume total de chuva acumulado no recorte: ").append(formatarNumero(chuvaTotal)).append(" mm\n");
		texto.append("6. Tendencia recente de volume: ").append(tendencia).append("\n");

		areaAnalisesResumo.setText(texto.toString());
		areaAnalisesDetalhadas.setText(texto.toString()
				+ "\nLeitura sugerida:\n"
				+ "- Use esta pagina para detectar periodos de queda de volume.\n"
				+ "- Cruze o grafico de chuva com o indicador de volume para inferir recuperacao.\n"
				+ "- Observe represas com maior frequencia de volume abaixo de 30% para priorizar acoes.");
	}

	private void atualizarPaginaChuvaVolume() {

		// Recria o painel com base nos dados filtrados para manter o grafico sincronizado com a tela principal
		painelChuvaVolume.removeAll();

		DadosGraficoChuvaVolume dadosGrafico = montarDadosGraficoChuvaVolumeFiltrado();
		if (dadosGrafico == null || dadosGrafico.estaVazio()) {
			JPanel vazio = new JPanel(new BorderLayout());
			vazio.setOpaque(false);

			JLabel titulo = new JLabel("Chuva x Volume", SwingConstants.CENTER);
			titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
			titulo.setForeground(new Color(84, 92, 108));

			JLabel mensagem = new JLabel("Nao ha dados suficientes para gerar o grafico com os filtros atuais.",
					SwingConstants.CENTER);
			mensagem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
			mensagem.setForeground(new Color(120, 126, 142));

			vazio.add(titulo, BorderLayout.NORTH);
			vazio.add(mensagem, BorderLayout.CENTER);
			painelChuvaVolume.add(vazio, BorderLayout.CENTER);
		} else {
			painelChuvaVolume.add(new TelaGraficoChuvaVolume(dadosGrafico), BorderLayout.CENTER);
		}

		painelChuvaVolume.revalidate();
		painelChuvaVolume.repaint();
	}

	private DadosGraficoChuvaVolume montarDadosGraficoChuvaVolumeFiltrado() {

		// Agrupa os dados filtrados por data para alimentar o painel detalhado de chuva x volume
		NavigableMap<LocalDate, List<MonitoramentoRegistro>> agrupado = new TreeMap<>();

		for (MonitoramentoRegistro registro : dadosFiltrados) {
			agrupado.computeIfAbsent(registro.getData(), chave -> new ArrayList<>()).add(registro);
		}

		List<String> datas = new ArrayList<>();
		List<Double> chuvas = new ArrayList<>();
		List<Double> volumes = new ArrayList<>();

		for (Map.Entry<LocalDate, List<MonitoramentoRegistro>> entrada : agrupado.entrySet()) {
			List<MonitoramentoRegistro> registrosData = entrada.getValue();
			double mediaChuva = registrosData.stream().mapToDouble(MonitoramentoRegistro::getChuvaMm).average().orElse(0.0);
			double mediaVolume = registrosData.stream().mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent).average()
					.orElse(0.0);

			datas.add(formatoData.format(entrada.getKey()));
			chuvas.add(mediaChuva);
			volumes.add(mediaVolume);
		}

		return new DadosGraficoChuvaVolume(datas, chuvas, volumes);
	}

	private String calcularTendenciaVolume() {

		// Compara media de volume no inicio e no fim do periodo para inferir tendencia
		if (dadosFiltrados.size() < 4) {
			return "dados insuficientes para inferencia robusta";
		}

		List<MonitoramentoRegistro> ordenados = new ArrayList<>(dadosFiltrados);
		ordenados.sort(Comparator.comparing(MonitoramentoRegistro::getData));

		int janela = Math.max(2, ordenados.size() / 4);
		double mediaInicio = ordenados.subList(0, janela).stream().mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent)
				.average().orElse(0.0);
		double mediaFim = ordenados.subList(ordenados.size() - janela, ordenados.size()).stream()
				.mapToDouble(MonitoramentoRegistro::getVolumeUtilPercent).average().orElse(0.0);

		double variacao = mediaFim - mediaInicio;
		if (variacao > 2.0) {
			return "melhora gradual (" + formatarNumero(variacao) + " pontos percentuais)";
		}
		if (variacao < -2.0) {
			return "queda gradual (" + formatarNumero(variacao) + " pontos percentuais)";
		}
		return "estabilidade no periodo";
	}

	private String formatarNumero(double valor) {
		return String.format(LOCALE_BR, "%.2f", valor);
	}

	private static class BarChartPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private final String titulo;
		private Map<String, Double> dados = new LinkedHashMap<>();

		BarChartPanel(String titulo) {
			this.titulo = titulo;
			setBackground(COR_PAINEL);
			setBorder(BorderFactory.createLineBorder(new Color(222, 228, 238)));
		}

		void setDados(Map<String, Double> dados) {
			this.dados = new LinkedHashMap<>(dados);
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int largura = getWidth();
			int altura = getHeight();
			int margemEsquerda = 50;
			int margemDireita = 20;
			int margemTopo = 40;
			int margemBase = 60;
			int areaLargura = largura - margemEsquerda - margemDireita;
			int areaAltura = altura - margemTopo - margemBase;

			g2.setColor(new Color(44, 58, 88));
			g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
			g2.drawString(titulo, margemEsquerda, 22);

			if (dados.isEmpty()) {
				g2.setColor(new Color(129, 140, 159));
				g2.drawString("Sem dados para o filtro selecionado.", margemEsquerda, margemTopo + 30);
				g2.dispose();
				return;
			}

			double max = dados.values().stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
			if (max <= 0.0) {
				max = 1.0;
			}

			g2.setColor(new Color(213, 220, 232));
			g2.drawLine(margemEsquerda, margemTopo + areaAltura, margemEsquerda + areaLargura, margemTopo + areaAltura);
			g2.drawLine(margemEsquerda, margemTopo, margemEsquerda, margemTopo + areaAltura);

			int quantidade = dados.size();
			int larguraBarra = Math.max(22, areaLargura / (quantidade * 2));
			int espaco = Math.max(8, (areaLargura - (larguraBarra * quantidade)) / (quantidade + 1));

			int indice = 0;
			for (Map.Entry<String, Double> entrada : dados.entrySet()) {
				double valor = entrada.getValue();
				int alturaBarra = (int) ((valor / max) * areaAltura);
				int x = margemEsquerda + espaco + indice * (larguraBarra + espaco);
				int y = margemTopo + areaAltura - alturaBarra;

				g2.setColor(COR_GRAFICO_1);
				g2.fillRoundRect(x, y, larguraBarra, alturaBarra, 8, 8);

				g2.setColor(new Color(79, 89, 109));
				g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
				String rotulo = reduzirTexto(entrada.getKey(), 10);
				int larguraRotulo = g2.getFontMetrics().stringWidth(rotulo);
				g2.drawString(rotulo, x + (larguraBarra - larguraRotulo) / 2, margemTopo + areaAltura + 16);

				String valorFormatado = String.format(LOCALE_BR, "%.1f", valor);
				int larguraValor = g2.getFontMetrics().stringWidth(valorFormatado);
				g2.drawString(valorFormatado, x + (larguraBarra - larguraValor) / 2, y - 5);
				indice++;
			}

			g2.dispose();
		}

		private String reduzirTexto(String texto, int limite) {
			if (texto == null || texto.length() <= limite) {
				return texto;
			}
			return texto.substring(0, limite - 1) + ".";
		}
	}

	private static class LineChartPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final DateTimeFormatter FORMATO_EIXO = DateTimeFormatter.ofPattern("dd/MM");

		private final String titulo;
		private NavigableMap<LocalDate, Double> dados = new TreeMap<>();

		LineChartPanel(String titulo) {
			this.titulo = titulo;
			setBackground(COR_PAINEL);
			setBorder(BorderFactory.createLineBorder(new Color(222, 228, 238)));
		}

		void setDados(NavigableMap<LocalDate, Double> dados) {
			this.dados = new TreeMap<>(dados);
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int largura = getWidth();
			int altura = getHeight();
			int margemEsquerda = 55;
			int margemDireita = 20;
			int margemTopo = 40;
			int margemBase = 60;
			int areaLargura = largura - margemEsquerda - margemDireita;
			int areaAltura = altura - margemTopo - margemBase;

			g2.setColor(new Color(44, 58, 88));
			g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
			g2.drawString(titulo, margemEsquerda, 22);

			if (dados.isEmpty()) {
				g2.setColor(new Color(129, 140, 159));
				g2.drawString("Sem dados para o filtro selecionado.", margemEsquerda, margemTopo + 30);
				g2.dispose();
				return;
			}

			List<Map.Entry<LocalDate, Double>> pontos = reduzirPontos(dados, 28);
			double min = pontos.stream().mapToDouble(Map.Entry::getValue).min().orElse(0.0);
			double max = pontos.stream().mapToDouble(Map.Entry::getValue).max().orElse(1.0);
			if (Math.abs(max - min) < 0.0001) {
				max = min + 1.0;
			}

			g2.setColor(new Color(213, 220, 232));
			g2.drawLine(margemEsquerda, margemTopo + areaAltura, margemEsquerda + areaLargura, margemTopo + areaAltura);
			g2.drawLine(margemEsquerda, margemTopo, margemEsquerda, margemTopo + areaAltura);

			int tamanho = pontos.size();
			int[] xs = new int[tamanho];
			int[] ys = new int[tamanho];

			for (int i = 0; i < tamanho; i++) {
				double proporcaoX = tamanho == 1 ? 0.0 : (double) i / (tamanho - 1);
				double proporcaoY = (pontos.get(i).getValue() - min) / (max - min);
				xs[i] = margemEsquerda + (int) (proporcaoX * areaLargura);
				ys[i] = margemTopo + areaAltura - (int) (proporcaoY * areaAltura);
			}

			g2.setColor(COR_GRAFICO_2);
			for (int i = 0; i < tamanho - 1; i++) {
				g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
			}

			for (int i = 0; i < tamanho; i++) {
				g2.fillOval(xs[i] - 3, ys[i] - 3, 6, 6);
			}

			g2.setColor(new Color(79, 89, 109));
			g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			g2.drawString(String.format(LOCALE_BR, "%.1f", max), 10, margemTopo + 5);
			g2.drawString(String.format(LOCALE_BR, "%.1f", min), 10, margemTopo + areaAltura);

			if (tamanho > 1) {
				String inicio = pontos.get(0).getKey().format(FORMATO_EIXO);
				String meio = pontos.get(tamanho / 2).getKey().format(FORMATO_EIXO);
				String fim = pontos.get(tamanho - 1).getKey().format(FORMATO_EIXO);
				g2.drawString(inicio, margemEsquerda - 10, margemTopo + areaAltura + 18);
				g2.drawString(meio, margemEsquerda + (areaLargura / 2) - 10, margemTopo + areaAltura + 18);
				g2.drawString(fim, margemEsquerda + areaLargura - 10, margemTopo + areaAltura + 18);
			}

			g2.dispose();
		}

		private List<Map.Entry<LocalDate, Double>> reduzirPontos(NavigableMap<LocalDate, Double> origem, int maximoPontos) {
			List<Map.Entry<LocalDate, Double>> lista = new ArrayList<>(origem.entrySet());
			if (lista.size() <= maximoPontos) {
				return lista;
			}

			List<Map.Entry<LocalDate, Double>> amostra = new ArrayList<>();
			double passo = (double) (lista.size() - 1) / (maximoPontos - 1);
			for (int i = 0; i < maximoPontos; i++) {
				int indice = (int) Math.round(i * passo);
				indice = Math.min(indice, lista.size() - 1);
				amostra.add(lista.get(indice));
			}
			return amostra;
		}
	}
}
