/*
 * Modifique a questão anterior para utilizar tipos primitivo atômicos ou a interface BlockingQueue.
 */

import java.util.concurrent.*;
import java.util.*;



class Buffer {
    BlockingQueue<Integer> buffer;
    int capacidade;
    
    public Buffer(int capacidade){
        this.buffer = new LinkedBlockingDeque<Integer>(capacidade);
        this.capacidade = capacidade;
    }

    public void produzir() {
        int valor = 1;
        int i = 0;
        while (i < 200) {
            synchronized (this) {
                while (this.buffer.size() == this.capacidade) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {}
                }
                try {
                    this.buffer.put(valor);
                } catch (InterruptedException ie) {}
                System.out.println("Produziu: "+ valor);
                System.out.println("Buffer: "+ this.buffer);
                valor++;

                notify();
            }
            i++;
        }
    }

    public void consumir() {
        int i = 0;
        while (i < 200) {
            synchronized (this) {
                while (this.buffer.isEmpty()) {
                    try {
                        wait();
                    } catch (InterruptedException ie) {}
                }
                try {
                    this.buffer.take();
                } catch (InterruptedException ie) {}
                System.out.println("Consumiu o último item!");
                System.out.println("Buffer: "+ this.buffer);

                notify();

            }
            i++;
        }
    }

}

class Produtor extends Thread {
    Buffer buffer;

    public Produtor(Buffer b) {
        this.buffer = b;
    }

    public void run() {
        this.buffer.produzir();
    }

}

class Consumidor extends Thread {
    Buffer buffer;

    public Consumidor(Buffer b) {
        this.buffer = b;
    }

    public void run() {
        this.buffer.consumir();
    }
}

public class ProdutorConsumidor {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        Buffer buffer = new Buffer(200);

        Produtor produtor = new Produtor(buffer);
        Consumidor consumidor = new Consumidor(buffer);

        produtor.start();
        consumidor.start();

    }
}



