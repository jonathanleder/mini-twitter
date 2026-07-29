# Mini Twitter API

Una implementación de una API RESTful para un servicio de microblogging similar a Twitter, construida con Java 21, Spring Boot 3.5.5 y Spring Data MongoDB.

## 📝 Características

- ✅ Gestión de usuarios (crear, listar, buscar, eliminar)
- ✅ Publicación de tweets
- ✅ Sistema de retweets
- ✅ Feed paginado (solo tweets normales)
- ✅ Tweets por usuario con carga progresiva (limit/offset)
- ✅ DTOs enriquecidos para frontend
- ✅ Validación de datos en el constructor (objetos válidos siempre)
- ✅ Pruebas unitarias y de integración (79 tests, 98.3% cobertura de instrucciones)
- ✅ Manejo centralizado de excepciones
- ✅ CORS habilitado para frontend local

## 🚀 Tecnologías

- **Backend**: Java 21, Spring Boot 3.5.5
- **Base de datos**:
  - MongoDB 7 (desarrollo/producción)
  - MongoDB embebido vía Flapdoodle (tests automáticos, no requiere Docker)
- **Acceso a datos**: Spring Data MongoDB
- **Testing**: JUnit 5.13, MockMvc
- **Herramientas**: Lombok 1.18.38, Maven, Docker, JaCoCo (cobertura)

## 📋 Requisitos

