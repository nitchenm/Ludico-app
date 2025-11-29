*LUDICO-APP*

Ludico es tu aplicacion para organizar tus juegos de mesas favoritos , desde partidas casuales hasta torneos competitivos de tcg del mas alto nivel
Ludico es para todos.

👥  Integrantes

Sebastian Mondaca

Nitchen Martinez 


✨  Funcionalidades
La aplicación ofrece las siguientes características principales:

Autenticación de Usuario: Permite a los usuarios registrar una nueva cuenta y iniciar sesión (Login) para acceder a las funcionalidades de la aplicación, asegurando un entorno personalizado y seguro

Organización de Eventos: Los usuarios pueden crear y configurar nuevos eventos, definiendo nombre, fecha, hora, ubicación y detalles adicionales.

Integración en Eventos: Los usuarios pueden buscar y unirse a eventos existentes, ya sea mediante un código de invitación o a través de la lista de eventos públicos disponibles.

Optimizacion UX/UI: Los usuarios interactuan con tecnologias de diseño como Material3 creando un enterno mas enriquecedor con funcionalidades y filtros que aumentan su experiencia en la App

🌐 4. Endpoints Utilizados
El proyecto consume microservicio propio/desarrollado por el equipo.

Propósito: Obtener datos de [describir qué datos se obtienen].

Endpoint(s) Ejemplo:

POST

// Login
http://localhost:8080/auth/login?email=test@ludico.com&password=Test123456

// Events
http://localhost:8080/api/v1/events

Nombre del Servicio: Ludico-app-backend

Propósito: Gestionar la lógica de negocio y proveer datos específicos de la aplicación.

*Podras encontrar nuestro backend en el repositorio https://github.com/nitchenm/Ludico-app-backend donde tambien se mostraran las instrucciones*

🛠️ 5. Pasos para Ejecutar
Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local:

A. Prerrequisitos
Java Development Kit (JDK): Versión 21

Android Studio: Última versión estable.

B. Ejecución de la Aplicación Móvil
Clonar el Repositorio:

git clone https://github.com/nitchenm/Ludico-app
cd Ludico-app
Abrir en Android Studio

Configurar Variables de Entorno : Asegúrate de que la URL base del microservicio este correctamente configuradas en el archivo local.properties o donde corresponda.

Sincronizar Proyecto: Espera a que Gradle sincronice todas las dependencias.

Ejecutar: Selecciona un emulador propio de android studio o conecta un dispositivo físico y presiona el botón Run 

🔒 6. Archivos de Firma



