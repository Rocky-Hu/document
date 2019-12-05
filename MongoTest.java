import ch.qos.logback.core.util.TimeUtil;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.glasshouse.seed.mongo.entity.Address;
import org.glasshouse.seed.mongo.entity.Person;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @Author rocky.hu
 * @Date 9/12/2019 11:24 AM
 */
public class MongoTest {

    private MongoClient mongoClient = null;

    @Before
    public void setUp() {
        mongoClient = MongoClients.create();
    }

    @Test
    public void accessDatabase() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        System.out.println(mongoDatabase.getName());

        CodecRegistry codecRegistry = mongoDatabase.getCodecRegistry();
        System.out.println(codecRegistry.getClass());

        ReadConcern readConcern = mongoDatabase.getReadConcern();
        System.out.println(readConcern.getLevel());

        ReadPreference readPreference = mongoDatabase.getReadPreference();
        System.out.println(readPreference.getName());

        WriteConcern writeConcern = mongoDatabase.getWriteConcern();
        System.out.println(writeConcern.getJournal());
        System.out.println(writeConcern.getW());
        System.out.println(writeConcern.getWObject());
        System.out.println(writeConcern.getWString());
        System.out.println(writeConcern.getWTimeout(TimeUnit.SECONDS));
        System.out.println(writeConcern.isAcknowledged());
        System.out.println(writeConcern.isServerDefault());

        MongoIterable<String> mongoIterable = mongoDatabase.listCollectionNames();
        MongoCursor<String> mongoCursor = mongoIterable.iterator();
        while (mongoCursor.hasNext()) {
            System.out.println(mongoCursor.next());
        }

    }

    @Test
    public void accessCollection() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");
        System.out.println(mongoCollection);
    }

    @Test
    public void createDocument() {
        Document document = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));
    }

    @Test
    public void insertDocument() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        Document document = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("versions", Arrays.asList("v3.2", "v3.0", "v2.6"))
                .append("info", new Document("x", 203).append("y", 102));

        mongoCollection.insertOne(document);

        List<Document> documents = new ArrayList<Document>();
        for (int i=0;i<100;i++) {
            documents.add(new Document("i", i));
        }
        mongoCollection.insertMany(documents);
    }

    @Test
    public void countDocuments() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");
        System.out.println(mongoCollection.countDocuments());
    }

    @Test
    public void findDocument() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");
        Document firstDocument = mongoCollection.find().first();
        System.out.println(firstDocument.toJson());

        MongoCursor<Document> mongoCursor = mongoCollection.find().iterator();
        try {
            while (mongoCursor.hasNext()) {
                System.out.println(mongoCursor.next().toJson());
            }
        } finally {
            mongoCursor.close();
        }
    }

    @Test
    public void getASingleDocumentThatMatchesAFilter() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        Document document = mongoCollection.find(Filters.eq("i", 71)).first();

        System.out.println(document.toJson());
    }

    @Test
    public void getAllDocumentsThatMatchAFilter() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        Block<Document> printBlock = new Block<Document>() {
            @Override
            public void apply(Document document) {
                System.out.println(document.toJson());
            }
        };

        mongoCollection.find(Filters.gt("i", 50)).forEach(printBlock);
     }

     @Test
     public void updateASingleDocument() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        mongoCollection.updateOne(Filters.eq("i", 10), new Document("$set", new Document("i", 110)));

        Document document = mongoCollection.find(Filters.eq("i", 110)).first();
        System.out.println(document.toJson());
    }

    @Test
    public void updateMultipleDocuments() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        UpdateResult updateResult = mongoCollection.updateMany(Filters.lt("i", 100), Updates.inc("i", 100));
        System.out.println(updateResult.getModifiedCount());
    }

    @Test
    public void deleteASingleDocumentThatMatchAFilter() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        mongoCollection.deleteOne(Filters.eq("i", 110));
    }

    @Test
    public void deleteAllDocumentsThatMatchAFilter() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        DeleteResult deleteResult = mongoCollection.deleteMany(Filters.gte("i", 100));
        System.out.println(deleteResult.getDeletedCount());
    }

    @Test
    public void createIndexes() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("test");

        mongoCollection.createIndex(new Document("i", 1));
    }

    @Test
    public void insertPojo() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");

        // Creating a custom CodecRegistry
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        // Using the CodecRegistry
        // 1. You can set it when instantiating a MongoClient object
        //MongoClientSettings settings = MongoClientSettings.builder()
        //        .codecRegistry(pojoCodecRegistry)
        //        .build();
        //MongoClient mongoClient = MongoClients.create(settings);

        // 2. You can use an alternative CodecRegistry with a MongoDatabase
        // database = database.withCodecRegistry(pojoCodecRegistry);

        // 3. You can use an alternative CodecRegistry with a MongoCollection
        // collection = collection.withCodecRegistry(pojoCodecRegistry);

        mongoDatabase = mongoDatabase.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Person> personMongoCollection = mongoDatabase.getCollection("person", Person.class);

        //Person ada = new Person("Ada Byron", 20, new Address("St James Square", "London", "W1"));

        //personMongoCollection.insertOne(ada);

        // Insert Many Persons
        List<Person> people = Arrays.asList(
                new Person("Charles Babbage", 45, new Address("5 Devonshire Street", "London", "W11")),
                new Person("Alan Turing", 28, new Address("Bletchley Hall", "Bletchley Park", "MK12")),
                new Person("Timothy Berners-Lee", 61, new Address("Colehill", "Wimborne", null))
        );

        personMongoCollection.insertMany(people);
    }

    @Test
    public void queryPojo() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");

        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoDatabase = mongoDatabase.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Person> personMongoCollection = mongoDatabase.getCollection("person", Person.class);

        Block<Person> printBlock = new Block<Person>() {
            @Override
            public void apply(Person person) {
                System.out.println(person);
            }
        };

        personMongoCollection.find().forEach(printBlock);
    }

    @Test
    public void getASinglePersonThatMatchesAFilter() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        mongoDatabase = mongoDatabase.withCodecRegistry(pojoCodecRegistry);

        MongoCollection<Person> personMongoCollection = mongoDatabase.getCollection("person", Person.class);

        Person person = personMongoCollection.find(Filters.eq("address.city", "Wimborne")).first();
        System.out.println(person);
    }

    @Test
    public void updateASinglePerson() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Person> personMongoCollection = mongoDatabase.getCollection("person", Person.class);
        UpdateResult updateResult = personMongoCollection.updateOne(Filters.eq("name", "Ada Byron"), Updates.combine(Updates.set("age", 23), Updates.set("name", "Ada Lovelace")));
    }

    @Test
    public void updateMultiplePersons() {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("glasshouse");
        MongoCollection<Person> personMongoCollection = mongoDatabase.getCollection("person", Person.class);

        UpdateResult updateResult = personMongoCollection.updateMany(Filters.not(Filters.eq("zip", null)), Updates.set("zip", null));
        System.out.println(updateResult.getModifiedCount());
    }

}