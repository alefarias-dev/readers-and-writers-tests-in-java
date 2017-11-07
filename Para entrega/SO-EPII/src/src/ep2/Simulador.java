package ep2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.Semaphore;

//Recebe num. de writers, num. de readers e o modo. Se 0, roda RW. Se 1, roda Busy Wait.
//Metodo main() da classe simulador retorna o tempo de execucao do modo.
class Simulador {
	
	//0: readers and writers.
	//1: busy wait.
	public static int NUM_OF_READERS = 99;
	public static int NUM_OF_WRITERS = 1;
	public static int MODE = 1;
	
	//Configura a quantidade de readers e writers.
	public static void setRW(int r, int w) { NUM_OF_READERS = r; NUM_OF_WRITERS = w; }
	
	public Simulador(int readers, int writers, int mode) { 
		setRW(readers, writers);
		MODE = mode;
	}
	
	//Carrega arquivo bd.txt
	public static String[] carregarDatabase () throws Exception {
		
		String nomeArquivo = "bd/bd.txt";
		FileReader        fr    = new FileReader(nomeArquivo);
		BufferedReader    br    = new BufferedReader(fr);
		ArrayList<String> lines = new ArrayList<String>();
				
		String line = null;
		while ((line = br.readLine()) != null) lines.add(line);
		
		br.close();
		return lines.toArray(new String[lines.size()]);
	}
	
	public static long start() throws Exception {
		
		//Carrega o vetor com o conteudo do arquivo bd.txt.
		String[] db = carregarDatabase();
		//Cria database com o vetor.
		Database database = new Database(db);
		
		//Cria o vetor de Threads com readers e writers.
		Thread[] readers_writers = new Thread[NUM_OF_READERS + NUM_OF_WRITERS];
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			if (i < NUM_OF_READERS) readers_writers[i] = new Thread(new Reader(i, database, MODE));
			else readers_writers[i] = new Thread(new Writer(i, database, MODE));
		}
		
		//Embaralhou os threads do vetor.
		List<Thread> rw = Arrays.asList(readers_writers);
		Collections.shuffle(rw);
		readers_writers = (Thread[]) rw.toArray();
			
		//Iniciou o cronometro.
		long tempoInicio = System.currentTimeMillis();
		
		//Inicia todas as threads sequencialmente.
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			readers_writers[i].start();
		}
		
		while(Thread.activeCount() > 1){}
		
		//Desligou o cronometro.		
		long tempoFinal = System.currentTimeMillis();
		
		//System.out.println("Tempo Total: " + (tempoFinal - tempoInicio) + " milissegundos.");
		//System.out.println("Modo: " + ((MODE == 0) ? "RW" : "BUSY"));
		//System.out.println("Proporcao (R:W): " + NUM_OF_READERS + ":" + NUM_OF_WRITERS);
		return tempoFinal - tempoInicio;
	}
}

// ******************************************************************************************

interface RWLock {
	
	public abstract void acquireReadLock(int readerNum);
	public abstract void acquireWriteLock(int writerNum);
	public abstract void releaseReadLock(int readerNum);
	public abstract void releaseWriteLock(int writerNum);
	public abstract void busyLock(Runnable writerNum);
	public abstract void busyRelease(Runnable writerNum);
}

// ******************************************************************************************

class Database implements RWLock {
	
	private int readerCount;  // the number of active readers
	private Semaphore mutex;  // controls access to readerCount
	private Semaphore db;     // controls access to the database
	public String[] database; // possui as linhas da base de dados 

	public Database(String[] database) {
		readerCount = 0;
		mutex = new Semaphore(1);
		db = new Semaphore(1);
		this.database = database;
	}

	public void acquireReadLock(int readerNum) {
		try { mutex.acquire(); } catch (InterruptedException e) { }
		++readerCount;

		// se eh o primeiro, avisa que a base esta sendo lida
		if (readerCount == 1) 
			try { db.acquire(); } catch (InterruptedException e) { }
		
		//System.out.println("Leitor: " + readerNum + " esta lendo. Leitores: " + readerCount);
		mutex.release();
	}

