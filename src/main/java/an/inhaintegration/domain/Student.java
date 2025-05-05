package an.inhaintegration.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String stuId;

    private String provider;
    private String providerId;
    private String picture;
    private String password;
    private String phoneNumber;
    private String email;

    @Enumerated(EnumType.STRING)
    private StudentRole role;   // [USER, ADMIN]

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentals = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "univ_info_id")
    private UnivInfo univInfo;

    public void editPassword(String password) {
        this.password = password;
    }

    public void editEmail(String email) {
        this.email = email;
    }
}
