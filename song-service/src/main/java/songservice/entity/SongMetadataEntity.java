package songservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "song_metadata")
public class SongMetadataEntity {

    @Id
    private Integer id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private String releaseYear;
}
