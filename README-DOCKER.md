# TradeWave Docker Guide

Bu proje Docker ile çalıştırılabilir.

## Gereksinimler
- Docker
- Docker Compose

## Kurulum ve Çalıştırma

### 1. Docker Compose ile Çalıştırma (Önerilen)

Uygulamayı ve PostgreSQL veritabanını birlikte başlatmak için:

```bash
docker-compose up -d
```

### 2. Logları Görüntüleme

```bash
docker-compose logs -f
```

### 3. Durdurma

```bash
docker-compose down
```

### 4. Veritabanı ile Birlikte Silme

```bash
docker-compose down -v
```

## Manuel Docker Komutları

### PostgreSQL Container'ı Başlatma

```bash
docker run -d ^
  --name tradewave-postgres ^
  -e POSTGRES_DB=tradewave_db ^
  -e POSTGRES_USER=postgres ^
  -e POSTGRES_PASSWORD=190512 ^
  -p 5432:5432 ^
  postgres:16-alpine
```

### Uygulama Image'ını Build Etme

```bash
docker build -t tradewave-app .
```

### Uygulama Container'ını Başlatma

```bash
docker run -d ^
  --name tradewave-app ^
  -p 8080:8080 ^
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/tradewave_db ^
  -e SPRING_DATASOURCE_USERNAME=postgres ^
  -e SPRING_DATASOURCE_PASSWORD=190512 ^
  tradewave-app
```

## Erişim

- **Uygulama:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs
- **PostgreSQL:** localhost:5432

## Sorun Giderme

### Container durumunu kontrol etme

```bash
docker ps
```

### Container loglarını görüntüleme

```bash
docker logs tradewave-app
```

### Container'a bağlanma

```bash
docker exec -it tradewave-app sh
```

### PostgreSQL'e bağlanma

```bash
docker exec -it tradewave-postgres psql -U postgres -d tradewave_db
```

## Production İçin Öneriler

1. `.env` dosyası kullanarak hassas bilgileri saklayın
2. `application.properties` dosyasında üretim ayarlarını yapın
3. Health check endpoint'lerini aktif edin
4. Log seviyelerini ayarlayın
5. JVM memory ayarlarını optimize edin:

```dockerfile
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]
```
# Multi-stage build
# Stage 1: Build the application
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

