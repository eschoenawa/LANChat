package de.eschoenawa.lanchat.server;

public class ServerBuilderFactory {
    public static Server.Builder getServerBuilder() {
        return new ServerImpl.BuilderImpl();
    }
}
