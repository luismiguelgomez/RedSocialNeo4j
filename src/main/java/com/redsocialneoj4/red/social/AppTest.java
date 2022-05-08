package com.redsocialneoj4.red.social;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppTest implements AutoCloseable {
    private final Driver driver;
    private static final Logger LOGGER = Logger.getLogger(RedSocialApplication.class.getName());

    public AppTest(String uri, String user, String password, Config config) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
    }

    public AppTest(Driver driver){
        this.driver = driver;
    }

    @Override
    public void close() throws Exception {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    public void findPerson(final String personName) {
        String readPersonByNameQuery = "MATCH (p:Person)\n" +
                "WHERE p.name = $person_name\n" +
                "RETURN p.name AS name";

        Map<String, Object> params = Collections.singletonMap("person_name", personName);

        try (Session session = driver.session()) {
            Record record = session.readTransaction(tx -> {
                Result result = tx.run(readPersonByNameQuery, params);
                return result.single();
            });

            System.out.println(String.format("Found person: %s", record.get("name").asString()));
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, readPersonByNameQuery + " raised an exception", ex);
            throw ex;
        }
    }

    public void createSeller(final String person1Name, String cedula) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        String createFriendshipQuery = "CREATE (p1:Person { name: $person1_name })\n" +
                "CREATE (p2:Person { name: $person2_name })\n" +
                "CREATE (p1)-[:KNOWS]->(p2)\n" +
                "RETURN p1, p2";

        Map<String, Object> params = new HashMap<>();
        params.put("person1_name", person1Name);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(createFriendshipQuery, params);
                return result.single();
            });
            System.out.println(String.format("Created friendship between: %s, %s",
                    record.get("p1").get("name").asString(),
                    record.get("p2").get("name").asString()));
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, createFriendshipQuery + " raised an exception", ex);
            throw ex;
        }
    }

}
