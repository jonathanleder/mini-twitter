# Mini Twitter API

Una implementación de una API RESTful para un servicio de microblogging similar a Twitter, construida con Java 21, Spring Boot 3.5.5 y JPA/Hibernate 7.0.7 con MySQL.

## 📝 Características

- ✅ Gestión de usuarios (crear, listar, buscar, eliminar)
- ✅ Publicación de tweets
- ✅ Sistema de retweets
- ✅ Feed paginado (solo tweets normales)
- ✅ Tweets por usuario con carga progresiva (limit/offset)
- ✅ DTOs enriquecidos para frontend
- ✅ Validación de datos en el constructor (objetos válidos siempre)
- ✅ Pruebas unitarias y de integración (57/61 tests pasando)
- ✅ Manejo centralizado de excepciones
- ✅ CORS habilitado para frontend local

## 🚀 Tecnologías

- **Backend**: Java 21, Spring Boot 3.5.5
- **Base de datos**: 
  - MySQL 8.0 (desarrollo/producción)
  - H2 en memoria (tests automáticos)
- **ORM**: JPA 3.2, Hibernate 7.0.7
- **Testing**: JUnit 5.13, MockMvc
- **Herramientas**: Lombok 1.18.38, Maven, Docker, JaCoCo (cobertura)

## 📋 Requisitos

- Java 21 JDK
- Maven 3.9+
- Docker y Docker Compose (para MySQL en desarrollo)
- Git

## 🛠️ Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/jonathanleder/mini-twitter.git
cd mini-twitter
```

### 2. Configurar variables de entorno

Crear archivo `.env` en la raíz del proyecto:

```env
MYSQL_DATABASE=mini-twitter
MYSQL_USER=user
MYSQL_PASSWORD=1234
MYSQL_ROOT_PASSWORD=1234
DB_HOST=localhost
DB_PORT=3306
PHPMYADMIN_PORT=8091
```

### 3. Levantar la base de datos (Docker)

```bash
docker-compose up -d
```

Esto levantará:
- **MySQL 8.0** en puerto 3306
- **PHPMyAdmin** en http://localhost:8091 (usuario: root, contraseña: 1234)

### 4. Compilar el proyecto

```bash
mvn clean compile
```

### 5. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación estará disponible en: **http://localhost:8080**

### 6. Verificar que está funcionando

```bash
curl http://localhost:8080/usuarios
```

Deberías ver un array JSON con los usuarios creados por `DataInitializer`.

## 📊 Estructura del Proyecto

```
mini-twitter/
├── src/
│   ├── main/
│   │   ├── java/unrn/
│   │   │   ├── main/              # Punto de entrada y configuración
│   │   │   │   ├── Main.java
│   │   │   │   └── AppConfiguration.java (perfil MySQL/H2)
│   │   │   ├── model/             # Entidades de dominio
│   │   │   │   ├── Usuario.java
│   │   │   │   └── Tweet.java
│   │   │   ├── service/           # Lógica de negocio
│   │   │   │   └── TwitterService.java
│   │   │   ├── web/               # Controladores REST
│   │   │   │   ├── UsuarioController.java
│   │   │   │   ├── TweetController.java
│   │   │   │   └── TwitterGlobalExceptionHandler.java
│   │   │   ├── DTOs/              # Objetos de transferencia de datos
│   │   │   │   ├── TweetDto.java
│   │   │   │   ├── FeedResponseDto.java
│   │   │   │   └── NuevoTweet.java
│   │   │   ├── repositorios/      # Acceso a datos (JPA)
│   │   │   │   ├── TweetRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── JpaTweetRepository.java
│   │   │   ├── util/              # Utilidades
│   │   │   │   ├── EmfBuilder.java (H2)
│   │   │   │   └── EmfMySQLBuilder.java (MySQL)
│   │   │   └── config/
│   │   │       └── DataInitializer.java (carga datos de prueba)
│   │   └── resources/
│   │       ├── application.properties (MySQL)
│   │       └── data.sql (SQL inicial)
│   └── test/
│       ├── java/unrn/             # Tests unitarios e integración
│       │   ├── web/
│       │   ├── service/
│       │   └── model/
│       └── resources/
│           └── application.properties (H2)
├── compose.yaml                   # Docker Compose (MySQL + PHPMyAdmin)
├── .env                          # Variables de entorno
├── .gitignore
├── pom.xml
└── README.md
```

## 📚 Documentación de la API

### Usuarios

#### Crear usuario

```http
POST /usuarios
Content-Type: application/json

{
  "username": "juan_perez"
}
```

Respuesta (201 Created):
```json
{ "id": 1, "username": "juan_perez" }
```

#### Listar todos los usuarios

```http
GET /usuarios
```

Respuesta (200 OK):
```json
[
  { "id": 1, "username": "juan_perez" },
  { "id": 2, "username": "maria_garcia" }
]
```

#### Eliminar usuario

```http
DELETE /usuarios/{usuarioId}
```

---

### Tweets

#### Crear tweet

```http
POST /tweets
Content-Type: application/json

