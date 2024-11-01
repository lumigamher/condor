# Chat con open AI springboot / project CONDOR APP

Esta es la documentación de la API de chat desarrollada en Spring Boot. A continuación se describen los endpoints, la configuración del servidor y cómo realizar solicitudes.

## Requisitos previos

- **Java 17** o superior.
- **Maven** para la gestión de dependencias.
- **Spring Boot** framework.

## Configuración de la Base de Datos

La API utiliza una base de datos SQL. Asegúrate de configurar la conexión en el archivo `application.properties`.

```properties
# Configuración de base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/chat_db
spring.datasource.username=root
spring.datasource.password=contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración de JPA e Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Configuración de JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# Configuración de OpenAI
openai.api.url=${API_URL}
openai.api.key=${OPENAI_API_KEY}
openai.model=chatgpt-4o-latest
openai.account=app-msj
openai.default.prompt=prompt por defecto

# Configuración de perfiles activos
spring.profiles.active=conversation-test  

# Nivel de logging
logging.level.com.projectcondor.condor.service=DEBUG
```

## Cómo Correr el Proyecto

1. Clona el repositorio.
2. Configura el archivo `application.properties` con tus credenciales de base de datos.
3. Corre el proyecto con el siguiente comando:
   ```bash
   mvn spring-boot:run
   ```
4. La API estará disponible en `http://localhost:8080`.

---

## Endpoints

### 1. Autenticación

#### POST `/api/auth/register`
Registra a un nuevo usuario.

- **Request Body:**
  ```json
  {
    "username": "usuario123",
    "password": "contraseñaSegura",
    "email": "correo@example.com"
  }
  ```

- **Response:**
  ```json
  {
    "userId": "1",
    "username": "usuario123",
    "email": "correo@example.com",
    "token": "jwt_token_aqui"
  }
  ```

#### POST `/api/auth/login`
Autentica a un usuario y devuelve un token JWT.

- **Request Body:**
  ```json
  {
    "username": "usuario123",
    "password": "contraseñaSegura"
  }
  ```

- **Response:**
  ```json
  {
    "userId": "1",
    "username": "usuario123",
    "token": "jwt_token_aqui"
  }
  ```

---

### 2. Usuarios

#### GET `/api/users/{userId}`
Obtiene la información de un usuario.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Response:**
  ```json
  {
    "userId": "1",
    "username": "usuario123",
    "email": "correo@example.com",
    "createdAt": "2023-01-01T12:00:00Z"
  }
  ```

#### PUT `/api/users/{userId}`
Actualiza la información de un usuario.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Request Body:**
  ```json
  {
    "username": "nuevoUsuario",
    "email": "nuevoCorreo@example.com"
  }
  ```

- **Response:**
  ```json
  {
    "userId": "1",
    "username": "nuevoUsuario",
    "email": "nuevoCorreo@example.com"
  }
  ```

---

### 3. Conversaciones

#### GET `/api/conversations`
Obtiene las conversaciones del usuario autenticado.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Response:**
  ```json
  [
    {
      "conversationId": "123",
      "participants": ["1", "2"],
      "lastMessage": {
        "senderId": "1",
        "content": "Hola",
        "timestamp": "2023-01-01T12:00:00Z"
      }
    }
  ]
  ```

#### POST `/api/conversations`
Crea una nueva conversación.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Request Body:**
  ```json
  {
    "participantId": "2"
  }
  ```

- **Response:**
  ```json
  {
    "conversationId": "123",
    "participants": ["1", "2"]
  }
  ```

---

### 4. Mensajes

#### GET `/api/conversations/{conversationId}/messages`
Obtiene los mensajes de una conversación específica.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Response:**
  ```json
  [
    {
      "messageId": "1",
      "senderId": "1",
      "content": "Hola",
      "timestamp": "2023-01-01T12:00:00Z"
    },
    {
      "messageId": "2",
      "senderId": "2",
      "content": "Hola, ¿cómo estás?",
      "timestamp": "2023-01-01T12:01:00Z"
    }
  ]
  ```

#### POST `/api/conversations/{conversationId}/messages`
Envía un mensaje en una conversación.

- **Headers:**
  ```json
  {
    "Authorization": "Bearer jwt_token_aqui"
  }
  ```

- **Request Body:**
  ```json
  {
    "content": "Mensaje de prueba"
  }
  ```

- **Response:**
  ```json
  {
    "messageId": "3",
    "senderId": "1",
    "content": "Mensaje de prueba",
    "timestamp": "2023-01-01T12:02:00Z"
  }
  ```

---

## Ejemplo de Solicitudes con `curl`

### Registro
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario123", "password": "contraseñaSegura", "email": "correo@example.com"}'
```

### Inicio de Sesión
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "usuario123", "password": "contraseñaSegura"}'
```

### Obtener Conversaciones
```bash
curl -X GET http://localhost:8080/api/conversations \
  -H "Authorization: Bearer jwt_token_aqui"
```

---

## Autenticación

La API utiliza autenticación basada en JWT. Todos los endpoints (excepto `/auth/register` y `/auth/login`) requieren el token JWT en el encabezado de la solicitud.

### Ejemplo de Header de Autenticación
```json
{
  "Authorization": "Bearer jwt_token_aqui"
}
```

---

## Errores Comunes

- **401 Unauthorized**: El token JWT es inválido o ha expirado.
- **403 Forbidden**: No tienes permiso para acceder a este recurso.
- **404 Not Found**: El recurso no fue encontrado.
- **500 Internal Server Error**: Error en el servidor, revisa los logs para más detalles.

---

## Notas

- Asegúrate de almacenar el token de forma segura en el cliente.
- Los timestamps están en formato ISO 8601 (UTC).
- Los datos en la base de datos se actualizarán automáticamente con `spring.jpa.hibernate.ddl-auto=update`, pero para producción se recomienda cambiar a `validate`.

