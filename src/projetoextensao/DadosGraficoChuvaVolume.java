package projetoextensao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DadosGraficoChuvaVolume {

	private final List<String> datas;
	private final List<Double> chuvas;
	private final List<Double> volumes;

	public DadosGraficoChuvaVolume(List<String> datas, List<Double> chuvas, List<Double> volumes) {
		if (datas.size() != chuvas.size() || datas.size() != volumes.size()) {
			throw new IllegalArgumentException("As listas do gráfico precisam ter o mesmo tamanho.");
		}

		this.datas = Collections.unmodifiableList(new ArrayList<>(datas));
		this.chuvas = Collections.unmodifiableList(new ArrayList<>(chuvas));
		this.volumes = Collections.unmodifiableList(new ArrayList<>(volumes));
	}

	public List<String> getDatas() {
		return datas;
	}

	public List<Double> getChuvas() {
		return chuvas;
	}

	public List<Double> getVolumes() {
		return volumes;
	}

	public boolean estaVazio() {
		return datas.isEmpty();
	}

}
