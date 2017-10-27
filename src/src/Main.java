import java.io.*;
import java.util.*;

public class Main {
	
	/****************************************************
	 * Carrega linha a linha o arquivo bd.txt e retorna
	 * um arranjo de strings (palavras) 
	 * **************************************************/
	public static String[] carregarArquivo () throws Exception {
		
		// abre arquivo bd.txt
		String nomeArquivo = "bd/bd.txt";
		FileReader        fr    = new FileReader(nomeArquivo);
		BufferedReader    br    = new BufferedReader(fr);
		ArrayList<String> lines = new ArrayList<String>();
		
		// coloca linha a linha na string line
		String line = null;
		while ((line = br.readLine()) != null) lines.add(line);
		
		// fecha arquivo bd.txt
		br.close();
		
		// retorna o array com todas as linhas do arquivo txt
		return lines.toArray(new String[lines.size()]);
	}
	
	public static void main(String[] args) throws Exception {
		
		String[] database = carregarArquivo();
		System.out.println("quantidade de linhas carregadas: "+database.length);
	}
}