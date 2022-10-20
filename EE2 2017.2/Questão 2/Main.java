/*
 * Adapte a solução anterior para utilizar tipos primitivos atômicos. Métodos get e set estão disponíveis para estes tipos.
 */

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class Conta {
    private AtomicInteger saldo = new AtomicInteger(0);

    public void sacar(int saque) {
        this.saldo.addAndGet(saque*-1);
    }

    public void depositar(int deposito) {
            this.saldo.addAndGet(deposito);
    }

    public int getSaldo() {
        return this.saldo.get();
    }
}

class Pessoa extends Thread {
    String nome;
    Conta conta;

    public Pessoa(String n, Conta c) {
        this.nome = n;
        this.conta = c;
    }

    public void run() {
        Random r = new Random();

        int deposito = r.nextInt(Integer.MAX_VALUE);
        int saque = r.nextInt(Integer.MAX_VALUE);

        this.conta.depositar(deposito);
        System.out.println(this.nome + " depositou " + deposito);

        if (this.conta.getSaldo() >= saque) {
            this.conta.sacar(saque);
            System.out.println(this.nome + " sacou " + saque);
        } else {
            System.out.println(this.nome + " não conseguiu sacar o valor desejado");
        }

    }
}

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Conta conta = new Conta();

        Pessoa p1 = new Pessoa("Maria", conta);
        Pessoa p2 = new Pessoa("João", conta);
        Pessoa p3 = new Pessoa("Lucia", conta);
        Pessoa p4 = new Pessoa("Carlos", conta);
        Pessoa p5 = new Pessoa("Deborah", conta);
        Pessoa p6 = new Pessoa("Paulo", conta);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
        p6.start();
    }
}
