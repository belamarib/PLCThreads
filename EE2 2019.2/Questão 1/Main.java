/*
 * Implemente em Java um Produtor-Consumidor de valores inteiros.
 */

import java.util.*;

class Produtor implements Runnable {
    private ProdutorConsumidor buffer;

    public Produtor (ProdutorConsumidor b) {
        this.buffer = b;
    }

    public void run() {
        while (true) {
            this.buffer.produzir();
        }
    }
}

class Consumidor implements Runnable {
    private ProdutorConsumidor buffer;

    public Consumidor(ProdutorConsumidor b) {
        this.buffer = b;
    }

    public void run() {
        while (true) {
            this.buffer.consumir();
        }
    }
}

class ProdutorConsumidor {
    private ArrayList<Integer> buffer;
    private int capacidade;

    public ProdutorConsumidor(int capacidade) {
        this.buffer = new ArrayList<Integer>(capacidade);
        this.capacidade = capacidade;
    }

    public void produzir() {
        synchronized(this) {
            Random valor = new Random();
            while (buffer.size() == this.capacidade) {
                try {
                    System.out.println("Buffer cheio!");
                    wait();
                } catch (InterruptedException ie) {}
            }
            int valor_produzido = valor.nextInt(1000);
            buffer.add(valor_produzido);
            System.out.println("Produzido o valor " + valor_produzido);
            notifyAll();
        }
    }

    public void consumir() {
        synchronized(this) {
            while (buffer.isEmpty()) {
                try {
                    System.out.println("Buffer vazio!");
                    wait();
                } catch (InterruptedException ie) {}
            }
            int valor_removido = buffer.get(buffer.size()-1);
            buffer.remove(buffer.size()-1);
            System.out.println("Consumido o valor " + valor_removido);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ProdutorConsumidor buffer = new ProdutorConsumidor(100);
        Thread produtor = new Thread(new Produtor(buffer));
        Thread consumidor = new Thread(new Consumidor(buffer));

        produtor.start();
        consumidor.start();

    }
}