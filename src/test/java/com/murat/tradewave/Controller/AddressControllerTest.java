package com.murat.tradewave.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.murat.tradewave.controller.AddressController;
import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.security.JwtAuthenticationFilter;
import com.murat.tradewave.service.AddresServiceImpl;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AddressController.class)
@AutoConfigureMockMvc(addFilters = false)
class AddressControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AddresServiceImpl addresServiceImpl;

    @Autowired ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    com.murat.tradewave.security.JwtService jwtService;

    @Test
    @DisplayName("GET /address -> returns all addresses")
    void findAll_returnsList() throws Exception {
        Address a1 = sampleAddress(1L, "Home");
        Address a2 = sampleAddress(2L, "Office");
        given(addresServiceImpl.getAllAdress()).willReturn(List.of(a1, a2));

        mockMvc.perform(get("/address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(jsonPath("$[0].name", Matchers.is("Home")))
                .andExpect(jsonPath("$[1].id", Matchers.is(2)));
    }


//    @Test
//    @DisplayName("GET /address/{id} -> returns empty when not present")
//    void findById_returnsEmpty() throws Exception {
//        given(addresServiceImpl.getAddressByid(999L))
//                .willReturn(Optional.empty());
//
//        mockMvc.perform(get("/address/{id}", 999L))
//                .andExpect(status().isNoContent());
//    }

    @Test
    @DisplayName("POST /address/save -> calls service with body")
    void save_callsService() throws Exception {
        AddressRequest req = sampleRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/address/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        ArgumentCaptor<AddressRequest> captor = ArgumentCaptor.forClass(AddressRequest.class);
        Mockito.verify(addresServiceImpl).addToAddress(captor.capture());
        AddressRequest captured = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("Home", captured.getName());
        org.junit.jupiter.api.Assertions.assertTrue(captured.isDefault());
    }

    @Test
    @DisplayName("PUT /address/update -> delegates to addToAddress (as in controller)")
    void update_callsService() throws Exception {
        AddressRequest req = sampleRequest();
        mockMvc.perform(MockMvcRequestBuilders.put("/address/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        Mockito.verify(addresServiceImpl).addToAddress(ArgumentMatchers.any(AddressRequest.class));
    }

    @Test
    @DisplayName("DELETE /address/delete -> calls removeFromAddress with body")
    void delete_callsService() throws Exception {
        AddressRequest req = sampleRequest();

        mockMvc.perform(MockMvcRequestBuilders.delete("/address/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        Mockito.verify(addresServiceImpl).removeFromAddress(ArgumentMatchers.any(AddressRequest.class));
    }

    private Address sampleAddress(Long id, String name) {
        return Address.builder()
                .id(id)
                .name(name)
                .title("Primary")
                .street("Main St 1")
                .city("Istanbul")
                .discrict("Kadikoy")
                .postalCode("34000")
                .country("TR")
                .isDefault(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private AddressRequest sampleRequest() {
        AddressRequest r = new AddressRequest();
        r.setName("Home");
        r.setTitle("Primary");
        r.setStreet("Main St 1");
        r.setCity("Istanbul");
        r.setDistrict("Marmara");
        r.setPostalCode("34000");
        r.setCountry("TR");
        r.setDefault(true);
        return r;
    }
}
