/*
 * Implemente em Java um Produtor-Consumidor de valores inteiros.
 */

import java.util.concurrent.*;

class Buffer {
    ArrayBlockingQueue<Integer> buffer;
    int capacidade;

    public Buffer(int capacidade) {
        this.capacidade = capacidade;
        this.buffer = new ArrayBlockingQueue<Integer>(capacidade);
    }

    public void produzir() {
        int contador = 1;
        while (true) {
            synchronized(this) {
                while (this.buffer.size() == this.capacidade) {
                    System.out.println("Buffer cheio! Esperando...");
                    try {
                        wait();
                    } catch (InterruptedException ie) {}
                }
                this.buffer.add(contador);
                System.out.println("Buffer produziu: " + contador);
                System.out.println("Buffer: " + buffer);
                contador++;
                notify();
            }
        }
    }

    public void consumir() {
        while (true) {
            synchronized(this) {
                while (this.buffer.isEmpty()) {
                    System.out.println("Buffer vazio! Esperando...");
                    try {
                        wait();
                    } catch (InterruptedException ie) {}
                }
                this.buffer.remove();
                System.out.println("Buffer consumiu");
                System.out.println("Buffer: " + buffer);
                notify();
            }
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
        Buffer buffer = new Buffer(200);

        Produtor produtor = new Produtor(buffer);
        Consumidor consumidor = new Consumidor(buffer);

        produtor.start();
        consumidor.start();
    }
}