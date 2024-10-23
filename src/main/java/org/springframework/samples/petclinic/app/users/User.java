package org.springframework.samples.petclinic.app.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @ValidPassword
    private String password;
    @Setter
    private boolean accountNonExpired = true;
    @Setter
    private boolean accountNonLocked = true;
    @Setter
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    @Enumerated(EnumType.STRING)
    private Role role;

}

