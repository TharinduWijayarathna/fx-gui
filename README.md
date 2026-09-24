# JavaDesktopTemplate

A layered **Spring Boot + JavaFX** desktop application template. Five screens — **Login**,
**Register**, **Dashboard**, **People** and **Profile** — on top of a reusable app shell, a
`DataTable` component, and a design system ported from a Tailwind/Blade web app.

Everything visible is placeholder content for a real product: the branding comes from
`application.yml`, the sidebar sections are dummies, and the data comes from in-memory
fixtures behind interfaces.

## Running it

```bash
./mvnw javafx:run          # run the app
./mvnw test                # service + configuration tests
./mvnw spring-boot:run     # same app, via the Boot plugin
```

Requires JDK 21. The Maven wrapper downloads Maven on first run.

### Demo account

```
demo@example.com / password
```

Registering a new account signs you straight in.

### IntelliJ

Open the folder and let it import the Maven project, then use the shared
**JavaDesktopTemplate** run configuration in `.run/`.

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

## Screens

| Screen | View | What it is |
| --- | --- | --- |
| Login / Register | `LoginView`, `RegisterView` | Validated auth forms on the patterned `AuthShell` |
| Dashboard | `DashboardView` | Stat tiles, an area chart and two list cards |
| People | `PeopleView` | **The `DataTable` worked example** — copy this file for your own list screens |
| Profile | `ProfileView` | Edit your name/email/organisation and change your password — these persist |

The topbar's user menu is wired: **Profile** navigates, **Sign out** ends the session and
returns to login. Sidebar entries without a route raise a toast, so the shell can be clicked
through end to end while you replace them.

## The table component

`presentation/component/DataTable` is the piece most apps need first. Rows go in, columns are
declared, and search, select filters, sorting, tick-box selection, bulk actions, an empty
state and pagination come with it:

```java
DataTable<Person> table = new DataTable<>(people.all());

table.searchable("Name, email or role…");
table.filter("Status", "All statuses", statusLabels, p -> p.status().label());

table.node("Name", this::identityCell).searchBy(Person::name).sortBy(comparing(Person::name)).width(270);
table.column("Role", Person::role).searchable().width(160);
table.badge("Status", p -> p.status().label(), p -> p.status().color()).width(124);
table.number("Monthly", p -> Money.compact(p.monthlySpend()), Person::monthlySpend).width(100);
table.actions(this::rowActions);

table.bulkActions(emailButton, archiveButton);
table.emptyState("No people match", "Try a different search term.", null);
table.onRowAction(person -> open(person));
```

Rows flow `source → FilteredList → SortedList → page`, so sorting and paging run over the
whole data set rather than the visible page, and the `TableView` is handed one page at a time.
`table.rows()` is the live backing list — mutate it and the counts, paging and selection
follow.

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
| Presentation | `presentation` | `Router`, `Route`, `view/` screens, `component/` reusable widgets |
| Service | `service` | `AuthenticationService`, `SessionService`, `ProfileService`, `DashboardService`, `PersonService` + `impl/`, request DTOs in `dto/` |
| Repository | `repository` | `UserRepository`, `PersonRepository` + in-memory implementations |
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
| `presentation/Route` | The route table's enum; `requiresAuthentication()` is enforced in `Router` |

Screens are prototype-scoped `@Component`s implementing `presentation.view.AppView`, so `Router`
pulls a fresh instance from the context on every navigation — the desktop equivalent of a route
hit. Spring Boot runs with `web-application-type: none`; there is no servlet container.

## Components

| Component | What it renders |
| --- | --- |
| `AppShell` | Sidebar + topbar + scrolling content; every signed-in page wraps itself in it |
| `DataTable` | The table above: filters, sorting, selection, pagination, empty state |
| `Components` | Buttons, inputs, selects, cards, stats, badges, avatars, breadcrumbs, page headers |
| `Brand` | The logo: a gradient tile carrying a Heroicons glyph, plus the product wordmark |
| `Field`, `PasswordBox` | Label + control + error/hint; password field with a reveal toggle |
| `AreaChartView` | The dashboard's gradient area chart |
| `ResponsiveRow` | Tailwind-style grids that reflow at a breakpoint |
| `EmptyState`, `Toasts`, `Icons` | Dashed empty placeholder, top-right toast stack, the icon set |

## Re-skinning it

| What | Where |
| --- | --- |
| Product name, tagline, window size | `src/main/resources/application.yml` (`app.*`) |
| Logo mark and gradient | `presentation/component/Brand` — swap the glyph path or the gradient |
| Colours, type, every component style | `resources/.../css/theme.css` — the `-brand-*` ramp at the top is the product's colour |
| Sidebar sections | the `NAV` list at the top of `presentation/component/AppShell` |
| Demo data | `repository/impl/*`, `service/impl/DashboardServiceImpl` |

Icons and the logo glyph are [Heroicons](https://heroicons.com) outline paths (MIT, Tailwind
Labs), inlined as SVG path data — there is no bitmap logo to replace and nothing to license.
DM Sans is bundled as static TTFs and registered at startup.

## Layout

```
src/main/java/com/vintorr/javadesktoptemplate/
├── JavaDesktopTemplateApplication.java   entry point
├── JavaFxApplication.java                JavaFX ↔ Spring lifecycle
├── config/                               ApplicationProperties
├── domain/
│   ├── model/                            User, Person, DashboardMetrics, ScheduleEntry, AttentionItem
│   └── exception/                        ValidationException
├── repository/                           UserRepository, PersonRepository + impl/
├── service/                              service interfaces, impl/, dto/
├── presentation/
│   ├── Router.java, Route.java
│   ├── event/                            StageReadyEvent
│   ├── view/                             AppView, Login, Register, Dashboard, People, Profile
│   └── component/                        AppShell, DataTable, Components, Brand, Icons, …
└── common/                               security/, util/

src/main/resources/
├── application.yml                       branding + window geometry
└── com/vintorr/javadesktoptemplate/
    ├── css/theme.css                     the whole design system
    └── fonts/DMSans-*.ttf
```

## Notes

- There is no database. Users and people live in in-memory repositories; the dashboard numbers
  are fixtures. All three sit behind interfaces so they can be replaced without touching
  anything above them.
- `Sha256PasswordEncoder` is demo-grade, so nothing sits in memory in plain text. Real auth
  wants an adaptive hash — swap the `PasswordEncoder` bean.
- `src/test/.../ScreenshotTool` renders every screen to a PNG off-screen — handy for eyeballing
  UI changes without launching:
  ```bash
  ./mvnw test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
  java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" \
    com.vintorr.javadesktoptemplate.ScreenshotTool target/screenshots
  ```
