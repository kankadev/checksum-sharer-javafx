package dev.kanka.checksumsharer.utils;

import dev.kanka.checksumsharer.MainController;
import dev.kanka.checksumsharer.dao.FileDAO;
import dev.kanka.checksumsharer.hash.Algorithm;
import dev.kanka.checksumsharer.hash.ChecksumCalculationTask;
import dev.kanka.checksumsharer.models.KnkFile;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FileUtil {

    private static final Logger logger = LogManager.getLogger();

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
                logger.debug("handle file {}", file);

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
                        logger.debug(knkFile);
                        finishedFiles.add(knkFile);
                    }
                    return finishedFiles;
                }
            };

            executorService.submit(processCompletedTasks);

            processCompletedTasks.setOnSucceeded(event -> {
                logger.debug("processCompletedTasks is succeeded.");
                try {
                    HashSet<KnkFile> knkFiles = processCompletedTasks.get();

                    for (KnkFile file : knkFiles) {
                        FileDAO.insertFile(file);
                    }

                    MainController.getInstance().setProgressIndicatorVisible(false);
                } catch (InterruptedException | ExecutionException e) {
                    Alerts.error("Fatal error", "Fatal error", e.getStackTrace().toString());
                }
            });

            executorService.shutdown();
        }
    }
}
