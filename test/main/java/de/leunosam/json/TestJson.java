package de.leunosam.json;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import de.leunosam.json.deserialize.JsonDeserializer;
import de.leunosam.json.util.JsonField;

class TestJson {

    @Test
    void testEquals() {
        Car car1 = new Car();
        Car car2 = new Car();
        assertTrue(car1.equals(car1));
        assertTrue(car1.equals(car2));
        assertFalse(car1.equals(null));
        assertFalse(car1.equals(new Object()));
        car2.id = "M-KD 890";
        assertFalse(car1.equals(car2));
        car1.id = car2.id;
        car1.horsePower = 140;
        assertFalse(car1.equals(car2));
    }

    @Test
    void testHash() {
        Car car = new Car();
        car.id = "B-YX 127";
        car.horsePower = 150;
        car.seats = 7;
        assertEquals(Objects.hash(car.id, car.horsePower, car.seats), car.hashCode());
    }

    private class Car extends Json {

        private static final long serialVersionUID = -7973612655349905384L;

        @JsonField
        private String id;
        @JsonField
        private Integer horsePower;
        @JsonField
        private Integer seats;

        @Override
        protected JsonDeserializer<? extends Json> getDeserializer() {
            return null;
        }

    }

}
