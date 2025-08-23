package response.resource;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeleteResourceResponse {

    private List<Integer> ids;
}
