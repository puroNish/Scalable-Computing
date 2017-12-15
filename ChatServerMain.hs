{-# LANGUAGE OverloadedStrings, RecordWildCards, LambdaCase #-}

import  Conduit
import  Data.Conduit
import  Data.Conduit.Network
import qualified Data.ByteString.Char8 as BS
import  Data.Conduit.TMChan
import  Text.Printf              (printf)
import  Control.Concurrent.STM
import qualified Data.Map as Map
import  Data.Word8               (_cr)
import  Control.Monad
import  Control.Concurrent.Async (concurrently)
import  Control.Exception        (finally)

type ClientName = BS.ByteString

data Client = Client
  { clientName  ::  ClientName
  , clientChan  ::  TMChan Message
  , clientApp  ::  AppData
  }

instance Show Client where
    show client =BS.unpack (clientName client)++"@"++show (appSockAddr $ clientApp client)

data Server = Server {
    clients :: TVar (Map.Map ClientName Client)
}

data Message = Notice BS.ByteString
             | Tell ClientName BS.ByteString
             | Broadcast ClientName BS.ByteString
             | Command BS.ByteString
             deriving Show

newServer :: IO Server
newServer = do
  c <- newTVarIO Map.empty
  return Server { clients = c }

newClient :: ClientName -> AppData -> STM Client
newClient name app = do
    chan <- newTMChan
    return Client { clientName     = name
                  , clientApp      = app
                  , clientChan     = chan
                  }

broadcast :: Server -> Message -> STM ()
broadcast Server{..} msg = do
    clientmap <- readTVar clients
    mapM_ (\client -> sendMessage client msg) (Map.elems clientmap)


sendMessage :: Client -> Message -> STM ()
sendMessage Client{..} msg = writeTMChan clientChan msg

(<++>) = BS.append

handleMessage :: Server -> Client -> Conduit Message IO BS.ByteString
handleMessage server client@Client{..} = awaitForever $ \case
    Notice msg -> output $ "*** " <++> msg
    Tell name msg      -> output $ "*" <++> name <++> "*: " <++> msg
    Broadcast name msg -> output $ "<" <++> name <++> ">: " <++> msg
    Command msg        -> case BS.words msg of
        ["/help"] ->
            mapM_ output [ "------ help -----"
                         , "/list - list users online"
                         , "/help - show this message"
                         , "/quit - leave"
                         ]
        ["/list"] -> do
            cl <- liftIO $ atomically $ listClients server
            output $ BS.concat $
                "----- online -----\n" : map ((flip BS.snoc) '\n') cl

        ["/quit"] -> do
            error . BS.unpack $ clientName <++> " has quit"

        -- ignore empty strings
        [""] -> return ()
        [] -> return ()

        -- broadcasts
        ws ->
            if BS.head (head ws) == '/' then
                output $ "Unrecognized command: " <++> msg
            else
                liftIO $ atomically $
                    broadcast server $ Broadcast clientName msg
  where
    output s = yield (s <++> "\n")


listClients :: Server -> STM [ClientName]
listClients Server{..} = do
    c <- readTVar clients
    return $ Map.keys c

checkAddClient :: Server -> ClientName -> AppData -> IO (Maybe Client)
checkAddClient server@Server{..} name app = atomically $ do
    clientmap <- readTVar clients
    if Map.member name clientmap then
        return Nothing
    else do
        client <- newClient name app
        writeTVar clients $ Map.insert name client clientmap
        broadcast server  $ Notice (name <++> " has connected")
        return (Just client)


readName :: Server -> AppData -> ConduitM BS.ByteString BS.ByteString IO Client
readName server app = go
  where
  go = do
    yield "What is your name? "
    name <- lineAsciiC $ takeCE 80 =$= filterCE (/= _cr) =$= foldC
    if BS.null name then
        go
    else do
        ok <- liftIO $ checkAddClient server name app
        case ok of
            Nothing -> do
                respond "The name '%s' is in use, please choose another\n" name
                go
            Just client -> do
                respond "Welcome, %s!\nType /help to list commands.\n" name
                return client
  respond msg name = yield $ BS.pack $ printf msg $ BS.unpack name


clientSink :: Client -> Sink BS.ByteString IO ()
clientSink Client{..} = mapC Command =$ sinkTMChan clientChan True

runClient :: ResumableSource IO BS.ByteString -> Server -> Client -> IO ()
runClient clientSource server client@Client{..} =
    void $ concurrently
        (clientSource $$+- linesUnboundedAsciiC =$ clientSink client)
        (sourceTMChan clientChan
            $$ handleMessage server client
            =$ appSink clientApp)

removeClient :: Server -> Client -> IO ()
removeClient server@Server{..} client@Client{..} = atomically $ do
    modifyTVar' clients $ Map.delete clientName
    broadcast server $ Notice (clientName <++> " has disconnected")

main :: IO ()
main = do
    server <- newServer
    runTCPServer (serverSettings 8590 "*") $ \app -> do
        (fromClient, client) <-
            appSource app $$+ readName server app `fuseUpstream` appSink app
        print client
        (runClient fromClient server client)
            `finally` (removeClient server client)
