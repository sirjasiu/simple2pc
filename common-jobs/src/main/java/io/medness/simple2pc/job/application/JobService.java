package io.medness.simple2pc.job.application;

import io.medness.simple2pc.job.application.port.in.AbortJob;
import io.medness.simple2pc.job.application.port.in.CommitJob;
import io.medness.simple2pc.job.application.port.in.GenerateJobPath;
import io.medness.simple2pc.job.application.port.in.PrepareJob;
import io.medness.simple2pc.job.application.port.out.HandleJob;
import io.medness.simple2pc.job.application.port.out.LoadJob;
import io.medness.simple2pc.job.application.port.out.PersistJob;
import io.medness.simple2pc.job.domain.Job;
import io.medness.simple2pc.job.domain.JobNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

@Service
@Transactional
public class JobService implements PrepareJob, CommitJob, AbortJob, GenerateJobPath {

    private final HandleJob handleJob;
    private final LoadJob loadJob;
    private final PersistJob persistJob;

    public JobService(@Lazy HandleJob handleJob, LoadJob loadJob, PersistJob persistJob) {
        this.handleJob = handleJob;
        this.loadJob = loadJob;
        this.persistJob = persistJob;
    }

    @Override
    public void abort(UUID jobId) {
        loadJob.findById(jobId)
                .filter(Job::isNotFinalized)
                .ifPresentOrElse(
                        (job) -> {
                            handleJob.abort(job);
                            job.abort();
                        },
                        () -> {
                            throw new JobNotFoundException();
                        });

    }

    @Override
    public void commit(UUID jobId) {
        loadJob.findById(jobId)
                .filter(Job::isNotFinalized)
                .ifPresentOrElse(
                        (job) -> {
                            handleJob.commit(job);
                            job.commit();
                        },
                        () -> {
                            throw new JobNotFoundException();
                        });
    }

    @Override
    public <T extends Serializable> Job<T> prepare(T jobData) {
        Job<T> job = persistJob.save(new Job<>(jobData));
        handleJob.prepare(job);
        return job;
    }

    @Override
    public <T extends Serializable> URI generate(Job<T> job, UriComponentsBuilder builder) {
        return builder.replacePath("api/v1/jobs/" + job.getId().toString()).build().toUri();
    }
}
