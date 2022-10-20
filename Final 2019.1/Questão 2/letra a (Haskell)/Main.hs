{-
Um fabricante de sorvetes contratou você para simular parte do processo de produção deles. Na produção, a mistura de dois ingredientes (o aromatizante e o espessante) 
acontece apenas quando o recipiente de mistura refrigerado (RMR) está disponível, ou seja, eles são retirados de diferentes depósitos quando podem ser efetivamente 
misturados. Para o RMR ficar disponível, é necessário que ele seja esvaziado, o que acontece com o giro do RMR a fim de retirar o sorvete. Assim, as operações de 
retirada do sorvete e de mistura dos ingredientes precisam do RMR de forma exclusiva. Defina as operações para misturar os ingredientes e esvaziar o RMR. Considere 
que os ingredientes ficam guardados em depósitos distintos e que vamos diferenciar a quantidade que utilizamos de cada um deles. Além disso, vamos abstrair o tempo 
necessário para misturar ingredientes e returar sorvete do recipiente.

a) Implemente, em Haskell, o que foi descrito acima, utilizando mutable variables (MVar) e memória transacional (TVar), como você achar necessário.
-}

import Control.Concurrent
import Control.Concurrent.STM
import Text.Printf

type Sorvete = TVar Int -- contém as quantidades de sorvetes feitos

main = do
  -- contém as quantidades dos ingredientes nas misturas
  aromatizante <- newMVar 0
  espessante <- newMVar 0

  sorvete <- atomically(newTVar 0)

  forkIO $ mistura aromatizante espessante
  forkIO $ retirada aromatizante espessante sorvete

mistura :: MVar Int -> MVar Int -> IO()
mistura a e = loop
  where loop = do
        aromatizante <- takeMVar a
        printf "1 aromatizante adicionado na mistura!\n"
        putMVar a (aromatizante+1)
        espessante <- takeMVar e
        printf "2 espessantes adicionados na mistura!\n"
        putMVar e (espessante+2)
        loop

retirada :: MVar Int -> MVar Int -> TVar Int -> IO()
retirada a e s = loop
  where loop = do
        aromatizante <- takeMVar a
        espessante <- takeMVar e
        if (aromatizante > 0 && espessante > 1) then do
          sorvetes <- atomically(readTVar s)
          atomically(writeTVar s (sorvetes+1))
          printf "Sorvete retirado! Quantidade total: %d\n" sorvetes
          putMVar a (aromatizante-1)
          putMVar e (espessante-2)
        else do
          putMVar a aromatizante
          putMVar e espessante
        loop