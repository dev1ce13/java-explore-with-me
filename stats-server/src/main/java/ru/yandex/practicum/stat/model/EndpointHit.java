package ru.yandex.practicum.stat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.stat.dto.ViewStats;

import javax.persistence.*;
import java.time.LocalDateTime;

@NamedNativeQuery(name = "EndpointHit.getNoUniqueStat",
query = "SELECT app as app, " +
        "uri as uri, " +
        "count(ip) as hits " +
        "FROM hits " +
        "WHERE timestamp between ?1 and ?2 " +
        "GROUP BY app, uri",
resultSetMapping = "Mapping.ViewStats")
@NamedNativeQuery(name = "EndpointHit.getUniqueStat",
        query = "SELECT app as app, " +
                "uri as uri, " +
                "count(DISTINCT ip) as hits " +
                "FROM hits " +
                "WHERE timestamp between ?1 and ?2 " +
                "GROUP BY app, uri",
        resultSetMapping = "Mapping.ViewStats")
@SqlResultSetMapping(name = "Mapping.ViewStats",
classes = @ConstructorResult(
        targetClass = ViewStats.class,
        columns = {
                @ColumnResult(name = "app", type = String.class),
                @ColumnResult(name = "uri", type = String.class),
                @ColumnResult(name = "hits", type = Integer.class)
        }
))

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hits", schema = "public")
public class EndpointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
