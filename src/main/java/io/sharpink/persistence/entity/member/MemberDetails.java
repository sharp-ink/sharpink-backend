package io.sharpink.persistence.entity.member;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MEMBER_DETAILS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@OneToOne
	private Member member;

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
