package ru.evgeny;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Organization {
   // private long ogrn;
    private String inn;
    private String name;
    private String address;
    private String director;
    private int capital;
    private Date date;
}
