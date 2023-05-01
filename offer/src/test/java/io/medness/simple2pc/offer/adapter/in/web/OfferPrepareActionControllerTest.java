package io.medness.simple2pc.offer.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import io.medness.simple2pc.offer.application.port.in.FindOffer;
import io.medness.simple2pc.offer.domain.Offer;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class OfferPrepareActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FindOffer findOffer;

    @Test
    public void shouldCreateOfferAndPurchaseIt() throws Exception {
        // when
        UUID id = createAnOfferAndGetId();
        String location = preparePurchaseAndGetLocation(id);

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "state": "committed"
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200));

        Optional<Offer> offer = findOffer.find(id);

        // then
        assertThat(offer).isPresent();
        assertThat(offer.get().getBuyerId()).isEqualTo(UUID.fromString("d7fd5276-df86-4892-91a4-0fd832bacfdb"));
        assertThat(offer.get().isReservation()).isFalse();

    }

    @Test
    public void shouldCreateOfferAndAbortPurchase() throws Exception {
        // when
        UUID id = createAnOfferAndGetId();
        String location = preparePurchaseAndGetLocation(id);

        mockMvc.perform(patch(location)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "state": "aborted"
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200));

        Optional<Offer> offer = findOffer.find(id);

        // then
        assertThat(offer).isPresent();
        assertThat(offer.get().getBuyerId()).isNull();
        assertThat(offer.get().isReservation()).isFalse();

    }

    @Nullable
    private String preparePurchaseAndGetLocation(UUID id) throws Exception {
        MvcResult preparePurchaseResult = mockMvc.perform(post("/api/v1/offers/" + id + "/prepare-actions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "action": "purchase",
                                            "price": 1.5,
                                            "buyerId": "d7fd5276-df86-4892-91a4-0fd832bacfdb"
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(202))
                .andExpect(header().exists("location"))
                .andReturn();
        String location = preparePurchaseResult.getResponse().getHeader("location");
        return location;
    }

    private UUID createAnOfferAndGetId() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/offers/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "name": "product",
                                            "price": 1.5
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("price").value("1.5"))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return UUID.fromString(id);
    }

}