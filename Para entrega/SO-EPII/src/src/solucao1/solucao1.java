package solucao1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.Semaphore;


class SimuladorRW {
	
	public static int NUM_OF_READERS = 3;
	public static int NUM_OF_WRITERS = 2;
	
	public static void setRW(int r, int w) { NUM_OF_READERS = r; NUM_OF_WRITERS = w; }
	
	public SimuladorRW(int readers, int writers) { setRW(readers, writers); }
	
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
		System.out.println("rodando solucao readers and writers");
		
		setRW(50, 50);
		
		Thread[] readers_writers = new Thread[NUM_OF_READERS+NUM_OF_WRITERS];
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			if (i < NUM_OF_READERS) readers_writers[i] = new Thread(new Reader(i, database));
			else readers_writers[i] = new Thread(new Writer(i, database));
		}
		
		List<Thread> rw = Arrays.asList(readers_writers);
		Collections.shuffle(rw);
		readers_writers = (Thread[]) rw.toArray();
			
		for (int i = 0; i < NUM_OF_READERS + NUM_OF_WRITERS; i++) {
			readers_writers[i].start();
		}
		
		System.out.println("fim...");
	}
}

interface RWLock {
	public abstract void acquireReadLock(int readerNum);

	public abstract void acquireWriteLock(int writerNum);

	public abstract void releaseReadLock(int readerNum);

	public abstract void releaseWriteLock(int writerNum);
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

	public void acquireReadLock(int readerNum) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
		}

		++readerCount;

		if (readerCount == 1) {
			try {
				db.acquire();
			} catch (InterruptedException e) {
			}
		}

		System.out.println("Reader " + readerNum
				+ " is reading. Reader count = " + readerCount);
		mutex.release();
	}

	public void releaseReadLock(int readerNum) {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
		}

		--readerCount;

		if (readerCount == 0) {
			db.release();
		}

		System.out.println("Reader " + readerNum
				+ " is done reading. Reader count = " + readerCount);
		mutex.release();
	}

	public void acquireWriteLock(int writerNum) {
		try {
			db.acquire();
		} catch (InterruptedException e) {
		}
		System.out.println("Writer " + writerNum + " is writing.");
	}

	public void releaseWriteLock(int writerNum) {
		System.out.println("Writer " + writerNum + " is done writing.");
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

	public void run() {
		
			SleepUtilities.nap();

			System.out.println("reader " + readerNum + " wants to read.");
			database.acquireReadLock(readerNum);
			
			SleepUtilities.nap();

			database.releaseReadLock(readerNum);
		
	};
}

class Writer implements Runnable {
	private RWLock database;
	private int writerNum;

	public Writer(int w, RWLock d) {
		writerNum = w;
		database = d;
	}

	public void run() {
		
			SleepUtilities.nap();

			System.out.println("writer " + writerNum + " wants to write.");
			database.acquireWriteLock(writerNum);
			
			SleepUtilities.nap();

			database.releaseWriteLock(writerNum);
		
	}

}

class SleepUtilities {
	public static void nap() {
		nap(NAP_TIME);
	}

	public static void nap(int duration) {
		int sleeptime = (int) (NAP_TIME * Math.random());
		try {
			Thread.sleep(sleeptime * 1);
		} catch (InterruptedException e) {
		}
	}

	private static final int NAP_TIME = 5;
}