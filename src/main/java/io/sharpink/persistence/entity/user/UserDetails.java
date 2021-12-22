package io.sharpink.persistence.entity.user;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "USER_DETAILS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    @OneToOne
    @ToString.Exclude
    private User user;

    @Column(name = "FIRSTNAME")
    protected String firstName;

    @Column(name = "LASTNAME")
    protected String lastName;

    @Column(name = "PROFILE_PICTURE")
    protected String profilePicture;

    @Column(name = "BIRTH_DATE")
    protected Date birthDate;

    @Column(name = "COUNTRY")
    protected String country;

    @Column(name = "TOWN")
    protected String town;

    @Column(name = "JOB")
    protected String job;

    @Column(name = "IS_EMAIL_PUBLIC")
    protected boolean emailPublished;

    @Column(name = "CUSTOM_MESSAGE")
    protected String customMessage;

    @Column(name = "BIOGRAPHY")
    protected String biography;

}
