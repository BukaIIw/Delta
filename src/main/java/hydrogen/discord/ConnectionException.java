package hydrogen.discord;

import hydrogen.discord.DiscordIPCException;

public class ConnectionException extends DiscordIPCException {
    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
