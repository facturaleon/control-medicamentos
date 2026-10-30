# Control mensual de medicamentos

Proyecto de prueba técnica con Spring Boot + Angular + H2 (base de datos local en archivo).
No necesita instalar MySQL ni Docker.

## Requisitos
- Java 17 o superior
- Maven 3.8+
- Node.js 18.13+ y npm

## 1. Ejecutar backend
En una terminal:
```bash
cd backend
mvn spring-boot:run
```
API: http://localhost:8080
La base H2 se guarda en `backend/data/medicamentos.mv.db` y conserva los datos entre reinicios.

## 2. Ejecutar frontend
En otra terminal:
```bash
cd frontend
npm install
npm start
```
Abrir http://localhost:4200

## Funcionalidad
- Listado de reportes en estado REGISTRADO.
- Crear reportes por UNICODIGO, mes y año desde julio de 2025 hasta el mes actual.
- Agregar medicamentos desde un catálogo de ejemplo precargado.
- Validar cantidades no negativas, medicamentos repetidos, reportes duplicados y stock disponible.
- Eliminar reportes y registrar de nuevo el mismo periodo.
- El stock se calcula sumando los ingresos y egresos anteriores del mismo medicamento y UNICODIGO. Julio de 2025 parte de cero.

## API
- `GET /api/reportes`
- `POST /api/reportes`
- `DELETE /api/reportes/{id}`
- `GET /api/medicamentos`
- `GET /api/reportes/stock?unicodigo=...&medicamentoId=...&mes=...&anio=...`

## Notas
- Se usa H2 para que sea fácil ejecutar la prueba sin configurar un servidor de base de datos.
- El catálogo de medicamentos se carga automáticamente con datos de ejemplo. Si la institución entrega una base de datos real, se debe adaptar `Medicamento` y la configuración.
- Para una entrega formal, agrega pruebas automatizadas y configura manejo específico de errores de concurrencia.
