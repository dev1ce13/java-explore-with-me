package ru.yandex.practicum.main.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.main.request.dto.ParticipationRequestDto;
import ru.yandex.practicum.main.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("users/{userId}")
public class PrivateRequestController {

    private final RequestService service;

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> privateGetRequestsForEvent(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId
    ) {
        log.info("/GET user with id={}, event with id={} requests", userId, eventId);
        return service.privateGetRequestsForEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto privateConfirmRequest(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId,
            @PathVariable(name = "reqId") int reqId
    ) {
        log.info("/PATCH user with id={}, event with id={} confirm request with id={}", userId, eventId, reqId);
        return service.privateConfirmRequest(userId, eventId, reqId);
    }

    @PatchMapping("/events/{eventId}/requests/{reqId}/reject")
    public ParticipationRequestDto privateRejectRequest(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "eventId") int eventId,
            @PathVariable(name = "reqId") int reqId
    ) {
        log.info("/PATCH user with id={}, event with id={} reject request with id={}", userId, eventId, reqId);
        return service.privateRejectRequest(userId, eventId, reqId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> privateGetRequests(@PathVariable(name = "userId") int userId) {
        log.info("/GET requests user with id={}", userId);
        return service.privateGetRequests(userId);
    }

    @PostMapping("/requests")
    public ParticipationRequestDto privateAddRequest(
            @PathVariable(name = "userId") int userId,
            @RequestParam(name = "eventId") int eventId
    ) {
        log.info("/POST user with id={} add request for event with id={}", userId, eventId);
        return service.privateAddRequest(userId, eventId);
    }

    @PatchMapping("/requests/{reqId}/cancel")
    public ParticipationRequestDto privateCancelRequest(
            @PathVariable(name = "userId") int userId,
            @PathVariable(name = "reqId") int reqId
    ) {
        log.info("/PATCH user with id={} cancel request with id={}", userId, reqId);
        return service.privateCancelRequest(userId, reqId);
    }
}
