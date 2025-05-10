package an.inhaintegration.icross.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="schedule_id")
    private Long id;

    @Column(nullable = false)
    private String content;             // 내용

    @Column(nullable = false)
    private String time;    // 시작 시간~끝 시간 (ex. 10:30~12:00)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univ_info_id")
    private UnivInfo univInfo;

    @Setter
    @Column(nullable = false)
    private boolean completed;

    public void setUnivInfo(UnivInfo univInfo) {
        this.univInfo = univInfo;
        univInfo.getScheduleList().add(this);
    }

    public void complete() {
        this.completed = !this.completed;
    }
}
