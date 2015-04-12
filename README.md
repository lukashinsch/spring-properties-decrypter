[![Coverage Status](https://coveralls.io/repos/lukashinsch/spring-properties-decrypter/badge.svg?branch=master)](https://coveralls.io/r/lukashinsch/spring-properties-decrypter?branch=master)
[![Build Status](https://travis-ci.org/lukashinsch/spring-properties-decrypter.svg?branch=master)](https://travis-ci.org/lukashinsch/spring-properties-decrypter)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/eu.hinsch/spring-properties-decrypter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/eu.hinsch/spring-properties-decrypter/)


# spring-properties-decrypter
Transparently decrypt property values to be used in spring environment using [jasypt](http://www.jasypt.org/) library.

# Howto use

## Gradle dependency
```
runtime('eu.hinsch:spring-properties-decrypter:0.1.6')
```

## Maven dependency
```
<dependency>
  <groupId>eu.hinsch</groupId>
  <artifactId>spring-properties-decrypter</artifactId>
  <version>0.1.6</version>
</dependency>
```

## Decryption password
Set property 'propertyDecryption.password' via environment or system property to decryption key

## Encrpted properties
Define any spring environment property anywhere it can be defined (application*.properties/yaml, system, environment, command line...) and insert encrypted value like this:
```
myProperty={encrypted}ENCRYPTEDVALUERAWDATA
```

## Alternative prefix
To use a prefix other than '{encrypted}' define via
```
propertyDecryption.prefix=MY-PREFIX
```

## Encryption algorithm
By default uses PBEWithMD5AndDES (to allow running in default JRE).

To configure alternative algorithm:
```
propertyDecryption.algorithm=ALGORITHMNAME
```

## Howto encrypt passwords
See http://www.jasypt.org/cli.html
