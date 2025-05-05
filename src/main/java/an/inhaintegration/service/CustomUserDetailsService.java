package an.inhaintegration.service;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final StudentRepository studentRepository;

    @Override
    public UserDetails loadUserByUsername(String stuId) throws UsernameNotFoundException {
        Student student = studentRepository.findByStuId(stuId).orElseThrow(StudentNotFoundException::new);

        return new CustomUserDetails(student);
    }
}