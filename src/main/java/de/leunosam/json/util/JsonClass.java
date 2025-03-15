package de.leunosam.json.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.leunosam.json.Json;

/**
 * If this annotation is added to a child of the class {@link Json}, the
 * class-name is added as Json-Field to the serialized Json, e.g.:<br>
 * {<br>
 * &emsp;"class": "MyClass",<br>
 * &emsp;"otherValue": null,<br>
 * &emsp;...<br>
 * }
 * 
 * @author LeunoSam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonClass {
    // nothing todo
}
