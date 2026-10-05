# SpeedFast - Semana 8 | CRUD + JDBC + Swing

Proyecto mejorado a partir del trabajo de **Desarrollo Orientado a Objetos II**.

## Objetivo

Completar el ciclo funcional de SpeedFast integrando la lógica de negocio con persistencia en MySQL mediante JDBC, operaciones CRUD, validaciones, manejo de excepciones y una interfaz gráfica Swing.

La actividad exige DAO con `PreparedStatement` y `ResultSet`, CRUD completo, conexión con Swing, validaciones, manejo de errores y separación por capas.

## Estructura

```text
SpeedFastEntrega/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   └── speedfast/
        │       ├── Main.java
        │       ├── db/
        │       │   └── ConexionDB.java
        │       ├── model/
        │       │   ├── Repartidor.java
        │       │   ├── Pedido.java
        │       │   ├── Entrega.java
        │       │   ├── TipoPedido.java
        │       │   └── EstadoPedido.java
        │       ├── dao/
        │       │   ├── RepartidorDAO.java
        │       │   ├── PedidoDAO.java
        │       │   ├── EntregaDAO.java
        │       │   └── ClienteDAO.java
        │       ├── view/
        │       │   └── MainFrame.java
        │       └── concurrencia/
        │           └── ... trabajo conservado de Semana 5
        └── resources/
            └── schema.sql
```

## 1. Base de datos

En MySQL Workbench ejecuta:

`src/main/resources/schema.sql`

Esto crea `speedfast_db` y las tablas:

- `repartidores`
- `pedidos`
- `entregas`

Las relaciones de `entregas` mantienen las claves foráneas hacia pedidos y repartidores.

## 2. Conexión

La clase `ConexionDB` centraliza JDBC.

Por defecto utiliza:

- URL: `jdbc:mysql://localhost:3306/speedfast_db`
- usuario: `root`
- contraseña: vacía

Para usar otras credenciales, se pueden configurar las variables de entorno:

- `SPEEDFAST_DB_URL`
- `SPEEDFAST_DB_USER`
- `SPEEDFAST_DB_PASSWORD`

## 3. JDBC y DAO

Cada DAO tiene:

- `create()`
- `readAll()`
- `update()`
- `delete()`

Las consultas usan `PreparedStatement` y los resultados se recorren con `ResultSet`. Los recursos JDBC se cierran mediante `try-with-resources`.

## 4. Interfaz Swing

La aplicación tiene tres pestañas:

### Repartidores
Permite:
- registrar
- listar
- editar
- eliminar

### Pedidos
Permite:
- registrar
- listar
- editar
- eliminar
- filtrar por estado
- filtrar por tipo

Tipos:
- COMIDA
- ENCOMIENDA
- EXPRESS

Estados:
- PENDIENTE
- EN_REPARTO
- ENTREGADO

### Entregas
Permite:
- asociar pedido y repartidor mediante `JComboBox`
- registrar fecha y hora
- listar
- editar
- eliminar

Los combos muestran información legible pero conservan internamente el ID mediante los objetos `Pedido` y `Repartidor`.

## 5. Validaciones

Antes de acceder a MySQL se validan:

- campos obligatorios
- selección de pedido
- selección de repartidor
- formato de fecha
- formato de hora

Los errores SQL se muestran mediante `JOptionPane`.

## 6. Ejecución en IntelliJ IDEA

1. Abrir la carpeta `SpeedFastEntrega` como proyecto Maven.
2. Esperar la carga de dependencias.
3. Verificar que MySQL esté iniciado.
4. Ejecutar `schema.sql` en MySQL Workbench.
5. Revisar las credenciales de `ConexionDB` o definir variables de entorno.
6. Ejecutar `speedfast.Main`.

## 7. Dependencia JDBC

El proyecto usa Maven y la dependencia oficial:

`com.mysql:mysql-connector-j`

