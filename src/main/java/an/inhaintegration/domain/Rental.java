package an.inhaintegration.domain;

import an.inhaintegration.dto.rental.RentalResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private LocalDate rentalDate;
    private LocalDate finishRentalDate;
    private Long rentalDateDiff;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    public void setStudent(Student student) {
        this.student = student;
        student.getRentals().add(this);
    }

    public void setItem(Item item) {
        this.item = item;
        item.getRentals().add(this);
    }

    // 대여 시작 메서드 (주문서 개념)
    public void createRental(Student student, Item item) {

        this.setStudent(student);
        this.setItem(item);
        this.status = RentalStatus.ING;
        this.rentalDate = LocalDate.now();
        item.removeStock();
    }

    // 대여 완료 메서드
    public void finishRental(){
        this.finishRentalDate = LocalDate.now();
        setRentalDateDiff();

        if(this.status == RentalStatus.OVERDUE) {
            this.status = RentalStatus.FINISH_OVERDUE;
        } else {
            this.status = RentalStatus.FINISH;
        }

        item.addStock();
    }

    // 반납 날짜 - 대여 시작 날짜
    public void setRentalDateDiff(){
        this.rentalDateDiff = ChronoUnit.DAYS.between(rentalDate,finishRentalDate);
    }

    // 대여 상태 업데이트 메서드
    public void updateStatus(RentalStatus status){
         this.status = status;
    }

    // Rental -> RentalResponseDto 변환 메서드
    public RentalResponseDto toRentalResponseDto() {

        return new RentalResponseDto(this.getId(), this.getStatus(), this.getItem().getName(), this.getItem().getCategory(), this.getRentalDate());
    }
}
