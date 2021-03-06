package dev.kanka.checksumsharer.utils;

import dev.kanka.checksumsharer.Constants;
import dev.kanka.checksumsharer.MainController;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.hash.Algorithm;
import dev.kanka.checksumsharer.hash.ChecksumCalculationTask;
import dev.kanka.checksumsharer.models.KnkFile;
import dev.kanka.checksumsharer.settings.PreferenceKeys;
import dev.kanka.checksumsharer.settings.Settings;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.prefs.Preferences;

public class FileUtil {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Preferences preferences = Preferences.userRoot().node(Settings.class.getName());

    private FileUtil() {
        throw new AssertionError("Don't instantiate this class.");
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }

    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static void handleNewFiles(List<File> files) {

        MainController.getInstance().setProgressIndicatorVisible(true);

        if (files != null && !files.isEmpty()) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            List<ChecksumCalculationTask> tasks = new ArrayList<>(Algorithm.values().length);
            List<Future<KnkFile>> futures = new ArrayList<>();

            for (File file : files) {
                LOGGER.debug("handle file {}", file);

                KnkFile knkFile = new KnkFile(file.getAbsolutePath());

                for (Algorithm algorithm : Algorithm.values()) {
                    ChecksumCalculationTask task = new ChecksumCalculationTask(knkFile, algorithm);
                    tasks.add(task);
                    Future<KnkFile> knkFileFuture = executorService.submit(task);
                    futures.add(knkFileFuture);
                }
            }

            Task<HashSet<KnkFile>> processCompletedTasks = new Task<>() {
                @Override
                protected HashSet<KnkFile> call() throws Exception {
                    HashSet<KnkFile> finishedFiles = new HashSet();

                    for (Future<KnkFile> future : futures) {
                        KnkFile knkFile = future.get();
                        LOGGER.debug(knkFile);
                        finishedFiles.add(knkFile);
                    }
                    return finishedFiles;
                }
            };

            executorService.submit(processCompletedTasks);

            processCompletedTasks.setOnSucceeded(event -> {
                LOGGER.debug("processCompletedTasks is succeeded.");
                try {
                    HashSet<KnkFile> knkFiles = processCompletedTasks.get();

                    for (KnkFile file : knkFiles) {
                        int id = FileDAO.insertFile(file);
                        Optional<KnkFile> knkFile = FileDAO.getFile(id); // TODO improve, because we have two file objects for one purpose here now...
                        exportChecksumFilesToLocalFileSystem(knkFile.get());
                    }

                    MainController.getInstance().setProgressIndicatorVisible(false);
                } catch (InterruptedException | ExecutionException e) {
                    Alerts.error("Fatal error", "Fatal error", e.getStackTrace().toString());
                }
            });

            executorService.shutdown();
        }
    }

    public static void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Alerts.error("Error", "Export text file failed", ex.getStackTrace().toString());
            LOGGER.error(ex);
        }
    }

    /**
     * Exports all information about a file including checksums into a text file on the local storage, if the user sets an export directory in the settings.
     *
     * @param knkFile
     */
    public static void exportChecksumFilesToLocalFileSystem(KnkFile knkFile) {
        for (int i = 1; i <= Constants.NUMBER_LOCAL_STORAGE_PATHS; i++) {
            String localStoragePath = preferences.get(PreferenceKeys.LOCAL_STORAGE_EXPORT_PATH + i, null);

            if (localStoragePath != null && !localStoragePath.isEmpty()) {
                File path = new File(localStoragePath);

                if (!path.exists() || !path.isDirectory()) {
                    Alerts.error("Error", "Local storage path not exists.", "The local storage directory doesn't exists: " + localStoragePath).show();
                    continue;
                }

                File file = new File(localStoragePath + File.separator + knkFile.getId() + " - " + knkFile.getName() + ".txt");
                saveTextToFile(knkFile.formatForTextFile(), file);

                if (!file.exists()) {
                    Alerts.error("Error", "Couldn't create file.", "This file couldn't be created: " + file.getAbsolutePath()).show();
                }
            }
        }
    }
}
