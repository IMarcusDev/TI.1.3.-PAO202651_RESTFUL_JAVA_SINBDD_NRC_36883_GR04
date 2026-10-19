# web-client-restful

## Objective
Create the RESTful Java web client `04. CLIWEB/WEB_CON_UNI_JAVA_RESTful` (Servlets + JSP), mirroring the folder/package structure of the SOAP web client `TI.1.1.../04. CLIWEB/WEB_CON_UNI_JAVA_SOAP` exactly (`ec.edu.monster.{controlador,modelo}`, `webapp/ec/edu/monster/vista`, tests in `src/test/java/ec/edu/monster/pruebas`).

## Approach
- Servlets, filter, JSPs, CSS and structure from the SOAP web client.
- REST layer (`modelo/dto`, `modelo/enums`, `modelo/http`, REST-backed `ConversorServicio`) reused from `04. CLIESC/ESC_CON_UNI_JAVA_RESTful`, same placement.
- Branch `feat/web-client-restful` stacked on `feat/desktop-client-restful`.

## Authorized scope
Only `04. CLIWEB/WEB_CON_UNI_JAVA_RESTful/**`, `.gitignore` (target/ line) and this file.

## Tasks
- [x] T1 Project skeleton: pom.xml (war), web.xml, beans.xml, index.jsp (route: delegated writer)
- [x] T2 Model + REST layer (copied from the desktop REST client)
- [x] T3 Controllers (servlets, filter, FabricaServicios, InicioAplicacion, SesionUsuario, Opcion, ValidadorConversion)
- [x] T4 Views (JSPs, fragment, CSS)
- [x] T5 Unit tests mirroring SOAP web client `pruebas/`
- [x] T6 Build + tests pass
- [x] T7 Live check on local Payara 7 (route: parent)

## Acceptance
`mvn -q test` passes; structure matches the SOAP web client file-for-file where applicable; login/catalog/conversion work against the deployed REST server.

## Progress / evidence
- Route: delegated writer (2+ non-trivial files). 55 source files created; target/ ignored in root .gitignore.
- T1: pom.xml (war, Jakarta EE 11.0.0-M1, JUnit 5.11.4, Mockito 5.20.0, compiler 17, org.json 20250517 bundled in WEB-INF/lib), nb-configuration.xml (pfv5ee8, jaxws hints dropped), web.xml, beans.xml, index.jsp copied from the SOAP web client.
- T2: modelo/ (dto, enums, http, REST ConversorServicio, FormatoResultado, Magnitudes, NombresUnidades, ServicioException, Sesion) copied verbatim from 04. CLIESC/ESC_CON_UNI_JAVA_RESTful.
- T3: controlador/ copied verbatim from the SOAP web client except FabricaServicios (shared RESTfulClient + per-user ConversorServicio; availability = GET {base}jakartaee11 == 200) and the InicioAplicacion comment.
- T4: webapp (css, ec/edu/monster/vista/*.jsp, fragmentos/cabecera.jspf) copied verbatim; views had no SOAP references.
- T5: same 14 SOAP test classes + RESTfulClientTest. Verbatim: AplicacionWeb, AutenticacionFilter, CatalogoServlet, ConversionServlet, FormatoResultado, LogoutServlet, Magnitudes, NombresUnidades, Opcion, Sesion, ValidadorConversion. Adapted to REST types: WebFixture, LoginServletTest, FabricaServiciosTest (+1 test: created service uses the configured base URL). From the desktop REST client: ConversorServicioTest, RESTfulClientTest.
- T6: `mvn -q -B package` exit 0; 15 suites, 148 tests, 0 failures/errors/skipped; target/WEB_CON_UNI_JAVA_RESTful-1.0-SNAPSHOT.war built.
- T7: deployed to local Payara 7 at /WEB_CON_UNI_JAVA_RESTful alongside the REST server. curl flow: /menu without session -> 302 /login; bad login -> "Credenciales Incorrectas"; login -> 302 /menu; catalogo Masa lists g/kg/lb/oz/qq; 1 qq -> 100,0000 kg; POST /logout -> 302 /login and /menu redirects to login again (GET /logout intentionally does nothing, same as SOAP). Browser clicks not verified by agent.
- Commit: feat(cliweb) work-unit commit on feat/web-client-restful.
- Next: user browser check; push/PR are user decisions.
