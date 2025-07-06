package com.example.gradingsystem;

import com.example.gradingsystem.dao.TestDAO;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
public class TestDAOTests {
    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @BeforeAll
    static void beforeAll() {
        mongo.start();
        String uri = mongo.getReplicaSetUrl();
        MongoConnector.init(uri);
        TestDAO.init(MongoConnector.getInstance().getDatabase());
    }

    @AfterEach
    void cleanUp() {
        MongoConnector.getInstance().getDatabase().getCollection("tests").deleteMany(new Document());
    }

    @Test
    void testInsertAndGetTest() {
        com.example.gradingsystem.datamodel.Test test = new com.example.gradingsystem.datamodel.Test("Math Test", LocalDate.of(2025, 7, 6));

        TestDAO.getInstance().insertTest(test);
        List<com.example.gradingsystem.datamodel.Test> tests = TestDAO.getInstance().getTests();

        assertEquals(1, tests.size());
        assertEquals("Math Test", tests.get(0).getName());
        assertEquals(LocalDate.of(2025, 7, 6), tests.get(0).getWhenTaken());
    }
}
