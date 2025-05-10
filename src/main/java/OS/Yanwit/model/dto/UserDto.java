package OS.Yanwit.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    @JsonIgnore
    private long id;

    @NotBlank
    @Size(max = 64, message = "username should be less than 65 symbols")
    private String username;

    @NotBlank
    @Size(max = 64, message = "email should be less than 65 symbols")
    private String email;

    @NotBlank
    @Size(max = 32, message = "phone should be less than 32 symbols")
    private String phone;

    @NotBlank
    @Size(max = 128, message = "password should be less than 129 symbols")
    private String password;

    private String aboutMe;

    private List<Long> followersIds;

    private List<Long> followeesIds;
}
