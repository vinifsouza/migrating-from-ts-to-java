package com.vinifsouza.api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.vinifsouza.api.domain.event.Event;
import com.vinifsouza.api.domain.event.EventAddressProjection;
import com.vinifsouza.api.domain.event.EventRequestDTO;
import com.vinifsouza.api.domain.event.EventResponseDTO;
import com.vinifsouza.api.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.List;

@Service
public class EventService {
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${admin.key}")
    private String adminKey;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private EventRepository repository;

    @Autowired
    private AddressService addressService;

    public List<EventResponseDTO> getAll(int page, int size, boolean retrivePastEvents) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventAddressProjection> eventsPage;
        if (retrivePastEvents) {
            eventsPage = this.repository.findUpcomingEvents(new Date(), pageable);
        } else {
            eventsPage = this.repository.findAllProjectedBy(pageable);
        }

        System.out.println("event " + eventsPage.toString() );

        return eventsPage.map(
                    event -> new EventResponseDTO(
                                event.getId(),
                                event.getTitle(),
                                event.getDescription(),
                                event.getDate(),
                                event.getCity(),
                                event.getUf(),
                                event.getRemote(),
                                event.getEventUrl(),
                                event.getImageUrl()
                )).stream().toList();
    }

    public Event createEvent(EventRequestDTO data) {
        String imgUrl = null;

        if (data.image() != null) {
            imgUrl = this.uploadImg(data.image());
        }

        Event newEvent = new Event();
        newEvent.setTitle(data.title());
        newEvent.setDescription(data.description());
        newEvent.setEventUrl(data.eventUrl());
        newEvent.setDate(new Date(data.date()));
        newEvent.setImageUrl(imgUrl);
        newEvent.setRemote(data.remote());

        repository.save(newEvent);

        if (!data.remote()) {
            this.addressService.createAddress(data, newEvent);
        }

        return newEvent;
    }

    public List<EventResponseDTO> searchEvents(String title){
        title = (title != null) ? title : "";

        List<EventAddressProjection> eventsList = this.repository.findEventsByTitle(title);
        return eventsList.stream().map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getUf() != null ? event.getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImageUrl())
                )
                .toList();
    }

    public List<EventResponseDTO> getFilteredEvents(int page, int size, String city, String uf, Date startDate, Date endDate){
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? endDate : new Date();

        Pageable pageable = PageRequest.of(page, size);

        Page<EventAddressProjection> eventsPage = this.repository.findFilteredEvents(city, uf, startDate, endDate, pageable);
        return eventsPage.map(event -> new EventResponseDTO(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getCity() != null ? event.getCity() : "",
                        event.getUf() != null ? event.getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImageUrl())
                )
                .stream().toList();
    }

    public void deleteEvent(UUID eventId, String adminKey){
        System.out.println("this.adminKey " + adminKey);
        if(adminKey == null || !adminKey.equals(this.adminKey)){
            throw new IllegalArgumentException("Invalid admin key");
        }

        this.repository.delete(this.repository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found")));

    }

    private String uploadImg(MultipartFile multipartFile) {
        String filename = UUID.randomUUID().toString() + "-" + multipartFile.getOriginalFilename();

        try {
            File file = this.convertMultipartFile(multipartFile);
            s3Client.putObject(bucketName, filename, file);
            file.delete();
            return s3Client.getUrl(bucketName, filename).toString();
        } catch (Exception e) {
            System.out.println("Erro ao enviar o arquivo: " + multipartFile.getOriginalFilename());
            return "";
        }
    }

    private File convertMultipartFile(MultipartFile multipartFile) throws IOException {
        File convFile  = new File(multipartFile.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(multipartFile.getBytes());
        fos.close();
        return convFile;
    }
}
