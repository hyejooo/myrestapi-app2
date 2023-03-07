package com.kt.myrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class Account {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER) //entity를 따로 만들지 않았지만 테이블을 생성해줌
    @Enumerated(value= EnumType.STRING) //accountRole까지 한꺼번에 읽어와라
    @Column(name = "account_roles")
    private Set<AccountRole> roles;
}
