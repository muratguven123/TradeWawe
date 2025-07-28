package com.murat.tradewave.service;

import com.murat.tradewave.dto.Address.request.AddressRequest;
import com.murat.tradewave.dto.Address.response.AdressResponse;
import com.murat.tradewave.helper.Mapper;
import com.murat.tradewave.model.Address;
import com.murat.tradewave.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AddresServiceImpl implements AddresService {

    private final AddressRepository addressRepository;
    private final Mapper mapper;

    @Override
    @Transactional
    public void addToAddress(AddressRequest addressRequest) {
        Address address = Address.builder()
                .name(addressRequest.getAddresName())
                .title(addressRequest.getTittle())
                .country(addressRequest.getCountry())
                .city(addressRequest.getCity())
                .street(addressRequest.getStreet())
                .discrict(addressRequest.getDiscrict())
                .id(addressRequest.getId())
                .createdAt(addressRequest.getCreatedAt())
                .postalCode(addressRequest.getPostalCode())
                .build();
        addressRepository.save(address);
    }

    @Override
    @Transactional
    public void removeFromAddress(AddressRequest addressRequest) {
//Burada yapılması gereken ek bir iş var onu bir sonraki sprinte devredeceğim şimdilik içerideki id ile dönsün mantık olarak yanlış olan yerin farkındayım
        Address address = addressRepository.findById(Math.toIntExact(addressRequest.getId())).stream().findFirst().orElseThrow(()->new RuntimeException("Address Not Exist"));
        addressRepository.delete(address);
    }
    @Override
    @Transactional
    public List<AdressResponse> getAdress(AdressResponse adressResponse) {

        List<Address> adress= addressRepository.findAllByUserAdressName(adressResponse.getAddresName());

        return adress.stream()
                .map(address -> AdressResponse.builder()
                        .addresName(address.getName())
                        .id(address.getId())
                        .country(address.getCountry())
                        .city(address.getCity())
                        .title(address.getTitle())
                        .postalCode(address.getPostalCode())
                        .street(address.getStreet())
                        .isDefault(address.isDefault())
                        .discrit(address.getDiscrict())
                        .build())
                .toList();
    }
    @Override
    @Transactional
    public AdressResponse updateAddress(AdressResponse adressResponse) {
        Address address = addressRepository.findAllByUserAdressName(adressResponse.getAddresName()).stream().findFirst().orElse(null);
        if (address != null) {
            address.setName(adressResponse.getAddresName());
            address.setCity(adressResponse.getCity());
            address.setTitle(adressResponse.getTitle());
            address.setPostalCode(adressResponse.getPostalCode());
            address.setStreet(adressResponse.getStreet());
            address.setCountry(adressResponse.getCountry());
            address.setId(adressResponse.getId());
            addressRepository.save(address);
        }
        return mapper.mapToResponse(address);
    }
    public List<Address> getAllAdress() {
        return addressRepository.findAll();
    }
    public Optional<Address> getAddressByid(Long id){
        return addressRepository.findById(id.intValue());
    }
}
