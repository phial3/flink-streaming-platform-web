package com.flink.streaming.web.ao.impl;

import com.flink.streaming.web.ao.JobServerAO;
import com.flink.streaming.web.model.dto.JobConfigDTO;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public abstract class AbstractJobServerAO implements JobServerAO {
    private static final int SHORT_MAX = 65536;
    private static int counter = -1;
    private static final AtomicBoolean flag = new AtomicBoolean(false);
    private final ConcurrentLinkedDeque<JobInstance> jobQueue = new ConcurrentLinkedDeque<>();

    @PostConstruct
    protected synchronized void init() throws Exception {
        if (!flag.get()) {
            flag.set(true);
        }
        run();
    }

    protected long addJob(JobInstance task) {
        jobQueue.addLast(task);
        return task.getId();
    }

    protected JobInstance getJobInstance() {
        return jobQueue.pollFirst();
    }

    protected void updateState(final JobInstance instance, Object metadata) {

    }

    protected void run() throws Exception {
        new Thread(() -> {
            while (true) {
                JobInstance instance = null;
                while ((instance = getJobInstance()) != null) {
                    try {
                        supplyAsync(instance);
                    } catch (Exception e) {
                        log.error("task queue async run error!", e);
                    }
                }
            }
        }).start();
    }

    private void supplyAsync(final JobInstance task) {
        CompletableFuture.supplyAsync(() -> {
            try {
                return task.call();
            } catch (Exception e) {
                log.error("async task error. task={} ", task, e);
                updateState(task, "");
            }
            return null;
        }).whenComplete((appId, e) -> {
            log.info("async task complete. appId={}", appId);
            updateState(task, appId);
        }).exceptionally(e -> {
            log.error("async task exception. ", e);
            updateState(task, e.getMessage());
            return null;
        });
    }


    class JobInstance<T> extends JobConfigDTO {
        private static final long serialVersionUID = -5159232711753328112L;

        private Object metadata;
        private Callable<T> call;

        public JobInstance(Callable<T> newJob) {
            super();
            this.call = newJob;
            if (this.getId() == null) {
                this.setId(nextId());
            }
        }

        public Object getMetadata() {
            return metadata;
        }

        public void setMetadata(Object metadata) {
            this.metadata = metadata;
        }

        public <T> T call() throws Exception {
            return (T) this.call.call();
        }
    }

    protected static synchronized long nextId() {
        long now = System.currentTimeMillis();
        if (counter == -1) {
            long seed = now ^ Thread.currentThread().getId();
            Random rnd = new Random(Long.hashCode(seed));
            counter = rnd.nextInt(SHORT_MAX);
        }
        long id = (now << 16) | counter;
        counter = (counter + 1) % SHORT_MAX;
        return id;
    }
}
