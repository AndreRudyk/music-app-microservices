package storageservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "storage")
public class StorageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String storageType;

    @Column(nullable = false)
    private String bucket;

    @Column(nullable = false)
    private String path;
}
