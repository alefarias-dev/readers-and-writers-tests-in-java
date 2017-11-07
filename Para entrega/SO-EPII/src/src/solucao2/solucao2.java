package solucao2;

import java.io.*;
import java.util.*;
import java.util.concurrent.Semaphore;

class Simulador {
	
	public static int NUM_OF_READERS = 3;
	public static int NUM_OF_WRITERS = 2;
	
	public static void setRW(int r, int w) { NUM_OF_READERS = r; NUM_OF_READERS = w; }
	
	public Simulador(int readers, int writers) { setRW(readers, writers); }
	
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
	
	public static void main(String args[]) throws Exception {
		
		String[] db = carregarDatabase();
		RWLock database = new Database(db);
		System.out.println("rodando solucao busy");
		
		setRW(2, 5);
		
		Thread[] readers_writers = new Thread[NUM_OF_READERS+NUM_OF_WRITERS];
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			if (i < NUM_OF_READERS) readers_writers[i] = new Thread(new Reader(i, database));
			else readers_writers[i] = new Thread(new Writer(i, database));
		}
		
		List<Thread> rw = Arrays.asList(readers_writers);
		Collections.shuffle(rw);
		readers_writers = (Thread[]) rw.toArray();
			
		for (int i = 0; i < NUM_OF_WRITERS + NUM_OF_WRITERS; i++) readers_writers[i].start();		
	}
}

interface RWLock {

	public abstract void busyLock(Runnable writerNum);
	
	public abstract void busyRelease(Runnable writerNum);
}

class Database implements RWLock {
	private int readerCount;
	private Semaphore mutex;
	private Semaphore db;
	public String[] database;

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

			SleepUtilities.nap();

			database.busyRelease(this);
		}
	};
}

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
			
			SleepUtilities.nap();

			database.busyRelease(this);
		}
	}

}

class SleepUtilities {

	public static void nap() {
		nap(NAP_TIME);
	}

	public static void nap(int duration) {
		int sleeptime = (int) (NAP_TIME * Math.random());
		try {
			Thread.sleep(sleeptime * 1000);
		} catch (InterruptedException e) {
		}
	}

	private static final int NAP_TIME = 5;
}