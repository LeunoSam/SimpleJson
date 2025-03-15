# Simple Json
## General information
This is a librabry for the serialization and deserialization of Jsons. You are free to use this library for any purpose, for more information take a look at the [license](https://github.com/LeunoSam/SimpleJson/blob/main/LICENSE).

## What is possible?
You can serialize and deserialize any Objects that extend the abstract class **Json**.

## Which field-types / classes are supported?
You can use **Boolean**, **Integer**, **Long**, **Double**, **String** as fields of your own Json-Classes. You can use other classes as field, if they extend **Json**. Fields that are **Lists**, can contain Boolean, Integer, Long, Double, String or a Json as item and will be serialized as Json-Array.

## How does serialization work?
You have to annotate all fields, which should be serialized, with the annotation **JsonField**. These fields will be included in the generated Json-String by the default _serialize_ method of the class **Json**. The default _serialize_ method will get these fields by using reflections.

If you do not want to use reflections or you want to define the serialization by your own, you can override the method _getSerializer_, which has to return a JsonSerializer. This serializer has to contain your Json-Object.

## How does deserialization work?
For each new Json-Class you add, you have to implement the method _getDeserializer_. There you have to return a **JsonDeserializer** for your Json-Class. A JsonDeserializer has to implement the method _getObject_, which converts a **String** to an instance of your Class. However, you can use predefined methods of the class **JsonDeserializer** for the deserialization, for example the method _readFields_.

The method _readFields_ will return a **Map**, that contains all Json-Fields as Key and the values of these Fields as Value. Both (Key and Value) are **Strings**, but there are other methods in the class **JsonDeserializer** to convert these Values to a **Boolean**, **Integer**, **Long**, **Double**, **String** or to one of your own Json-Classes.
