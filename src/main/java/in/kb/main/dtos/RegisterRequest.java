package in.kb.main.dtos;

import in.kb.main.entitys.Role;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    private String email;

    private String password;

    private Role role = Role.ROLE_USER;
}
