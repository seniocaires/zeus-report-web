package zeusreportweb.entity;

import java.util.Calendar;
import java.util.Date;

import com.sporeon.baseutil.DataUtil;

/**
 * Entidade para representar as datas previstas.
 * @author Senio Caires
 */
public class Previsto {

	/**
	 * Data formatada.
	 * @author Senio Caires
	 */
	private Date data;

	/**
	 * Horas previstas.
	 * @author Senio Caires
	 */
	private String horas;

	public Previsto(Date data, String horas) {
		this.data = data;
		this.horas = horas;
	}

	/**
	 * Retorna a data.
	 * @author Senio Caires
	 * @return {@link Date}
	 */
	public final Date getData() {
		return data;
	}

	/**
	 * Retorna a data formatada.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getDataFormatada() {
		return DataUtil.dateParaString(getData());
	}

	/**
	 * Retorna a descrição do dia da semana.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getDiaSemana() {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(getData());

		switch (calendar.get(Calendar.DAY_OF_WEEK)) {
		case 1:
			return "Dom";
		case 2:
			return "Seg";
		case 3:
			return "Ter";
		case 4:
			return "Qua";
		case 5:
			return "Qui";
		case 6:
			return "Sex";
		default:
			return "Sáb";
		}
	}

	/**
	 * Retorna as horas previstas.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHoras() {
		return horas;
	}

	@Override
	public String toString() {
		return getDataFormatada();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getDataFormatada() == null) ? 0 : getDataFormatada().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Previsto other = (Previsto) obj;
		if (getDataFormatada() == null) {
			if (other.getDataFormatada() != null)
				return false;
		} else if (!getDataFormatada().equals(other.getDataFormatada()))
			return false;
		return true;
	}
}