{
  "usuarioId": 1,
  "texto": "¡Hola a todos! Este es mi primer tweet."
}
```

#### Crear retweet

```http
POST /tweets/retweet
Content-Type: application/json

{
  "usuarioId": 2,
  "tweetOrigenId": 1
}
```

#### Obtener feed paginado (solo tweets normales)

```http
GET /tweets/feed?page=0&size=10
```

Respuesta (200 OK):
```json
{
  "content": [
    {
      "id": 1,
      "texto": "¡Hola a todos!",
      "autorUsername": "juan_perez",
      "fecha": "2025-11-30T18:20:19.006957",
      "origenId": null,
      "tweetOriginalTexto": null,
      "usuarioOriginal": null,
      "usuarioRetweet": null,
      "esRetweet": false
    }
  ],
  "totalPages": 2,
  "currentPage": 0,
  "size": 10,
  "totalElements": 20,
  "hasNext": true,
  "hasPrevious": false
}
```

#### Obtener tweets de usuario (carga progresiva)

```http
GET /tweets/usuario/{usuarioId}?limit=15&offset=0
```

Respuesta (200 OK):
```json
{
  "tweets": [
    {
      "id": 1,
      "texto": "Mi primer tweet",
      "autorUsername": "juan_perez",
      "fecha": "2025-11-30T18:20:19.006957",
      "origenId": null,
      "tweetOriginalTexto": null,
      "usuarioOriginal": null,
      "usuarioRetweet": null,
      "esRetweet": false
    }
  ],
  "limit": 15,
  "offset": 0,
  "total": 25,
  "hasMore": true
}
```

#### Listar todos los tweets

```http
GET /tweets
```

---

## 🧪 Testing

### Ejecutar todos los tests

```bash
mvn test
```

### Ejecutar solo tests unitarios

```bash
mvn test -Dtest=*Test
```

### Ejecutar solo tests de integración

```bash
mvn test -Dtest=*IT
```

### Generar reporte de cobertura

```bash
mvn clean test jacoco:report
```

El reporte estará en: `target/site/jacoco/index.html`

**Estado actual**: 57/61 tests pasando (93.4% éxito)

---

## 🗂️ Estructura de Base de Datos

### Tabla: usuarios
```sql
CREATE TABLE usuarios (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255) NOT NULL UNIQUE
);
```

### Tabla: tweets
```sql
CREATE TABLE tweets (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id BIGINT NOT NULL,
  texto VARCHAR(500) NOT NULL,
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  tweet_origen_id BIGINT,
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
  FOREIGN KEY (tweet_origen_id) REFERENCES tweets(id) ON DELETE CASCADE
);
```

---

## 🔄 Dual Environment Setup

### Para desarrollo (MySQL persistente)

```bash
mvn spring-boot:run
```

Usa la configuración de `src/main/resources/application.properties` (MySQL)

### Para tests (H2 en memoria)

```bash
mvn test
```

Usa la configuración de `src/test/resources/application.properties` (H2)

---

## 🆘 Troubleshooting

### "Connection refused" en MySQL

Verificar que Docker está ejecutando:
```bash
docker-compose ps
```

Si no está corriendo:
```bash
docker-compose up -d
```

### "Table doesn't exist"

Hibernate debería crear las tablas automáticamente. Si no:
1. Verificar que `spring.jpa.hibernate.ddl-auto=create` está en `application.properties`
2. Reiniciar la aplicación

### Tests fallan en Windows

Algunos tests pueden ser sensibles a rutas. Intentar:
```bash
mvn clean test -DforkCount=1
```

### "Could not initialize proxy" error en la API

Este error ocurre cuando Hibernate intenta cargar datos de una entidad relacionada fuera de una transacción. Está resuelto en la versión actual:
- Los métodos que retornan DTOs (`listarFeedPaginadoDto`, `listarTweetsDeUsuarioConLimitDto`) mapean los datos **dentro** de la transacción JPA
- Esto asegura que todos los proxies de Hibernate se inicialicen antes de cerrar la sesión

---

## 📝 Notas de Desarrollo

- El proyecto sigue DDD (Domain-Driven Design)
- Las validaciones están en constructores (objetos siempre válidos)
- Usa el patrón Tell-Don't-Ask
- No hay getters/setters innecesarios (encapsulación fuerte)
- Los DTOs separan la API del modelo de dominio
- Lazy loading issues resueltos con mapeo dentro de transacciones

---

## 🤝 Contribución

1. Haz un fork
2. Crea una rama (`git checkout -b feature/tu-caracteristica`)
3. Haz commit (`git commit -am 'Agrega característica'`)
4. Haz push (`git push origin feature/tu-caracteristica`)
5. Abre un Pull Request

---

## 📄 Licencia

MIT - Ver [LICENSE](LICENSE) para detalles.
