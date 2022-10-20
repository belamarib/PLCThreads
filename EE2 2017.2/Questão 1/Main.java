/*
 * Uma conta bancária é compartilhada por seis pessoas. Cada uma pode depositar ou retirar dinheiro desde que o saldo não se torne negativo. 
 * Implemente em Java uma solução para conta bancária, utilizando bloco sincronizado. Considere que saques e depósitos são de valores aleatórios.
 */

import java.util.*;

class Conta {
    private int saldo;

    public Conta() {
        this.saldo = 0;
    }

    public void sacar(int saque) {
        synchronized(this) {
            this.saldo -= saque;
        }
    }

    public void depositar(int deposito) {
        synchronized(this) {
            this.saldo += deposito;
        }
    }

    public int getSaldo() {
        return this.saldo;
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
