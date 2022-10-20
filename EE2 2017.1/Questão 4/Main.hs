{-
Defina um tipo chamado Conta constituı́do de uma variável transacional do tipo inteiro. Defina as seguintes funções em Haskell:
(a) saque :: Conta− > Int− > STM() que realiza retirada de uma quantia de uma conta
(b) deposito :: Conta− > Int− > STM() que realiza um depósito em uma conta. Utilize a função saque para definir deposito
(c) saque2, uma modificação da função saque, que bloqueia, caso o saldo da conta vá se tornar negativo.
(d) Suponha que você possa retirar dinheiro de uma conta A se esta tiver saldo suficiente, senão, retira de uma conta B. Defina a função saque3, utilizando a função orElse e a função saque2.
-}

import Control.Concurrent
import Control.Concurrent.STM
import Text.Printf

type Conta = TVar Int

-- (a)
saque :: Conta -> Int -> STM()
saque c quantia = do
    valor <- readTVar c
    writeTVar c (valor-quantia)

-- (b)
deposito :: Conta -> Int -> STM()
deposito c quantia = do
    saque c (0 - quantia) -- saque com valor negativo, vai depositar

-- (c)
saque2 :: Conta -> Int -> STM()
saque2 c quantia = do
    valor <- readTVar c
    if (valor < quantia) then return()
    else writeTVar c (valor-quantia)

-- (d)
saque3 :: Conta -> Conta -> Int -> STM()
saque3 a b quantia = do
    orElse (saque2 a quantia) (saque2 b quantia)

-- tem um problema nessa função, porque ela só chama (saque2 b quantia) se (saque2 a quantia) retornar retry
-- mas saque2 não retorna retry, e sim return(), pois o retry deixaria a função em loop

--Teste das funções
main = do
  w <- atomically(newTVar 10)
  x <- atomically(readTVar w)
  print x -- valor inicial da conta
  atomically(deposito w 10)
  x <- atomically(readTVar w)
  print x -- valor após o depósito
  atomically(saque2 w 21)
  x <- atomically(readTVar w)
  print x -- valor após o primeiro saque (inválido)
  atomically(saque2 w 7)
  x <- atomically(readTVar w)
  print x -- valor após o segundo saque
  y <- atomically(newTVar 5)
  atomically(saque3 y w 12)
  x <- atomically(readTVar w)
  print x -- valor após o terceiro saque (não funciona por conta do problema em saque3)
  z <- atomically(readTVar y)
  print z -- valor de y após o terceiro saque (inválido)
  return()