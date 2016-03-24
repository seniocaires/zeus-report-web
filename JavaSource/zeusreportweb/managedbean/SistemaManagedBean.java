package zeusreportweb.managedbean;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.sporeon.baseutil.ComparacaoUtil;
import com.sporeon.baseutil.DataUtil;
import com.sporeon.baseutil.ManipulacaoUtil;
import com.sporeon.facesutil.util.JSFHelper;

import zeusreportweb.entity.Previsto;
import zeusreportweb.entity.Registro;
import zeusreportweb.entity.Usuario;
import zeusreportweb.enumeracao.Perfil;
import zeusreportweb.exception.ProjetoException;

/**
 * Managed bean da aplicação.
 * @author Senio Caires
 */
@ViewScoped
@ManagedBean(name = "managedBean")
public class SistemaManagedBean implements Serializable {

  /**
   * serialVersionUID.
   * @author Senio Caires
   */
  private static final long serialVersionUID = -3593184308580139798L;

  /**
   * Type para lista de previstos.
   * @author Senio Caires
   */
  private static final Type PREVISTO_TYPE = new TypeToken<List<Previsto>>() {
  }.getType();

  /**
   * Usuário do Zeus.
   * @author Senio Caires
   */
  private Usuario usuario;

  /**
   * Data/horas previstas.
   * @author Senio Caires
   */
  private List<Previsto> previstos;

  /**
   * Mês para gerar as datas previstas.
   * @author Senio Caires
   */
  private String mes;

  /**
   * Ano para gerar as datas previstas.
   * @author Senio Caires
   */
  private Integer ano;

  /**
   * Url do relatório.
   * @author Senio Caires
   */
  private String urlRelatorio;

  /**
   * Perfil.
   * @author Senio Caires
   */
  private Perfil perfil;

  /**
   * Gráfico de linha.
   * @author Senio Caires
   */
  private LineChartModel graficoLinha;

  /**
   * Inicializar o managed bean.
   * @author Senio Caires
   */
  @PostConstruct
  public void init() {

    getUsuario().setLogin(getCookie("login").getValue());

    if (!ComparacaoUtil.isVazio(getCookie("perfil").getValue())) {
      setPerfil(Perfil.valueOf(getCookie("perfil").getValue()));
    }

    try {
      setAno(Integer.valueOf(getCookie("ano").getValue()));
      setMes(URLDecoder.decode(getCookie("mes").getValue(), "UTF-8"));
    } catch (NumberFormatException excecao) {
      setAno(getAnoAtual());
      setMes(DataUtil.getMesPorExtenso(getMesAtual()));
    } catch (UnsupportedEncodingException e) {
      setAno(getAnoAtual());
      setMes(DataUtil.getMesPorExtenso(getMesAtual()));
    }

    Gson gson = new Gson();

    this.previstos = gson.fromJson(getCookie("previstos").getValue(), PREVISTO_TYPE);
  }

  /**
   * Cria o gráfico de linha.
   * @author Senio Caires
   */
  private void criarGraficoLinha() {

    graficoLinha = preencherDadosGraficoLinha();
    graficoLinha.setTitle("Previsto / Realizado");
    graficoLinha.setLegendPosition("e");
    graficoLinha.setAnimate(true);

    Axis eixoY = graficoLinha.getAxis(AxisType.Y);
    eixoY.setTickFormat(" ");
    eixoY.setMin(0);
    eixoY.setMax(getMaiorHorario() + 2);

    DateAxis eixoX = new DateAxis();
    eixoX.setTickAngle(90);
    eixoX.setMin(DataUtil.dateParaString(getDataInicial(), "yyyy-MM-dd"));
    eixoX.setMax(DataUtil.dateParaString(getDataFinal(), "yyyy-MM-dd"));
    eixoX.setTickFormat("%d/%m/%y");
    graficoLinha.getAxes().put(AxisType.X, eixoX);
  }

  /**
   * Retorna o maior horário da lista de previsto e realizado.</br>
   * Usado para gráfico.
   * @author Senio Caires
   * @return {@link Float}
   */
  private Float getMaiorHorario() {

    Float retorno = 0.0F;

    for (Registro registro : getUsuario().getRegistros()) {
      if (retorno < Float.valueOf(registro.getTotalDiaNaoVazio().replace(":", "."))) {
        retorno = Float.valueOf(registro.getTotalDiaNaoVazio().replace(":", "."));
      }
      if (retorno < Float.valueOf(registro.getHorasPrevistas().replace(":", "."))) {
        retorno = Float.valueOf(registro.getHorasPrevistas().replace(":", "."));
      }
    }

    return retorno;
  }

