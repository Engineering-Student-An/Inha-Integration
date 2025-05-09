package an.inhaintegration.icross.domain;

import an.inhaintegration.rentalee.domain.Student;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnivInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "univ_info_id")
    private Long id;

    private String iClassPassword;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ElementCollection
    private List<Long> subjectList = new ArrayList<>();

    @OneToMany(mappedBy = "univInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnivInfoTask> univInfoTaskList = new ArrayList<>();
}
