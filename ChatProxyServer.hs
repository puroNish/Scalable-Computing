{-# LANGUAGE OverloadedStrings #-}
import           Conduit
import           Control.Concurrent.Async (concurrently)
import           Control.Monad            (void)
import           Data.ByteString          (ByteString)
import           Data.Conduit.Network
import           Data.Word8               (_cr)

creds :: [(ByteString, ByteString)]
creds =
    [ ("Trinity", "CS7NS1")
    ]

checkAuth :: Conduit ByteString IO ByteString
checkAuth = do
    yield "Username: "
    username <- lineAsciiC $ takeCE 80 =$= filterCE (/= _cr) =$= foldC
    yield "Password: "
    password <- lineAsciiC $ takeCE 80 =$= filterCE (/= _cr) =$= foldC
    if ((username, password) `elem` creds)
        then do
            yield "Successfully authenticated.\n"
        else do
            yield "Invalid username/password.\n"
            error "Invalid authentication, please log somewhere..."

main :: IO ()
main =
    runTCPServer (serverSettings 1704 "*") $ \client -> do
        (fromClient, ()) <- appSource client $$+ checkAuth =$ appSink client
        runTCPClient (clientSettings 9090 "localhost") $ \server ->
            void $ concurrently
                (appSource server $$ appSink client)
                (fromClient $$+- appSink server)