  /**
   * Preenche os dados do gráfico de linha.
   * @author Senio Caires
   * @return {@link LineChartModel}
   */
  private LineChartModel preencherDadosGraficoLinha() {

    LineChartModel modelo = new LineChartModel();

    LineChartSeries previsto = new LineChartSeries();
    LineChartSeries realizado = new LineChartSeries();
    realizado.setFill(true);
    previsto.setLabel("Previsto");
    realizado.setLabel("Realizado");

    for (Registro registro : getUsuario().getRegistros()) {

      realizado.set(DataUtil.dateParaString(registro.getData(), "yyyy-MM-dd"), Float.valueOf(registro.getTotalDiaNaoVazio().replace(":", ".")));
      previsto.set(DataUtil.dateParaString(registro.getData(), "yyyy-MM-dd"), Float.valueOf(registro.getHorasPrevistas().replace(":", ".")));
    }

    modelo.addSeries(realizado);
    modelo.addSeries(previsto);

    return modelo;
  }

  /**
   * Salvar os dados em cookies.
   * @author Senio Caires
   */
  private void salvarCookies() {

    setCookie("login", getUsuario().getLogin(), -1);

    setCookie("ano", getAno().toString(), -1);

    setCookie("mes", getMes(), -1);

    setCookie("perfil", getPerfil().toString(), -1);

    Gson gson = new Gson();
    setCookie("previstos", gson.toJson(getPrevistos(), PREVISTO_TYPE), -1);
  }

  /**
   * Gerar o relatório do Zeus.
   * @author Senio Caires
   */
  public void gerarRelatorio() {

    try {
      acessarZeus();
    } catch (ProjetoException excecao) {
      JSFHelper.addGlobalMessageError("", excecao.getMessage());
      return;
    }

    try {

      PdfReader reader = new PdfReader(new URL(getUrlRelatorio()).openStream());
      String conteudoRelatorioZeus = PdfTextExtractor.getTextFromPage(reader, 1);

      try {

        String nome = (conteudoRelatorioZeus.split("Período: " + getDataInicialFormatada() + " " + getDataFinalFormatada())[0]).split("Relatório de horário")[1].trim();

        if (nome != null && !nome.trim().equals("")) {

          preencherDadosUsuarioZeus(conteudoRelatorioZeus, nome);

          List<String> totaisDias = new ArrayList<String>();
          List<String> saldosDias = new ArrayList<String>();
          String saldoAcumulado = "000:00";

          for (Registro registroIndice : getUsuario().getRegistros()) {

            boolean modulo = false;
            Date horarioAnteriorComData = new Date();
            String totalDia = "";
            String diferencaHorarios = "";
            for (Date horarioComDataIndice : registroIndice.getHorariosComData()) {

              if (modulo) {

                List<String> horariosComDataParaSoma = new ArrayList<String>();
                horariosComDataParaSoma.add(DataUtil.dateParaString(horarioAnteriorComData, "HH:mm:ss"));
                horariosComDataParaSoma.add(DataUtil.dateParaString(horarioComDataIndice, "HH:mm:ss"));
                diferencaHorarios = diferencaHorarios(DataUtil.dateParaString(horarioAnteriorComData, "MM/dd/yyyy HH:mm:ss"), DataUtil.dateParaString(horarioComDataIndice, "MM/dd/yyyy HH:mm:ss"));

                if (!totalDia.equals("")) {
                  totalDia = somaHorarios(totalDia, diferencaHorarios, 2);
                } else {
                  totalDia = diferencaHorarios;
                }
              }

              modulo = !modulo;
              horarioAnteriorComData = horarioComDataIndice;
            }

            registroIndice.setTotalDia(totalDia);
            if (!totalDia.equals("")) {
              totaisDias.add(totalDia);
            }

            String horasDataPrevista = getHorasDataPrevista(DataUtil.dateParaString(registroIndice.getData()), 2);
            registroIndice.setHorasPrevistas(horasDataPrevista);

            String saldoDia = saldoDia((totalDia.equals("") ? "00:00" : totalDia), (horasDataPrevista.equals("") ? "00:00" : horasDataPrevista), 2);
            registroIndice.setSaldoDia(saldoDia);

            if (!saldoDia.equals("")) {
              saldosDias.add(saldoDia);
            }

            saldoAcumulado = acumulaSaldoDia(saldoAcumulado, saldoDia);
            registroIndice.setSaldoAcumulado(saldoAcumulado);
          }

          String somaTotalDia = "00:00";

          for (String totalDiaIndice : totaisDias) {
            somaTotalDia = somaHorarios(ManipulacaoUtil.adicionarChar('0', 6, somaTotalDia, true), ManipulacaoUtil.adicionarChar('0', 6, totalDiaIndice, true), 3);
          }

        }

      } catch (Exception e) {
        JSFHelper.addGlobalMessageError("", e.getMessage());
      }

    } catch (IOException e) {
      JSFHelper.addGlobalMessageError("", e.getMessage());
    }

    criarGraficoLinha();
  }

