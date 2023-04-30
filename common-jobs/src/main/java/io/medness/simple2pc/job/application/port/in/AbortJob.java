package io.medness.simple2pc.job.application.port.in;

import java.util.UUID;

public interface AbortJob {

    void abort(UUID jobId);
}
