{-
Uma fábrica de porcas e parafusos funciona com o uso de uma máquina de fabricação de porcas e de uma outra máquina que fabrica parafusos. 
Após a produção, porcas e parafusos são colocados em caixas separadas de onde são retirados por duas máquinas que montam pares porca-parafuso. 
Cada par porca-parafuso é colocado em uma única caixa. Implemente, em Haskell, o que foi descrito. As máquinas são definidas por funções executadas em threads.
-}

import Control.Concurrent
import Text.Printf

main = do
  -- caixas que armazenam porcas e parafusos
  porcas <- newMVar 0
  parafusos <- newMVar 0
  lock <- newMVar 0

  -- caixa que armazena pares porca-parafuso
  pares <- newMVar 0

  forkIO $ fabricacaoPorca porcas lock
  forkIO $ fabricacaoParafusos parafusos lock
  forkIO $ fabricacaoPares "Maquina 1" porcas parafusos pares lock
  forkIO $ fabricacaoPares "Maquina 2" porcas parafusos pares lock

fabricacaoPorca :: MVar Int -> MVar Int -> IO()
fabricacaoPorca p l = loop
  where loop = do
        porcas <- takeMVar p
        lock <- takeMVar l
        printf "Porca fabricada!\n"
        putMVar p (porcas+1)
        putMVar l (lock+1)
        loop

fabricacaoParafusos :: MVar Int -> MVar Int -> IO()
fabricacaoParafusos p l = loop
  where loop = do
        parafusos <- takeMVar p
        lock <- takeMVar l
        printf "Parafuso fabricado!\n"
        putMVar p (parafusos+1)
        putMVar l (lock+1)
        loop

fabricacaoPares :: String -> MVar Int -> MVar Int -> MVar Int -> MVar Int -> IO()
fabricacaoPares maquina porcas parafusos pares l = loop
  where loop = do
        por <- takeMVar porcas
        par <- takeMVar parafusos
        lock <- takeMVar l
        if (por > 0 && par > 0) then do
          printf "%s fabricou par porca-parafuso!\n" maquina
          p <- takeMVar pares
          putMVar porcas (por-1)
          putMVar parafusos (par-1)
          putMVar pares (p+1)
        else do 
          printf "Nao foi possivel fabricar um par!\n"
          putMVar porcas por
          putMVar parafusos par
        putMVar l (lock+1)
        loop