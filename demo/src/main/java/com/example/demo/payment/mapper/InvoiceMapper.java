package com.example.demo.payment.mapper;

import com.example.demo.payment.dto.InvoiceDTO;
import com.example.demo.payment.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(target = "userFullName", expression = "java(invoice.getUser() != null ? invoice.getUser().getFullName() : null)")
    @Mapping(target = "groupName", expression = "java(invoice.getGrouping() != null ? invoice.getGrouping().getName() : null)")
    @Mapping(target = "paidAmount", ignore = true)
    InvoiceDTO toDto(Invoice invoice);
}
