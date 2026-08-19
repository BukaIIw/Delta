package aethereal.discord;

import aethereal.discord.ConnectionException;

public class NoDiscordClientException extends ConnectionException {
    public NoDiscordClientException() {
        super("No Discord client found on any IPC pipe (0-9). Is Discord running?");
    }
}
