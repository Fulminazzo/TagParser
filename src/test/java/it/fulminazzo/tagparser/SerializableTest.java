package it.fulminazzo.tagparser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.fulminazzo.tagparser.serializables.Serializable;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SerializableTest {
    private Person person;

    @BeforeEach
    void setUp() {
        person = new Person("Alex", 10,
                new Person("Alice", 15, null, new ArrayList<>(), new HashMap<>()),
                Arrays.asList(
                        new Person("Robert", 12, null, new ArrayList<>(), new HashMap<>()),
                        new Person("Luke", 12, null, new ArrayList<>(), new HashMap<>()),
                        new Person("Steve", 12, null, new ArrayList<>(), new HashMap<>())
                ),
                new LinkedHashMap<String, Boolean>() {{
                    put("task1", false);
                    put("task2", true);
                    put("task3", false);
                }},
                "Frank");
    }

    @Test
    void testYAML() {
        final String expected = "name: \"Alex\"\n" +
                "age: 10\n" +
                "partner:\n" +
                "    name: \"Alice\"\n" +
                "    age: 15\n" +
                "    partner: null\n" +
                "    enemies: []\n" +
                "    friends: []\n" +
                "    tasks: {}\n" +
                "enemies:\n" +
                "- name: \"Robert\"\n" +
                "  age: 12\n" +
                "  partner: null\n" +
                "  enemies: []\n" +
                "  friends: []\n" +
                "  tasks: {}\n" +
                "- name: \"Luke\"\n" +
                "  age: 12\n" +
                "  partner: null\n" +
                "  enemies: []\n" +
                "  friends: []\n" +
                "  tasks: {}\n" +
                "- name: \"Steve\"\n" +
                "  age: 12\n" +
                "  partner: null\n" +
                "  enemies: []\n" +
                "  friends: []\n" +
                "  tasks: {}\n" +
                "friends:\n" +
                "- \"Frank\"\n" +
                "tasks:\n" +
                "    task1: false\n" +
                "    task2: true\n" +
                "    task3: false"
                ;
        assertEquals(expected, person.toYAML());
    }

    @Test
    void testJSON() {
        Gson gson = new GsonBuilder().serializeNulls().create();
        final String expected = gson.toJson(person);
        assertEquals(expected, person.toJSON());
    }

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    static class Person implements Serializable {
        private final String name;
        private final int age;
        private final Person partner;
        private final List<Person> enemies;
        private final String[] friends;
        private final Map<String, Boolean> tasks;

        Person(String name, int age, Person partner, List<Person> enemies, Map<String, Boolean> tasks, String... friends) {
            this.name = name;
            this.age = age;
            this.partner = partner;
            this.enemies = enemies;
            this.tasks = tasks;
            this.friends = friends;
        }

        @Override
        public @NotNull String toHTML() {
            return "";
        }
    }
}