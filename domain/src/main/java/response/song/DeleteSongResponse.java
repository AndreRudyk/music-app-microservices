package response.song;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class DeleteSongResponse {

    private List<Integer> ids;
}
