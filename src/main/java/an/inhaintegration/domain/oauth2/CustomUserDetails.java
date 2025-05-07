package an.inhaintegration.domain.oauth2;

import an.inhaintegration.domain.Student;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserDetails implements UserDetails, OAuth2User {

    @Getter
    private final Student student;
    private Map<String, Object> attributes;

    public CustomUserDetails(Student student) {
        this.student = student;
    }

    public CustomUserDetails(Student student, Map<String, Object> attributes) {
        this.student = student;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return attributes != null ? (String) attributes.get("name") : student.getName();
    }

    @Override
    // 현재 user의 role을 반환 (ex. "ROLE_ADMIN" / "ROLE_USER" 등)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + student.getRole().name());
    }

    @Override
    public String getPassword() {
        return student.getPassword();
    }

    @Override
    public String getUsername() {
        return student.getLoginId();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    public Long getId() {
        return student.getId();
    }
}
