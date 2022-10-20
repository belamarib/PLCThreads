/*
 * Implemente uma fila bloqueante em Java. Não usar funções da biblioteca de Java.
 */

import java.util.*;

class No {
    private int valor;
    private No proximo;

    public No (int valor) {
        this.valor = valor;
    }

    public void setProximo(No p) {
        this.proximo = p;
    }

    public No getProximo() {
        return this.proximo;
    }

    public int getValor() {
        return this.valor;
    }
}

class Fila {
    private No primeiro;
    private No ultimo;
    private int tamanho;
    private int capacidade;

    public Fila(int capacidade) {
        this.capacidade = capacidade;
    }

    public void put(int valor) {
        synchronized(this) {
            while (this.tamanho == this.capacidade) {
                try {
                    System.out.println("Tamanho máximo alcançado...");
                    wait();
                } catch (InterruptedException ie) {} 
            }
            No no = new No(valor);
            if (this.tamanho == 0) {
                this.primeiro = no;
            } else {
                this.ultimo.setProximo(no);
            }
            this.ultimo = no;
            this.tamanho++;
            System.out.println("Valor " + valor + " adicionado na fila!");
            notifyAll();
        }
    }

    public int take() {
        synchronized(this) {
            while (this.tamanho == 0) {
                try {
                    System.out.println("Fila vazia...");
                    wait();
                } catch (InterruptedException ie) {}
            }
            int valor_removido = this.primeiro.getValor();
            if (this.tamanho == 1) {
                this.primeiro = null;
                this.ultimo = null;
            } else {
                this.primeiro = this.primeiro.getProximo();
            }
            this.tamanho--;
            System.out.println("Valor " + valor_removido + " retirado da fila!");
            notifyAll();
            return valor_removido;
        }
    }

}

class Produtor implements Runnable {
    private Fila fila;

    public Produtor(Fila f) {
        this.fila = f;
    }

    public void run() {
        Random num = new Random();
        while (true) {
            int valor = num.nextInt(101);
            this.fila.put(valor);
        }
    }
}

class Consumidor implements Runnable {
    private Fila fila;

    public Consumidor (Fila f) {
        this.fila = f;
    }

    public void run() {
        while (true) {
            this.fila.take();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Fila fila = new Fila(50);
        Thread produtor = new Thread(new Produtor(fila));
        Thread consumidor = new Thread(new Consumidor(fila));

        produtor.start();
        consumidor.start();
    }
}
