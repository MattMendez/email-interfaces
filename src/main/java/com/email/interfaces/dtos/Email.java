package com.email.interfaces.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Email {

    private Integer id;

    private String sender;

    private String receiver;

    private String subject;

    private String body;

    private String date;

    private Boolean read;
}
