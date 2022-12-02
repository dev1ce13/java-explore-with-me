package ru.yandex.practicum.stat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.stat.dto.EndpointHitDto;
import ru.yandex.practicum.stat.dto.NewEndpointHitDto;
import ru.yandex.practicum.stat.dto.ViewStats;
import ru.yandex.practicum.stat.mapper.HitMapper;
import ru.yandex.practicum.stat.model.EndpointHit;
import ru.yandex.practicum.stat.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository statRepository;

    @Override
    public EndpointHitDto addHit(NewEndpointHitDto newEndpointHitDto) {
        EndpointHit endpointHit = HitMapper.mapToEndpointHitFromNewEndpointHitDto(newEndpointHitDto);
        return HitMapper.mapToEndpointHitDtoFromEndpointHit(statRepository.save(endpointHit));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> stats;
        List<ViewStats> response = new ArrayList<>();
        if (unique) {
            stats = statRepository.getUniqueStat(start, end);
            for (ViewStats stat : stats) {
                for (String uri : uris) {
                    if (stat.getUri().contains(uri)) {
                        response.add(stat);
                    }
                }
            }
        } else {
            stats = statRepository.getNoUniqueStat(start, end);
            for (ViewStats stat : stats) {
                for (String uri : uris) {
                    if (stat.getUri().contains(uri)) {
                        response.add(stat);
                    }
                }
            }
        }
        return response;
    }
}
