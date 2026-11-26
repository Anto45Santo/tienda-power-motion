# Manual de Usuario del Sistema · Power Motion

**Tienda de ropa, calzado y accesorios deportivos · Aplicación de escritorio en JavaFX**

Versión 1.0 · Noviembre de 2026

---

## Contenido

**[Parte I · Información general](#parte-i--información-general)**

- [1. Introducción](#1-introducción)
  - [1.1 Tipos de usuario](#11-tipos-de-usuario)
  - [1.2 Requisitos](#12-requisitos)
  - [1.3 Cómo iniciar el programa](#13-cómo-iniciar-el-programa)
  - [1.4 Usuarios de prueba](#14-usuarios-de-prueba)
  - [1.5 Elementos comunes de la interfaz](#15-elementos-comunes-de-la-interfaz)
- [2. Mapa del sistema](#2-mapa-del-sistema)
  - [2.1 Vida de un pedido](#21-vida-de-un-pedido)
- [3. Bienvenida, crear cuenta e iniciar sesión](#3-bienvenida-crear-cuenta-e-iniciar-sesión)
  - [3.1 Pantalla de bienvenida](#31-pantalla-de-bienvenida)
  - [3.2 Iniciar sesión](#32-iniciar-sesión)
  - [3.3 Crear una cuenta](#33-crear-una-cuenta)

**[Parte II · Manual del cliente](#parte-ii--manual-del-cliente)**

- [4. Barra superior y catálogo](#4-barra-superior-y-catálogo)
  - [4.1 Barra superior](#41-barra-superior)
  - [4.2 Catálogo](#42-catálogo)
  - [4.3 Panel de filtros](#43-panel-de-filtros)
  - [4.4 Tarjeta de producto](#44-tarjeta-de-producto)
- [5. Detalle de un producto](#5-detalle-de-un-producto)
  - [5.1 Fotos](#51-fotos)
  - [5.2 Comprar el producto](#52-comprar-el-producto)
  - [5.3 Lista de deseos](#53-lista-de-deseos)
  - [5.4 Guía de tallas](#54-guía-de-tallas)
  - [5.5 Opiniones de clientes](#55-opiniones-de-clientes)
- [6. Me gusta, carrito y pago](#6-me-gusta-carrito-y-pago)
  - [6.1 Me gusta](#61-me-gusta)
  - [6.2 Carrito](#62-carrito)
  - [6.3 Pago y envío del pedido](#63-pago-y-envío-del-pedido)
- [7. Mis compras y Mi perfil](#7-mis-compras-y-mi-perfil)
  - [7.1 Mis compras](#71-mis-compras)
  - [7.2 Mi perfil](#72-mi-perfil)
- [8. Solución de problemas del cliente](#8-solución-de-problemas-del-cliente)

**[Parte III · Manual del administrador](#parte-iii--manual-del-administrador)**

- [9. Ingreso y gestión de pedidos](#9-ingreso-y-gestión-de-pedidos)
  - [9.1 Ingreso y menú lateral](#91-ingreso-y-menú-lateral)
  - [9.2 Pedidos](#92-pedidos)
- [10. Productos y stock](#10-productos-y-stock)
  - [10.1 Lista de productos](#101-lista-de-productos)
  - [10.2 Crear un producto nuevo](#102-crear-un-producto-nuevo)
  - [10.3 Editar un producto o agregar unidades](#103-editar-un-producto-o-agregar-unidades)
- [11. Imágenes, reseñas y usuarios](#11-imágenes-reseñas-y-usuarios)
  - [11.1 Imágenes de la bienvenida](#111-imágenes-de-la-bienvenida)
  - [11.2 Reseñas](#112-reseñas)
  - [11.3 Usuarios](#113-usuarios)
- [12. Dónde se guarda la información](#12-dónde-se-guarda-la-información)
- [13. Solución de problemas del administrador](#13-solución-de-problemas-del-administrador)

---

# Parte I · Información general

Esta parte es para todos los usuarios: qué es el sistema, cómo se instala y se inicia, y cómo se entra con una cuenta.

## 1. Introducción

Power Motion es un programa de escritorio para vender ropa, calzado y accesorios deportivos. Este manual explica,
paso a paso, cómo usar cada pantalla del sistema, tanto si eres **cliente** como **administrador** de la tienda.

### 1.1 Tipos de usuario

- **Cliente**: Ver el catálogo, buscar y filtrar productos, marcar "me gusta", agregar al carrito, pagar por transferencia subiendo el comprobante, seguir sus pedidos, opinar sobre lo que compró y editar su perfil.
- **Administrador**: Aprobar o rechazar pagos, avanzar los pedidos hasta la entrega, crear y editar productos con su stock por talla y color, cambiar las fotos de la bienvenida, moderar opiniones y gestionar usuarios.

### 1.2 Requisitos

- Computador con Windows, macOS o Linux.
- **JDK 21** (Java) instalado.
- **NetBeans** (o cualquier editor con Maven) para abrir y ejecutar el proyecto.
- Conexión a internet **solo la primera vez**, para que Maven descargue JavaFX.

### 1.3 Cómo iniciar el programa

1. Abre NetBeans y elige **File › Open Project**.
2. Selecciona la carpeta del proyecto `Proyecto_Tienda` y pulsa **Open Project**.
3. Haz clic derecho sobre el proyecto y elige **Run** (o pulsa **F6**).
4. Se abre la ventana **Power Motion** en la pantalla de bienvenida.

> La primera vez que se ejecuta, el programa crea solo la carpeta `datos/` con productos, usuarios y pedidos de
> ejemplo, para que puedas probarlo de inmediato.

### 1.4 Usuarios de prueba

- **Cliente**: correo `andres.gomez@correo.com`, contraseña `12345678`.
- **Administrador**: correo `admin@powermotion.co`, contraseña `admin123`.

Estos datos también aparecen en letra pequeña debajo del formulario de inicio de sesión.

### 1.5 Elementos comunes de la interfaz

- **Avisos:** después de una acción (agregar al carrito, guardar, aprobar…) aparece un mensaje corto que se oculta solo
  a los pocos segundos.
- **Mensajes en rojo:** indican un dato faltante o incorrecto en un formulario. Corrige el campo y vuelve a intentarlo.
- **Ventanas de confirmación:** antes de borrar o desactivar algo, el sistema pregunta. Pulsa **Sí** para
  continuar o **No** para dejarlo como estaba.
- **Enlaces azules con flecha** (por ejemplo **‹ Volver al catálogo**): regresan a la pantalla anterior.

## 2. Mapa del sistema

Todo empieza en la bienvenida y pasa por el inicio de sesión. El sistema reconoce el tipo de cuenta: el **cliente**
entra a la tienda y el **administrador** entra a su panel. El pedido que envía el cliente llega a la pantalla
**Pedidos** del administrador.

**Recorrido de las pantallas:**

- **Bienvenida** → botón **Iniciar sesión** o botón **Registrarse**.
- **Iniciar sesión** → si es cliente, entra al **Catálogo**; si es administrador, entra a **Pedidos**.
  El enlace **Crear cuenta nueva** lleva a **Crear cuenta**.
- **Crear cuenta** → al crearla, entra directamente al **Catálogo**.
- **Compra del cliente:** Catálogo → Detalle de producto → Carrito → Pago → Mis compras.
- **Barra superior del cliente** (disponible en todas sus pantallas): Catálogo, Categorías, buscador, Me gusta,
  Carrito y el menú del usuario (Mi perfil, Mis compras, Cerrar sesión).
- **Menú lateral del administrador:** Pedidos, Productos (→ Formulario de producto), Imágenes, Reseñas, Usuarios y
  Cerrar sesión.
- El pedido que el cliente envía desde **Pago** aparece en **Pedidos** del administrador.

### 2.1 Vida de un pedido

- **Por aprobar**: El cliente envió el pedido con su comprobante; falta revisar el pago. Lo cambia: Cliente (al enviar el pedido).
- **Aprobado**: El pago fue verificado y se descontó el stock. Lo cambia: Administrador.
- **Rechazado**: El pago no fue válido; el cliente ve el motivo y puede subir otro comprobante. Lo cambia: Administrador.
- **Enviado**: El pedido salió hacia la dirección de entrega. Lo cambia: Administrador.
- **Entregado**: El cliente recibió el pedido; ya puede opinar sobre los productos. Lo cambia: Administrador.

## 3. Bienvenida, crear cuenta e iniciar sesión

### 3.1 Pantalla de bienvenida

Es la primera pantalla al abrir el programa. A la derecha rotan las fotos de la tienda cada 5 segundos; a la izquierda
están el mensaje de la marca, tres beneficios (envío a todo el país, cambios hasta 30 días y productos 100 % originales)
y dos botones:

- **Iniciar sesión:** abre el formulario para entrar con una cuenta existente.
- **Registrarse:** abre el formulario para crear una cuenta nueva de cliente.

### 3.2 Iniciar sesión

1. En la bienvenida pulsa **Iniciar sesión**.
2. Escribe tu **Correo electrónico** y tu **Contraseña**. El ícono del ojo, a la derecha de la contraseña, la muestra
   u oculta.
3. Pulsa **Ingresar** (o la tecla **Enter**).

Un **cliente** entra al catálogo de la tienda y un **administrador** entra al panel de pedidos. Para volver a la
bienvenida pulsa **← Volver al inicio**.

Mensajes que pueden aparecer y qué hacer:

- «Escribe tu correo y tu contraseña.» Llena los dos campos.
- «Correo o contraseña incorrectos.» Revisa el correo y la contraseña (distingue mayúsculas y minúsculas). Si la olvidaste, el administrador puede restablecerla (sección 11.3).

### 3.3 Crear una cuenta

1. En la bienvenida pulsa **Registrarse**, o en el inicio de sesión pulsa **Crear cuenta nueva**.
2. Completa los campos. Los marcados con **\*** son obligatorios:
   - **Nombre\*** y **Apellido\***.
   - **Correo electrónico\***: con él entrarás al sistema.
   - **Teléfono** (opcional).
   - **Contraseña\*** (mínimo 8 caracteres) y **Confirmar contraseña\***.
3. Marca la casilla **Soy mayor de 18 años y acepto los Términos y condiciones y la Política de privacidad**.
4. Pulsa **Crear cuenta**. El sistema guarda la cuenta y entra directamente al catálogo.

Si ya tienes cuenta, pulsa **¿Ya tienes cuenta? Inicia sesión**.

Mensajes que pueden aparecer y qué hacer:

- «Completa los campos marcados con \*.» Falta el nombre, el apellido o el correo.
- «El correo no es válido.» Escribe un correo con el formato `nombre@dominio.com`.
- «Ya existe una cuenta con ese correo.» Ese correo ya está registrado: inicia sesión o usa otro.
- «La contraseña debe tener mínimo 8 caracteres.» Escribe una contraseña más larga.
- «Las contraseñas no coinciden.» Escribe la misma contraseña en los dos campos.
- «Debes aceptar los términos para continuar.» Marca la casilla de términos.

---

# Parte II · Manual del cliente

Esta parte explica todo lo que puede hacer un cliente después de iniciar sesión: buscar productos, comprarlos, seguir sus pedidos y administrar su cuenta.

## 4. Barra superior y catálogo

### 4.1 Barra superior

La barra azul oscura de arriba está en todas las pantallas del cliente:

- **Logo Power Motion**: Vuelve al catálogo.
- **Categorías**: Menú para ir directo a un grupo de productos: **Hombre** o **Mujer** (Calzado › Running, Training, Casual; Ropa › Camisetas, Chaquetas, Pantalonetas, Licras, Tops, Buzos; Accesorios; Ver todo), **Accesorios** (Maletines, Termos, Training, Recuperación), **Marcas** y **Ver todo**.
- **Buscador**: Escribe un nombre, marca o deporte (por ejemplo "running" o "nike") y pulsa la lupa o **Enter**.
- **Corazón**: Abre **Me gusta**. El número azul indica cuántos productos tienes guardados.
- **Bolsa**: Abre el **Carrito**. El número azul indica cuántas unidades tienes.
- **Tu nombre**: Menú del usuario: **Mi perfil**, **Mis compras**, **Me gusta**, **Carrito** y **Cerrar sesión**.

### 4.2 Catálogo

Es la pantalla principal del cliente. Muestra los productos en tarjetas, 10 por página.

- **Filtros rápidos** (arriba): **Todo**, **Calzado**, **Ropa**, **Accesorios**, **Hombre**, **Mujer** y **Ofertas**
  (solo productos con descuento). Se elige uno a la vez.
- **Total de productos:** a la derecha de los filtros, por ejemplo "18 productos".
- **Ordenar:** lista desplegable con **Destacados**, **Más recientes**, **Menor precio**, **Mayor precio** y
  **Mayor descuento**.
- **Páginas:** abajo; usa los números o las flechas para pasar de página.
- **Filtro activo:** si llegaste desde el menú Categorías o desde el buscador, aparece arriba un botón con el filtro
  (por ejemplo *Búsqueda: "running"*). Pulsa la **✕** para quitarlo y ver todo de nuevo.

### 4.3 Panel de filtros

1. Pulsa **Filtros** (arriba a la derecha). Se abre un panel lateral.
2. Marca las opciones que quieras en cada grupo: **Marca**, **Tipo**, **Talla textil**, **Talla de calzado (US)** y
   **Color**. Puedes marcar varias en el mismo grupo: basta con que el producto cumpla una de ellas.
3. En **Precio** escribe un valor mínimo (**Desde $**) y/o máximo (**Hasta $**).
4. El botón de abajo muestra cuántos productos cumplen, por ejemplo **Ver 6 resultados**. Púlsalo para aplicar.
5. **Limpiar** desmarca todo. La **✕** cierra el panel sin aplicar.

### 4.4 Tarjeta de producto

Cada tarjeta muestra la foto, el nombre, el precio actual y, si hay descuento, el precio anterior tachado.

- **Etiqueta azul** arriba a la izquierda: porcentaje de descuento (por ejemplo **-30%**) o **NUEVO**.
- **Corazón** arriba a la derecha: guarda o quita el producto de **Me gusta**. Lleno = guardado.
- **Comprar:** si el producto es de talla única y tiene un solo color, se agrega directo al carrito; si hay que elegir
  talla o color, abre el detalle del producto. Si no quedan unidades, el botón dice **Agotado**.
- **Clic en la tarjeta:** abre el detalle del producto.

## 5. Detalle de un producto

### 5.1 Fotos

- La foto grande muestra la imagen elegida; las **flechas ‹ ›** pasan a la anterior o a la siguiente (al llegar a la
  última vuelve a la primera).
- Debajo están las **miniaturas**: haz clic en una para verla en grande.

### 5.2 Comprar el producto

1. Elige el **Color**. El texto "Color: Azul" muestra el elegido.
2. Elige la **Talla**. Solo aparecen las tallas que maneja el producto; las que no tienen unidades en ese color se ven
   desactivadas. Los accesorios muestran **Talla: Única** y no piden talla.
3. Elige la **Cantidad**. El máximo es el número de unidades disponibles, que se indica al lado (por ejemplo
   "6 disponibles en talla 9").
4. Pulsa **Agregar al carrito**. Aparece el aviso *Agregado al carrito*.

Mensajes que pueden aparecer y qué hacer:

- «Elige una talla.» Selecciona una talla antes de agregar.
- «Agotado en este color.» Elige otro color o talla.
- «Ya tienes en el carrito todas las unidades disponibles de esta talla.» No puedes agregar más de lo que hay en stock.

### 5.3 Lista de deseos

El botón **Añadir a lista de deseos** guarda el producto en **Me gusta** (es lo mismo que el corazón de la tarjeta).
Cuando ya está guardado dice **En tu lista de deseos**; púlsalo otra vez para quitarlo.

### 5.4 Guía de tallas

El enlace **Guía de tallas** (junto a la talla) abre una tabla de equivalencias: para calzado, talla US a centímetros;
para ropa, talla a centímetros de pecho. No aparece en accesorios.

### 5.5 Opiniones de clientes

- Abajo se ven el **promedio de estrellas**, el número de opiniones y las **dos opiniones más recientes**.
- **Ver las N opiniones** abre todas.
- **Escribir opinión** solo está activo si **compraste y recibiste** ese producto (pedido en estado *Entregado*).
  Elige de 1 a 5 estrellas, escribe tu comentario y pulsa **Publicar**.

## 6. Me gusta, carrito y pago

### 6.1 Me gusta

Muestra los productos que guardaste con el corazón, el último guardado primero. Funciona igual que el catálogo: puedes
abrir el detalle, quitar el corazón o pulsar **Comprar**. Tus "me gusta" quedan guardados aunque cierres el programa.

### 6.2 Carrito

Cada producto del carrito muestra su foto, talla, color, cantidad y precio. Para cada uno puedes:

- Cambiar la **cantidad** con las flechas (el máximo es el stock disponible).
- **Eliminar:** lo quita del carrito.
- **Mover a Me gusta:** lo quita del carrito y lo guarda en Me gusta.

El **Resumen de compra** de la derecha calcula:

- **Productos**: Suma a precio normal (sin descuentos)
- **Descuentos**: Lo que ahorras por los productos en oferta
- **Envío**: **Gratis** desde $ 400.000; si no, $ 15.000
- **Total**: Productos − Descuentos + Envío

Pulsa **Continuar compra** para pagar. El carrito se conserva aunque cierres el programa.

### 6.3 Pago y envío del pedido

El pago se hace por **transferencia** (Nequi, Daviplata, Bancolombia o PSE) al número que aparece en el recuadro
oscuro, y luego se sube la foto del comprobante.

1. Revisa el **Total a transferir** y los productos del resumen.
2. Haz la transferencia desde tu aplicación bancaria al número receptor. Puedes pulsar **Copiar número** o escanear el
   código QR.
3. Completa los **Datos de envío**: **Nombre completo**, **Teléfono**, **Dirección de entrega**, **Ciudad** y
   **Departamento** (todos obligatorios). El nombre, el teléfono y la dirección se llenan con los datos de tu perfil;
   puedes cambiarlos.
4. En **Comprobante de pago** pulsa **Examinar…** y elige la captura o foto de la transferencia (JPG o PNG, máximo
   5 MB).
5. Pulsa **Enviar pedido**.

El sistema crea el pedido con un número (por ejemplo **#PM-10492**) en estado **Por aprobar**, vacía el carrito y te
lleva a **Mis compras**. El administrador revisará el comprobante.

Mensajes que pueden aparecer y qué hacer:

- «Completa los datos de envío.» Llena todos los campos marcados con asterisco.
- «Adjunta la imagen del comprobante de pago.» Elige la imagen con **Examinar…**.
- «La imagen supera 5 MB.» Usa una imagen más liviana (por ejemplo una captura de pantalla).
- «Ya no hay suficientes unidades de … Revisa tu carrito.» Otro cliente compró esas unidades; ajusta la cantidad en el carrito.

## 7. Mis compras y Mi perfil

### 7.1 Mis compras

Se abre desde el menú del usuario. Muestra tus pedidos del más reciente al más antiguo, con fotos de los productos,
número, fecha, cantidad, estado y total.

- **Filtros:** **Todas**, **En proceso** (por aprobar, aprobado o enviado), **Entregadas** y **Rechazadas**. Cada uno
  muestra cuántos pedidos tiene.
- **Ver detalle:** abre el pedido con sus productos, cantidades, precios y la dirección de envío.
- **Calificar** (solo en pedidos entregados): abre el producto para escribir tu opinión.
- **Pedido rechazado:** debajo del estado aparece el **motivo** que escribió el administrador (por ejemplo
  "Comprobante ilegible"). Pulsa **Subir otro comprobante**, elige la nueva imagen y el pedido vuelve a
  **Por aprobar**.

### 7.2 Mi perfil

- **Tarjeta de la izquierda:** tu nombre, correo, fecha de registro y cuántas compras y "me gusta" tienes.
- **Mis datos:** edita **Nombre**, **Apellido**, **Correo electrónico**, **Teléfono** y **Dirección principal**, y
  pulsa **Guardar cambios**. La dirección se usa para llenar el formulario de pago.
- **Cambiar contraseña:** escribe la contraseña actual, la nueva (mínimo 8 caracteres) y repítela.
- **Cerrar sesión:** vuelve al inicio de sesión. Tu carrito y tus "me gusta" quedan guardados.
- **Eliminar mi cuenta:** borra tu cuenta después de confirmar. No se permite si tienes compras en proceso o compras
  registradas (en ese caso pide al administrador que la dé de baja).

Mensajes que pueden aparecer y qué hacer:

- «Nombre, apellido y correo son obligatorios.» Llena esos tres campos.
- «Ese correo ya lo usa otra cuenta.» Usa otro correo.
- «La contraseña actual no es correcta.» Escribe tu contraseña actual tal como la creaste.
- «Revisa la nueva contraseña.» La nueva debe tener 8 caracteres o más y coincidir en los dos campos.

## 8. Solución de problemas del cliente

- **Mensaje "Correo o contraseña incorrectos"**. Causa: Datos mal escritos. Solución: Revisa mayúsculas; el administrador puede restablecer la contraseña.
- **Un producto que vi antes ya no aparece en la tienda**. Causa: el administrador lo desactivó o se agotó. Solución: búscalo más adelante o pregunta en la tienda cuándo vuelve a haber unidades.
- **No puedo escribir una opinión**. Causa: El pedido con ese producto aún no está **Entregado**. Solución: Espera a que el administrador lo marque como entregado.
- **No puedo agregar más unidades al carrito**. Causa: Ya tienes todas las unidades disponibles. Solución: Elige otra talla o color.
- **No puedo eliminar mi cuenta**. Causa: Tienes compras registradas o en proceso. Solución: Pide al administrador que la dé de baja.
- **Mensaje "No se pudo copiar el comprobante"**. Causa: El archivo está abierto en otro programa o no hay permisos. Solución: Cierra el archivo en el otro programa y vuelve a intentar.

---