- Java 21 JDK
- Maven 3.9+
- Docker y Docker Compose (para MongoDB en desarrollo)
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
MONGO_DATABASE=mini-twitter
MONGO_USER=user
MONGO_PASSWORD=1234
DB_HOST=localhost
DB_PORT=27017
MONGO_EXPRESS_PORT=8091
```

### 3. Levantar la base de datos (Docker)

```bash
docker compose up -d mongo
```

Esto levantará:
- **MongoDB 7** en puerto 27017
- **Mongo Express** en http://localhost:8091 (usuario/contraseña: los definidos en `.env`)

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
│   │   │   ├── main/              # Punto de entrada
│   │   │   │   └── Main.java (@EnableMongoRepositories)
│   │   │   ├── model/             # Entidades de dominio (documentos Mongo)
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
│   │   │   ├── repositorios/      # Acceso a datos (Spring Data MongoDB)
│   │   │   │   ├── TweetRepository.java / UsuarioRepository.java (contratos de dominio)
│   │   │   │   ├── MongoTweetRepository.java / MongoUsuarioRepository.java (Spring Data)
│   │   │   │   ├── TweetRepositoryImpl.java / UsuarioRepositoryImpl.java
│   │   │   │   └── SequenceGeneratorService.java (simula autoincremento de Long)
│   │   │   └── config/
│   │   │       └── DataInitializer.java (carga datos de prueba)
│   │   └── resources/
│   │       └── application.properties (Mongo, vía spring.data.mongodb.uri)
│   └── test/
│       ├── java/unrn/             # Tests unitarios e integración
│       │   ├── web/                (controllers, *WebIT, exception handler)
│       │   ├── service/            (TwitterServiceTest, contra Mongo embebido)
│       │   ├── model/              (Usuario/Tweet, dominio puro)
│       │   ├── repositorios/       (DatabaseSequenceTest)
│       │   └── config/             (DataInitializerTest)
│       └── resources/
│           └── application.properties (Mongo embebido/Flapdoodle)
├── compose.yaml                   # Docker Compose (MongoDB + Mongo Express)
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
      "origenFecha": null,
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
      "origenFecha": null,
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

> `origenFecha` solo tiene valor cuando `esRetweet` es `true`: es la fecha de creación del tweet **original**, distinta de `fecha` (que es cuándo se hizo el retweet).

#### Listar todos los tweets

```http
GET /tweets
```

---

## 🧪 Testing

**79 tests, 98.3% de cobertura de instrucciones (97.1% de líneas)**, medida con JaCoCo.

- Tests de dominio puro (`unrn.model`): validaciones de `Usuario`/`Tweet`, sin tocar base de datos.
- Tests de servicio (`unrn.service.TwitterServiceTest`): `@SpringBootTest` contra MongoDB embebido (Flapdoodle), cubren feed paginado, cascada de borrado de retweets, casos de error (usuario/tweet inexistente), etc.
- Tests de controllers con Mockito (`TweetControllerTest`, `UsuarioControllerTest`): mockean `TwitterService`, no tocan la base.
- Tests de integración de la capa web (`*WebIT`): `@SpringBootTest` + `MockMvc` end-to-end contra Mongo embebido.
- Tests del manejador global de excepciones (`TwitterGlobalExceptionHandlerTest`/`TestUnit`).

### Ejecutar todos los tests

```bash
mvn test
```

Corre los 79 tests (incluye los `*WebIT.java` y `*TestUnit.java`, agregados explícitamente en el `<includes>` de `maven-surefire-plugin` en `pom.xml` porque sus nombres no matchean los patrones por defecto de Surefire).

### Generar reporte de cobertura

```bash
mvn clean test jacoco:report
```

El reporte estará en: `target/site/jacoco/index.html`. Cobertura por clase: 100% en la mayoría; `Main` (37.5%, el `public static void main` no se testea) y un puñado de setters protegidos sin uso real en `Usuario`/`Tweet` (artefactos de compatibilidad, Spring Data los popula por reflexión) quedan deliberadamente sin cubrir.

---

## 🗂️ Estructura de Base de Datos (MongoDB)

### Colección: `usuarios`
```json
{ "_id": 1, "userName": "juan_perez" }
```
`userName` tiene un índice único (`@Indexed(unique = true)`).

### Colección: `tweets`
Los tweets guardan el autor y, si son un retweet, el tweet de origen **denormalizados** (username y texto embebidos), para evitar joins en cada lectura:
```json
{
  "_id": 21,
  "autorId": 2,
  "autorUsername": "maria_garcia",
  "text": null,
  "origenId": 1,
  "origenAutorUsername": "juan_perez",
  "origenTexto": "¡Hola a todos!",
  "fechaCreacion": "2026-07-28T18:57:44.16"
}
```

### Colección: `database_sequences`
Simula el autoincremento de `Long` que antes generaba Hibernate (Mongo no autogenera IDs numéricos):
```json
{ "_id": "tweets", "seq": 21 }
```

---

## 🔄 Dual Environment Setup

### Para desarrollo (MongoDB persistente)

```bash
docker compose up -d mongo
mvn spring-boot:run
```

Usa la configuración de `src/main/resources/application.properties` (`spring.data.mongodb.uri`)

### Para tests (MongoDB embebido)

```bash
mvn test
```

No requiere Docker: `src/test/resources/application.properties` deja `spring.data.mongodb.uri` sin definir, por lo que Spring Boot autoconfigura un `mongod` embebido (Flapdoodle) para cada corrida.

---

## 🆘 Troubleshooting

### "Connection refused" contra MongoDB

Verificar que Docker está ejecutando:
```bash
docker compose ps
```

Si no está corriendo:
```bash
docker compose up -d mongo
```

### Tests fallan en Windows

Algunos tests pueden ser sensibles a rutas. Intentar:
```bash
mvn clean test -DforkCount=1
```

---

## 📝 Notas de Desarrollo

- El proyecto sigue DDD (Domain-Driven Design)
- Las validaciones están en constructores (objetos siempre válidos)
- Usa el patrón Tell-Don't-Ask
- No hay getters/setters innecesarios (encapsulación fuerte)
- Los DTOs separan la API del modelo de dominio
- El acceso a datos usa Spring Data MongoDB; los campos `autor`/`origen` de `Tweet` son transitorios (solo viven en memoria durante la operación que los crea) y se complementan con campos denormalizados (`autorUsername`, `origenTexto`, etc.) que sí se persisten

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
