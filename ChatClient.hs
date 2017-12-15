{-# LANGUAGE OverloadedStrings #-}
import           Conduit
import           Control.Concurrent.Async (concurrently)
import           Control.Monad            (void)
import           Data.Conduit.Network

main :: IO ()
main =
    runTCPClient (clientSettings 8590 "*") $ \server ->
        void $ concurrently
            (stdinC $$ appSink server)
            (appSource server $$ stdoutC)
