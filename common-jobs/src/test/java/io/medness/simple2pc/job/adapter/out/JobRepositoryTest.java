package io.medness.simple2pc.job.adapter.out;

import io.medness.simple2pc.ItTest;
import io.medness.simple2pc.TestConfig;
import io.medness.simple2pc.job.application.port.out.LoadJob;
import io.medness.simple2pc.job.domain.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@ContextConfiguration(classes = TestConfig.class)
class JobRepositoryTest extends ItTest {

    @Autowired
    JobRepository repository;

    @Test
    public void shouldProperlySerializeAndDeserializeJob() {
        // given
        Job<String> job = new Job<>("data");

        // when
        repository.save(job);
        Optional<Job<String>> loadedJob = ((LoadJob) repository).findById(job.getId());

        // then
        assertThat(loadedJob).isPresent();
        assertThat(loadedJob.get().getData()).isEqualTo("data");
    }
}