package de.eschoenawa.lanchat.server;

public class ServerBuilderFactory {
    public static Server.Builder getServerBuilder(int port) {
        return new ServerImpl.BuilderImpl(port);
    }
}
