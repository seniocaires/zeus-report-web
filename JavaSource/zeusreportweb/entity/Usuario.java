package zeusreportweb.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade para representar os usuários do Zeus.
 * @author Senio Caires
 */
public class Usuario {

	/**
	 * Código do usuário.
	 * @author Senio Caires
	 */
	private String codigo;

	/**
	 * Nome do usuário.
	 * @author Senio Caires
	 */
	private String nome;

	/**
	 * Login do usuário.
	 * @author Senio Caires
	 */
	private String login;

	/**
	 * Senha do usuário.
	 * @author Senio Caires
	 */
	private String senha;

	/**
	 * Lista de registros de ponto do usuário.
	 * @author Senio Caires
	 */
	private List<Registro> registros;

	/**
	 * Retorna o código do usuário.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getCodigo() {
		return codigo;
	}

	/**
	 * Altera o código do usuário.
	 * @author Senio Caires
	 * @param codigoParametro - Código do usuário
	 */
	public final void setCodigo(final String codigoParametro) {
		this.codigo = codigoParametro;
	}

	/**
	 * Retorna o nome do usuário.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getNome() {
		return nome;
	}

	/**
	 * Altera o nome do usuário.
	 * @author Senio Caires
	 * @param nomeParametro - Nome do usuário
	 */
	public final void setNome(final String nomeParametro) {
		this.nome = nomeParametro;
	}

	/**
	 * Retorna a lista de registros de ponto.
	 * @author Senio Caires
	 * @return {@link List}<{@link Registro}>
	 */
	public final List<Registro> getRegistros() {

		if (registros == null) {
			registros = new ArrayList<Registro>();
		}

		return registros;
	}

	/**
	 * Altera a lista de registros de ponto.
	 * @author Senio Caires
	 * @param registrosParametro - Registros de ponto
	 */
	public final void setRegistros(final List<Registro> registrosParametro) {
		this.registros = registrosParametro;
	}

	/**
	 * Retorna o login do usuário.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getLogin() {

		if (login != null) {
			login = login.toUpperCase();
		}

		return login;
	}

	/**
	 * Altera o login.
	 * @author Senio Caires
	 * @param loginParametro
	 */
	public final void setLogin(final String loginParametro) {
		this.login = loginParametro;
	}

	/**
	 * Retorna a senha do usuário.
	 * @author Senio Caires
	 * @return {@link String}
	 */
	public final String getSenha() {
		return senha;
	}

	/**
	 * Altera a senha.
	 * @author Senio Caires
	 * @param senhaParametro
	 */
	public final void setSenha(final String senhaParametro) {
		this.senha = senhaParametro;
	}
}