  /**
   * @author Senio Caires
   * @param conteudoRelatorioZeus
   * @param nomeUsuarioZeus
   */
  private final void preencherDadosUsuarioZeus(String conteudoRelatorioZeus, String nomeUsuarioZeus) {

    getUsuario().setNome(nomeUsuarioZeus);

    String[] registrosNaoTratados = conteudoRelatorioZeus.split("Entrada Saída Entrada Saída Entrada Saída")[1].split("TOTAIS")[0].split("\n");
    Date dataIndice = getDataInicial();
    getUsuario().setRegistros(null);

    for (String registroNaoTratado : registrosNaoTratados) {

      if (!registroNaoTratado.contains(DataUtil.dateParaString(dataIndice))) {
        continue;
      } else {

        Registro registro = new Registro(getUsuario(), dataIndice);

        String[] horariosNaoTratados = registroNaoTratado.split(DataUtil.dateParaString(dataIndice))[1].trim().split("   ")[0].trim().split(" ");

        for (String horario : horariosNaoTratados) {
          if (horario != null && !horario.trim().equals("")) {
            registro.getHorarios().add(horario.trim());
          }
        }

        getUsuario().getRegistros().add(registro);
        dataIndice = DataUtil.adicionarDias(dataIndice, 1);
      }
    }
  }

