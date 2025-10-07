
# Build stage
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# คัดลอกไฟล์ pom.xml และ mvnw มาก่อน (เพื่อ cache dependency)
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# คัดลอกโค้ดจริง
COPY src src

# สร้าง jar
RUN ./mvnw clean package -DskipTests

# Runtime (Distroless)
FROM gcr.io/distroless/java21-debian12

WORKDIR /app
COPY --from=builder /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
