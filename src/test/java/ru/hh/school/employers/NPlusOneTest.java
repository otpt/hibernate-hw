package ru.hh.school.employers;

import static org.junit.Assert.assertEquals;

import org.hibernate.jpa.QueryHints;
import org.junit.Rule;
import org.junit.Test;
import ru.hh.school.TransactionRule;

import javax.persistence.EntityGraph;
import java.util.List;

public class NPlusOneTest extends EmployerTest {

  @Rule
  public TransactionRule transactionRule = new TransactionRule(sessionFactory);

  @Test
  public void shouldExecuteOneStatement() {
    EntityGraph entityGraph = getSession()
            .getEntityManagerFactory()
            .createEntityManager()
            .getEntityGraph(Employer.VACANCIES_GRAPH);
    List<Employer> employers = getSession().createQuery("from Employer", Employer.class)
            .setHint(QueryHints.HINT_FETCHGRAPH, entityGraph)
            .getResultList();

    employers.forEach((emp) -> emp.getVacancies().size());

    assertEquals(1L, getSelectCount());
  }

}
