package aethereal.discord;

import aethereal.discord.DiscordIPCException;

public class ConnectionException extends DiscordIPCException {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
