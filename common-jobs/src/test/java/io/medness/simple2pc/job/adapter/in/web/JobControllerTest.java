package io.medness.simple2pc.job.adapter.in.web;

import io.medness.simple2pc.ItTest;
import io.medness.simple2pc.TestConfig;
import io.medness.simple2pc.job.adapter.out.JobHandler;
import io.medness.simple2pc.job.adapter.out.JobRepository;
import io.medness.simple2pc.job.domain.Job;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestConfig.class)
@AutoConfigureMockMvc
class JobControllerTest extends ItTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobHandler<String> handler;

    @Autowired
    private JobRepository repository;

    @Test
    public void shouldNotPerformOperationOnUnknownJob() throws Exception {
        // expect
        UUID randomJobId = UUID.randomUUID();
        mockMvc.perform(patch("/api/v1/jobs/" + randomJobId)
                        .content("""
                                {
                                    "state": "committed"
                                }
                                """.stripIndent())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCommitJob() throws Exception {
        // given
        Job<String> job = new Job<>("op", "data");
        repository.save(job);
        when(handler.canHandle("op")).thenReturn(true);

        UUID jobId = job.getId();
        // when
        mockMvc.perform(patch("/api/v1/jobs/" + jobId)
                        .content("""
                                {
                                    "state": "committed"
                                }
                                """.stripIndent())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200));
        // then
        verify(handler).commit("data");
    }

    @Test
    public void shouldAbortJob() throws Exception {
        // given
        Job<String> job = new Job<>("op", "data");
        repository.save(job);
        when(handler.canHandle("op")).thenReturn(true);

        UUID jobId = job.getId();
        // when
        mockMvc.perform(patch("/api/v1/jobs/" + jobId)
                        .content("""
                                {
                                    "state": "aborted"
                                }
                                """.stripIndent())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200));
        // then
        verify(handler).abort("data");
    }

    @Test
    public void shouldFailWhenTryingToPerformActionOnFinalizedJob() throws Exception {
        // given
        Job<String> job = new Job<>("op", "data");
        job.commit();
        repository.save(job);
        when(handler.canHandle("op")).thenReturn(true);

        UUID jobId = job.getId();
        // when
        mockMvc.perform(patch("/api/v1/jobs/" + jobId)
                        .content("""
                                {
                                    "state": "aborted"
                                }
                                """.stripIndent())
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(404));
        // then
        verify(handler, never()).commit("data");
    }
}