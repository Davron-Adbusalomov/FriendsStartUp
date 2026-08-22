package com.example.demo.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE center SET status = 'DELETED' WHERE id = ?")
@Where(clause = "status != 'DELETED'")
public class Center extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid2")
    @org.hibernate.annotations.GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "location")
    private String location;

    private String contactInfo;

    @Column(name = "subdomain", unique = true)
    private String subdomain;

    @Column(name = "logo")
    private String logo;

    /** Oyning nechinchi kunida oylik invoice/oylik generatsiyasi ishga tushishi (1-31, default 1). */
    @Column(name = "billing_day")
    private Integer billingDay = 1;
}
