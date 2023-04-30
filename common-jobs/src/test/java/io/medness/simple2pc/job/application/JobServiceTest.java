package io.medness.simple2pc.job.application;

import io.medness.simple2pc.job.application.port.out.HandleJob;
import io.medness.simple2pc.job.application.port.out.LoadJob;
import io.medness.simple2pc.job.application.port.out.PersistJob;
import io.medness.simple2pc.job.domain.Job;
import io.medness.simple2pc.job.domain.JobNotFoundException;
import io.medness.simple2pc.job.domain.JobState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock
    HandleJob handleJob;

    @Mock
    LoadJob loadJob;

    @Mock
    PersistJob persistJob;

    @InjectMocks
    JobService service;

    @Test
    public void shouldPrepareJobForTheFutureOperations() {
        // given
        Job<String> job = new Job<>("op", "data");
        when(persistJob.save(any(Job.class))).thenReturn(job);

        // when
        Job<String> returnedJob = service.prepare("op", "data");

        // then
        assertThat(returnedJob).isEqualTo(job);
        verify(persistJob).save(any(Job.class));
        verify(handleJob).prepare(job);
    }

    @Test
    public void shouldThrowExceptionWhenOperationNotSupported() {
        // given
        Job<String> job = new Job<>("op", "data");
        when(persistJob.save(any(Job.class))).thenReturn(job);
        doThrow(new IllegalStateException("Operation not supported")).when(handleJob).prepare(job);

        // expect
        assertThatThrownBy(() -> service.prepare("op", "data"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldHandleCommit() {
        //given
        Job<String> job = new Job<>("op", "data");
        UUID jobId = job.getId();
        when(loadJob.<String>findById(jobId)).thenReturn(Optional.of(job));

        // when
        service.commit(jobId);

        // then
        assertThat(job.getState()).isEqualTo(JobState.COMMITTED);
        verify(handleJob).commit(job);
    }

    @Test
    public void shouldHandleAbort() {
        //given
        Job<String> job = new Job<>("op", "data");
        UUID jobId = job.getId();
        when(loadJob.<String>findById(jobId)).thenReturn(Optional.of(job));

        // when
        service.abort(jobId);

        // then
        assertThat(job.getState()).isEqualTo(JobState.ABORTED);
        verify(handleJob).abort(job);
    }

    @Test
    public void shouldThrowExceptionWhenJobNotFound() {
        //given
        Job<String> job = new Job<>("op", "data");
        UUID jobId = job.getId();

        // expect
        assertThatThrownBy(() -> service.commit(jobId))
                .isInstanceOf(JobNotFoundException.class);

    }

    @Test
    public void shouldThrowExceptionWhenJobIsAlreadyFinalized() {
        //given
        Job<String> job = new Job<>("op", "data");
        UUID jobId = job.getId();
        when(loadJob.<String>findById(jobId)).thenReturn(Optional.of(job));
        job.commit();

        // expect
        assertThatThrownBy(() -> service.commit(jobId))
                .isInstanceOf(JobNotFoundException.class);

    }


}