package de.leunosam.json.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.text.Annotation;

import de.leunosam.json.Json;

/**
 * Add this {@link Annotation} to {@link Field}s of a {@link Json}-Class, which
 * should be present in the serialized {@link String}. If you want to have a
 * different Field-Name in the Json-String than in java, override the value of
 * this {@link Annotation}.
 * 
 * @author LeunoSam
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonField {

    String value() default "";

}
