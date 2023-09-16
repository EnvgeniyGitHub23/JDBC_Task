package ru.evgeny.entity;

import lombok.*;
import java.util.Date;
import java.util.Objects;

/*
  Класс, который хранит сведения об организации.
  Переопределены методы equals и hashcode для сравнения по уникальному полю long ogrn.
*/

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    @Override // переопределяем чтобы сравнивать только по ОГРН
    public int hashCode() {
       return Objects.hash(ogrn);
    }
}
