package de.eschoenawa.lanchat.util;

import java.util.ArrayList;
import java.util.List;

public class Blacklist {

    private static final int MAX_STRIKES = 0;
    //TODO move to config
    private static final long TIMEOUT = 1000;

    private final List<BlacklistEntry> entries = new ArrayList<>();

    public synchronized void addToList(String msg) {
        if (msg == null) {
            return;
        }
        entries.add(new BlacklistEntry(msg, System.currentTimeMillis()));
    }

    public synchronized boolean isInBlacklist(String msg) {
        removeExpiredEntries();
        for (BlacklistEntry be : entries) {
            if (be.strikecount > MAX_STRIKES && be.message.equals(msg)) {
                return true;
            } else if (be.message.equals(msg)) {
                be.strikecount++;
                return false;
            }
        }
        return false;
    }

    private void removeExpiredEntries() {
        entries.removeIf(be -> System.currentTimeMillis() - be.timestamp > TIMEOUT);
    }

    private static class BlacklistEntry {
        private final String message;
        private final long timestamp;
        private int strikecount;

        BlacklistEntry(String message, long timestamp) {
            this.message = message;
            this.timestamp = timestamp;
            this.strikecount = 0;
        }
    }
}
