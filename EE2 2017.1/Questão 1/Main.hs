{-
Uma fábrica de lâmpadas utiliza máquinas de fabricação distintas que produzem bulbos, soquetes e embalagens que são colocados em caixas separadas. 
Após a produção destes elementos, duas máquinas produzem lâmpadas, juntando bulbos e soquetes e colocando cada lâmpada em uma embalagem. 
Cada lâmpada embalada é colocada em uma única caixa até que seja completamente preenchida com 50 lâmpadas. 
Quando preenchida, a caixa é transportada para um depósito, sendo substituı́da por uma caixa vazia.
(a) Implemente, em Haskell, o que foi descrito acima, utilizando mutable variables (MVar) e envolvendo tipos primitivos atômicos.
-}

import Control.Concurrent
import Control.Concurrent.STM
import Text.Printf

type CaixaLampadas = TVar Int

main = do
  caixaBulbos <- newMVar 0
  caixaSoquetes <- newMVar 0
  caixaEmbalagens <- newMVar 0
  lock <- newMVar 0

  caixaLampadas <- atomically(newTVar 0)

  forkIO $ produtoraBulbos caixaBulbos lock
  forkIO $ produtoraSoquetes caixaSoquetes lock
  forkIO $ produtoraEmbalagens caixaEmbalagens lock
  forkIO $ produtoraLampadas "Maquina 1" caixaBulbos caixaSoquetes caixaEmbalagens caixaLampadas lock
  forkIO $ produtoraLampadas "Maquina 2" caixaBulbos caixaSoquetes caixaEmbalagens caixaLampadas lock
  forkIO $ consumidoraLampadas caixaLampadas lock

produtoraBulbos :: MVar Int -> MVar Int -> IO()
produtoraBulbos cx l = loop 
  where loop = do
        i <- takeMVar cx
        lock <- takeMVar l
        printf "Bulbo %d produzido!\n" (i+1)
        putMVar cx (i+1)
        putMVar l (lock+1)
        loop

produtoraSoquetes :: MVar Int -> MVar Int -> IO()
produtoraSoquetes cx l = loop 
  where loop = do
        i <- takeMVar cx
        lock <- takeMVar l
        printf "Soquete %d produzido!\n" (i+1)
        putMVar cx (i+1)
        putMVar l (lock+1)
        loop

produtoraEmbalagens :: MVar Int -> MVar Int -> IO()
produtoraEmbalagens cx l = loop 
  where loop = do
        i <- takeMVar cx
        lock <- takeMVar l
        printf "Embalagem %d produzida!\n" (i+1)
        putMVar cx (i+1)
        putMVar l (lock+1)
        loop

produtoraLampadas :: String -> MVar Int -> MVar Int -> MVar Int -> TVar Int -> MVar Int -> IO()
produtoraLampadas maquina cxB cxS cxE cxL l = loop 
  where loop = do
        iB <- takeMVar cxB
        iS <- takeMVar cxS
        iE <- takeMVar cxE
        lock <- takeMVar l
        if (iB < 1 || iS < 1 || iE < 1) then do
          printf "Ops! Esta faltando algum ingrediente...\n"
          putMVar cxB iB
          putMVar cxS iS
          putMVar cxE iE
        else do
          iL <- atomically(readTVar cxL)
          if (iL > 49) then do
            printf "Caixa de lampadas cheia!\n"
            putMVar cxB iB
            putMVar cxS iS
            putMVar cxE iE
            atomically(writeTVar cxL iL)
          else do
            printf "%s produziu uma lampada!\n" maquina
            putMVar cxB (iB-1)
            putMVar cxS (iS-1)
            putMVar cxE (iE-1)
            atomically(writeTVar cxL (iL+1))
        putMVar l (lock+1)
        loop

consumidoraLampadas :: TVar Int ->MVar Int -> IO()
consumidoraLampadas cx l = loop
  where loop = do
        i <- atomically(readTVar cx)
        lock <- takeMVar l
        if (i > 49) then do
          printf "Caixa de lampadas esvaziada!\n"
          atomically(writeTVar cx 0)
        else do
          printf "Caixa de lampadas nao esta cheia o suficiente...\n"
          atomically(writeTVar cx i)
        putMVar l (lock+1)
        loop