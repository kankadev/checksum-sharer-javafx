package dev.kanka.checksumsharer.watchdir;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * watches directories for file changes
 */
public class WatchDirService {
    private static WatchDirService instance;
    private static WatchService watcher;
    private final Map<WatchKey, Path> keys = new HashMap<>();
    private final boolean recursive = false; // TODO: read this from Preferences?
    private boolean trace = false;
    private static final Logger LOGGER = LogManager.getLogger();

    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private WatchDirService() {
        try {
            this.watcher = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static WatchDirService getInstance() {
        if (instance == null) {
            instance = new WatchDirService();
        }
        return instance;
    }

    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                LOGGER.debug("Register: {}", dir);
            } else {
                if (!dir.equals(prev)) {
                    LOGGER.debug("Update: {} -> {}", prev, dir);
                }
            }
        }
        keys.put(key, dir);
        this.trace = true;
    }

    /**
     * Register the given directory, and all its subdirectories, with the WatchService.
     */
    public void registerAll(final Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });

        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        new Thread(() -> {
            while (true) {

                // wait for key to be signalled
                WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }

                Path dir = keys.get(key);
                if (dir == null) {
                    LOGGER.error("WatchKey not recognized!");
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    LOGGER.debug("{}: {}", event.kind().name(), child);

                    // if directory is created, and watching recursively, then
                    // register it and its subdirectories
                    if (recursive && (kind == ENTRY_CREATE)) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                            }
                        } catch (IOException x) {
                            // ignore to keep sample readable
                        }
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        }).start();
    }
}
