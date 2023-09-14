package ru.evgeny;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Organization {
    private long ogrn;
    private String inn;
    private String name;
    private String address;
    private String director;
    private int capital;
    private Date date;


    @Override // переопределяем чтобы сравнивать только по ОГРН
    public boolean equals(Object o) {
       if (this == o) return true;
       if (o == null || getClass() != o.getClass()) return false;
       Organization org = (Organization) o;
       return ogrn == org.ogrn;
    }

    @Override
    public int hashCode() {
       return Objects.hash(ogrn);
    }
}
