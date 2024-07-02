package com.sparta.realestatefeed.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.realestatefeed.entity.Apart;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApartResponseDto {

    private Long id;
    private String apartName;
    private String address;
    private String area;
    private Long salePrice;
    private String isSaled;
    private LocalDateTime modifiedAt;
    private String nickName;
    private Long likesCount;

    public ApartResponseDto(Apart apart) {
        this.id = apart.getId();
        this.apartName = apart.getApartName();
        this.address = apart.getAddress();
        this.area = apart.getArea();
        this.salePrice = apart.getSalePrice();
        this.isSaled = apart.getIsSaled().getDescription();
        this.modifiedAt = apart.getModifiedAt();
        this.nickName = apart.getUser().getNickName();
    }

    public void updateLikesCount(Long likesCount) {
        this.likesCount = likesCount;
    }
}
