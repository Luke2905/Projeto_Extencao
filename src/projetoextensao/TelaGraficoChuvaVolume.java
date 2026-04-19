package projetoextensao;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TelaGraficoChuvaVolume extends JPanel {

	private static final Color COR_FUNDO = new Color(241, 245, 249);
	private static final Color COR_CARD = Color.WHITE;
	private static final Color COR_BORDA = new Color(226, 232, 240);
	private static final Color COR_TEXTO = new Color(15, 23, 42);
	private static final Color COR_SUBTEXTO = new Color(100, 116, 139);
	private static final Color COR_GRADE = new Color(226, 232, 240);
	private static final Color COR_CHUVA = new Color(37, 99, 235);
	private static final Color COR_VOLUME = new Color(234, 88, 12);

	private final DadosGraficoChuvaVolume dados;

	public TelaGraficoChuvaVolume(DadosGraficoChuvaVolume dados) {
		this.dados = dados;

		setLayout(new BorderLayout(0, 24));
		setBackground(COR_FUNDO);
		setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

		add(criarCabecalho(), BorderLayout.NORTH);
		add(criarConteudoPrincipal(), BorderLayout.CENTER);
		add(criarRodape(), BorderLayout.SOUTH);
	}

	public static void exibir(DadosGraficoChuvaVolume dados) {
		if (dados == null || dados.estaVazio()) {
			JOptionPane.showMessageDialog(null, "Não há dados suficientes para gerar o gráfico.");
			return;
		}

		JFrame frame = new JFrame("Painel Analítico - Chuva x Volume");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setContentPane(new TelaGraficoChuvaVolume(dados));
		frame.setMinimumSize(new Dimension(1100, 760));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JPanel criarCabecalho() {
		JPanel cabecalho = new JPanel();
		cabecalho.setLayout(new BoxLayout(cabecalho, BoxLayout.Y_AXIS));
		cabecalho.setOpaque(false);

		JLabel titulo = new JLabel("Painel de Monitoramento - Chuva x Volume");
		titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
		titulo.setForeground(COR_TEXTO);
		titulo.setAlignmentX(LEFT_ALIGNMENT);

		JLabel subtitulo = new JLabel("Comparação temporal entre chuva média e volume útil médio, com leitura visual mais clara para apresentação.");
		subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 15));
		subtitulo.setForeground(COR_SUBTEXTO);
		subtitulo.setAlignmentX(LEFT_ALIGNMENT);

		cabecalho.add(titulo);
		cabecalho.add(Box.createVerticalStrut(6));
		cabecalho.add(subtitulo);

		return cabecalho;
	}

	private JPanel criarConteudoPrincipal() {
		JPanel conteudo = new JPanel(new BorderLayout(0, 20));
		conteudo.setOpaque(false);
		conteudo.add(criarPainelResumo(), BorderLayout.NORTH);
		conteudo.add(criarPainelGrafico(), BorderLayout.CENTER);
		conteudo.add(criarPainelInsights(), BorderLayout.SOUTH);
		return conteudo;
	}

	private JPanel criarPainelResumo() {
		JPanel painel = new JPanel(new GridLayout(1, 4, 16, 0));
		painel.setOpaque(false);

		String primeiraData = dados.getDatas().get(0);
		String ultimaData = dados.getDatas().get(dados.getDatas().size() - 1);
		int indicePicoChuva = indiceMaximo(dados.getChuvas());
		int indiceMenorVolume = indiceMinimo(dados.getVolumes());

		painel.add(criarCardResumo("Período Analisado", primeiraData + " até " + ultimaData,
				dados.getDatas().size() + " ponto(s) na série", new Color(219, 234, 254), COR_CHUVA));
		painel.add(criarCardResumo("Chuva Média", formatarDecimal(calcularMedia(dados.getChuvas())) + " mm",
				"Pico em " + dados.getDatas().get(indicePicoChuva), new Color(219, 234, 254), COR_CHUVA));
		painel.add(criarCardResumo("Volume Médio", formatarDecimal(calcularMedia(dados.getVolumes())) + " %",
				"Mínimo em " + dados.getDatas().get(indiceMenorVolume), new Color(255, 237, 213), COR_VOLUME));
		painel.add(criarCardResumo("Tendência", descreverTendencia(),
				"Comparação do primeiro e do último registro", new Color(226, 232, 240), new Color(71, 85, 105)));

		return painel;
	}

	private JPanel criarCardResumo(String titulo, String valor, String detalhe, Color fundo, Color corDestaque) {
		JPanel card = new JPanel(new BorderLayout(0, 10));
		card.setBackground(COR_CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COR_BORDA, 1),
				BorderFactory.createEmptyBorder(16, 16, 16, 16)));

		JPanel faixa = new JPanel();
		faixa.setBackground(corDestaque);
		faixa.setPreferredSize(new Dimension(0, 6));

		JPanel corpo = new JPanel();
		corpo.setLayout(new BoxLayout(corpo, BoxLayout.Y_AXIS));
		corpo.setOpaque(false);

		JLabel rotulo = new JLabel(titulo);
		rotulo.setFont(new Font("SansSerif", Font.BOLD, 13));
		rotulo.setForeground(COR_SUBTEXTO);
		rotulo.setAlignmentX(LEFT_ALIGNMENT);

		JLabel dado = new JLabel(valor);
		dado.setFont(new Font("SansSerif", Font.BOLD, 22));
		dado.setForeground(COR_TEXTO);
		dado.setAlignmentX(LEFT_ALIGNMENT);

		JLabel descricao = new JLabel(detalhe);
		descricao.setFont(new Font("SansSerif", Font.PLAIN, 12));
		descricao.setForeground(COR_SUBTEXTO);
		descricao.setAlignmentX(LEFT_ALIGNMENT);

		corpo.add(rotulo);
		corpo.add(Box.createVerticalStrut(10));
		corpo.add(dado);
		corpo.add(Box.createVerticalStrut(6));
		corpo.add(descricao);

		card.add(faixa, BorderLayout.NORTH);
		card.add(corpo, BorderLayout.CENTER);
		card.setOpaque(true);
		card.setBackground(fundo);

		return card;
	}

	private JPanel criarPainelGrafico() {
		JPanel card = new JPanel(new BorderLayout(0, 16));
		card.setBackground(COR_CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COR_BORDA, 1),
				BorderFactory.createEmptyBorder(20, 20, 20, 20)));

		JPanel topo = new JPanel(new BorderLayout());
		topo.setOpaque(false);

		JPanel textos = new JPanel();
		textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
		textos.setOpaque(false);

		JLabel titulo = new JLabel("Evolução Temporal da Série");
		titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
		titulo.setForeground(COR_TEXTO);

		JLabel subtitulo = new JLabel("Duas escalas independentes melhoram a leitura: chuva em milímetros à esquerda e volume útil em porcentagem à direita.");
		subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
		subtitulo.setForeground(COR_SUBTEXTO);

		textos.add(titulo);
		textos.add(Box.createVerticalStrut(4));
		textos.add(subtitulo);

		topo.add(textos, BorderLayout.WEST);
		topo.add(criarLegenda(), BorderLayout.EAST);

		card.add(topo, BorderLayout.NORTH);
		card.add(new PainelGrafico(dados), BorderLayout.CENTER);

		return card;
	}

	private JPanel criarLegenda() {
		JPanel legenda = new JPanel(new GridLayout(2, 1, 0, 8));
		legenda.setOpaque(false);
		legenda.add(criarItemLegenda(COR_CHUVA, "Chuva média por data (mm)"));
		legenda.add(criarItemLegenda(COR_VOLUME, "Volume útil médio por data (%)"));
		return legenda;
	}

	private JPanel criarItemLegenda(Color cor, String texto) {
		JPanel item = new JPanel(new BorderLayout(8, 0));
		item.setOpaque(false);

		JPanel marcador = new JPanel();
		marcador.setBackground(cor);
		marcador.setPreferredSize(new Dimension(18, 10));

		JLabel label = new JLabel(texto);
		label.setForeground(COR_TEXTO);
		label.setFont(new Font("SansSerif", Font.PLAIN, 12));

		item.add(marcador, BorderLayout.WEST);
		item.add(label, BorderLayout.CENTER);
		return item;
	}

	private JPanel criarPainelInsights() {
		JPanel painel = new JPanel(new GridLayout(1, 3, 16, 0));
		painel.setOpaque(false);

		int indicePicoChuva = indiceMaximo(dados.getChuvas());
		int indicePicoVolume = indiceMaximo(dados.getVolumes());
		int indiceMenorVolume = indiceMinimo(dados.getVolumes());

		painel.add(criarInsight("Maior Chuva",
				"A maior chuva média ocorreu em " + dados.getDatas().get(indicePicoChuva)
						+ " com " + formatarDecimal(dados.getChuvas().get(indicePicoChuva)) + " mm."));
		painel.add(criarInsight("Maior Volume",
				"O maior volume útil médio foi registrado em " + dados.getDatas().get(indicePicoVolume)
						+ " com " + formatarDecimal(dados.getVolumes().get(indicePicoVolume)) + " %."));
		painel.add(criarInsight("Ponto de Atenção",
				"O menor volume útil médio apareceu em " + dados.getDatas().get(indiceMenorVolume)
						+ ", com " + formatarDecimal(dados.getVolumes().get(indiceMenorVolume)) + " %."));

		return painel;
	}

	private JPanel criarInsight(String titulo, String texto) {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(COR_CARD);
		card.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COR_BORDA, 1),
				BorderFactory.createEmptyBorder(16, 16, 16, 16)));

		JLabel rotulo = new JLabel(titulo);
		rotulo.setFont(new Font("SansSerif", Font.BOLD, 14));
		rotulo.setForeground(COR_TEXTO);
		rotulo.setAlignmentX(LEFT_ALIGNMENT);

		JLabel detalhe = new JLabel("<html><body style='width: 260px'>" + texto + "</body></html>");
		detalhe.setFont(new Font("SansSerif", Font.PLAIN, 12));
		detalhe.setForeground(COR_SUBTEXTO);
		detalhe.setAlignmentX(LEFT_ALIGNMENT);

		card.add(rotulo);
		card.add(Box.createVerticalStrut(8));
		card.add(detalhe);
		return card;
	}

	private JPanel criarRodape() {
		JPanel rodape = new JPanel(new BorderLayout());
		rodape.setOpaque(false);

		JLabel texto = new JLabel("Leitura recomendada: observe a direção das linhas e compare os picos e quedas ao longo das datas para entender o comportamento do reservatório.");
		texto.setHorizontalAlignment(SwingConstants.CENTER);
		texto.setFont(new Font("SansSerif", Font.PLAIN, 13));
		texto.setForeground(COR_SUBTEXTO);

		rodape.add(texto, BorderLayout.CENTER);
		return rodape;
	}

	private String descreverTendencia() {
		double chuvaInicial = dados.getChuvas().get(0);
		double chuvaFinal = dados.getChuvas().get(dados.getChuvas().size() - 1);
		double volumeInicial = dados.getVolumes().get(0);
		double volumeFinal = dados.getVolumes().get(dados.getVolumes().size() - 1);

		String tendenciaChuva = chuvaFinal > chuvaInicial ? "chuva em alta" : chuvaFinal < chuvaInicial ? "chuva em queda" : "chuva estável";
		String tendenciaVolume = volumeFinal > volumeInicial ? "volume em alta" : volumeFinal < volumeInicial ? "volume em queda" : "volume estável";

		return tendenciaChuva + " / " + tendenciaVolume;
	}

	private double calcularMedia(List<Double> valores) {
		double soma = 0.0;
		for (Double valor : valores) {
			soma += valor;
		}
		return soma / valores.size();
	}

	private int indiceMaximo(List<Double> valores) {
		int indice = 0;
		for (int i = 1; i < valores.size(); i++) {
			if (valores.get(i) > valores.get(indice)) {
				indice = i;
			}
		}
		return indice;
	}

	private int indiceMinimo(List<Double> valores) {
		int indice = 0;
		for (int i = 1; i < valores.size(); i++) {
			if (valores.get(i) < valores.get(indice)) {
				indice = i;
			}
		}
		return indice;
	}

	private String formatarDecimal(double valor) {
		return String.format("%.1f", valor);
	}

	private static class PainelGrafico extends JPanel {

		private final DadosGraficoChuvaVolume dados;

		PainelGrafico(DadosGraficoChuvaVolume dados) {
			this.dados = dados;
			setPreferredSize(new Dimension(1280, 620));
			setBackground(Color.WHITE);
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(COR_BORDA, 1),
					BorderFactory.createEmptyBorder(12, 12, 12, 12)));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int margemEsquerda = 90;
			int margemDireita = 90;
			int margemTopo = 30;
			int margemBase = 80;

			int larguraUtil = getWidth() - margemEsquerda - margemDireita;
			int alturaUtil = getHeight() - margemTopo - margemBase;
			int inicioX = margemEsquerda;
			int fimX = getWidth() - margemDireita;
			int baseY = getHeight() - margemBase;

			double maxChuva = ajustarMaximo(dados.getChuvas());
			double maxVolume = ajustarMaximo(dados.getVolumes());

			g2.setColor(COR_GRADE);
			for (int i = 0; i <= 5; i++) {
				double proporcao = i / 5.0;
				int y = baseY - (int) Math.round(alturaUtil * proporcao);
				g2.drawLine(inicioX, y, fimX, y);

				g2.setColor(COR_SUBTEXTO);
				g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
				g2.drawString(formatar(maxChuva * proporcao), 24, y + 4);

				String textoDireita = formatar(maxVolume * proporcao);
				int larguraTexto = g2.getFontMetrics().stringWidth(textoDireita);
				g2.drawString(textoDireita, getWidth() - larguraTexto - 24, y + 4);

				g2.setColor(COR_GRADE);
			}

			g2.setColor(new Color(203, 213, 225));
			g2.drawRect(inicioX, margemTopo, larguraUtil, alturaUtil);

			int quantidade = dados.getDatas().size();
			double passo = quantidade > 1 ? (double) larguraUtil / (quantidade - 1) : 0;
			int saltoRotulos = Math.max(1, quantidade / 10);

			Integer ultimoXChuva = null;
			Integer ultimoYChuva = null;
			Integer ultimoXVolume = null;
			Integer ultimoYVolume = null;

			for (int i = 0; i < quantidade; i++) {
				int x = quantidade > 1 ? inicioX + (int) Math.round(i * passo) : inicioX + (larguraUtil / 2);
				int yChuva = baseY - (int) Math.round((dados.getChuvas().get(i) / maxChuva) * alturaUtil);
				int yVolume = baseY - (int) Math.round((dados.getVolumes().get(i) / maxVolume) * alturaUtil);

				g2.setStroke(new BasicStroke(3f));
				g2.setColor(COR_CHUVA);
				if (ultimoXChuva != null && ultimoYChuva != null) {
					g2.drawLine(ultimoXChuva, ultimoYChuva, x, yChuva);
				}
				g2.fillOval(x - 5, yChuva - 5, 10, 10);

				g2.setColor(COR_VOLUME);
				if (ultimoXVolume != null && ultimoYVolume != null) {
					g2.drawLine(ultimoXVolume, ultimoYVolume, x, yVolume);
				}
				g2.fillOval(x - 5, yVolume - 5, 10, 10);

				g2.setColor(COR_SUBTEXTO);
				g2.drawLine(x, baseY, x, baseY + 6);
				if (i % saltoRotulos == 0 || i == quantidade - 1) {
					String data = dados.getDatas().get(i);
					int larguraTexto = g2.getFontMetrics().stringWidth(data);
					g2.drawString(data, x - (larguraTexto / 2), baseY + 24);
				}

				if (quantidade <= 8) {
					g2.setColor(COR_CHUVA);
					g2.drawString(formatar(dados.getChuvas().get(i)), x - 12, yChuva - 12);
					g2.setColor(COR_VOLUME);
					g2.drawString(formatar(dados.getVolumes().get(i)), x - 12, yVolume - 12);
				}

				ultimoXChuva = x;
				ultimoYChuva = yChuva;
				ultimoXVolume = x;
				ultimoYVolume = yVolume;
			}

			g2.setColor(COR_CHUVA);
			g2.setFont(new Font("SansSerif", Font.BOLD, 13));
			g2.drawString("Chuva (mm)", inicioX, 16);

			g2.setColor(COR_VOLUME);
			String volumeTexto = "Volume útil (%)";
			int larguraVolumeTexto = g2.getFontMetrics().stringWidth(volumeTexto);
			g2.drawString(volumeTexto, fimX - larguraVolumeTexto, 16);

			g2.setColor(COR_SUBTEXTO);
			g2.setFont(new Font("SansSerif", Font.BOLD, 13));
			String eixoXTexto = "Datas";
			int larguraEixoX = g2.getFontMetrics().stringWidth(eixoXTexto);
			g2.drawString(eixoXTexto, inicioX + (larguraUtil / 2) - (larguraEixoX / 2), getHeight() - 24);

			g2.dispose();
		}

		private double ajustarMaximo(List<Double> valores) {
			double maximo = 1.0;
			for (Double valor : valores) {
				if (valor > maximo) {
					maximo = valor;
				}
			}
			return Math.ceil(maximo * 1.15);
		}

		private String formatar(double valor) {
			return String.format("%.1f", valor);
		}

	}

}
