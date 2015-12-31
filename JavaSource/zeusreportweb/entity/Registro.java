package zeusreportweb.entity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.sporeon.baseutil.DataUtil;

/**
 * Entidade para representar os registros de ponto dos usuários do Zeus.
 * @author Senio Caires
 */
public class Registro {

	/**
	 * Dono do registro de ponto.
	 * @author Senio Caires
	 */
	private Usuario usuario;

	/**
	 * Data do registro.
	 * @author Senio Caires
	 */
	private Date data;

	/**
	 * Horários registrados.
	 * @author Senio Caires
	 */
	private List<String> horarios;

	private String horasPrevistas = "00:00";

	private String totalDia = "00:00";

	private String saldoDia = "00:00";

	private String saldoAcumulado = "00:00";

	/**
	 * Construtor.
	 * @author Senio Caires
	 * @param usuarioParametro - Usuário do zeus
	 * @param dataParametro - Data do registro
	 */
	public Registro(Usuario usuarioParametro, Date dataParametro) {
		this.usuario = usuarioParametro;
		this.data = dataParametro;
	}

	/**
	 * Retorna o usuário.
	 * @author Senio Caires
	 * @return
	 */
	public final Usuario getUsuario() {
		return usuario;
	}

	/**
	 * Altera o usuário
	 * @author Senio Caires
	 * @param usuarioParametro - Usuário
	 */
	public final void setUsuario(final Usuario usuarioParametro) {
		this.usuario = usuarioParametro;
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
	 * Altera a data.
	 * @author Senio Caires
	 * @param dataParametro - Data do registro
	 */
	public final void setData(final Date dataParametro) {
		this.data = dataParametro;
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
	 * Retorna a lista de horários.
	 * @author Senio Caires
	 * @return {@link List}<{@link String}>
	 */
	public final List<String> getHorarios() {

		if (horarios == null) {
			horarios = new ArrayList<String>();
		}

		return horarios;
	}

	/**
	 * Altera a lista de horários.
	 * @author Senio Caires
	 * @param horariosParametro - Horários
	 */
	public final void setHorarios(final List<String> horariosParametro) {
		this.horarios = horariosParametro;
	}

	private String getHorario(int horario) {

		String retorno = null;

		try {
			retorno = getHorarios().get(horario);
		} catch (IndexOutOfBoundsException e) {
			retorno = "";
		}

		return retorno;
	}

	public String getHorario1() {
		return getHorario(0);
	}

	public String getHorario2() {
		return getHorario(1);
	}

	public String getHorario3() {
		return getHorario(2);
	}

	public String getHorario4() {
		return getHorario(3);
	}

	public String getHorario5() {
		return getHorario(4);
	}

	public String getHorario6() {
		return getHorario(5);
	}

	/**
	 * Retorna a lista de horários.
	 * @author Senio Caires
	 * @return {@link List}<{@link Date}>
	 */
	public final List<Date> getHorariosComData() {

		List<Date> resultado = new ArrayList<Date>();

		for (String horario : getHorarios()) {
			resultado.add(DataUtil.stringParaDate(DataUtil.dateParaString(getData()) + " " + horario + ":00", "dd/MM/yyyy HH:mm", "dd/MM/yyyy HH:mm"));
		}

		return resultado;
	}

	public Boolean getSaldoDiaPositivo() {
		return getSaldoDia().contains("+");
	}

	public Boolean getSaldoDiaNegativo() {
		return getSaldoDia().contains("-");
	}

	public Boolean getSaldoAcumuladoPositivo() {
		return getSaldoAcumulado().contains("+");
	}

	public Boolean getSaldoAcumuladoNegativo() {
		return getSaldoAcumulado().contains("-");
	}

	public String getTotalDiaNaoVazio() {
		return getTotalDia() == null || "".equals(getTotalDia()) ? "00:00" : getTotalDia();
	}

	public String getTotalDia() {
		return totalDia;
	}

	public void setTotalDia(String totalDia) {
		this.totalDia = totalDia;
	}

	public String getSaldoDia() {
		return saldoDia;
	}

	public void setSaldoDia(String saldoDia) {
		this.saldoDia = saldoDia;
	}

	public String getSaldoAcumulado() {
		return saldoAcumulado;
	}

	public void setSaldoAcumulado(String saldoAcumulado) {
		this.saldoAcumulado = saldoAcumulado;
	}

	public String getHorasPrevistas() {
		return horasPrevistas;
	}

	public void setHorasPrevistas(String horasPrevistas) {
		this.horasPrevistas = horasPrevistas;
	}
}
