package de.leunosam.json.serialize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import de.leunosam.json.Json;
import de.leunosam.json.deserialize.JsonDeserializer;
import de.leunosam.json.util.JsonClass;
import de.leunosam.json.util.JsonField;

class TestJsonSerializer {

    @Test
    void testNullSerialization() {
        Employee e = new Employee();
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", e.serialize());
    }

    @Test
    void testBooleanSerialization() {
        Employee etrue = new Employee();
        Employee efalse = new Employee();
        etrue.male = true;
        efalse.male = false;
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":true,\"home\":null}", etrue.serialize());
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":false,\"home\":null}", efalse.serialize());
    }

    @Test
    void testIntegerSerialization() {
        Employee epositive = new Employee();
        Employee ezero = new Employee();
        Employee enegative = new Employee();
        epositive.id = 17;
        ezero.id = 0;
        enegative.id = -69;
        assertEquals("{\"employeeNumber\":17,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", epositive.serialize());
        assertEquals("{\"employeeNumber\":0,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", ezero.serialize());
        assertEquals("{\"employeeNumber\":-69,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", enegative.serialize());
    }

    @Test
    void testLongerialization() {
        Employee epositive = new Employee();
        Employee ezero = new Employee();
        Employee enegative = new Employee();
        epositive.birthDate = 42l;
        ezero.birthDate = 0l;
        enegative.birthDate = -111l;
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":42,\"male\":null,\"home\":null}", epositive.serialize());
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":0,\"male\":null,\"home\":null}", ezero.serialize());
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":-111,\"male\":null,\"home\":null}", enegative.serialize());
    }

    @Test
    void testDoubleSerialization() {
        Employee epositive = new Employee();
        Employee ezero = new Employee();
        Employee enegative = new Employee();
        epositive.money = 1010.98789d;
        ezero.money = 0d;
        enegative.money = -20.001d;
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":1010.98789,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", epositive.serialize());
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":0.0,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", ezero.serialize());
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":-20.001,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", enegative.serialize());
    }

    @Test
    void testStringSerialization() {
        Employee e = new Employee();
        e.name = "Käåé®«ñµæ©béüúíóöïœø¶";
        assertEquals("{\"employeeNumber\":null,\"name\":\"Käåé®«ñµæ©béüúíóöïœø¶\",\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":null}", e.serialize());
    }

    @Test
    void testObjectSerialization() {
        Employee e = new Employee();
        Home h = new Home();
        h.city = "München";
        h.street = "König Ludwig Straße";
        h.houseNumber = 42;
        e.home = h;
        assertEquals("{\"employeeNumber\":null,\"name\":null,\"money\":null,"
                + "\"birthDate\":null,\"male\":null,\"home\":{\"class\":\"Home\","
                + "\"city\":\"München\",\"street\":\"König Ludwig Straße\",\"houseNumber\":42}}",
                e.serialize());
    }

    @Test
    void testListSerialization() {
        Lists l = new Lists();
        l.booleans = List.of(true, false, true, true, false);
        l.integers = List.of(100, 200, 420, 69, 17);
        l.doubles = List.of(0d, 19d, -6.76d);
        l.longs = List.of(-1l, -379781378l, 7816421378l);
        l.strings = List.of("", "   \tHello", "World!!");
        l.homes = List.of();
        assertEquals(
                "{\"booleans\":[true,false,true,true,false]," + "\"integers\":[100,200,420,69,17],"
                        + "\"doubles\":[0.0,19.0,-6.76]," + "\"longs\":[-1,-379781378,7816421378],"
                        + "\"strings\":[\"\",\"   \tHello\",\"World!!\"]," + "\"homes\":[]}",
                l.serialize());
    }

    @Test
    void testObjectListSerialization() {
        Lists l = new Lists();
        Home h1 = new Home();
        Home h2 = new Home();
        h1.city = "Berlin";
        h2.city = "Düsseldorf";
        l.homes = List.of(h1, h2);
        assertEquals("{\"booleans\":null,\"integers\":null,\"doubles\":null,"
                + "\"longs\":null,\"strings\":null,\"homes\":["
                + "{\"class\":\"Home\",\"city\":\"Berlin\",\"street\":null,\"houseNumber\":null},"
                + "{\"class\":\"Home\",\"city\":\"Düsseldorf\",\"street\":null,"
                + "\"houseNumber\":null}]}", l.toString());
    }

    @Test
    void testIllegalObjects() {
        IllegalField field = new IllegalField();
        IllegalListField list = new IllegalListField();
        field.myShort = Short.valueOf("12");
        list.objects = List.of(Byte.valueOf("127"), Byte.valueOf("10"));
        assertThrows(JsonSerializationException.class, field::serialize);
        assertThrows(JsonSerializationException.class, list::serialize);
    }

    private class Employee extends Json {

        private static final long serialVersionUID = 8006358858233459721L;

        @JsonField("employeeNumber")
        private Integer id;
        @JsonField
        private String name;
        @JsonField
        private Double money;
        @JsonField
        private Long birthDate;
        @JsonField
        private Boolean male;
        @JsonField
        private Home home;

        @Override
        protected JsonDeserializer<Employee> getDeserializer() {
            return null;
        }
    }

    @JsonClass
    private class Home extends Json {

        private static final long serialVersionUID = -8162127789126690084L;

        @JsonField
        private String city;
        @JsonField
        private String street;
        @JsonField
        private Integer houseNumber;

        @Override
        protected JsonDeserializer<Home> getDeserializer() {
            return null;
        }
    }

    private class Lists extends Json {

        private static final long serialVersionUID = -8162197789466690037L;

        @JsonField
        private List<Boolean> booleans;
        @JsonField
        private List<Integer> integers;
        @JsonField
        private List<Double> doubles;
        @JsonField
        private List<Long> longs;
        @JsonField
        private List<String> strings;
        @JsonField
        private List<Home> homes;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return null;
        }
    }

    private class IllegalField extends Json {

        private static final long serialVersionUID = -2221606998951959283L;

        @JsonField
        private Short myShort;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return null;
        }
    }

    private class IllegalListField extends Json {

        private static final long serialVersionUID = -5762100798211961850L;

        @JsonField
        private List<Object> objects;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return null;
        }
    }
}
