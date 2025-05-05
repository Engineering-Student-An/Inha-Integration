package an.inhaintegration.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @Setter
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @Setter
    private Item item;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(nullable = false)
    private boolean isChecked;

    public void check() {
        this.isChecked = !this.isChecked;
    }
}
