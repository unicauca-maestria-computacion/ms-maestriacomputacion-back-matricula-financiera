# Reglas de negocio: cobros de matricula, becas y descuentos

Este documento resume las reglas de negocio implementadas en el backend para la gestion de matricula financiera y los calculos presupuestarios asociados.

## Modulos involucrados

- `ms-maestriacomputacion-back-matricula-financiera`: consulta estudiantes matriculados, periodos, grupo, estado de pago, valor en SMLV, becas y descuentos.
- `ms-maestriacomputacion-back-info-presupuestaria`: calcula valores monetarios de matricula, descuentos, ingresos netos y reportes financieros.

## Carga de estudiantes matriculados

### Estudiantes incluidos

Solo se incluyen estudiantes que tengan matricula academica activa en el periodo consultado.

La consulta base exige:

- Que el estudiante este asociado a una matricula academica.
- Que el curso pertenezca al periodo seleccionado.
- Que la matricula academica tenga `m.estado = 1`.

Referencia de codigo:

- `EstudianteJpaRepository.findByPeriodoId(...)`

### Validacion del periodo

Cuando se consulta la lista de estudiantes por periodo, el backend valida que:

- El periodo recibido no sea nulo.
- Exista un periodo academico con el `tagPeriodo` y anio consultados.

Si el periodo no existe, se genera error de entidad no encontrada.

Referencia de codigo:

- `ManageEnrolledStudentsUseCaseImpl.getStudentsByPeriod(...)`

## Semestre financiero y academico

### Calculo del semestre financiero

El semestre financiero se calcula con base en el periodo de ingreso del estudiante y el periodo consultado.

Formula:

```text
semestreFinanciero = (anioConsulta - anioIngreso) * 2
                   + (tagPeriodoConsulta - tagPeriodoIngreso)
                   + 1
```

Ejemplo:

```text
periodoIngreso = 2023-1
periodoConsulta = 2026-1

semestreFinanciero = (2026 - 2023) * 2 + (1 - 1) + 1
semestreFinanciero = 7
```

Si el resultado es menor que cero, se fuerza a cero.

Referencia de codigo:

- `ManageEnrolledStudentsUseCaseImpl.calculateSemester(...)`

### Calculo del semestre academico

Cuando el semestre financiero calculado es mayor que cero:

```text
semestreAcademico = min(semestreFinanciero, 4)
```

Esto significa que el semestre academico no supera el valor 4, aunque financieramente el estudiante este en semestres superiores.

Referencia de codigo:

- `ManageEnrolledStudentsUseCaseImpl.getStudentsByPeriod(...)`
- `ManageEnrolledStudentsUseCaseImpl.getStudentByCode(...)`

## Valor en SMLV

El valor en SMLV se calcula con el semestre financiero y las materias matriculadas.

Reglas:

```text
Si semestreFinanciero <= 4:
    valorEnSMLV = 6

Si semestreFinanciero >= 9:
    valorEnSMLV = 1

Si semestreFinanciero esta entre 5 y 8:
    Si el estudiante solo cursa Trabajo de Grado 2 o Trabajo de Grado II:
        valorEnSMLV = 1
    En otro caso:
        valorEnSMLV = 6
```

Si el estudiante no tiene materias cargadas para el periodo, se asume:

```text
valorEnSMLV = 6
```

Referencia de codigo:

- `ManageEnrolledStudentsUseCaseImpl.calculateSmlv(...)`
- `ManageEnrolledStudentsUseCaseImpl.isTg2(...)`

## Estado de pago

El estado de pago se obtiene desde la tabla `matricula_financiera`, campo `esta_pago`.

La busqueda se hace por:

- Estudiante.
- `tag_periodo`.
- Anio del periodo academico.

Valores posibles:

- `true`: el estudiante esta pago.
- `false`: el estudiante no esta pago.
- `null`: no hay estado de pago definido.

Referencia de codigo:

