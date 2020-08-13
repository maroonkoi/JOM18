package com.softserve.edu.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@Entity(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    private String firstName;

    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    private String lastName;

    @Column(unique = true)
    @Pattern(regexp = ".+@.+\\..+", message = "Please provide a valid email address")
    private String email;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name="role_id", nullable = false)
    private Role role;

    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String password;

    @JsonIgnore
    @ToString.Exclude
    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    private List<Marathon> marathons;

    @JsonIgnore
    @OneToMany(mappedBy = "trainee")
    private List<Progress> progresses;

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
