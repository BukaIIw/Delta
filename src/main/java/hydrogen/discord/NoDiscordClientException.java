package hydrogen.discord;

import hydrogen.discord.ConnectionException;

public class NoDiscordClientException extends ConnectionException {
    public NoDiscordClientException() {
        super("No Discord client found on any IPC pipe (0-9). Is Discord running?");
    }
}
