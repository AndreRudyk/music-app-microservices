package request.song;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;
import validation.ValidDuration;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongMetadataRequest {

    @NotNull(message = "ID is required.")
    @Pattern(regexp = "\\d+", message = "ID must be a numeric string.")
    private String id;

    @NotNull(message = "Name is required.")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters.")
    private String name;

    @NotNull(message = "Artist is required.")
    @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters.")
    private String artist;

    @NotNull(message = "Album is required.")
    @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters.")
    private String album;

    @NotNull(message = "Duration is required.")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Duration must be in the format mm:ss.")
    @ValidDuration
    private String duration;

    @NotNull(message = "Year is required.")
    @Pattern(regexp = "^(19|20)\\d{2}$", message = "Year must be between 1900 and 2099.")
    private String year;
}
