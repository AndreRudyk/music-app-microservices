package resourceservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "resource")
public class ResourceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String bucketKey;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    public ResourceEntity(String bucketKey, String fileUrlUrl, StorageType storageType) {
        this.bucketKey = bucketKey;
        this.fileUrl = fileUrlUrl;
        this.storageType = storageType;
    }
}
