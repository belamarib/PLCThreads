/*
 * Crie N jogadores e um vetor com N-1 cadeiras, cada cadeira é um tipo atômico. A cada rodada, todos os jogadores tentarão (aleatoriamente) 
 * sentar em alguma cadeira vazia. Eles continuam tentando até todas as cadeiras estarem ocupadas. Em seguida, é mostrado na tela o único jogador 
 * que não conseguiu sentar “O jogador X foi eliminado”, onde X é o seu nome. Todas as cadeiras ficam vagas novamente e uma é retirada do jogo. 
 * Inicia-se uma nova rodada. O programa acaba quando só sobrar 1 jogador, e deverá ser mostrado na tela “O jogador X foi o vencedor!”
 */

import java.util.*;

class Cadeiras {
    private List<Jogador> cadeiras;
    private List<Jogador> jogadores_ativos;
    
    private int qte_vazias; 
    private int qte_total; 

    public Cadeiras (int total) {
        this.cadeiras = new ArrayList();
        this.qte_vazias = total;
        this.qte_total = total;
    }

    public void sentar(Jogador jogador) {
        synchronized(this) {
            
            try {
                jogador.sleep(10);
            } catch (InterruptedException ie) {}
            if (qte_total == 0) {
                System.out.println("O jogador " + this.jogadores_ativos.get(0).getNome() + " foi o vencedor!");
                this.jogadores_ativos.get(0).interrupt();
            } else {
                if (this.qte_vazias > 0 && !this.cadeiras.contains(jogador)) {
                    this.cadeiras.add(jogador);
                    this.qte_vazias--;
                    System.out.println("O jogador " + jogador.getNome() + " sentou!");
                } else if (this.qte_vazias == 0 && !this.cadeiras.contains(jogador)) {
                    System.out.println("O jogador " + jogador.getNome() + " foi eliminado!");
                    jogador.interrupt();
                    this.jogadores_ativos.remove(jogador);
                    this.jogadores_ativos.remove(jogador);
                    this.cadeiras = new ArrayList(); 
                    this.qte_vazias = this.qte_total - 1; 
                    this.qte_total--;
                }
            }
            notifyAll();
        }
    }

    public void preparar_jogadores(List<Jogador> j) {
        this.jogadores_ativos = j;
    }
}

class Jogador extends Thread {
    private String nome;
    private Cadeiras cadeiras;

    public Jogador(String n, Cadeiras c) {
        this.nome = n;
        this.cadeiras = c;
    }

   public String getNome() {
        return this.nome;
   }

   public void run() {
        while(!this.isInterrupted()) {
            this.cadeiras.sentar(this); 
        }
    }
}

public class Main {
    public static void main(String[] args) {
        int qte_cadeiras = 9;

        Cadeiras cadeiras = new Cadeiras(qte_cadeiras);

        Jogador jogador1 = new Jogador("Joao", cadeiras);
        Jogador jogador2 = new Jogador("Isabela", cadeiras);
        Jogador jogador3 = new Jogador("Carlos", cadeiras);
        Jogador jogador4 = new Jogador("Maria", cadeiras);
        Jogador jogador5 = new Jogador("Lucia", cadeiras);
        Jogador jogador6 = new Jogador("Davi", cadeiras);
        Jogador jogador7 = new Jogador("Celia", cadeiras);
        Jogador jogador8 = new Jogador("Mateus", cadeiras);
        Jogador jogador9 = new Jogador("Paula", cadeiras);
        Jogador jogador10 = new Jogador("Gabriel", cadeiras);

        List<Jogador> jogadores_rodada = new ArrayList();

        jogadores_rodada.add(jogador1);
        jogadores_rodada.add(jogador2);
        jogadores_rodada.add(jogador3);
        jogadores_rodada.add(jogador4);
        jogadores_rodada.add(jogador5);
        jogadores_rodada.add(jogador6);
        jogadores_rodada.add(jogador7);
        jogadores_rodada.add(jogador8);
        jogadores_rodada.add(jogador9);
        jogadores_rodada.add(jogador10);

        cadeiras.preparar_jogadores(jogadores_rodada);

        for (int i = 0; i < jogadores_rodada.size(); i++) {
            jogadores_rodada.get(i).start();
        }
    }
}