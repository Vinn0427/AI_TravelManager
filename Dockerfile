# Multi-stage build: Frontend (Vite) → Backend (Spring Boot) → Runtime (JRE)

############################
# 1) Build frontend assets #
############################
FROM node:18-alpine AS frontend
WORKDIR /app/frontend

# Install dependencies first for better layer caching
COPY travelmanager/frontend/package*.json ./
RUN npm ci --legacy-peer-deps

# Copy source and build
COPY travelmanager/frontend/ .

# Optional: pass map api key at build-time (used by Vite build if referenced)
ARG VITE_MAP_API_KEY
ENV VITE_MAP_API_KEY="${VITE_MAP_API_KEY}"

RUN npm run build


########################################
# 2) Build backend jar with static dist #
########################################
FROM maven:3.9.6-eclipse-temurin-17 AS backend-build
WORKDIR /app

# Copy backend project
COPY travelmanager/ /app/travelmanager/

# Inject frontend build output into Spring Boot static resources
COPY --from=frontend /app/frontend/dist /app/travelmanager/src/main/resources/static

# Build Spring Boot jar
RUN mvn -f /app/travelmanager/pom.xml -B -DskipTests clean package


############################
# 3) Runtime image (JRE 17) #
############################
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Copy jar
COPY --from=backend-build /app/travelmanager/target/*.jar /app/app.jar

# Runtime configuration (override via docker run -e JAVA_OPTS="...")
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Expose Spring Boot port
EXPOSE 8080

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]


