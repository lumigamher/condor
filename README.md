# Project Condor - Aplicación de Chat con IA

Aplicación Spring Boot que integra la API de OpenAI para proporcionar una interfaz de chat inteligente con autenticación, mensajería en tiempo real y gestión de conversaciones.

## Características

- Autenticación de usuarios con JWT
- Chat en tiempo real usando WebSocket
- Integración con modelos GPT de OpenAI
- Gestión del historial de conversaciones
- Persistencia de mensajes
- Soporte CORS
- Seguridad WebSocket 

## Stack Tecnológico

- Java 11
- Spring Boot 2.7.18
- Spring Security
- Spring WebSocket
- Spring Data JPA
- MySQL 8
- Autenticación JWT
- Project Lombok
- Maven

## Prerrequisitos

- JDK 11 o superior
- Maven 3.6+
- MySQL 8.0+
- Clave API de OpenAI

## Variables de Entorno

```properties
MYSQL_ROOT_PASSWORD=tu_contraseña_mysql
JWT_SECRET=tu_clave_secreta_jwt
API_URL=https://api.openai.com/v1/chat/completions
OPENAIAPI_KEY=tu_clave_api_openai
PROMPT=tu_prompt_sistema_predeterminado
```

## Esquema de Base de Datos

### Tabla Users
```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone_number VARCHAR(255),
    address TEXT,
    birth_date DATE,
    gender VARCHAR(50)
);
```

### Tabla Messages
```sql
CREATE TABLE messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    content TEXT,
    from_ai BOOLEAN,
    conversation_id VARCHAR(255) NOT NULL,
    timestamp DATETIME NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
```

## Endpoints de la API

### Autenticación
- `POST /api/auth/signup`: Registro de nuevo usuario
- `POST /api/auth/login`: Inicio de sesión

### Operaciones de Chat
- `POST /api/chat/send`: Enviar mensaje
- `GET /api/chat/history`: Obtener historial de chat
- `POST /api/chat/conversation`: Iniciar nueva conversación
- `DELETE /api/chat/conversation/{conversationId}`: Limpiar conversación

## Endpoints WebSocket

- `ws://localhost:8081/ws`: Endpoint principal WebSocket
- `/app/chat.send`: Destino para enviar mensajes
- `/app/chat.connect`: Manejo de conexiones
- `/user/queue/messages`: Cola de mensajes específica del usuario
- `/user/queue/errors`: Notificaciones de error

## Seguridad

La aplicación implementa varias medidas de seguridad:

1. Autenticación basada en JWT
2. Seguridad WebSocket con verificación JWT
3. Encriptación de contraseñas usando BCrypt
4. Configuración CORS
5. Protección contra CSRF

## Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/tu-usuario/condor.git
```

2. Crear variables de entorno o actualizar `application.properties`

3. Construir el proyecto:
```bash
mvn clean install
```

4. Ejecutar la aplicación:
```bash
mvn spring-boot:run
```

## Pruebas

El proyecto incluye varias clases de prueba:
- `CondorApplicationTests`: Pruebas básicas de carga de contexto
- `OpenAIConnectionTest`: Pruebas de conectividad con API OpenAI
- `OpenAIConversationTester`: Pruebas interactivas de conversación

Ejecutar pruebas:
```bash
mvn test
```

## Ejemplo de Cliente WebSocket

```javascript
const socket = new SockJS('http://localhost:8081/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
    'Authorization': 'Bearer ' + jwtToken
}, frame => {
    console.log('Conectado: ' + frame);
    stompClient.subscribe('/user/queue/messages', message => {
        console.log(JSON.parse(message.body));
    });
});
```

## Opciones de Configuración

### Propiedades de la Aplicación
```properties
server.port=8081
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
logging.level.com.projectcondor.condor.service=DEBUG
spring.mvc.async.request-timeout=30000
```

### Configuración OpenAI
```properties
openai.model=chatgpt-4o-latest
openai.account=app-msj
```

## Manejo de Errores

La aplicación implementa un manejo integral de errores:
- Credenciales inválidas
- Problemas de conexión a base de datos
- Fallos de API OpenAI
- Problemas de conexión WebSocket
- Errores de validación JWT

## Contribuir

1. Fork del repositorio
2. Crear rama de característica
3. Commit de cambios
4. Push a la rama
5. Crear Pull Request

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - ver el archivo LICENSE para más detalles.