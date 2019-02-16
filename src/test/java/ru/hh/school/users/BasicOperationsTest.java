package ru.hh.school.users;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import ru.hh.school.BaseTest;
import ru.hh.school.TestHelper;
import ru.hh.school.TransactionRule;

/**
 * Повторение материала лекции.
 */
public class BasicOperationsTest extends BaseTest {

  /**
   * Оборачивает каждый @Test в транзакцию
   */
  @Rule
  public TransactionRule transactionRule = new TransactionRule(sessionFactory);

  @BeforeClass
  public static void setupTables() {
    TestHelper.executeScript(pg.getPostgresDatabase(), "create_users.sql");
  }

  @Before
  public void cleanTestData() {
    TestHelper.execute(pg.getPostgresDatabase(), "delete from users");
  }

  @Test
  public void saveNewUserShouldInsertDbRow() {
    User user = new User("John", "Lennon");

    getSession().persist(user);

    assertEquals(1L, getInsertCount());
    assertEquals(user, getSession().get(User.class, user.getId()));
  }

  @Test
  public void deleteUserShouldDeleteDbRow() {
    User user = new User("John", "Lennon");
    getSession().persist(user);

    User extractedUser = getSession().get(User.class, user.getId());
    assertNotNull(extractedUser);

    getSession().delete(user);

    extractedUser = getSession().get(User.class, user.getId());
    assertNull(extractedUser);
  }

  /**
   * Тест рабочий (при условии что правильно оформили entity)
   * Для ознакомления.
   */
  @Test
  public void getByIdShouldReturnUserFromCache() {
    User user = new User("John", "Lennon");
    getSession().persist(user);
    user.setFirstName("CHANGED_NAME");

    User extractedUser = getSession().get(User.class, user.getId());

    // обратите внимание, что в БД за пользователем хибер не пошел, а взял его из кеша 1го уровня
    assertEquals(0L, getUpdateCount());
    assertEquals(0L, getSelectCount());
    assertNotNull(extractedUser);
    assertEquals(user, extractedUser);
  }

  /**
   * Тест рабочий (при условии что правильно оформили entity).
   * Для ознакомления.
   */
  @Test
  public void getWithQueryShouldReturnUserIfRowExists() {
    User user = new User("John", "Lennon");
    getSession().persist(user);

    user.setFirstName("CHANGED_NAME");

    User extractedUser = getSession().createQuery("from User where id = :userId", User.class)
      .setParameter("userId", user.getId())
      .getSingleResult();

    // обратите внимание, что в данном случае хибер обновил запись в бд (из-за измененного имени)
    // и сходил за пользователем в БД (а не в кеш 1го уровня)
    // https://vladmihalcea.com/a-beginners-guide-to-jpahibernate-flush-strategies/
    assertEquals(1L, getUpdateCount());
    assertEquals(1L, getSelectCount());

    assertNotNull(extractedUser);
    assertEquals(user, extractedUser);
  }

}
