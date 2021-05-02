package de.eschoenawa.lanchat.server;

import java.util.HashMap;
import java.util.Map;

public class TimeoutStrikeBlacklist {

    private static final int DEFAULT_MAX_STRIKES = 0;
    private static final long DEFAULT_TIMEOUT = 1000;

    private Map<String, BlacklistEntry> entries = new HashMap<>();
    private long timeout;
    private int maxStrikes;

    public TimeoutStrikeBlacklist() {
        this.timeout = DEFAULT_TIMEOUT;
        this.maxStrikes = DEFAULT_MAX_STRIKES;
    }

    public TimeoutStrikeBlacklist(long timeout, int maxStrikes) {
        this.timeout = timeout;
        this.maxStrikes = maxStrikes;
    }

    public synchronized void addToList(String msg) {
        if (msg == null) {
            return;
        }
        entries.put(msg, new BlacklistEntry(System.currentTimeMillis()));
    }

    public synchronized boolean isInBlacklist(String msg) {
        removeExpiredEntries();
        for (String entryMessage : entries.keySet()) {
            BlacklistEntry entryForCurrentMessage = entries.get(entryMessage);
            if (entryForCurrentMessage.strikeCount > maxStrikes && entryMessage.equals(msg)) {
                return true;
            } else if (entryMessage.equals(msg)) {
                entryForCurrentMessage.strikeCount++;
                return false;
            }
        }
        return false;
    }

    private void removeExpiredEntries() {
        entries.values().removeIf(blacklistEntry -> System.currentTimeMillis() - blacklistEntry.timestamp > timeout);
    }

    private static class BlacklistEntry {
        private long timestamp;
        private int strikeCount;

        BlacklistEntry(long timestamp) {
            this.timestamp = timestamp;
            this.strikeCount = 0;
        }
    }
}
