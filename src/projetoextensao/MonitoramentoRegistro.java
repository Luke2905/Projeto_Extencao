package projetoextensao;

import java.time.LocalDate;

public class MonitoramentoRegistro {

	// Atributos que representam cada linha de monitoramento
	private final int id;
	private final String represa;
	private final LocalDate data;
	private final double chuvaMm;
	private final double volumeUtilPercent;
	private final double chuvaAcumuladaMesMm;

	public MonitoramentoRegistro(int id, String represa, LocalDate data, double chuvaMm, double volumeUtilPercent,
			double chuvaAcumuladaMesMm) {
		this.id = id;
		this.represa = represa;
		this.data = data;
		this.chuvaMm = chuvaMm;
		this.volumeUtilPercent = volumeUtilPercent;
		this.chuvaAcumuladaMesMm = chuvaAcumuladaMesMm;
	}

	public int getId() {
		return id;
	}

	public String getRepresa() {
		return represa;
	}

	public LocalDate getData() {
		return data;
	}

	public double getChuvaMm() {
		return chuvaMm;
	}

	public double getVolumeUtilPercent() {
		return volumeUtilPercent;
	}

	public double getChuvaAcumuladaMesMm() {
		return chuvaAcumuladaMesMm;
	}
}
