package com.redsocialneoj4.red.social;

import org.neo4j.driver.*;
import org.neo4j.driver.exceptions.Neo4jException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.System.exit;

public class AppTest implements AutoCloseable {
    private final Driver driver;
    private static final Logger LOGGER = Logger.getLogger(RedSocialApplication.class.getName());

    public AppTest(String uri, String user, String password, Config config) {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password), config);
    }

    public AppTest(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void close() throws Exception {
        // The driver object should be closed before the application ends.
        driver.close();
    }

    //public void createFriendship(final String person1_name, final String person2_name, final String product_name) {
    public void createFriendship(final String person1_name, final String person2_name, final String product_name) {
        String createFriendshipQuery = "CREATE (seller:Person { name: $person1_name })\n" +
                "CREATE (buyer:Person { name: $person2_name })\n" +
                "CREATE (item:Product { name: $product_name })\n" +
                "CREATE (seller)-[:SELLS]->(item)\n" +
                "CREATE (buyer)-[:BUYS]->(item)\n" +
                "RETURN seller, buyer, item";

        Map<String, Object> params = new HashMap<>();
        params.put("person1_name", person1_name);
        params.put("person2_name", person2_name);
        params.put("product_name", product_name);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(createFriendshipQuery, params);
                return result.single();
            });
            System.out.println(String.format("Created friendship between: %s, %s, %s",
                    record.get("seller").get("name").asString(),
                    record.get("buyer").get("name").asString(),
                    record.get("item").get("name").asString()));
            // You should capture any errors along with the query and data for traceability
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, createFriendshipQuery + " raised an exception", ex);
            throw ex;
        }

    }


    public boolean crearComprador(final String person1_name, final String product_name) {
        boolean compradorCreado = false;
        String crearCompradorQuery =
                "CREATE (seller:Person { name: $seller_name })\n" +
                        "CREATE (buyer1:Person { name: $buyer1_name })\n" +
                        "CREATE (buyer2:Person { name: $buyer2_name })\n" +
                        "CREATE (buyer3:Person { name: $buyer3_name })\n" +
                        "CREATE (item1:Product { name: $item1_name })\n" +
                        "CREATE (item2:Product { name: $item2_name })\n" +
                        "CREATE (item3:Product { name: $item3_name })\n" +
                        "CREATE (item4:Product { name: $item4_name })\n" +
                        "CREATE (item5:Product { name: $item5_name })\n" +
                        "CREATE (seller)-[:SELLS]->(item)\n" +
                        "CREATE (buyer)-[:BUYS]->(item)\n" +
                        "RETURN seller, buyer, item";



        Map<String, Object> params = new HashMap<>();
        params.put("person1_name", person1_name);
        params.put("product_name", product_name);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(crearCompradorQuery, params);
                return result.single();
            });
            System.out.println(String.format("Created friendship between: %s, %s",
                    record.get("buyer").get("name").asString(),
                    record.get("item").get("name").asString()));

        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, crearCompradorQuery + " raised an exception", ex);
            throw ex;
        }

        exit(0);
        compradorCreado = true;
        return compradorCreado;
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

    public void crearVendedor(final String nombreVendedor) {
        // To learn more about the Cypher syntax, see https://neo4j.com/docs/cypher-manual/current/
        // The Reference Card is also a good resource for keywords https://neo4j.com/docs/cypher-refcard/current/
        String crearVendedorQuery = "CREATE (vendedor:Seller {name:'" + nombreVendedor + "'})";

        Map<String, Object> params = new HashMap<>();
        params.put("vendedor", nombreVendedor);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(crearVendedorQuery, params);
                return result.single();
            });
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, crearVendedorQuery + " raised an exception", ex);
            throw ex;
        }
    }

    public void crearRelacionEntreVendedorComprador(String nombreComprador, String nombreVendedor, String producto, String categoriaProducto) {
        String crearRelacionVendedorCompradorQuery = "MATCH (vendedor:Seller {name:'" + nombreVendedor + "'})\n" +
                "CREATE (vendedor)-[:SELLS]->(producto:Product{name:'" + producto + "', category:'" + categoriaProducto + "'})";

        Map<String, Object> params = new HashMap<>();
        params.put("relacion-vendedor-nombre", nombreVendedor);
        params.put("relacion-comprador-nombre", nombreComprador);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(crearRelacionVendedorCompradorQuery, params);
                return result.single();
            });
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, crearRelacionVendedorCompradorQuery + " raised an exception", ex);
            throw ex;
        }
    }

    public void crearRelacionEntreCompradorProducto(String nombreComprador, String nombreProducto) {
        String crearRelacionCompradorProducto = "MATCH (c:Buyer {name:'" + nombreComprador + "'})\n" +
                "MATCH (p:Product {name:'" + nombreProducto + "'}) \n" +
                "CREATE (c)-[:BUYS]->(p)\n";

        Map<String, Object> params = new HashMap<>();
        params.put("relacion-producto-nombre", nombreProducto);
        params.put("relacion-comprador-nombre", nombreComprador);

        try (Session session = driver.session()) {
            // Write transactions allow the driver to handle retries and transient errors
            Record record = session.writeTransaction(tx -> {
                Result result = tx.run(crearRelacionCompradorProducto, params);
                return result.single();
            });
        } catch (Neo4jException ex) {
            LOGGER.log(Level.SEVERE, crearRelacionCompradorProducto + " raised an exception", ex);
            throw ex;
        }
    }

}
