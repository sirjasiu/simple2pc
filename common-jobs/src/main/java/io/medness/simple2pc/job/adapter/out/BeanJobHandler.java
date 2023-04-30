package io.medness.simple2pc.job.adapter.out;

import io.medness.simple2pc.job.application.port.out.HandleJob;
import io.medness.simple2pc.job.domain.Job;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public class BeanJobHandler implements HandleJob {

    private final List<JobHandler<? extends Serializable>> handlers;

    public BeanJobHandler(List<JobHandler<? extends Serializable>> handlers) {
        this.handlers = handlers;
    }

    @Override
    public <T extends Serializable> void prepare(Job<T> job) {
        executeForJob(job, JobHandler::prepare);
    }

    @Override
    public <T extends Serializable> void commit(Job<T> job) {
        executeForJob(job, JobHandler::commit);
    }

    @Override
    public <T extends Serializable> void abort(Job<T> job) {
        executeForJob(job, JobHandler::abort);
    }

    private <T extends Serializable> void executeForJob(Job<T> job, BiConsumer<JobHandler<T>, T> operation) {
        //noinspection unchecked
        handlers.stream()
                .filter(handler -> handler.canHandle(job.getOperationName()))
                .findFirst()
                .ifPresentOrElse(
                        handler -> operation.accept((JobHandler<T>) handler, job.getData()),
                        () -> {
                            throw new IllegalStateException("Operation not supported");
                        });
    }

}
