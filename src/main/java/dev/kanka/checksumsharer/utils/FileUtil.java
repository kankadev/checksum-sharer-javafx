package dev.kanka.checksumsharer.utils;

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
import java.util.List;
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
        logger.debug("handleNewFiles()");

        if (files != null && !files.isEmpty()) {
            ExecutorService exec = Executors.newCachedThreadPool();
            List<ChecksumCalculationTask> tasks = new ArrayList<>(Algorithm.values().length);

            for (File file : files) {
                logger.debug("handle file {}", file);

                KnkFile knkFile = new KnkFile(file.getAbsolutePath());

                for (Algorithm algorithm : Algorithm.values()) {
                    ChecksumCalculationTask task = new ChecksumCalculationTask(knkFile, algorithm);
                    logger.debug(task);
                    tasks.add(task);
                    exec.execute(task);
                }
            }

            logger.debug("All files were added to the tasks queue and processing was started.");

            // Create a `Task` that waits until all `ChecksumCalculationTask`s finished and processes them all
            Task<Void> processCompletedTasks = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    logger.debug("processCompletedTasks#call()");

                    // block until tasks complete
                    for (Future<?> f : tasks) {
                        f.get();
                        logger.debug("Future f: " + f);
                    }

                    logger.debug("Process completed tasks now...");

                    // process completed tasks
                    for (ChecksumCalculationTask task : tasks) {

                        logger.debug("Process completed task {}", task);

                        String checksum = task.getValue();
                        logger.debug("checksum: " + checksum);
                        Algorithm algorithm = task.getAlgorithm();
                        logger.debug("algorithm: " + algorithm);
                        KnkFile knkFile = task.getKnkFile();
                        logger.debug(knkFile);

                        switch (task.getAlgorithm()) {
                            case SHA_256 -> knkFile.setSha256(task.getValue());
                            case SHA_512 -> knkFile.setSha512(task.getValue());
                            case SHA3_384 -> knkFile.setSha3384(task.getValue());
                            case SHA3_512 -> knkFile.setSha3512(task.getValue());
                        }

                        FileDAO.insertFile(knkFile);
                    }

                    logger.debug("end call();");

                    return null;
                }
            };
            exec.submit(processCompletedTasks);
            exec.shutdown();
        }
    }
}
