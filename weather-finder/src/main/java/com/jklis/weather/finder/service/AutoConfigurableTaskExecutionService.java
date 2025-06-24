package com.jklis.weather.finder.service;

import com.jklis.database.utilities.Utilities;
import com.jklis.utilities.Either;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class AutoConfigurableTaskExecutionService {

    private final Either<ScheduledExecutorService, ExecutorService> exEith;
    private final boolean singleThread;
    private final static Logger LOGGER
            = Logger.getLogger(AutoConfigurableTaskExecutionService.class.getName());
    
    static final String SINGLE_THREAD_BOOL_PROP = "task.singleThread";
    static final String SINGLE_THREAD_DELAY_PROP = "task.singleThread.delay";
    static final String MULTI_THREAD_NUMBER_PROP = "task.multiThread.threadNumber";

    public AutoConfigurableTaskExecutionService() {
        singleThread = Boolean.parseBoolean(Utilities.getProperty(SINGLE_THREAD_BOOL_PROP));
        exEith = singleThread
                ? Either.withLeft(Executors.newSingleThreadScheduledExecutor())
                : Either.withRight(Executors.newFixedThreadPool(getThreadNumber()));
    }

    public <T> List<T> execute(Queue<Callable<T>> tasks) {
        if (singleThread) {
            return executeDelatedOnSingleThread(tasks);
        } else {
            return executeMultithread(tasks);
        }
    }

    private <T> List<T> executeDelatedOnSingleThread(Queue<Callable<T>> tasks) {
        List<T> results = new ArrayList<>();
        try {
            getScheduledExecutorService().scheduleWithFixedDelay(() -> {
                try {
                    if (tasks.isEmpty()) {
                        getScheduledExecutorService().shutdown();
                        return;
                    }
                    results.add(tasks.poll().call());
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }, 0, getTaskDelay(), TimeUnit.MILLISECONDS);
            awaitTermination();
        } catch (InterruptedException ex) {
            Logger.getLogger(AutoConfigurableTaskExecutionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return results;
    }

    private <T> List<T> executeMultithread(Queue<Callable<T>> tasks) {
        List<T> results = new ArrayList<>();
        List<Future<T>> futures = tasks.stream()
                .map(t -> getExecutorService().submit(t))
                .collect(Collectors.toList());
        getExecutorService().shutdown();
        for (Future<T> future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        return results;
    }

    private void awaitTermination() throws InterruptedException {
        getActiveService().awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
    }

    private ExecutorService getActiveService() {
        return exEith.isLeft() ? exEith.left() : exEith.right();
    }

    private ScheduledExecutorService getScheduledExecutorService() {
        return exEith.left();
    }

    private ExecutorService getExecutorService() {
        return exEith.right();
    }

    private int getThreadNumber() {
        return Integer.parseInt(Utilities.getProperty(MULTI_THREAD_NUMBER_PROP));
    }

    private int getTaskDelay() {
        return Integer.parseInt(Utilities.getProperty(SINGLE_THREAD_DELAY_PROP));
    }
    
}
