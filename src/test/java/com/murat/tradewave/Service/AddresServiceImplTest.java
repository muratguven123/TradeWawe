package com.murat.tradewave.Service;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.repository.AddressRepository;
import com.murat.tradewave.service.AddresServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddresServiceImplTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private Mapper mapper;

    @InjectMocks
    private AddresServiceImpl addresService;

    @Test
    void addToAddress_savesAddressFromRequest() {
        AddressRequest request = new AddressRequest();
        request.setName("home");
        request.setTitle("Home");
        request.setCountry("Turkey");
        request.setCity("Istanbul");
        request.setStreet("Istiklal");
        request.setDistrict("Beyoglu");
        request.setPostalCode("34000");

        addresService.addToAddress(request);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(captor.capture());
        Address saved = captor.getValue();
        assertEquals("home", saved.getName());
        assertEquals("Home", saved.getTitle());
        assertEquals("Turkey", saved.getCountry());
        assertEquals("Istanbul", saved.getCity());
        assertEquals("Istiklal", saved.getStreet());
        assertEquals("Beyoglu", saved.getDiscrict());
        assertEquals("34000", saved.getPostalCode());
    }

    @Test
    void getAdress_returnsResponsesFromRepository() {
        Address address = Address.builder()
                .id(1L)
                .name("home")
                .title("Home")
                .country("Turkey")
                .city("Istanbul")
                .street("Istiklal")
                .discrict("Beyoglu")
                .postalCode("34000")
                .isDefault(true)
                .build();
        when(addressRepository.findAllByUserAdressName("home")).thenReturn(List.of(address));

        AdressResponse request = AdressResponse.builder().addresName("home").build();
        List<AdressResponse> responses = addresService.getAdress(request);

        assertEquals(1, responses.size());
        AdressResponse resp = responses.get(0);
        assertEquals("home", resp.getAddresName());
        assertEquals(1L, resp.getId());
        assertEquals("Turkey", resp.getCountry());
        assertEquals("Istanbul", resp.getCity());
        assertEquals("Home", resp.getTitle());
        assertEquals("34000", resp.getPostalCode());
        assertEquals("Istiklal", resp.getStreet());
        assertTrue(resp.isDefault());
        assertEquals("Beyoglu", resp.getDiscrit());
    }

    @Test
    void updateAddress_updatesExistingAddress() {
        Address existing = Address.builder()
                .id(1L)
                .name("home")
                .title("Home")
                .country("Turkey")
                .city("Istanbul")
                .street("Istiklal")
                .postalCode("34000")
                .build();
        when(addressRepository.findAllByUserAdressName("home")).thenReturn(List.of(existing));

        AdressResponse updateReq = AdressResponse.builder()
                .addresName("home")
                .id(1L)
                .title("Updated")
                .country("USA")
                .city("New York")
                .street("5th Avenue")
                .postalCode("10001")
                .build();
        when(mapper.mapToResponse(existing)).thenReturn(updateReq);

        AdressResponse result = addresService.updateAddress(updateReq);

        ArgumentCaptor<Address> captor = ArgumentCaptor.forClass(Address.class);
        verify(addressRepository).save(captor.capture());
        Address saved = captor.getValue();
        assertEquals("home", saved.getName());
        assertEquals("Updated", saved.getTitle());
        assertEquals("USA", saved.getCountry());
        assertEquals("New York", saved.getCity());
        assertEquals("5th Avenue", saved.getStreet());
        assertEquals("10001", saved.getPostalCode());
        assertEquals(1L, saved.getId());
        assertEquals(updateReq, result);
    }

    @Test
    void getAllAdress_returnsAllAddresses() {
        List<Address> addresses = List.of(new Address(), new Address());
        when(addressRepository.findAll()).thenReturn(addresses);

        List<Address> result = addresService.getAllAdress();

        assertEquals(addresses, result);
        verify(addressRepository).findAll();
    }

    @Test
    void getAddressByid_returnsAddressOptional() {
        Address address = new Address();
        when(addressRepository.findById(1)).thenReturn(Optional.of(address));

        Optional<Address> result = addresService.getAddressByid(1L);

        assertTrue(result.isPresent());
        assertEquals(address, result.get());
        verify(addressRepository).findById(1);
    }
}
