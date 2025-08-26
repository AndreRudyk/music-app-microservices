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

    public ResourceEntity(String bucketKey, String fileUrlUrl) {
        this.bucketKey = bucketKey;
        this.fileUrl = fileUrlUrl;
    }
}