- `BdCompartidaRepository.findEstadoPagoPorEstudianteYPeriodo(...)`

## Auto-registro de matricula financiera

Al enriquecer la informacion de un estudiante para un periodo, el backend asegura que exista un registro en `matricula_financiera`.

Si no existe, crea un registro con:

- `estudiante_id`
- `periodo_id`
- ultimo grupo conocido del estudiante
- `esta_pago = null`

Si el registro ya existe, aplica `ON DUPLICATE KEY UPDATE`.

Regla importante:

```sql
esta_pago = IF(esta_pago, TRUE, VALUES(esta_pago))
```

Esto significa:

- Si `esta_pago` ya era `TRUE`, se conserva como `TRUE`.
- Si no era `TRUE`, se usa el nuevo valor recibido.

Referencia de codigo:

- `ManageEnrolledStudentsUseCaseImpl.enrich(...)`
- `BdCompartidaRepository.registrarMatriculaFinanciera(...)`

## Grupo de investigacion

El grupo del estudiante se obtiene desde `matricula_financiera.grupo_id`, cruzando con la tabla `grupo`.

La busqueda se hace por estudiante, periodo y anio.

Si se crea un nuevo registro financiero, se intenta heredar el ultimo grupo conocido del estudiante.

Referencia de codigo:

- `BdCompartidaRepository.findGrupoNombrePorEstudianteYPeriodo(...)`
- `BdCompartidaRepository.findUltimoGrupoIdByEstudiante(...)`

## Becas y descuentos

### Fuente de datos

Las becas y descuentos se consultan cruzando:

- `solicitudes`
- `solicitud_beca_descuento`
- `solicitudes_en_concejo`

Campos obtenidos:

- `tipo`
- `porcentaje`
- `resolucion`
- `estado`
- `avalado_concejo`

Referencia de codigo:

- `BdCompartidaRepository.findBecasDescuentosByEstudianteAndPeriodo(...)`

### Vigencia de la beca o descuento

Una beca o descuento aplica al periodo si la fecha de inicio del periodo esta dentro del rango de fechas de la solicitud en concejo.

Condicion SQL:

```sql
? BETWEEN sec.fecha_inicio AND sec.fecha_fin
```

El parametro `?` corresponde a `periodo.fechaInicio`.

## Descuento por votacion

El estudiante aplica a descuento por votacion si tiene una solicitud de certificado de votacion aprobada.

Condiciones:

- El tipo de solicitud debe tener codigo `CER_VOTO`.
- El estado de la solicitud debe ser `APROBADA`.

Referencia de codigo:

- `BdCompartidaRepository.tieneSolicitudCerVotoAprobada(...)`

## Calculo monetario en informacion presupuestaria

Los valores monetarios se calculan en `ms-maestriacomputacion-back-info-presupuestaria`, dentro del servicio:

- `FinancialCalculationService`

### Regla general de contabilizacion

Solo se contabilizan estudiantes pagados.

Un estudiante se considera pagado si:

```text
estaPago = true
o
estadoMatriculaFinanciera = true
```

Si el estudiante no esta pagado:

- `valorDescuentoVoto = 0`
- `valorDescuentoBeca = 0`
- `valorDescuentoEgresado = 0`
- `totalDescuentos = 0`
- `valorNeto = 0`
- `totalNetoConDerechos = 0`

Referencia de codigo:

- `FinancialCalculationService.calcular(...)`

### Valor de matricula

Formula:

```text
valorMatricula = valorSMLV * valorEnSMLV
```

Donde:

- `valorSMLV` viene de la configuracion financiera del periodo.
- `valorEnSMLV` viene del modulo de matricula financiera.

### Derechos complementarios

Los derechos complementarios se calculan como:

```text
derechosComplementarios = biblioteca + recursosComputacionales
```

Estos valores:

- Se suman al neto por estudiante.
- No hacen parte de la base de descuentos.

### Descuento por votacion

