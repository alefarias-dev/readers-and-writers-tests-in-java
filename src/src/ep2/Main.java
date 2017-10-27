package ep2;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		int r = 100;
		int w = 0;
		int mode = 0;
		
		long[] valores = new long[50];
		
		for (r = 100; r >= 0; r--) {
			
			long total_rw = 0;
			long total_bw = 0;
			
			for (int i = 0; i < 50; i++) {
				Simulador sim = new Simulador(r, w, 0);
				total_rw += sim.start();
			}
			
			for (int i = 0; i < 50; i++) {
				Simulador sim = new Simulador(r, w, 1);
				total_bw += sim.start();
			}
			
			System.out.println("["+r+"|"+w+"] "+(total_rw/50)+" "+(total_bw/50));
			w++;
		}
		System.out.println("fim...");
	}
}
