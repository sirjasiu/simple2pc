package io.medness.simple2pc.job.adapter.out;

import io.medness.simple2pc.job.application.port.out.LoadJob;
import io.medness.simple2pc.job.application.port.out.PersistJob;
import io.medness.simple2pc.job.domain.Job;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobRepository extends CrudRepository<Job<? extends Serializable>, UUID>, PersistJob, LoadJob {

    @Override
    <T extends Serializable> Optional<Job<T>> findById(UUID jobId);
}
