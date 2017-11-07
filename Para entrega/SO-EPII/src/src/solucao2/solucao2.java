package solucao2;
/**
 * Original code by Silberschatz, Galvin, and Gagne
 * from Operating System Concepts with Java, 7th Edition
 * Modified by William McDaniel Albritton
 *
 * ReaderWriterSolution.java
 *
 * This class creates the reader and writer threads and
 * the database they will be using to coordinate access.
 */

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

class Simulador {
	
	public static int NUM_OF_READERS = 3;
	public static int NUM_OF_WRITERS = 2;
	
	// configura quantidade de readers e writers
	public static void setRW(int r, int w) { NUM_OF_READERS = r; NUM_OF_READERS = w; }
	
	public Simulador(int readers, int writers) { setRW(readers, writers); }
	
	public static String[] carregarDatabase () throws Exception {
		
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
	
	public static void main(String args[]) throws Exception {
		
		// cria o database
		String[] db = carregarDatabase();
		RWLock database = new Database(db);
		System.out.println("rodando solucao busy");
		
		// configura qtde de readers e writers
		setRW(2, 5);
		
		// arranjo com todos os objetos escritores e leitores
		// e cria objetos readers e writers
		Thread[] readers_writers = new Thread[NUM_OF_READERS+NUM_OF_WRITERS];
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			if (i < NUM_OF_READERS) readers_writers[i] = new Thread(new Reader(i, database));
			else readers_writers[i] = new Thread(new Writer(i, database));
		}
		
		// embaralha todos os objetos
		List<Thread> rw = Arrays.asList(readers_writers);
		Collections.shuffle(rw);
		readers_writers = (Thread[]) rw.toArray();
			
		// apos embaralhar
		for (int i = 0; i < NUM_OF_WRITERS + NUM_OF_WRITERS; i++) readers_writers[i].start();		
	}
}

// ****************************************************************

/**
 * An interface for reader-writer locks.
 * 
 * In the text we do not have readers and writers pass their number into each
 * method. However we do so here to aid in output messages.
 */

interface RWLock {

	public abstract void busyLock(Runnable writerNum);
	
	public abstract void busyRelease(Runnable writerNum);
}

// ****************************************************************

/**
 * Database.java
 * 
 * This class contains the methods the readers and writers will use to
 * coordinate access to the database. Access is coordinated using semaphores.
 */

class Database implements RWLock {
	private int readerCount; // the number of active readers
	private Semaphore mutex; // controls access to readerCount
	private Semaphore db; // controls access to the database
	public String[] database; // possui as linhas da base de dados 

	public Database(String[] database) {
		readerCount = 0;
		mutex = new Semaphore(1);
		db = new Semaphore(1);
		this.database = database;
	}
	
	public void busyLock(Runnable rw) {
		try {
			db.acquire();
		} catch (InterruptedException e) {
		}
		
		if (rw instanceof Writer) System.out.println("Writer " + ((Writer) rw).getId() + " is writing.");
		if (rw instanceof Reader) System.out.println("Reader " + ((Reader) rw).getId() + " is reading.");
	}
	
	public void busyRelease(Runnable rw) {
	
		if (rw instanceof Writer) System.out.println("Writer " + ((Writer) rw).getId() + " is done writing.");
		if (rw instanceof Reader) System.out.println("Reader " + ((Reader) rw).getId() + " is done reading.");
		db.release();
	}

}

// ***********************************************************

/**
 * Reader.java
 * 
 * A reader to the database.
 * 
 */

class Reader implements Runnable {

	private RWLock database;
	private int readerNum;

	public Reader(int readerNum, RWLock database) {
		this.readerNum = readerNum;
		this.database = database;
	}
	
	public int getId() { return this.readerNum; }

	public void run() {
		int i = 0;
		while (i < 5) {
			SleepUtilities.nap();

			System.out.println("reader " + readerNum + " wants to read.");
			database.busyLock(this);
			
			i++;
			// you have access to read from the database
			// let's read for awhile .....
			SleepUtilities.nap();

			database.busyRelease(this);
		}
	};
}

// **************************************************************

/**
 * Writer.java
 * 
 * A writer to the database.
 * 
 */
class Writer implements Runnable {
	private RWLock database;
	private int writerNum;

	public Writer(int w, RWLock d) {
		writerNum = w;
		database = d;
	}
	
	public int getId() { return this.writerNum; }

	public void run() {
		int i = 0;
		while (i < 5) {
			SleepUtilities.nap();

			System.out.println("writer " + writerNum + " wants to write.");
			database.busyLock(this);
			
			i++;
			
			// you have access to write to the database
			// write for awhile ...
			SleepUtilities.nap();

			database.busyRelease(this);
		}
	}

}

// *****************************************************************

/**
 * Utilities for causing a thread to sleep. Note, we should be handling
 * interrupted exceptions but choose not to do so for code clarity.
 * 
 */

class SleepUtilities {
	/**
	 * Nap between zero and NAP_TIME seconds.
	 */
	public static void nap() {
		nap(NAP_TIME);
	}

	/**
	 * Nap between zero and duration seconds.
	 */
	public static void nap(int duration) {
		int sleeptime = (int) (NAP_TIME * Math.random());
		try {
			Thread.sleep(sleeptime * 1000);
		} catch (InterruptedException e) {
		}
	}

	private static final int NAP_TIME = 5;
}
