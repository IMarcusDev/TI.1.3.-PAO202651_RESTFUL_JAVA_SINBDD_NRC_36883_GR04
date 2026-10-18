# desktop-client-restful

## Objective
Create the RESTful Java desktop client `04. CLIESC/ESC_CON_UNI_JAVA_RESTful` (Swing), mirroring the folder/package structure of the SOAP desktop client `TI.1.1.../04. CLIESC/ESC_CON_UNI_JAVA_SOAP`, including the unit-test layout (`src/test/java/ec/edu/monster/pruebas`).

## Approach
- View layer (Swing) and MVC structure from the SOAP desktop client.
- HTTP/DTO/enums layer from `04. CLICON/CONSOLE_CON_UNI_JAVA_RESTful` (RESTfulClient, ConfigHTTP, DTOs, enums).
- Server contract: POST /converter with `Authorization` header + JSON {type,value,from,to}; login + catalog endpoints (see `04. SERVIDOR/WS_CON_UNI_JAVA_RESTFUL`).

## Authorized scope
Only `04. CLIESC/ESC_CON_UNI_JAVA_RESTful/**` and this file. No changes to server or console client.

## Tasks
- [x] T1 Project skeleton: pom.xml, App.java, package layout mirroring SOAP client (route: delegated writer)
- [x] T2 Model + HTTP layer adapted to REST (Sesion, ConversorServicio, Magnitudes, NombresUnidades, FormatoResultado, ServicioException, RESTfulClient, DTOs)
- [x] T3 Controllers (Login, Catalogo, Conversion)
- [x] T4 Views (LoginFrame/Vista, PrincipalFrame, Catalogo*, Conversion*, Estilos)
- [x] T5 Unit tests mirroring `pruebas/` of SOAP client, adapted to REST
- [x] T6 Build + tests pass

## Acceptance
`mvn -q test` passes in the new project; structure matches SOAP client file-for-file where applicable.

## Test policy
Tests mirror the SOAP client's tests; RED/GREEN where deterministic runner (JUnit via Maven) exists.

## Progress / evidence
- Route: delegated writer (single bounded writer); 2+ non-trivial files, mapping beyond inline budget.
- Seam: `LoginApi`/`ConversorApi` interfaces (mirror SOAP `LoginWebService`/`ConversorWebService`) in `modelo/http`, implemented by `LoginRestApi`/`ConversorRestApi` over `RESTfulClient` (java.net.http, 5s connect / 15s request timeouts). Token sent in `Authorization` header (login sends none). DTOs in `modelo/dto`, enums in `modelo/enums` (org.json 20250517).
- Views, Estilos, App wiring, Sesion, Magnitudes, NombresUnidades, FormatoResultado, ServicioException copied from SOAP client (only SOAP wording adapted).
- Tests: same 10 class names under `pruebas/` plus extra `RESTfulClientTest` (loopback fake server; verifies headers, body, paths, DTO parsing). SOAP-fault tests replaced by malformed-response and null-response tests.
- Verification: `mvn -q -B test` in `04. CLIESC/ESC_CON_UNI_JAVA_RESTful`: exit 0; 127 tests, 0 failures/errors/skipped (also passes with -Djava.awt.headless=true).
- Live check: deployed server to local Payara 7 (domain1, context root /WS_CON_UNI_JAVA_RESTFUL); client service layer smoke test passed (bad login rejected, login OK, catalog + conversion OK for all 6 magnitudes). GUI clicks not verified by agent.
- Commit: feat(cliesc) work-unit commit on feat/desktop-client-restful.
- Next: user manual GUI check; push/PR are user decisions.
