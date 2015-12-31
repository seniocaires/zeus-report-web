package zeusreportweb.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * Classe utilitária para fazer download de um arquivo de uma URL.
 * @author Senio Caires
 */
public class DownloadUtil {

	/**
	 * @author Senio Caires
	 */
	private static final int TAMANHO_BUFFER = 4096;

	/**
	 * Logger.
	 * @author Senio Caires
	 */
	private static final Logger logger = Logger.getLogger(DownloadUtil.class);

	/**
	 * Faz o download de um arquivo de uma URL.
	 * @author Senio Caires
	 * @param urlArquivo - URL do arquivo
	 * @param diretorioSalvarDownload - Diretório para salvar o download
	 * @param nomeArquivo - Nome do arquivo para gravação
	 * @throws IOException -
	 */
	public static void downloadArquivo(String urlArquivo, String diretorioSalvarDownload, String nomeArquivo) throws IOException {

		URL url = new URL(urlArquivo);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
		int codigoResposta = httpUrlConnection.getResponseCode();

		if (codigoResposta == HttpURLConnection.HTTP_OK) {

			InputStream inputStream = httpUrlConnection.getInputStream();
			String enderecoSalvarArquivo = diretorioSalvarDownload + File.separator + nomeArquivo;

			FileOutputStream outputStream = new FileOutputStream(enderecoSalvarArquivo);

			int bytesRead = -1;
			byte[] buffer = new byte[TAMANHO_BUFFER];

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

		} else {
			logger.error("Não foi possível fazer o download do arquivo. Servidor retornou o código HTTP: " + codigoResposta);
		}

		httpUrlConnection.disconnect();
	}
}
