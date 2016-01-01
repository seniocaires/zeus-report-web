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

	/**
	 * Horas previstas para a data.
	 * @author Senio Caires
	 */
	private String horasPrevistas = "00:00";

	/**
	 * Total de horas realizadas na data.
	 * @author Senio Caires
	 */
	private String totalDia = "00:00";

	/**
	 * Saldo de horas da data.
	 * @author Senio Caires
	 */
	private String saldoDia = "00:00";

	/**
	 * Saldo acumulado até a data.
	 * @author Senio Caires
	 */
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

	/**
	 * Retorna o horário de acordo com o número passado por parâmetro.
	 * @author Senio Caires
	 * @param horario
	 * @return {@link String}
	 */
	private final String getHorario(int horario) {

		String retorno = null;

		try {
			retorno = getHorarios().get(horario);
		} catch (IndexOutOfBoundsException e) {
			retorno = "";
		}

		return retorno;
	}

	/**
	 * Retorna a primeira posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario1() {
		return getHorario(0);
	}

	/**
	 * Retorna a segunda posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario2() {
		return getHorario(1);
	}

	/**
	 * Retorna a terceira posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario3() {
		return getHorario(2);
	}

	/**
	 * Retorna a quarta posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario4() {
		return getHorario(3);
	}

	/**
	 * Retorna a quinta posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario5() {
		return getHorario(4);
	}

	/**
	 * Retorna a sexta posição da lista de horários.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorario6() {
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

	/**
	 * Informa se o saldo do dia foi positivo.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	public final Boolean getSaldoDiaPositivo() {
		return getSaldoDia().contains("+");
	}

	/**
	 * Informa se o saldo do dia foi negativo.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	public final Boolean getSaldoDiaNegativo() {
		return getSaldoDia().contains("-");
	}

	/**
	 * Informa se o saldo acumulado foi positivo.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	public final Boolean getSaldoAcumuladoPositivo() {
		return getSaldoAcumulado().contains("+");
	}

	/**
	 * Informa se o saldo acumulado foi negativo.
	 * @author Senio Caires
	 * @return {@link Boolean}
	 */
	public final Boolean getSaldoAcumuladoNegativo() {
		return getSaldoAcumulado().contains("-");
	}

	/**
	 * Retorna o total do dia.</br>
	 * Se estiver vazio, retorna 00:00
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getTotalDiaNaoVazio() {
		return getTotalDia() == null || "".equals(getTotalDia()) ? "00:00" : getTotalDia();
	}

	/**
	 * Retorna o total do dia.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getTotalDia() {
		return totalDia;
	}

	/**
	 * Altera o total do dia.
	 * @author Senio Caires
	 * @param totalDia
	 */
	public final void setTotalDia(final String totalDia) {
		this.totalDia = totalDia;
	}

	/**
	 * Retorna o saldo do dia.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getSaldoDia() {
		return saldoDia;
	}

	/**
	 * Altera o saldo do dia.
	 * @author Senio Caires
	 * @param saldoDia
	 */
	public final void setSaldoDia(final String saldoDia) {
		this.saldoDia = saldoDia;
	}

	/**
	 * Retorna o saldo acumulado.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getSaldoAcumulado() {
		return saldoAcumulado;
	}

	/**
	 * Altera o saldo acumulado.
	 * @author Senio Caires
	 * @param saldoAcumulado
	 */
	public final void setSaldoAcumulado(final String saldoAcumulado) {
		this.saldoAcumulado = saldoAcumulado;
	}

	/**
	 * Retorna as horas previstas.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getHorasPrevistas() {
		return horasPrevistas;
	}

	/**
	 * Altera as horas previstas.
	 * @author Senio Caires
	 * @param horasPrevistas
	 */
	public final void setHorasPrevistas(final String horasPrevistas) {
		this.horasPrevistas = horasPrevistas;
	}
}
