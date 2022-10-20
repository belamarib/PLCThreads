/*
 * Implemente a classe Semáforo em Java com dois métodos que realizam as seguintes operações:
 * up: incrementa o contador do semáforo e acorda threads que estejam bloqueadas.
 * down: decrementa o contador do semáforo ou, caso seja zero, suspende a thread.
 * class Semaforo extends Object {
	private int contador;

	public Semaforo (int inicial) {
    	contador = inicial;
	}

	public void down() {
    	synchronized (this) {...}
	}

	public void up() {
    	synchronized (this) {...}
	}
 * }
 */

class Semaforo extends Thread {
    private int contador;

    public Semaforo(int inicial) {
        this.contador = inicial;
    }

    public void down(Thread obj) {
        synchronized(this) {
            this.contador++;
            obj.notifyAll();
        }
    }

    public void up(Thread obj) {
        synchronized(this) {
            if (this.contador == 0) {
                obj.interrupt();
            } else {
                this.contador--;
            }


        }
    }

}

