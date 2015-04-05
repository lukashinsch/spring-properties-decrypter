# spring-properties-decrypter
Transparently decrypt property values to be used in spring environment

Used algorithm: PBEWithMD5AndDES

# Howto use

## Gradle dependency
```
runtime('eu.hinsch:spring-property-decyrpter:0.1.0')
```

## Maven dependency
```
<dependency>
  <groupId>eu.hinsch</groupId>
  <artifactId>spring-properties-decrypter</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Decryption password
Set property 'propertyDecryption.password' via environment or system property to decryption key

## Encrpted properties
Define any spring environment property anywhere it can be defined (application*.properties/yaml, system, environment, command line...) and insert encrypted value like this:
myProperty={cypher}ENCRYPTEDVALUERAWDATA
