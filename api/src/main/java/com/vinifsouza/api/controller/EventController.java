package com.vinifsouza.api.controller;

import com.vinifsouza.api.domain.event.Event;
import com.vinifsouza.api.domain.event.EventRequestDTO;
import com.vinifsouza.api.domain.event.EventResponseDTO;
import com.vinifsouza.api.repositories.EventRepository;
import com.vinifsouza.api.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/event")
public class EventController {
    @Autowired
    private EventService service;

    @Autowired
    private EventRepository repository;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Event> create(@ModelAttribute @RequestBody EventRequestDTO body) {
        Event newEvent = this.service.createEvent(body);
        return ResponseEntity.ok(newEvent);
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") Boolean retrivePastEvents
    ) {
            List<EventResponseDTO> allEvents = this.service.getAll(page, size, retrivePastEvents);
            return ResponseEntity.ok(allEvents);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<EventResponseDTO>> getFilteredEvents(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size,
                                                                    @RequestParam String city,
                                                                    @RequestParam String uf,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<EventResponseDTO> events = this.service.getFilteredEvents(page, size, city, uf, startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDTO>> getSearchEvents(@RequestParam String title) {
        List<EventResponseDTO> events = this.service.searchEvents(title);
        return ResponseEntity.ok(events);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId, @RequestHeader("x-admin-key") String adminKey) {
        this.service.deleteEvent(eventId, adminKey);
        return ResponseEntity.noContent().build();
    }
}
