# Project Info

Offers simple tools for generating KML data based on input parameters.

Functions:
- LOB: for generating line of bearing from specific co-ordinates.

# Build

Requires Maven and Java 17

```
mvn clean package
```

# Run

```
java -jar target/<output>.jar
```

# Debug

```
java -Dspring.profiles.active=dev -jar target/<output>.jar
```

Note that the LiveReload port opens on port `35729` while `dev` profile is active.

For remote live reload, `ssh -L 35729:127.0.0.1:35729` can be used.

Additional debug parameters are defined in [application-dev.properties](src/main/resources/application-dev.properties).