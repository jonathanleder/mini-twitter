# Mini Twitter API

Una implementación de una API RESTful para un servicio de microblogging similar a Twitter, construida con Spring Boot y JPA/Hibernate.

## 📝 Características

- Gestión de usuarios (crear, listar, buscar, eliminar)
- Publicación de tweets
- Sistema de retweets
- Validación de datos
- Pruebas unitarias y de integración

## 🚀 Tecnologías

- **Backend**: Java 21, Spring Boot 3.x
- **Base de datos**: H2 (en memoria para pruebas), configurable para otras bases de datos
- **Persistencia**: JPA 3.2, Hibernate 7
- **Testing**: JUnit 5, MockMvc, TestContainers
- **Herramientas**: Lombok, Maven

## 📋 Requisitos

- Java 21 o superior
- Maven 3.6+
- (Opcional) Docker para pruebas con TestContainers

## 🛠️ Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/mini-twitter.git
   cd mini-twitter
   ```

2. Construir el proyecto:
   ```bash
   mvn clean install
   ```

3. Ejecutar la aplicación:
   ```bash
   mvn spring-boot:run
   ```

La aplicación estará disponible en `http://localhost:8080`

## 📚 Documentación de la API

### Usuarios

- `POST /usuarios` - Crear un nuevo usuario
  ```json
  {
    "username": "nombreUsuario"
  }
  ```

- `GET /usuarios` - Listar todos los usuarios
- `GET /usuarios/{username}` - Obtener un usuario por su nombre de usuario
- `DELETE /usuarios/{id}` - Eliminar un usuario por su ID

### Tweets

- `POST /tweets` - Crear un nuevo tweet
  ```json
  {
    "usuarioId": 1,
    "texto": "Contenido del tweet"
  }
  ```

- `POST /tweets/retweet` - Crear un retweet
  ```json
  {
    "usuarioId": 2,
    "tweetOrigenId": 1
  }
  ```

- `GET /tweets/usuario/{usuarioId}` - Listar tweets de un usuario

## 🧪 Testing

El proyecto incluye dos tipos de pruebas:

### Pruebas Unitarias
- Ubicación: `src/test/java/unrn/web/*Test.java`
- Prueban los controladores de forma aislada usando mocks
- Ejecutar con: `mvn test`

### Pruebas de Integración
- Ubicación: `src/test/java/unrn/web/*IT.java`
- Prueban la aplicación completa con una base de datos en memoria
- Ejecutar con: `mvn verify`

## 📊 Cobertura de Código

Para generar un informe de cobertura:

```bash
mvn jacoco:report
```

El informe estará disponible en: `target/site/jacoco/index.html`

## 🤝 Contribución

1. Haz un fork del proyecto
2. Crea una rama para tu característica (`git checkout -b feature/nueva-caracteristica`)
3. Haz commit de tus cambios (`git commit -am 'Agrega nueva característica'`)
4. Haz push a la rama (`git push origin feature/nueva-caracteristica`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.