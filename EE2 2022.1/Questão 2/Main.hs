{-
A máquina de refrigerante do "Rei do hambuguer", funciona de uma forma bastante simples. A todo momento existem clientes enchendo seus copos de refrigerantes.
Estes são de 3 tipos: P-Cola, Guaraná Polo Norte e Guaraná Quate. Cada cliente leva 1000 ms para encher o copo com o refrigerante. Porém a maquina só pode ser utilizada
por uma pessoa por vez, então se um cliente está enchendo o copo, outro cliente tem que aguardar a máquina estar disponível para ele. Cada cliente enche seu copo com 300 ml
de refrigerante, e a máquina suporta apenas 2000 ml de cada refrigerante, ou seja, 6000 ml no total. Para evitar transtorno, sempre que um refrigerante possui menos que 
1000 ml, ele é reposto com um refil de 1000 ml. Para repor o refrigerante, a máquina não pode estar sendo utilizada por nenhum cliente e demora 1500 ms para repor o refrigerante.
Dado o número de clientes que estarão o tempo todo querendo o refrigerante P-Cola, Guarana Polo Norte e Guaraná Quate, implemente a máquina de refrigerante do "Rei do hambuguer" 
utilizando o conceito de variáveis mutáveis ou memória transacional em Haskell. Sempre que alguém estiver utilizando a máquina de refrigerante informe o seguinte.
No caso de abastecimento, a máquina informa: "O refrigerante X foi reabastecido com 1000 ml, e agora possui Y ml", onde X é o nome do refrigerante e Y a quantidade que
possui do mesmo após ele ser reabastecido. Caso seja um cliente, informe: "O cliente N do refrigerante X está enchendo seu copo", onde N é o número do cliente para seu respectivo 
refrigerante e X o nome do refrigerante.
-}

import Control.Concurrent
import Text.Printf

main = do
  maquina_cola <- newMVar 2000
  maquina_polo_norte <- newMVar 2000
  maquina_quate <- newMVar 2000

  lock <- newMVar 0

  forkIO $ retirar 1 "cola" maquina_cola lock
  forkIO $ retirar 2 "cola" maquina_cola lock
  forkIO $ retirar 3 "cola" maquina_cola lock
  forkIO $ retirar 4 "polo" maquina_polo_norte lock
  forkIO $ retirar 5 "polo" maquina_polo_norte lock
  forkIO $ retirar 6 "polo" maquina_polo_norte lock
  forkIO $ retirar 7 "quate" maquina_quate lock
  forkIO $ retirar 8 "quate" maquina_quate lock
  forkIO $ retirar 9 "quate" maquina_quate lock

  forkIO $ reabastecer "cola" maquina_cola lock
  forkIO $ reabastecer "polo" maquina_polo_norte lock
  forkIO $ reabastecer "quate" maquina_quate lock

retirar :: Int -> String -> MVar Int -> MVar Int -> IO()
retirar c n maq l = loop
  where loop = do
        maquina <- takeMVar maq
        lock <- takeMVar l
        if (maquina >= 300) then do
          threadDelay 1000000 --1000000 microssegundos = 1000 milissegundos
          if (n == "cola") then do
            printf "O cliente %d do refrigerante %s está enchendo seu copo\n" c "P-Cola"
          else if (n == "polo") then do
            printf "O cliente %d do refrigerante %s está enchendo seu copo\n" c "Guarana Polo Norte"
          else do
            printf "O cliente %d do refrigerante %s está enchendo seu copo\n" c "Guarana Quate"
          putMVar maq (maquina-300)
        else do
          putMVar maq maquina
        putMVar l (lock+1)
        loop

reabastecer :: String -> MVar Int -> MVar Int -> IO()
reabastecer n maq l = loop
  where loop = do
        maquina <- takeMVar maq
        lock <- takeMVar l
        if (maquina < 1000) then do
          threadDelay 1500000 -- 1500000 microssegundos = 1500 milissegundos
          if (n == "cola") then do
            printf "O refrigerante %s foi reabastecido com 1000 ml, e agora possui %d ml\n" n (maquina+1000)
          else if (n == "polo") then do
            printf "O refrigerante %s foi reabastecido com 1000 ml, e agora possui %d ml\n" n (maquina+1000)
          else do
            printf "O refrigerante %s foi reabastecido com 1000 ml, e agora possui %d ml\n" n (maquina+1000)
          putMVar maq (maquina+1000)
        else do putMVar maq maquina
        putMVar l (lock+1)
        loop