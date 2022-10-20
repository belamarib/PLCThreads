/*
 * Um fabricante de sorvetes contratou você para simular parte do processo de produção deles. Na produção, a mistura de dois ingredientes (o aromatizante e o espessante) 
 * acontece apenas quando o recipiente de mistura refrigerado (RMR) está disponível, ou seja, eles são retirados de diferentes depósitos quando podem ser efetivamente 
 * misturados. Para o RMR ficar disponível, é necessário que ele seja esvaziado, o que acontece com o giro do RMR a fim de retirar o sorvete. Assim, as operações de 
 * retirada do sorvete e de mistura dos ingredientes precisam do RMR de forma exclusiva. Defina as operações para misturar os ingredientes e esvaziar o RMR. Considere 
 * que os ingredientes ficam guardados em depósitos distintos e que vamos diferenciar a quantidade que utilizamos de cada um deles. Além disso, vamos abstrair o tempo 
 * necessário para misturar ingredientes e returar sorvete do recipiente.
 * 
 * b) Apresente uma solução em Java. Pode-se utilizar classes da API de concorrência de java, não envolvendo tipos primitivos atômicos.
 */

class RMR {
    private int qte_aromatizante;
    private int qte_espessante;

    public RMR() {
        this.qte_aromatizante = 0;
        this.qte_espessante = 0;
    }

    public void produzir(String ingrediente) {
        synchronized(this) {
            if (ingrediente.equals("aromatizante")) {
                while (this.qte_aromatizante > 0) {
                    try {
                        System.out.println("Esperando para esvaziar RMR...");
                        wait();
                    } catch (InterruptedException ie) {}
                }
                this.qte_aromatizante++;
                System.out.println("Aromatizante adicionado no RMR!");
                System.out.println("Quantidade de aromatizantes: " + this.qte_aromatizante);
                System.out.println("Quantidade de espessantes: " + this.qte_espessante);
            } else {
                while (this.qte_espessante > 1) {
                    try {
                        System.out.println("Esperando para esvaziar RMR...");
                        wait();
                    } catch (InterruptedException ie) {}
                }
                this.qte_espessante++;
                System.out.println("Espessante adicionado no RMR!");
                System.out.println("Quantidade de aromatizantes: " + this.qte_aromatizante);
                System.out.println("Quantidade de espessantes: " + this.qte_espessante);
            } 
            notifyAll(); 
        }
    }

    public void consumir() {
        synchronized(this) {
            while (this.qte_aromatizante != 1 || this.qte_espessante != 2) {
                try {
                    System.out.println("Esperando ingredientes chegarem no RMR...");
                    wait();
                } catch (InterruptedException ie) {}
            }
            this.qte_aromatizante = 0;
            this.qte_espessante = 0;
            System.out.println("Sorvete feito!");
            notifyAll();
        }
    }

}

class Produtor implements Runnable {
    private String ingrediente;
    private RMR rmr;

    public Produtor(String i, RMR rmr) {
        this.ingrediente = i;
        this.rmr = rmr;
    }

    public void run() {
        while (true) {
            this.rmr.produzir(this.ingrediente);
        }
    }

}

class Consumidor implements Runnable {
    private RMR rmr;

    public Consumidor(RMR rmr) {
        this.rmr = rmr;
    }

    public void run() {
        while (true) {
            this.rmr.consumir();
        }
    }
}


public class Main {
    public static void main(String[] args) {
        RMR rmr = new RMR();
        Thread aromatizante = new Thread(new Produtor("aromatizante", rmr));
        Thread espessante = new Thread(new Produtor("espessante", rmr));
        Thread sorvete = new Thread(new Consumidor(rmr));

        aromatizante.start();
        espessante.start();
        sorvete.start();
    }
}