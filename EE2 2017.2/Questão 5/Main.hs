{-
Na preparação de sanduíches em uma lanchonete chamada Pé de Fava, uma pessoa fornece os ingredientes (pão, carne e tomate); duas outras são responsáveis por preparar os sanduíches. 
Porém, a lanchonete dispõe de apenas uma faca para ser utilizada na preparação. Considere que os recipientes de ingredientes são continuamente reabastecidos na capacidade máxima 
de porções (30 para cada ingrediente). Desenvolva uma solução em Haskell que modele o funcionamento desta lanchonete. Utilize memória transacional e variáveis mutáveis.

obs: o resultado final tá meio bugado
-}

import Control.Concurrent
import Control.Concurrent.STM
import Text.Printf

type Faca = TVar Bool

main = do
  pao <- newMVar 0
  carne <- newMVar 0
  tomate <- newMVar 0
  lock <- newMVar 0

  faca <- atomically (newTVar False)

  forkIO $ fornecer pao carne tomate lock
  forkIO $ preparar "Preparador 1" pao carne tomate faca lock
  forkIO $ preparar "Preparador 2" pao carne tomate faca lock

fornecer :: MVar Int -> MVar Int -> MVar Int -> MVar Int -> IO ()
fornecer p c t l = loop
  where
    loop = do
      qtePaes <- takeMVar p
      qteCarnes <- takeMVar c
      qteTomates <- takeMVar t
      lock <- takeMVar l
      if (qtePaes < 30 && qteCarnes < 30 && qteTomates < 30)
        then do
          printf "Fornecedor entregou ingredientes! Quantidade atual: %d\n" (qtePaes + 1)
          putMVar p (qtePaes + 1)
          putMVar c (qteCarnes + 1)
          putMVar t (qteTomates + 1)
        else do
          printf "Quantidade maxima de ingredientes atingida!\n"
          putMVar p qtePaes
          putMVar c qteCarnes
          putMVar t qteTomates
      putMVar l (lock + 1)
      loop

preparar :: String -> MVar Int -> MVar Int -> MVar Int -> Faca -> MVar Int -> IO ()
preparar nome p c t f l = loop
  where
    loop = do
      qtePaes <- takeMVar p
      qteCarnes <- takeMVar c
      qteTomates <- takeMVar t
      lock <- takeMVar l
      faca <- atomically (readTVar f)
      if (qtePaes > 0 && qteCarnes > 0 && qteTomates > 0 && faca == False)
        then do
          printf "%s pegou a faca!\n" nome
          printf "%s preparou sanduiche!\n" nome
          atomically (writeTVar f True)
          putMVar p (qtePaes -1)
          putMVar c (qteCarnes -1)
          putMVar t (qteTomates -1)
        else do
          printf "%s nao conseguiu preparar sanduiche!\n" nome
          putMVar p (qtePaes)
          putMVar c (qteCarnes)
          putMVar t (qteTomates)
          atomically (writeTVar f faca)
      putMVar l (lock + 1)
      loop