package io.medness.simple2pc.job.application.port.in;

import java.util.UUID;

public interface CommitJob {

    void commit(UUID jobId);
}
