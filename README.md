# JavaDesktopTemplate

A layered **Spring Boot + JavaFX** desktop application template, skinned with the **Vintorr
Rently** UI. Three screens — **Login**, **Register** and **Dashboard** — rebuilt in JavaFX with
the same design language as the Laravel/Blade app in `../vintorr-rently`.

The branding is configuration, not code: `app.title`, `app.tagline` and the window geometry live
in `application.yml`, so the shell re-skins without touching the presentation layer.

There is no database. Users live in an in-memory repository and the dashboard numbers come from
a demo service, both behind interfaces so they can be replaced without touching anything above.

## Running it

```bash
./mvnw javafx:run          # run the app
./mvnw test                # service + configuration tests
./mvnw spring-boot:run     # same app, via the Boot plugin
```

Requires JDK 21. The Maven wrapper downloads Maven on first run.

### IntelliJ

Open the folder and let it import the Maven project, then use the shared
**JavaDesktopTemplate** run configuration in `.run/`.

If you previously opened this project under its old name, hit **Reload All Maven Projects**
first — the IDE module is renamed to `java-desktop-template`, which is what the run
configuration binds to.

That configuration exists because JavaFX ships as JPMS modules. Launching it off the classpath
works, but the toolkit warns:

```
WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @...'
```

The configuration puts the platform modules on the module path instead:

```
--module-path "$PROJECT_DIR$/target/javafx-modules" --add-modules javafx.controls,javafx.graphics,javafx.base
```

A before-launch Maven goal (`dependency:copy-dependencies@copy-javafx-modules`) stages those
modules into `target/javafx-modules`. JavaFX publishes its real modules under a platform
classifier — the unclassified jar is an empty stub — so the `javafx-*` profiles in `pom.xml`
select the right one per OS, and the module path works on any machine.

`./mvnw javafx:run` needs none of this; the plugin builds its own module path.

### Demo account

```
demo@vintorr.com / password
```

Registering a new shop signs you straight in, same as the web app.

## Architecture

Strict layering — each layer depends only on the ones beneath it, and crosses every boundary
through an interface:

```
presentation  ──▶  service  ──▶  repository  ──▶  domain
      │               │              │
      └───────────────┴──────────────┴──────────▶  common
```

| Layer | Package | Contents |
| --- | --- | --- |
| Presentation | `presentation` | `Router`, `view/` screens, `component/` reusable widgets |
| Service | `service` | `AuthenticationService`, `SessionService`, `DashboardService` + `impl/`, request DTOs in `dto/` |
| Repository | `repository` | `UserRepository` contract + `impl/InMemoryUserRepository` |
| Domain | `domain` | `model/` records, `exception/ValidationException` |
| Common | `common` | `security/PasswordEncoder` (+ SHA-256 impl), `util/Money` |
| Config | `config` | `ApplicationProperties` — no JavaFX below the presentation layer |

The presentation layer never touches a repository, and no layer below it imports JavaFX. Every
service and repository is programmed to its interface, so `InMemoryUserRepository` becomes a
Spring Data repository, or `Sha256PasswordEncoder` becomes `BCryptPasswordEncoder`, as a
one-class swap.

### How Spring and JavaFX fit together

| Class | Role |
| --- | --- |
| `JavaDesktopTemplateApplication` | `@SpringBootApplication`; `main()` hands the main thread to JavaFX |
| `JavaFxApplication` | `init()` boots the Spring context, `start()` publishes `StageReadyEvent`, `stop()` closes both |
| `presentation/event/StageReadyEvent` | Carries the primary `Stage` into the Spring world |
| `presentation/Router` | Listens for `StageReadyEvent`, owns the stage/scene, swaps screens |

Screens are prototype-scoped `@Component`s implementing `presentation.view.AppView`, so `Router`
pulls a fresh instance from the context on every navigation — the desktop equivalent of a route
hit. Spring Boot runs with `web-application-type: none`; there is no servlet container.

## Where the design comes from

Every visual token is lifted from the web app rather than re-invented:

| Desktop | Web source |
| --- | --- |
| `css/theme.css` colour variables | `tailwind.config.js` (`brand`, `accent`, `surface`, semantic scales) |
| `.btn`, `.card`, `.form-input`, `.nav-link`, `.badge`, `.auth-shell` | `resources/css/app.css` `@layer components` |
| `component/Icons` path data | `components/ui/icon.blade.php` + the sidebar in `layouts/app.blade.php` |
| `component/Components` factories | `components/ui/{button,card,stat,badge,avatar,field,input,page-header,breadcrumb}.blade.php` |
| `component/PasswordBox` | `components/ui/password-input.blade.php` |
| `component/AreaChartView` | `components/ui/area-chart.blade.php` (same padding, guides, gradients) |
| `component/AuthShell` | `layouts/guest.blade.php` (patterned backdrop, logo, max-w-md card) |
| `view/DashboardView` | `layouts/app.blade.php` + `pages/dashboard/index.blade.php` |

DM Sans is bundled as static TTFs and registered at startup, so type matches the web app rather
than falling back to the system sans.

`component/ResponsiveRow` reproduces the Tailwind grid breakpoints: the stat row is 5-across on
a wide window and 2-across when narrow; the card pairs are 3/2 columns and stack below ~820px.

## Layout

```
src/main/java/com/vintorr/javadesktoptemplate/
├── JavaDesktopTemplateApplication.java   entry point
├── JavaFxApplication.java                JavaFX ↔ Spring lifecycle
├── config/                               ApplicationProperties
├── domain/
│   ├── model/                            User, DashboardMetrics, ScheduleEntry, OverdueBooking
│   └── exception/                        ValidationException
├── repository/                           UserRepository + impl/InMemoryUserRepository
├── service/                              service interfaces, impl/, dto/
├── presentation/
│   ├── Router.java
│   ├── event/                            StageReadyEvent
│   ├── view/                             AppView, LoginView, RegisterView, DashboardView
│   └── component/                        Components, Icons, Field, PasswordBox, AreaChartView, …
└── common/                               security/, util/

src/main/resources/
├── application.yml                       branding + window geometry
└── com/vintorr/javadesktoptemplate/
    ├── css/theme.css                     the whole design system
    ├── fonts/DMSans-*.ttf
    └── images/vintorr-rently-logo.png
```

## Notes

- Sidebar entries other than Dashboard, and the topbar actions, raise a toast explaining they
  aren't part of this port — only the three requested screens are built.
- Login gained a "New to Rently? Sign up" link; the web app relies on a URL for that, a desktop
  app needs a way across.
- `Sha256PasswordEncoder` is demo-grade, so nothing sits in memory in plain text. Real auth
  wants an adaptive hash — swap the `PasswordEncoder` bean.
- `src/test/.../ScreenshotTool` renders each screen to a PNG off-screen — handy for eyeballing
  UI changes without launching:
  ```bash
  ./mvnw test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
  java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" \
    com.vintorr.javadesktoptemplate.ScreenshotTool target/screenshots
  ```
