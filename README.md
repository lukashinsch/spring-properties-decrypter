# spring-properties-decrypter
Transparently decrypt property values to be used in spring environment

Used algorithm: PBEWithMD5AndDES (does not use stronger encryption to allow running in default JRE)

# Howto use

## Gradle dependency
```
runtime('eu.hinsch:spring-properties-decrypter:0.1.2')
```

## Maven dependency
```
<dependency>
  <groupId>eu.hinsch</groupId>
  <artifactId>spring-properties-decrypter</artifactId>
  <version>0.1.2</version>
</dependency>
```

## Decryption password
Set property 'propertyDecryption.password' via environment or system property to decryption key

## Encrpted properties
Define any spring environment property anywhere it can be defined (application*.properties/yaml, system, environment, command line...) and insert encrypted value like this:
```
myProperty={cypher}ENCRYPTEDVALUERAWDATA
```

## Alternative prefix
To use a prefix other than '{cypher}' define via
```
propertyDecryption.prefix=MY-PREFIX
```
