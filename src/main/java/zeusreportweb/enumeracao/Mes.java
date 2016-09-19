package zeusreportweb.enumeracao;

/**
 * Meses.
 * @author Senio Caires
 */
public enum Mes {

  /**
   * Janeiro.
   * @author Senio Caires
   */
  JANEIRO(1, "Janeiro"),

  /**
   * Fevereiro.
   * @author Senio Caires
   */
  FEVEREIRO(2, "Fevereiro"),

  /**
   * Março.
   * @author Senio Caires
   */
  MARCO(3, "Março"),

  /**
   * ABRIL.
   * @author Senio Caires
   */
  ABRIL(4, "Abril"),

  /**
   * MAIO.
   * @author Senio Caires
   */
  MAIO(5, "Maio"),

  /**
   * Junho.
   * @author Senio Caires
   */
  JUNHO(6, "Junho"),

  /**
   * Julho.
   * @author Senio Caires
   */
  JULHO(7, "Julho"),

  /**
   * Agosto.
   * @author Senio Caires
   */
  AGOSTO(8, "Agosto"),

  /**
   * Setembro.
   * @author Senio Caires
   */
  SETEMBRO(9, "Setembro"),

  /**
   * Outubro.
   * @author Senio Caires
   */
  OUTUBRO(10, "Outubro"),

  /**
   * Novembro.
   * @author Senio Caires
   */
  NOVEMBRO(11, "Novembro"),

  /**
   * Dezembro.
   * @author Senio Caires
   */
  DEZEMBRO(12, "Dezembro");

  /**
   * Número.
   * @author Senio Caires
   */
  private Integer numero;

  /**
   * Descrição.
   * @author Senio Caires
   */
  private String descricao;

  /**
   * Construtor.
   * @author Senio Caires
   * @param numero - Número
   * @param descricao - Descrição
   */
  private Mes(Integer numero, String descricao) {
    this.numero = numero;
    this.descricao = descricao;
  }

  /**
   * Retorna o número.
   * @author Senio Caires
   * @return {@link String}
   */
  public Integer getNumero() {
    return this.numero;
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
