package error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationExceptionResponse extends BasicExceptionResponse {

    private Map<String, String> details;

    public ValidationExceptionResponse(String errorMessage, int errorCode, Map<String, String> details) {
        super(errorMessage, errorCode);
        this.details = details;
    }
}
