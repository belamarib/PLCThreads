/*
 * Implemente em Java as classes Produtor, Consumidor e ProdutorConsumidor. Esta última possui o método main(). 
 * Os dados produzidos são valores do tipo inteiro. Não se pode utilizar classes da API de Java como, por exemplo, interface BlockingQueue, 
 * que possui métodos put e take, utilizando a implementação ArrayBlockingQueue.
 */

import java.util.concurrent.*;
import java.util.*;



class Buffer {
    ArrayBlockingQueue<Integer> buffer;
    int capacidade;
    
    public Buffer(int capacidade){
        this.buffer = new ArrayBlockingQueue<Integer>(capacidade);
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

                this.buffer.add(valor);
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
                this.buffer.remove();
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



