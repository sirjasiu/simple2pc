package io.medness.simple2pc.job.adapter.in.web;

import io.medness.simple2pc.job.application.port.in.AbortJob;
import io.medness.simple2pc.job.application.port.in.CommitJob;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs/{jobId}")
public class JobController {

    private final CommitJob commitJob;
    private final AbortJob abortJob;

    public JobController(CommitJob commitJob, AbortJob abortJob) {
        this.commitJob = commitJob;
        this.abortJob = abortJob;
    }

    @PatchMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void performAction(@PathVariable("jobId") UUID jobId, @RequestBody JobUpdateRequest request) {
        switch (request.state()) {
            case committed -> commitJob.commit(jobId);
            case aborted -> abortJob.abort(jobId);
        }
    }

}
