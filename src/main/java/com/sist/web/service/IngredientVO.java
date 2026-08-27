package com.sist.web.service;

import lombok.Data;

@Data
public class IngredientVO {

    // 재료명
    private String name;

    // 정규화된 수량
    private Double amount;

    // 단위
    private String unit;

    // 괄호 안의 추가 표현
    // ex) 1큰술, 1/2모, 5개
    private String amountText;

    // 파싱 전 원본
    private String original;
}