Si el estudiante aplica a votacion:

```text
descuentoVoto = valorMatricula * porcentajeVotacion
```

Si no aplica:

```text
descuentoVoto = 0
```

El porcentaje por defecto es:

```text
porcentajeVotacion = 0.10
```

salvo que exista otro valor configurado.

### Base para otros descuentos

Luego de aplicar votacion, la base para beca o egresado es:

```text
baseParaOtros = valorMatricula - descuentoVoto
```

### Beca real

El porcentaje de beca se resuelve de forma distinta segun el tipo de reporte.

Para reporte final, periodo cerrado o periodo vencido:

- Se ignora el porcentaje manual de la proyeccion.
- Se usan solo becas/descuentos con `avaladoConcejo = "SI"`.
- Si el porcentaje viene mayor a 1, se interpreta como porcentaje entero y se divide entre 100.

Ejemplo:

```text
25.0 -> 0.2500
0.25 -> 0.2500
```

Para periodo activo que no es reporte final:

- Se permite usar el valor manual de la proyeccion.

Referencia de codigo:

- `FinancialCalculationService.resolverPorcentajeBeca(...)`

### Descuento de egresado Unicauca

El descuento de egresado aplica solo si se cumplen todas estas condiciones:

- `aplicaEgresado = true`
- El estudiante tiene descuento por votacion.
- `semestreFinanciero <= 4`

El porcentaje por defecto es:

```text
porcentajeEgresado = 0.05
```

Si el estudiante es egresado pero no cumple las condiciones, el porcentaje de egresado se fuerza a cero.

### Exclusividad entre beca y egresado

La beca y el descuento de egresado no se acumulan entre si.

Regla:

```text
pctMaximoBeneficio = max(pctBecaReal, pctEgresadoReal)
```

El descuento adicional se calcula asi:

```text
descuentoAdicionalTotal = baseParaOtros * pctMaximoBeneficio
```

El descuento por votacion si es acumulable con el beneficio mayor entre beca y egresado.

### Total de descuentos por estudiante

Formula:

```text
totalDescuentos = descuentoVoto + descuentoAdicionalTotal
```

### Valor neto por estudiante

Formula:

```text
valorNeto = valorMatricula - totalDescuentos
```

### Total neto con derechos

Formula:

```text
totalNetoConDerechos = valorNeto + derechosComplementarios
```

### Totales del reporte

Durante el calculo general:

```text
totalNeto = suma(valorMatricula de estudiantes pagados)
totalDescuentos = suma(totalDescuentos de estudiantes pagados)
totalIngresos = totalNeto - totalDescuentos
```

Los derechos complementarios se calculan aparte:

```text
totalDerechosComplementarios = cantidadEstudiantesPagados * derechosComplementarios
```

## Proyeccion vs reporte final

### Periodo activo

Si la fecha final del periodo no ha pasado, se considera proyeccion.

En proyeccion activa:

- Se usan datos de la tabla de simulacion/proyeccion.
- Para proyecciones nuevas, se hereda el pago real como punto de partida.
- Se permite editar o simular porcentaje de beca.

Referencia de codigo:

- `ManageStudentFinancialReportUseCaseImpl.obtenerReporteFinanciero(...)`
- `ManageStudentFinancialReportUseCaseImpl.enriquecerProyecciones(...)`

### Reporte final

Si la fecha final del periodo ya paso, se considera reporte final.

En reporte final:

- Se ignoran proyecciones manuales.
- Se usan datos reales desde matricula financiera.
- El estado de pago viene de `matricula_financiera.esta_pago`.
- La beca real viene de becas/descuentos del estudiante.
- En el calculo final solo se toman becas avaladas por concejo cuando aplica la regla de reporte final/cerrado/vencido.

Referencia de codigo:

- `ManageStudentFinancialReportUseCaseImpl.obtenerReporteFinanciero(...)`
- `FinancialCalculationService.resolverPorcentajeBeca(...)`

