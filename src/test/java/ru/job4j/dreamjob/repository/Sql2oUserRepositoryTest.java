package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oVacancyRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveAndFindByEmailAndPassword() {
        var user = sql2oUserRepository.save(new User(1, "novikov@mail.ru", "Ivan Novikov", "123")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword("novikov@mail.ru", "123").get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    public void whenSaveAndFindSeveralUsers() {
        var user1 = sql2oUserRepository.save(new User(2, "sidorov@mail.ru", "Vasiliy Sidorov", "sid789")).get();
        var user2 = sql2oUserRepository.save(new User(3, "ozerov@mail.ru", "Maxim Ozerov", "oz654")).get();
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword("sidorov@mail.ru", "sid789").get();
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword("ozerov@mail.ru", "oz654").get();
        assertThat(savedUser1).usingRecursiveComparison().isEqualTo(user1);
        assertThat(savedUser2).usingRecursiveComparison().isEqualTo(user2);
    }

    @Test
    public void whenSaveExistingEmail() {
        var user1 = sql2oUserRepository.save(new User(6, "semenov@mail.ru", "Nikolay Semenov", "123")).get();
        assertThatThrownBy(() -> sql2oUserRepository.save(new User(7, "semenov@mail.ru", "Anatoliy Semenov", "s789")))
                .isInstanceOf(RuntimeException.class).hasMessage("IOExeption in save() method Sql2oUserRepository.class");
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
    }
}
