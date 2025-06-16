package org.removeBG.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_user_removebg")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String clerkId;

    @Column(unique = true, nullable = false)
    private String email;
    private String firstName;
    private String lastName;
    private Integer credits;
    private String photoUrl;

    @PrePersist
    public void prePersist() {
        if (this.credits == null) {
            this.credits = 5;
        }
    }
}
