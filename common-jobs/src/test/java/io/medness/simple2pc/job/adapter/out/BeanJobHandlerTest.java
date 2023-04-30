package io.medness.simple2pc.job.adapter.out;

import io.medness.simple2pc.job.domain.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeanJobHandlerTest {

    @Mock JobHandler<String> handler1;
    @Mock JobHandler<String> handler2;

    BeanJobHandler handler;

    @BeforeEach
    public void setup() {
        handler = new BeanJobHandler(List.of(handler1, handler2));
    }

    @Test
    public void shouldPerformOperationForFirstHander() {
        // given
        when(handler1.canHandle(any())).thenReturn(true);
        Job<String> job = new Job<>("op", "data");

        // when
        handler.prepare(job);

        // then
        verify(handler1).prepare("data");
        verify(handler2, never()).prepare("data");
    }

    @Test
    public void shouldPerformOperationForSecondHander() {
        // given
        when(handler2.canHandle(any())).thenReturn(true);
        Job<String> job = new Job<>("op", "data");

        // when
        handler.commit(job);

        // then
        verify(handler2).commit("data");
        verify(handler1, never()).commit("data");
    }

    @Test
    public void shouldThrowExceptionWhenHandlerCannotBeChosen() {
        // given
        Job<String> job = new Job<>("op", "data");

        // expect
        assertThatThrownBy(() -> handler.abort(job))
                .isInstanceOf(IllegalStateException.class);

    }
}