	public void releaseReadLock(int readerNum) {	
		try { mutex.acquire(); } catch (InterruptedException e) { }
		--readerCount;

		// se eh o ultima, avisa que nao tem ninguem lendo a base de dados
		if (readerCount == 0) db.release();
		
		//System.out.println("Leitor " + readerNum + " terminou a leitura. Leitores: " + readerCount);
		mutex.release();
	}

	public void acquireWriteLock(int writerNum) {
		try { db.acquire(); } catch (InterruptedException e) {}
		//System.out.println("Escritor " + writerNum + " esta escrevendo.");
	}

	public void releaseWriteLock(int writerNum) {
		//System.out.println("Escritor " + writerNum + " terminou de escrever.");
		db.release();
	}
	
	public void busyLock(Runnable rw) {
		try { db.acquire(); } catch (InterruptedException e) { }
		//if (rw instanceof Writer) System.out.println("Writer " + ((Writer) rw).getId() + " is writing.");
		//if (rw instanceof Reader) System.out.println("Reader " + ((Reader) rw).getId() + " is reading.");
	}
	
	public void busyRelease(Runnable rw) {
		//if (rw instanceof Writer) System.out.println("Writer " + ((Writer) rw).getId() + " is done writing.");
		//if (rw instanceof Reader) System.out.println("Reader " + ((Reader) rw).getId() + " is done reading.");
		db.release();
	}
	
	public String[] getdb() {return this.database;}
}

// ******************************************************************************************

class Reader implements Runnable {

	private Database database;
	private int readerNum;
	private int mode;
	public static int NUM_EXEC = 5;	

	public Reader(int readerNum, Database database, int mode) {
		this.readerNum = readerNum;
		this.database = database;
		this.mode = mode;
	}
	
	public int getId() { return this.readerNum; }

	public void run() {

		String texto = "";
		
		//System.out.println("Leitor " + readerNum + " precisa ler.");
		if (mode == 0) database.acquireReadLock(readerNum);
		else database.busyLock(this);
		
	
		// you have access to read from the database
		// let's read for awhile .....
		for (int i = 0; i < 100; i++) {
			texto = database.getdb()[getRandomNumber(0, database.database.length-1)];
			//System.out.println("acessou: "+i);
		}
		
		SleepUtilities.nap();
		
		if (mode == 0) database.releaseReadLock(readerNum);
		else database.busyRelease(this);
	};
	
	public static int getRandomNumber(int min, int max) {
		return (int) (Math.floor(Math.random() * (max - min + 1)) + min);
	}
}

// ******************************************************************************************

class Writer implements Runnable {
	
	private Database database;
	private int writerNum;
	private int mode;
	public static int NUM_EXEC = 5;

	public Writer(int w, Database d, int mode) {
		writerNum = w;
		database = d;
		this.mode = mode;
	}

	public int getId() { return this.writerNum; }

	public void run() {

		// System.out.println("Escritor " + writerNum + " precisa escrever.");
		if (mode == 0) database.acquireWriteLock(writerNum);
		else  database.busyLock(this);
		
		// you have access to write to the database
		// write for awhile ...
		for (int i = 0; i < 100; i++)
			database.database[getRandomNumber(0, database.database.length-1)] = "MODIFICADO";
		
		SleepUtilities.nap();
	
		if (mode == 0) database.releaseWriteLock(writerNum);
		else database.busyRelease(this);
		
	}
	
	public static int getRandomNumber(int min, int max) {
		return (int) (Math.floor(Math.random() * (max - min + 1)) + min);
	}

}

// *****************************************************************

class SleepUtilities {
	
	public static void nap() { nap(NAP_TIME); }
	
	public static void nap(int duration) {
		int sleeptime = (int) (NAP_TIME * Math.random());
		try { Thread.sleep(sleeptime * 1); } 
		catch (InterruptedException e) { }
	}

	private static final int NAP_TIME = 5;
}
