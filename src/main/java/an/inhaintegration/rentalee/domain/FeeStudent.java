package an.inhaintegration.rentalee.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStudent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_student_id")
    private Long id;

    @Column(nullable = false)
    private String stuId;

    @Column(nullable = false)
    private String name;

    public FeeStudent(String stuId, String name) {
        this.stuId = stuId;
        this.name = name;
    }
}
