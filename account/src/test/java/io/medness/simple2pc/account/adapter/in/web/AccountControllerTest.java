package io.medness.simple2pc.account.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import io.medness.simple2pc.account.adapter.out.AccountRepository;
import io.medness.simple2pc.account.application.port.in.CreateAccount;
import io.medness.simple2pc.account.application.port.in.Deposit;
import io.medness.simple2pc.account.application.port.in.FindAccount;
import io.medness.simple2pc.account.domain.Account;
import io.medness.simple2pc.job.adapter.out.JobHandler;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FindAccount findAccount;

    @Autowired
    private CreateAccount createAccount;

    @Autowired
    private Deposit deposit;

    @Test
    public void shouldNotGetNotKnownAccount() throws Exception {
        UUID randomJobId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/accounts/" + randomJobId))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateAccount() throws Exception {
        // when
        MvcResult result = mockMvc.perform(post("/api/v1/accounts/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "name": "user"
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("funds").value("0"))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Optional<Account> account = findAccount.find(UUID.fromString(id));

        // then
        assertThat(account).isPresent();
        assertThat(account.get().getFunds()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void shouldDepositToAccount() throws Exception {
        // given
        Account johny = createAccount.create("johny");
        // when
       mockMvc.perform(post("/api/v1/accounts/" + johny.getId() + "/actions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "action": "deposit",
                                            "funds": 120
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("funds").value("120.0"))
                .andReturn();

        Optional<Account> account = findAccount.find(johny.getId());

        // then
        assertThat(account).isPresent();
        assertThat(account.get().getFunds()).isEqualByComparingTo(new BigDecimal(120));
    }

    @Test
    public void shouldWithdrawFromAccount() throws Exception {
        // given
        Account johny = createAccount.create("johny");
        deposit.deposit(johny.getId(), new BigDecimal(120));

        // when
        mockMvc.perform(post("/api/v1/accounts/" + johny.getId() + "/actions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "action": "withdraw",
                                            "funds": 100
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("funds").value("20.0"))
                .andReturn();

        Optional<Account> account = findAccount.find(johny.getId());

        // then
        assertThat(account).isPresent();
        assertThat(account.get().getFunds()).isEqualByComparingTo(new BigDecimal(20));
    }

    @Test
    public void shouldNotBeAbleToWithdrawFromAccount() throws Exception {
        // given
        Account johny = createAccount.create("johny");
        deposit.deposit(johny.getId(), new BigDecimal(120));

        // when
        mockMvc.perform(post("/api/v1/accounts/" + johny.getId() + "/actions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "action": "withdraw",
                                            "funds": 200
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(400))
                .andReturn();

        Optional<Account> account = findAccount.find(johny.getId());

        // then
        assertThat(account).isPresent();
        assertThat(account.get().getFunds()).isEqualByComparingTo(new BigDecimal(120));
    }

}