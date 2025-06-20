# Infra Core

Infrastructure Core Library - Auth, Security, Common Entities for Spring Boot applications.

## Installation

### Prerequisites

- Java 21+
- Maven 3.6+
- GitHub Personal Access Token with `read:packages` permission

### Usage in your project

1. **Set up authentication:** Create/update your `~/.m2/settings.xml` file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>

</settings>
```

2. **Add repository to your `pom.xml`:**

```xml
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/fthgcr/infra-core-mvn</url>
    </repository>
</repositories>
```

3. **Add dependency to your `pom.xml`:**

```xml
<dependency>
    <groupId>com.fthgcr</groupId>
    <artifactId>infra-core</artifactId>
    <version>0.0.67-SNAPSHOT</version>
</dependency>
```

## Features

- JWT Authentication & Authorization
- Security Configuration
- Common Entity Base Classes
- Validation Utils
- Spring Boot Auto-Configuration

## Requirements

- Spring Boot 3.4.4+
- Java 21+

## License

This project is licensed under the MIT License. 