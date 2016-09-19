package zeusreportweb.enumeracao;

/**
 * Perfis.
 * @author Senio Caires
 */
public enum Perfil {

	/**
	 * PMMG.
	 * @author Senio Caires
	 */
	PMMG("PMMG"),

	/**
	 * PD Case
	 * @author Senio Caires
	 */
	PD_CASE("PD Case");

	/**
	 * Descrição.
	 * @author Senio Caires
	 */
	private String descricao;

	/**
	 * Construtor.
	 * @author Senio Caires
	 * @param descricao - Descrição
	 */
	private Perfil(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * Retorna a descrição.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public String getDescricao() {
		return this.descricao;
	}
}
