package cn.com.farben.gptcoder.operation.commons.auth.domain.entity;

import cn.com.farben.gptcoder.operation.commons.auth.infrastructure.enums.LicenseTypesEnum;

import java.time.LocalDate;

public record LicenseRecord(Integer accountNumber, Integer clientNumber, LocalDate expirationDate, LicenseTypesEnum type) {
    @Override
    public String toString() {
        return "license信息{" +
                "账户数：" + accountNumber +
                ", 客户端限制数：" + clientNumber +
                ", 授权截止日期：" + expirationDate +
                ", 授权类型：" + type.getDescribe() +
                '}';
    }
}