  /**
   * @author Senio Caires
   * @param dataInicio
   * @param dataTermino
   * @return {@link String}
   */
  @SuppressWarnings("unused")
  public String diferencaHorarios(String dataInicio, String dataTermino) {

    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

    Date primeiraData = null;
    Date segundaData = null;
    long diferencaSegundos = 0;
    long diferencaoMinutos = 0;
    long diferencaHoras = 0;
    long diferencaDias = 0;

    try {

      primeiraData = format.parse(dataInicio);
      segundaData = format.parse(dataTermino);

      long diferenca = segundaData.getTime() - primeiraData.getTime();

      diferencaSegundos = diferenca / 1000 % 60;
      diferencaoMinutos = diferenca / (60 * 1000) % 60;
      diferencaHoras = diferenca / (60 * 60 * 1000) % 24;
      diferencaDias = diferenca / (24 * 60 * 60 * 1000);

    } catch (Exception e) {
      JSFHelper.addGlobalMessageError("", e.getMessage());
    }

    return ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaoMinutos), true);
  }

  /**
   * @author Senio Caires
   * @param data
   * @param digitosHora
   * @return {@link String}
   */
  public String getHorasDataPrevista(String data, int digitosHora) {

    String resultado = "00:00";

    for (Previsto previsto : getPrevistos()) {

      if (DataUtil.dateParaString(previsto.getData()).equals(data)) {
        resultado = previsto.getHoras();
        break;
      }
    }

    return ManipulacaoUtil.adicionarChar('0', digitosHora + 3, String.valueOf(resultado), true);
  }

  /**
   * @author Senio Caires
   * @param totalDia
   * @param previsto
   * @param digitosHora
   * @return {@link String}
   */
  public String saldoDia(String totalDia, String previsto, int digitosHora) {

    String resultado = "00:00";

    int horasTotalDia = Integer.valueOf(totalDia.split(":")[0]);
    int minutosTotalDia = Integer.valueOf(totalDia.split(":")[1]);
    int horasPrevisto = Integer.valueOf(previsto.split(":")[0]);
    int minutosPrevisto = Integer.valueOf(previsto.split(":")[1]);
    int diferencaHoras = 0;
    int diferencaMinutos = 0;
    double totalDiaDouble = Double.valueOf(totalDia.replace(":", "."));
    double totalPrevistoDouble = Double.valueOf(previsto.replace(":", "."));
    String sinal = "";

    if (totalDiaDouble > totalPrevistoDouble) {
      sinal = "+";
    } else if (totalDiaDouble < totalPrevistoDouble) {
      sinal = "-";
    } else {
      sinal = " ";
    }

    if (horasTotalDia > horasPrevisto) {

      diferencaHoras = horasTotalDia - horasPrevisto;

      if (minutosTotalDia > minutosPrevisto) {
        diferencaMinutos = minutosTotalDia - minutosPrevisto;
      } else if (minutosTotalDia < minutosPrevisto) {
        diferencaHoras--;
        diferencaMinutos = (minutosTotalDia + 60) - minutosPrevisto;
      }
    } else if (horasTotalDia < horasPrevisto) {

      diferencaHoras = horasPrevisto - horasTotalDia;

      if (minutosTotalDia > minutosPrevisto) {
        diferencaHoras--;
        diferencaMinutos = (minutosPrevisto + 60) - minutosTotalDia;
      } else if (minutosTotalDia < minutosPrevisto) {
        diferencaMinutos = minutosPrevisto - minutosTotalDia;
      }
    } else {
      if (minutosTotalDia > minutosPrevisto) {
        diferencaMinutos = minutosTotalDia - minutosPrevisto;
      } else if (minutosTotalDia < minutosPrevisto) {
        diferencaMinutos = minutosPrevisto - minutosTotalDia;
      }
    }

    resultado = sinal + ManipulacaoUtil.adicionarChar('0', digitosHora, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaMinutos), true);

    return resultado;
  }

  /**
   * @author Senio Caires
   * @param saldoAcumulado
   * @param saldoDia
   * @return {@link String}
   */
  public String acumulaSaldoDia(String saldoAcumulado, String saldoDia) {

    String resultado = "000:00";

    int horasSaldoAcumulado = Integer.valueOf(saldoAcumulado.split(":")[0].replace("-", "").replace("+", "").trim());
    int minutosSaldoAcumulado = Integer.valueOf(saldoAcumulado.split(":")[1]);
    int horasSaldoDia = Integer.valueOf(saldoDia.split(":")[0].replace("-", "").replace("+", "").trim());
    int minutosSaldoDia = Integer.valueOf(saldoDia.split(":")[1]);
    int diferencaHoras = 0;
    int diferencaMinutos = 0;
    String sinal = "";

    if (saldoDia.contains("+")) {

      if (saldoAcumulado.contains("+")) {
        diferencaHoras = horasSaldoAcumulado + horasSaldoDia + Integer.valueOf((minutosSaldoAcumulado + minutosSaldoDia) / 60);
        diferencaMinutos = ((minutosSaldoAcumulado + minutosSaldoDia) % 60);
        sinal = "+";
      } else if (saldoAcumulado.contains("-")) {

        if (horasSaldoAcumulado < horasSaldoDia) {

          diferencaHoras = horasSaldoDia - horasSaldoAcumulado;

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaHoras--;
            diferencaMinutos = (minutosSaldoDia + 60) - minutosSaldoAcumulado;
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
          }

          sinal = "+";
        } else if (horasSaldoAcumulado > horasSaldoDia) {

          diferencaHoras = horasSaldoAcumulado - horasSaldoDia;

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaHoras--;
            diferencaMinutos = (minutosSaldoAcumulado + 60) - minutosSaldoDia;
          }

          sinal = "-";
        } else {

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
            sinal = "-";
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
            sinal = "+";
          } else {
            sinal = " ";
          }
        }
      } else {
        diferencaHoras = horasSaldoDia;
        diferencaMinutos = minutosSaldoDia;
        sinal = "+";
      }
    } else if (saldoDia.contains("-")) {

      if (saldoAcumulado.contains("+")) {

        if (horasSaldoAcumulado > horasSaldoDia) {

          diferencaHoras = horasSaldoAcumulado - horasSaldoDia;

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
            sinal = "+";
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaHoras--;
            diferencaMinutos = (minutosSaldoAcumulado + 60) - minutosSaldoDia;
            sinal = "+";
          } else {
            diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
            sinal = " ";
          }
        } else if (horasSaldoAcumulado < horasSaldoDia) {

          diferencaHoras = horasSaldoDia - horasSaldoAcumulado;

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaHoras--;
            diferencaMinutos = (minutosSaldoDia + 60) - minutosSaldoAcumulado;
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
          }

          sinal = "-";
        } else {

          if (minutosSaldoAcumulado > minutosSaldoDia) {
            diferencaMinutos = minutosSaldoAcumulado - minutosSaldoDia;
            sinal = "+";
          } else if (minutosSaldoAcumulado < minutosSaldoDia) {
            diferencaMinutos = minutosSaldoDia - minutosSaldoAcumulado;
            sinal = "-";
          } else {
            sinal = " ";
          }
        }
      } else if (saldoAcumulado.contains("-")) {
        diferencaHoras = horasSaldoAcumulado + horasSaldoDia + Integer.valueOf((minutosSaldoAcumulado + minutosSaldoDia) / 60);
        diferencaMinutos = ((minutosSaldoAcumulado + minutosSaldoDia) % 60);
        sinal = "-";
      } else {

        diferencaHoras = horasSaldoDia;
        diferencaMinutos = minutosSaldoDia;

        if (saldoDia.contains("+")) {
          sinal = "+";
        } else if (saldoDia.contains("-")) {
          sinal = "-";
        } else {
          sinal = " ";
        }
      }
    } else {

      diferencaHoras = horasSaldoAcumulado;
      diferencaMinutos = minutosSaldoAcumulado;

      if (saldoAcumulado.contains("+")) {
        sinal = "+";
      } else if (saldoAcumulado.contains("-")) {
        sinal = "-";
      } else {
        sinal = " ";
      }
    }

    resultado = sinal + ManipulacaoUtil.adicionarChar('0', 3, String.valueOf(diferencaHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(diferencaMinutos), true);

    return resultado;
  }

  /**
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getTotalHorasRealizadas() {

    String resultado = "00:00";

    for (Registro registro : getUsuario().getRegistros()) {
      resultado = somaHorarios(resultado, registro.getTotalDiaNaoVazio(), 3);
    }

    return ManipulacaoUtil.adicionarChar('0', 6, String.valueOf(resultado), true);
  }

  /**
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getTotalHorasPrevistas() {

    String resultado = "00:00";

    for (Previsto previsto : getPrevistos()) {
      resultado = somaHorarios(resultado, previsto.getHoras(), 3);
    }

    return ManipulacaoUtil.adicionarChar('0', 6, String.valueOf(resultado), true);
  }

  /**
   * @author Senio Caires
   * @param horaInicial
   * @param horaFinal
   * @param digitosHora
   * @return {@link String}
   */
  public final String somaHorarios(String horaInicial, String horaFinal, int digitosHora) {

    int totalHoras = Integer.valueOf(horaInicial.split(":")[0]) + Integer.valueOf(horaFinal.split(":")[0]) + Integer.valueOf((Integer.valueOf(horaInicial.split(":")[1]) + Integer.valueOf(horaFinal.split(":")[1])) / 60);
    int totalMinutos = (Integer.valueOf(horaInicial.split(":")[1]) + Integer.valueOf(horaFinal.split(":")[1])) % 60;

    return ManipulacaoUtil.adicionarChar('0', digitosHora, String.valueOf(totalHoras), true) + ":" + ManipulacaoUtil.adicionarChar('0', 2, String.valueOf(totalMinutos), true);
  }

  /**
   * @author Senio Caires
   */
  public void gerarDatasPrevistas() {

    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DataUtil.getDataAtual());
    int ultimoDiaMes = DataUtil.ultimoDiaDoMes(DataUtil.getData(1, Integer.valueOf(DataUtil.getNumeroMes(getMes())) - 1, getAno()));
    Date data;
    this.previstos = new ArrayList<Previsto>();

    for (int dia = 1; dia <= ultimoDiaMes; dia++) {

      if (dia > ultimoDiaMes) {
        break;
      }

      data = DataUtil.getData(dia, Integer.valueOf(DataUtil.getNumeroMes(getMes())) - 1, getAno());
      calendar.setTime(data);

      if (Perfil.PMMG.equals(getPerfil())) {
        if (calendar.get(Calendar.DAY_OF_WEEK) != 1 && calendar.get(Calendar.DAY_OF_WEEK) != 7 && calendar.get(Calendar.DAY_OF_WEEK) != 4) {
          getPrevistos().add(new Previsto(data, "08:30"));
        } else if (calendar.get(Calendar.DAY_OF_WEEK) == 4) {
          getPrevistos().add(new Previsto(data, "06:00"));
        }
      } else {
        if (calendar.get(Calendar.DAY_OF_WEEK) != 1 && calendar.get(Calendar.DAY_OF_WEEK) != 7) {
          getPrevistos().add(new Previsto(data, "08:00"));
        }
      }
    }

    salvarCookies();
  }

  /**
   * @author Senio Caires
   * @param nome
   * @param valor
   * @param expiracao
   */
  public void setCookie(String nome, String valor, int expiracao) {

    FacesContext facesContext = FacesContext.getCurrentInstance();

    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
    Cookie cookie = null;

    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (int indice = 0; indice < cookies.length; indice++) {
        if (cookies[indice].getName().equals(nome)) {
          cookie = cookies[indice];
          break;
        }
      }
    }

    try {

      if (cookie != null) {

        cookie.setValue(URLEncoder.encode(valor, "UTF-8"));

      } else {
        cookie = new Cookie(nome, URLEncoder.encode(valor, "UTF-8"));
        cookie.setPath(request.getContextPath());
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    cookie.setMaxAge(expiracao);

    HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
    response.addCookie(cookie);
  }

  /**
   * @author Senio Caires
   * @param nome
   * @return {@link Cookie}
   */
  public Cookie getCookie(String nome) {

    FacesContext facesContext = FacesContext.getCurrentInstance();

    HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
    Cookie cookie = new Cookie(nome, "");

    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (int indice = 0; indice < cookies.length; indice++) {
        if (cookies[indice].getName().equals(nome)) {
          cookie = cookies[indice];
          return cookie;
        }
      }
    }

    return cookie;
  }

  /**
   * @author Senio Caires
   * @param previsto
   */
  public void removerPrevisto(Previsto previsto) {

    getPrevistos().remove(previsto);
    salvarCookies();
  }

  /**
   * Método para acessar o Zeus.
   * @author Senio Caires
   * @throws ProjetoException
   */
  private final void acessarZeus() throws ProjetoException {

    WebClient webClient = new WebClient(BrowserVersion.CHROME);

    efetuarLoginZeus(webClient);

    acessarRelatorioZeus(webClient);

    webClient.close();
  }

  /**
   * Método para acessar o relatório do Zeus e configurar os parâmetros do relatório.
   * @author Senio Caires
   * @param webClient
   * @throws ProjetoException
   */
  private void acessarRelatorioZeus(WebClient webClient) throws ProjetoException {

    HtmlPage paginaRegistroPonto;
    HtmlSubmitInput botaoImprimir;
    UnexpectedPage responsePaginaRelatorio;

    try {
      paginaRegistroPonto = webClient.getPage("http://sistemas.pdcase.com/zeusprod21/HRegistroPonto2.aspx");
    } catch (FailingHttpStatusCodeException excecao) {
      throw new ProjetoException("FailingHttpStatusCodeException. Erro ao acessar página de registro de ponto do Zeus. " + excecao.getMessage());
    } catch (MalformedURLException excecao) {
      throw new ProjetoException("MalformedURLException. Erro ao acessar página de registro de ponto do Zeus. " + excecao.getMessage());
    } catch (IOException excecao) {
      throw new ProjetoException("IOException. Erro ao acessar página de registro de ponto do Zeus. " + excecao.getMessage());
    }

    botaoImprimir = (HtmlSubmitInput) paginaRegistroPonto.getElementByName("BUTTON2");

    try {

      responsePaginaRelatorio = botaoImprimir.click();
      setUrlRelatorio(responsePaginaRelatorio.getWebResponse().getWebRequest().getUrl().toString());

    } catch (IOException excecao) {
      throw new ProjetoException("IOException. Erro ao obter resposta após acessar relatório do Zeus. " + excecao.getMessage());
    }
  }

  /**
   * Método para efetuar o login no Zeus.
   * @author Senio Caires
   * @param webClient
   * @throws ProjetoException
   */
  private void efetuarLoginZeus(WebClient webClient) throws ProjetoException {

    HtmlPage paginaLogin = null;
    HtmlPage responsePaginaLogin = null;
    HtmlTextInput campoLogin;
    HtmlPasswordInput campoSenha;
    HtmlSubmitInput botaoConfirmar;

    try {
      paginaLogin = webClient.getPage("http://sistemas.pdcase.com/zeusprod21/hacessarsistema.aspx");
    } catch (FailingHttpStatusCodeException excecao) {
      throw new ProjetoException("FailingHttpStatusCodeException. Erro ao acessar página de login do Zeus. " + excecao.getMessage());
    } catch (MalformedURLException excecao) {
      throw new ProjetoException("MalformedURLException. Erro ao acessar página de login do Zeus. " + excecao.getMessage());
    } catch (IOException excecao) {
      throw new ProjetoException("IOException. Erro ao acessar página de login do Zeus. " + excecao.getMessage());
    }

    campoLogin = (HtmlTextInput) paginaLogin.getElementById("_PESLOGIN");
    campoSenha = (HtmlPasswordInput) paginaLogin.getElementById("_PESSENHA");
    botaoConfirmar = (HtmlSubmitInput) paginaLogin.getElementByName("BUTTON1");

    campoLogin.setValueAttribute(getUsuario().getLogin());
    campoSenha.setValueAttribute(getUsuario().getSenha());

    try {
      responsePaginaLogin = (HtmlPage) botaoConfirmar.click();
    } catch (IOException excecao) {
      throw new ProjetoException("IOException. Erro ao obter resposta após efetuar login no Zeus. " + excecao.getMessage());
    }

    validarLoginZeus(responsePaginaLogin);
  }

  /**
   * Verifica se o login no Zeus ocorreu corretamente.
   * @author Senio Caires
   * @param responsePaginaLogin
   * @throws ProjetoException
   */
  private void validarLoginZeus(final HtmlPage responsePaginaLogin) throws ProjetoException {

    if (responsePaginaLogin.asText().contains("Informe Login e Senha")) {
      throw new ProjetoException("Informe o usuário e senha do Zeus.");
    }

    if (responsePaginaLogin.asText().contains("Usuário não cadastrado")) {
      throw new ProjetoException("Usuário do Zeus está incorreto.");
    }

    if (responsePaginaLogin.asText().contains("Senha inválida")) {
      throw new ProjetoException("Senha do Zeus está incorreta.");
    }
  }

  /**
   * Retorna o usuário
   * @return {@link Usuario}
   */
  public Usuario getUsuario() {

    if (usuario == null) {
      usuario = new Usuario();
    }

    return usuario;
  }

  /**
   * Altera o usuário.
   * @author Senio Caires
   * @param usuario
   */
  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  /**
   * @author Senio Caires
   * @return {@link List}<{@link String}>
   */
  public List<String> getMeses() {
    return DataUtil.mesPorExtensoList();
  }

  /**
   * @author Senio Caires
   * @return {@link List}<{@link Previsto}>
   */
  public List<Previsto> getPrevistos() {

    if (previstos == null) {
      previstos = new ArrayList<Previsto>();
    }

    return previstos;
  }

  /**
   * @author Senio Caires
   * @param previstos
   */
  public void setPrevistos(List<Previsto> previstos) {
    this.previstos = previstos;
  }

  /**
   * @author Senio Caires
   * @return {@link Boolean}
   */
  public Boolean getExibirRelatorio() {
    return !getUsuario().getRegistros().isEmpty();
  }

  /**
   * @author Senio Caires
   * @return {@link Boolean}
   */
  public Boolean getExibirPrevistos() {
    return !getPrevistos().isEmpty();
  }

  /**
   * @author Senio Caires
   * @return {@link String}
   */
  public String getMes() {

    if (mes == null) {

      Calendar calendar = Calendar.getInstance();
      calendar.setTime(DataUtil.getDataAtual());

      mes = DataUtil.getMesPorExtenso(calendar.get(Calendar.MONTH) + 1);
    }

    return mes;
  }

  /**
   * @author Senio Caires
   * @param mes
   */
  public void setMes(String mes) {
    this.mes = mes;
  }

  /**
   * @author Senio Caires
   * @return {@link String}
   */
  public String getUrlRelatorio() {
    return urlRelatorio + "?" + getUsuario().getCodigo() + "," + getDataInicialFormatadaParametroRelatorio() + "," + getDataFinalFormatadaParametroRelatorio();
  }

  /**
   * @author Senio Caires
   * @param urlRelatorioParametro
   */
  public void setUrlRelatorio(String urlRelatorioParametro) {

    String[] urlPartes = urlRelatorioParametro.split("\\?");
    String[] parametrosPartes = urlPartes[1].split(",");

    urlRelatorio = urlPartes[0];

    getUsuario().setCodigo(parametrosPartes[0]);
  }

  /**
   * Retorna a data inicial formatada.
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getDataInicialFormatada() {
    return getPrevistos().get(0).getDataFormatada();
  }

  /**
   * Retorna a data final formatada.
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getDataFinalFormatada() {
    return getPrevistos().get(getPrevistos().size() - 1).getDataFormatada();
  }

  /**
   * Retorna a data inicial.
   * @author Senio Caires
   * @return {@link Date}
   */
  public final Date getDataInicial() {
    return DataUtil.stringParaDate(getDataInicialFormatada());
  }

  /**
   * Retorna a data final.
   * @author Senio Caires
   * @return {@link Date}
   */
  public final Date getDataFinal() {
    return DataUtil.stringParaDate(getDataFinalFormatada());
  }

  /**
   * Retorna a data inicial formatada como parâmetro para o relatório.
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getDataInicialFormatadaParametroRelatorio() {

    StringBuilder retorno = new StringBuilder();

    String[] dataPartes = getDataInicialFormatada().split("/");
    retorno.append(dataPartes[2]);
    retorno.append(dataPartes[1]);
    retorno.append(dataPartes[0]);

    return retorno.toString();
  }

  /**
   * Retorna a data final formatada como parâmetro para o relatório.
   * @author Senio Caires
   * @return {@link String}
   */
  public final String getDataFinalFormatadaParametroRelatorio() {

    StringBuilder retorno = new StringBuilder();

    String[] dataPartes = getDataFinalFormatada().split("/");
    retorno.append(dataPartes[2]);
    retorno.append(dataPartes[1]);
    retorno.append(dataPartes[0]);

    return retorno.toString();
  }

  /**
   * @author Senio Caires
   * @return {@link Integer}
   */
  public Integer getAnoAtual() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DataUtil.getDataAtual());
    return calendar.get(Calendar.YEAR);
  }

  /**
   * @author Senio Caires
   * @return {@link Integer}
   */
  public Integer getMesAtual() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(DataUtil.getDataAtual());
    return calendar.get(Calendar.MONTH) + 1;
  }

  /**
   * @author Senio Caires
   * @return {@link Integer}
   */
  public Integer getAnoAnterior() {
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.YEAR) - 1;
  }

  /**
   * @author Senio Caires
   * @return {@link Integer}
   */
  public Integer getAno() {

    if (ano == null) {
      ano = getAnoAtual();
    }

    return ano;
  }

  /**
   * @author Senio Caires
   * @param ano
   */
  public void setAno(Integer ano) {
    this.ano = ano;
  }

  /**
   * @author Senio Caires
   * @return {@link LineChartModel}
   */
  public LineChartModel getGraficoLinha() {
    return graficoLinha;
  }

  /**
   * @author Senio Caires
   * @param graficoLinha
   */
  public void setGraficoLinha(LineChartModel graficoLinha) {
    this.graficoLinha = graficoLinha;
  }

  /**
   * Retorna a lista de perfis.
   * @author Senio Caires
   * @return {@link List}<{@link Perfil}>
   */
  public List<Perfil> getPerfis() {

    List<Perfil> retorno = new ArrayList<>();

    for (Perfil perfil : Perfil.values()) {
      retorno.add(perfil);
    }

    return retorno;
  }

  /**
   * Retorna o perfil.
   * @author Senio Caires
   * @return {@link Perfil}
   */
  public Perfil getPerfil() {
    return perfil;
  }

  /**
   * Altera o perfil.
   * @author Senio Caires
   * @param perfil
   */
  public void setPerfil(Perfil perfil) {
    this.perfil = perfil;
  }
}
