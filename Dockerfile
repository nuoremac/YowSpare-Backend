# --- STAGE 1: Build ---
# Utilise une image Maven avec un JDK 17
FROM maven:3.9-eclipse-temurin-17 AS build

# Définit le répertoire de travail dans le conteneur
WORKDIR /app

# Copie les fichiers de configuration Maven pour télécharger les dépendances
COPY pom.xml .

# Télécharge les dépendances (cette couche est mise en cache si pom.xml ne change pas)
RUN mvn dependency:go-offline

# Copie le reste du code source
COPY src ./src

# Construit l'application et crée le JAR
RUN mvn clean install -DskipTests


# --- STAGE 2: Run ---
# Utilise une image JRE minimale pour l'exécution
FROM eclipse-temurin:17-jre-jammy

# Définit le répertoire de travail
WORKDIR /app

# Copie uniquement le JAR final depuis le stage de build
COPY --from=build /app/target/stock-api-0.0.1-SNAPSHOT.jar app.jar

# Expose le port sur lequel l'application s'exécute
EXPOSE 8081

# Commande pour lancer l'application au démarrage du conteneur
ENTRYPOINT ["java", "-jar", "app.jar"]
