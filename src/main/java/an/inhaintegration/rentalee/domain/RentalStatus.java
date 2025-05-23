package an.inhaintegration.rentalee.domain;

import lombok.Getter;

@Getter
public enum RentalStatus {

    // 연체, 진행중, 반납완료
    OVERDUE("연체 중"),
    ING("대여 중"),
    FINISH_OVERDUE("반납 완료 (연체)"),
    FINISH("반납 완료");

    private String displayName;

    RentalStatus(String displayName) {
        this.displayName = displayName;
    }

}
