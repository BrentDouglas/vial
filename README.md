# Vial
[![Build Status](https://travis-ci.org/machinecode-io/vial.svg?branch=master)](https://travis-ci.org/machinecode-io/vial)

Collections and primitive collections for JDK7.

## Goals

This project intended to, where possible, maintain compatibility with the JDK collections guarantees
while removing unnecessary heap allocations. Where is it not possible to eliminate them using the standard API
alternatives are provided that allows manually managing buffers.

### Iteration

#### Maps

Maps provided



The following methods WILL allocate memory:

XMap#iterator
XMap#cursor

The following methods MAY allocate memory:



## Maven dependency

Available in Central. For the latest release:

```xml
<dependency>
   <groupId>io.machinecode.vial</groupId>
   <artifactId>vial-core</artifactId>
   <version>0.1.0</version>
</dependency>
```

## License

[Apache 2.0](LICENSE.txt)
