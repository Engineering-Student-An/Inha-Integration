package an.inhaintegration.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Setter
    private String announcementStyle;

    @ElementCollection
    private List<Long> subjectList = new ArrayList<>();

    @ElementCollection
    private List<Integer> announcementPeriod = new ArrayList<>();
}
