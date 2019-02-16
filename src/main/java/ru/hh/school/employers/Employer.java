package ru.hh.school.employers;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.DAYS;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "employer")
@NamedEntityGraphs({@NamedEntityGraph(name = Employer.VACANCIES_GRAPH,
        attributeNodes = @NamedAttributeNode("vacancies"))
})
public class Employer {
  public static final String VACANCIES_GRAPH = "Employer[vacancies]";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "employer_id")
  private Integer id;

  @Column(name = "company_name", unique = true, nullable = false)
  private String companyName;

  @Column(name = "employees_count")
  private int employeesCount;

  @Enumerated(EnumType.STRING) // или EnumType.ORDINAL, но мы можем поломать код, если порядок членов в enum-е изменится
  @Column(name = "organization_form")
  private OrganizationForm organizationForm;

  // не используйте java.util.Date
  // https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#basic-datetime-java8
  @Column(name = "creation_time")
  private LocalDateTime creationTime;

  @Column(name = "bonus_points")
  private int bonusPoints;

  @OneToMany(mappedBy = "employer")
  private List<Vacancy> vacancies = new ArrayList<>();

  public List<Vacancy> getVacancies() {
    return vacancies;
  }

  /**
   * важная бизнес логика...
   */
  public void calculateBonusPoints() {
      bonusPoints = employeesCount * (int) DAYS.between(creationTime, now());
  }


  // статьи на тему реализации equals() и hashCode():
  //
  // https://vladmihalcea.com/hibernate-facts-equals-and-hashcode/
  // https://docs.jboss.org/hibernate/orm/5.3/userguide/html_single/Hibernate_User_Guide.html#mapping-model-pojo-equalshashcode
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Employer employer = (Employer) o;
    return Objects.equals(companyName, employer.companyName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(companyName);
  }

}
