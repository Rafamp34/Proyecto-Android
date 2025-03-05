# UMusicApp

UMusicApp es una aplicación en "proceso" de streaming de música para Android, desarrollada con Kotlin. La aplicación permite a los usuarios explorar canciones, crear y gestionar listas de reproducción, y personalizar su experiencia musical.

## Características

- **Autenticación de Usuario**: Funcionalidad de registro e inicio de sesión para una experiencia personalizada
- **Feed Principal**: Descubre canciones y listas de reproducción recomendadas
- **Biblioteca de Música**: Navega a través de canciones y listas de reproducción
- **Gestión de Playlists**: Crea, visualiza, edita y elimina listas de reproducción
- **Añadir/Eliminar Canciones**: Gestiona canciones dentro de tus listas de reproducción
- **Perfil de Usuario**: Visualiza y edita la información del usuario
- **Integración de Ubicación**: Función de ubicación usando Google Maps
- **Subida de Imágenes**: Añade imágenes personalizadas a las listas de reproducción usando la cámara del dispositivo o la galería
- **Soporte Offline**: Almacenamiento local para uso sin conexión

## Arquitectura

UMusicApp sigue el patrón de arquitectura MVVM (Modelo-Vista-ViewModel) y los principios de Clean Architecture:

- **Capa de Datos**: 
  - Fuentes de datos remotas (interacciones con API)
  - Fuentes de datos locales (Base de datos Room y DataStore)
  - Implementaciones de repositorios

- **Capa de Dominio**: 
  - Modelos
  - Interfaces de repositorios
  - Casos de uso

- **Capa de Presentación**:
  - Activities y Fragments (Vista)
  - ViewModels
  - Estados de UI

## Tecnologías

- **Kotlin**: Lenguaje moderno y conciso para desarrollo Android
- **Coroutines & Flow**: Para programación asíncrona
- **Hilt**: Inyección de dependencias
- **Retrofit**: Comunicación con API
- **Room**: Abstracción de base de datos SQLite
- **DataStore**: Solución de almacenamiento de datos
- **Navigation Component**: Para manejar la navegación
- **Coil**: Biblioteca para cargar imágenes
- **Google Maps**: Para funciones de ubicación
- **Material Design 3**: Para componentes UI modernos

## Estructura del Proyecto

El código está organizado en varios paquetes clave:

- `authentication`: Funcionalidad de autenticación de usuario
- `main`: Pantallas principales de la app (Inicio, Búsqueda, Perfil)
- `data`: Manejo de datos (fuentes remotas y locales)
- `di`: Módulos de inyección de dependencias
- `common`: Utilidades compartidas y extensiones
