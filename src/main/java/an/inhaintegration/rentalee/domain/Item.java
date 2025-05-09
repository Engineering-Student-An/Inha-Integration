package an.inhaintegration.rentalee.domain;

import an.inhaintegration.rentalee.dto.item.ItemRequestDto;
import an.inhaintegration.rentalee.dto.item.ItemResponseDto;
import an.inhaintegration.rentalee.exception.NotEnoughStockException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    // 현재 재고
    @Column(nullable = false)
    private int stockQuantity;

    // 총 재고
    @Column(nullable = false)
    private int allStockQuantity;

    private String category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rental> rentalList = new ArrayList<>();

    // 대여 가능 재고 증가 메서드
    public void addStock(){
        this.stockQuantity++;
    }

    // 총 재고 증가 메서드
    public void addAllStock(){
        this.allStockQuantity++;
        this.stockQuantity++;
    }

    // 재고 감소 메서드
    public void removeStock(){
        if(this.stockQuantity <= 0) throw new NotEnoughStockException();
        this.stockQuantity--;
    }

    // 물품 정보 수정 메서드
    public void updateInfo(String name, int allStockQuantity, String category){
        this.name = name;
        this.stockQuantity += (allStockQuantity - this.allStockQuantity);
        this.allStockQuantity = allStockQuantity;
        this.category = category;
    }

    public ItemResponseDto toItemResponseDto() {

        return new ItemResponseDto(this.getId(), this.getName(), this.getStockQuantity(), this.getAllStockQuantity());
    }

    public ItemRequestDto toItemRequestDto() {

        return new ItemRequestDto(this.getId(), this.getName(),
                this.getAllStockQuantity(), this.getAllStockQuantity() - this.getStockQuantity(),
                this.getCategory());
    }
}