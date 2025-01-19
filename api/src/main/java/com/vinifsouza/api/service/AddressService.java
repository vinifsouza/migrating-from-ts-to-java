package com.vinifsouza.api.service;

import com.vinifsouza.api.domain.address.Address;
import com.vinifsouza.api.domain.event.Event;
import com.vinifsouza.api.domain.event.EventRequestDTO;
import com.vinifsouza.api.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public void createAddress(EventRequestDTO data, Event event) {
        Address address = new Address();
        address.setCity(data.city());
        address.setUf(data.uf());
        address.setEvent(event);
        addressRepository.save(address);
    }

    public Optional<Address> findByEventId(UUID eventId) {
        return addressRepository.findByEventId(eventId);
    }
}