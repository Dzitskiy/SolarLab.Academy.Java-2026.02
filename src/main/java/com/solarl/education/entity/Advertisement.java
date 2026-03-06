package com.solarl.education.entity;

import com.solarl.education.enums.CategoryEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "advertisement")
@Data
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "category", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private CategoryEnum category;

    @Column(name = "subcategory", nullable = false, length = 100)
    private String subcategory;

    @Column(name = "cost", nullable = false)
    private Integer cost;

    @Column(name = "description", length = 250)
    private String description;

    @Column(name = "address", length = 150)
    private String address;

    @ManyToOne
    private Client client;

    @Column(name = "create_date_time")
    @CreationTimestamp
    private LocalDateTime createDateTime;

    @Override
    public String toString() {
        return "Advertisement{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", subcategory='" + subcategory + '\'' +
                ", cost=" + cost +
                ", description='" + description + '\'' +
                ", address='" + address + '\'' +
                ", createDateTime=" + createDateTime +
                '}';
    }
}
