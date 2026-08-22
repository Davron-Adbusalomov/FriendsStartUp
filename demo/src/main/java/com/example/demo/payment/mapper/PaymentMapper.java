package com.example.demo.payment.mapper;

import com.example.demo.payment.dto.PaymentDTO;
import com.example.demo.payment.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "userFullName", ignore = true)
    PaymentDTO toDto(Payment payment);
}
