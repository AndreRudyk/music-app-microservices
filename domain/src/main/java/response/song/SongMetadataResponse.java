package response.song;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongMetadataResponse {

    private Integer id;

    private String name;

    private String artist;

    private String album;

    private String duration;

    private String year;
}
