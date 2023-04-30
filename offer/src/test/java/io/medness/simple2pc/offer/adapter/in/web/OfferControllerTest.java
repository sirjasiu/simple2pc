package io.medness.simple2pc.offer.adapter.in.web;

import com.jayway.jsonpath.JsonPath;
import io.medness.simple2pc.offer.application.port.in.CreateOffer;
import io.medness.simple2pc.offer.application.port.in.FindOffer;
import io.medness.simple2pc.offer.application.port.in.Purchase;
import io.medness.simple2pc.offer.domain.Offer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FindOffer findOffer;

    @Autowired
    private CreateOffer createOffer;

    @Autowired
    private Purchase purchase;

    @Test
    public void shouldNotGetUnknownOffer() throws Exception {
        UUID randomOfferId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/offers/" + randomOfferId))
                .andDo(print())
                .andExpect(status().is(404));
    }

    @Test
    public void shouldCreateOffer() throws Exception {
        // when
        MvcResult result = mockMvc.perform(post("/api/v1/offers/")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "name": "user",
                                            "price": 1.5
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("price").value("1.5"))
                .andReturn();

        String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        Optional<Offer> offer = findOffer.find(UUID.fromString(id));

        // then
        assertThat(offer).isPresent();
        assertThat(offer.get().getPrice()).isEqualByComparingTo(new BigDecimal(1.5));
    }

    @Test
    public void shouldPurchaseOffer() throws Exception {
        // given
        Offer offer = createOffer.create("offer", new BigDecimal(1.5));

        // when
        mockMvc.perform(post("/api/v1/offers/" + offer.getId() + "/actions")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                """
                                        {
                                            "action": "purchase",
                                            "price": 1.5,
                                            "accountId": "d7fd5276-df86-4892-91a4-0fd832bacfdb"
                                        }
                                        """.stripIndent()
                        ))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("accountId").value("d7fd5276-df86-4892-91a4-0fd832bacfdb"))
                .andReturn();

        Optional<Offer> offerOptional = findOffer.find(offer.getId());

        // then
        assertThat(offerOptional).isPresent();
        assertThat(offerOptional.get().getAccountId()).isEqualTo(UUID.fromString("d7fd5276-df86-4892-91a4-0fd832bacfdb"));
        assertThat(offerOptional.get().isReservation()).isFalse();
    }

}