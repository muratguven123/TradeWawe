package com.murat.tradewave.model;

import com.murat.tradewave.Enums.Role;
import jakarta.persistence.*;
import lombok.*;
import com.murat.tradewave.model.Address;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class User {
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @Getter
    @Column(nullable = false)
    private String email;
    @Setter
    @Getter
    @Column(nullable = false)
    private String password;

    @Setter
    @Getter
    private String name;

    @Setter
    @Getter
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();


}
