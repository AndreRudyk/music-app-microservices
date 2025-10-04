package storageservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Storage {

    private Integer id;

    private String storageType;

    private String bucket;

    private String path;
}
