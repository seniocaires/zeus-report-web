package zeusreportweb.exception;

/**
 * Classe base para criação das classes de exceção.
 * @author Senio Caires
 */
public class ProjetoException extends Exception {

	/* ------------------------------
	 * CONSTANTES
	 * ------------------------------
	 */

	/**
	 * serialVersionUID gerado automaticamente.
	 * @author Senio Caires
	 */
	private static final long serialVersionUID = 1L;

	/* ------------------------------
	 * CONSTRUTORES
	 * ------------------------------
	 */

	/**
	 * Construtor padrão. <br/>
	 * Mensagem padrão da exceção: Ocorreu um erro na ao executar o projeto.
	 * @author Senio Caires
	 */
	public ProjetoException() {
		super("Ocorreu um erro na ao executar o projeto.");
	}

	/**
	 * Construtor passando uma mensagem personalizada.
	 * @param mensagem - Mensagem
	 * @author Senio Caires
	 */
	public ProjetoException(final String mensagem) {
		super(mensagem);
	}
}