{-
Um semáforo binário é definido tradicionalmente pelas operações "p" e "v". A primeira adquire um lock, fazendo o seguinte: testa se o lock é verdadeiro, se sim, 
adquire o lock tornando-o falso, senão espera. A segunda operação libera o lock, tornando-o verdadeiro. Defina um semáforo em Haskell usando STM. 
Para isso, defina um novo tipo com uma variável transacional do tipo Bool. Defina as duas funções "p" e "v".
-}

import Control.Concurrent
import Control.Concurrent.STM

type Semaforo = TVar Bool

p :: Semaforo -> STM()
p sem = do
  lock <- readTVar(sem)
  if (lock) then writeTVar sem False
  else retry

v :: Semaforo -> STM()
v sem = do
  lock <- readTVar(sem)
  writeTVar sem True

-- Testando o semáforo
main = do
  s <- atomically(newTVar False)
  a <- atomically(readTVar(s))
  print a -- valor inicial do semáforo
  atomically(v s)
  x <- atomically(readTVar(s))
  print x -- depois da função v
  atomically(p s)
  y <- atomically(readTVar(s))
  print y -- depois da função p
  atomically(p s)
  z <- atomically(readTVar(s))
  print z -- chama exceção porque fica chamando retry eternamente
  return ()