package com.restapiexample.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @Id
    private Long phoneNumber;
    private String userName;
    private String phNumberType;
}
