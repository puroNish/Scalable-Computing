{-# LANGUAGE OverloadedStrings, RecordWildCards, LambdaCase #-}

 
import Network.Socket
import System.IO
import Control.Exception
import Control.Concurrent
import Control.Monad (when)
import Control.Monad.Fix (fix)
import  Conduit
import  Data.Conduit
import  Data.Conduit.Network
import qualified Data.ByteString.Char8 as BS
import  Data.Conduit.TMChan
import  Text.Printf              (printf)
import  Control.Concurrent.STM
import qualified Data.Map as Map
import  Data.Word8               (_cr)
 
main :: IO ()
main = do
  server <- newServer
  runTCPServer (serverSettings 8590 "*") $ \app -> do
  (fromClient, client) <-
    appSource app $$+ readName server app `fuseUpstream` appSink app
  mainLoop sock chan 0
 
type Msg = (Int, String)

data Message = Notice BS.ByteString
             | Tell ClientName BS.ByteString
             | Broadcast ClientName BS.ByteString
             | Command BS.ByteString
             deriving Show
data Server = Server {
    clients :: TVar (Map.Map ClientName Client)
}

type ClientName = BS.ByteString

data Client = Client
  { clientName  ::  ClientName
  , clientChan  ::  TMChan Message
  , clientApp  ::  AppData
  }

newServer :: IO Server
newServer = do
  c <- newTVarIO Map.empty
  return Server { clients = c }

mainLoop :: Socket -> Chan Msg -> Int -> IO ()
mainLoop sock chan msgNum = do
  conn <- accept sock
  forkIO (runConn conn chan msgNum)
  mainLoop sock chan $! msgNum + 1
 
runConn :: (Socket, SockAddr) -> Chan Msg -> Int -> IO ()
runConn (sock, _) chan msgNum = do
    let broadcast msg = writeChan chan (msgNum, msg)
    hdl <- socketToHandle sock ReadWriteMode
    hSetBuffering hdl NoBuffering
 
    hPutStrLn hdl "Hi, what's your name?"
    name <- fmap init (hGetLine hdl)
    broadcast ("--> " ++ name ++ " entered chat.")
    hPutStrLn hdl ("Welcome, " ++ name ++ "!")
 
    commLine <- dupChan chan
 
    -- fork off a thread for reading from the duplicated channel
    reader <- forkIO $ fix $ \loop -> do
        (nextNum, line) <- readChan commLine
        when (msgNum /= nextNum) $ hPutStrLn hdl line
        loop
 
    handle (\(SomeException _) -> return ()) $ fix $ \loop -> do
        line <- fmap init (hGetLine hdl)
        case line of
             -- If an exception is caught, send a message and break the loop
             "quit" -> hPutStrLn hdl "Bye!"
             -- else, continue looping.
             _      -> broadcast (name ++ ": " ++ line) >> loop
 
    killThread reader                      -- kill after the loop ends
    broadcast ("<-- " ++ name ++ " left.") -- make a final broadcast
    hClose hdl                             -- close the handle
