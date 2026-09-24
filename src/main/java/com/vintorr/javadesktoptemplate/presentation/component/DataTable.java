package com.vintorr.javadesktoptemplate.presentation.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * The template's table component — the desktop counterpart of the web app's
 * {@code x-ui.responsive-table} plus its filter bar, empty state and paginator.
 *
 * <p>A table is described, not assembled: rows go in, columns are declared with a small
 * builder, and searching, filtering, sorting, selection and pagination come for free.</p>
 *
 * <pre>{@code
 * DataTable<Person> table = new DataTable<>(people);
 * table.searchable("Name, email, role…");
 * table.filter("Status", "All statuses", statuses, p -> p.status().label());
 * table.column("Name", Person::name).medium().searchable().width(220);
 * table.badge("Status", p -> p.status().label(), p -> p.status().color()).width(130);
 * table.number("Monthly", p -> Money.compact(p.monthlySpend()), Person::monthlySpend);
 * table.onRowAction(person -> ...);
 * }</pre>
 *
 * <p>Sorting and pagination run over the whole data set, not just the visible page: the
 * rows flow {@code source → FilteredList → SortedList → page}, and the {@code TableView}
 * is handed one page at a time.</p>
 */
public class DataTable<T> extends VBox {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final double ROW_HEIGHT = 52;
    private static final double HEADER_HEIGHT = 44;
    private static final List<Integer> PAGE_SIZES = List.of(10, 25, 50);

    private final ObservableList<T> source;
    private final FilteredList<T> filtered;
    private final SortedList<T> sorted;
    private final ObservableList<T> page = FXCollections.observableArrayList();
    private final TableView<T> table = new TableView<>(page);

    private final List<Column<T>> columns = new ArrayList<>();
    private final List<Supplier<Predicate<T>>> filters = new ArrayList<>();
    private final ObservableSet<T> selection = FXCollections.observableSet(new LinkedHashSet<>());

    private final GridPane filterBar = new GridPane();
    private final VBox card = new VBox();
    private final HBox bulkBar = new HBox(12);
    private final Label bulkLabel = new Label();
    private final Label range = new Label();
    private final Label pageLabel = new Label();
    private final Button previous = Components.iconButton("chevron-left");
    private final Button next = Components.iconButton("chevron-right");

    private TextField search;
    private CheckBox selectAll;
    private Consumer<T> rowAction;
    private int pageIndex;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private int filterColumn;

    public DataTable(List<T> rows) {
        this(FXCollections.observableArrayList(rows));
    }

    public DataTable(ObservableList<T> rows) {
        this.source = rows;
        this.filtered = new FilteredList<>(source, item -> true);
        this.sorted = new SortedList<>(filtered);

        setSpacing(16);

        buildFilterBar();
        buildTable();
        buildCard();

        sorted.addListener((javafx.collections.ListChangeListener<T>) change -> renderPage());
        // Rows that leave the data set must not stay ticked, or bulk actions act on ghosts.
        source.addListener((javafx.collections.ListChangeListener<T>) change -> {
            if (!selection.isEmpty()) {
                selection.retainAll(new java.util.HashSet<>(source));
            }
        });
        selection.addListener((javafx.collections.SetChangeListener<T>) change -> renderSelection());

        getChildren().add(card);
        renderPage();
    }

    // ------------------------------------------------------------- columns

    /** A text column. Configure it further with the returned builder. */
    public Column<T> column(String title, Function<T, String> value) {
        Column<T> column = new Column<>(title, value);
        register(column);
        return column;
    }

    /** A column whose cell is built by the caller — avatars, two-line cells, link rows. */
    public Column<T> node(String title, Function<T, Node> cell) {
        Column<T> column = new Column<>(title, item -> "");
        column.cell = cell;
        column.column.setSortable(false);
        register(column);
        return column;
    }

    /** A column of x-ui.badge pills. */
    public Column<T> badge(String title, Function<T, String> text, Function<T, String> color) {
        Column<T> column = new Column<>(title, text);
        column.cell = item -> Components.badge(text.apply(item), color.apply(item), true);
        register(column);
        return column;
    }

    /** A right-aligned column that sorts on the underlying number rather than its text. */
    public Column<T> number(String title, Function<T, String> text, Function<T, ? extends Number> value) {
        Column<T> column = column(title, text).right().styleClass("tabular");
        column.column.setComparator(Comparator.comparingDouble(item -> value.apply(item).doubleValue()));
        return column;
    }

    /** A trailing column of row actions: right-aligned, never sorted, takes the slack. */
    public Column<T> actions(Function<T, Node> cell) {
        Column<T> column = node("", cell).right().unsortable();
        column.column.setMinWidth(88);
        column.column.setPrefWidth(88);
        return column;
    }

    private void register(Column<T> column) {
        columns.add(column);
        column.owner = this;
        table.getColumns().add(column.column);
    }

    // ------------------------------------------------------------ toolbar

    /** Adds the search box above the table, matching the web app's filter card. */
    public DataTable<T> searchable(String prompt) {
        search = Components.input(prompt);
        search.textProperty().addListener((obs, was, is) -> applyFilters());
        addToFilterBar("Search", Components.searchField(search));
        return this;
    }

    /**
     * Adds a select whose options filter rows on one value — "All statuses", "Active", …
     * {@code valueOf} maps a row to the option it belongs to.
     */
    public DataTable<T> filter(String label, String allLabel, List<String> options, Function<T, String> valueOf) {
        List<String> all = new ArrayList<>();
        all.add(allLabel);
        all.addAll(options);

        ComboBox<String> select = Components.select(all, allLabel);
        select.valueProperty().addListener((obs, was, is) -> applyFilters());
        filters.add(() -> {
            String chosen = select.getValue();
            return chosen == null || chosen.equals(allLabel)
                    ? item -> true
                    : item -> chosen.equals(valueOf.apply(item));
        });

        addToFilterBar(label, select);
        return this;
    }

    /** Nodes shown next to "n selected" once rows are ticked. */
    public DataTable<T> bulkActions(Node... actions) {
        selectable();
        bulkBar.getChildren().setAll(bulkLabel, Components.hGrow());
        bulkBar.getChildren().addAll(actions);

        javafx.scene.control.Hyperlink clear = Components.link("Clear", "link");
        clear.setOnAction(e -> clearSelection());
        bulkBar.getChildren().add(clear);
        return this;
    }

    /** Adds the leading tick-box column and the select-all header box. */
    public DataTable<T> selectable() {
        if (selectAll != null) {
            return this;
        }

        selectAll = new CheckBox();
        selectAll.getStyleClass().add("check");
        selectAll.setOnAction(e -> {
            if (selectAll.isSelected()) {
                selection.addAll(sorted);
            } else {
                selection.clear();
            }
            table.refresh();
        });

        TableColumn<T, T> column = new TableColumn<>();
        column.setGraphic(selectAll);
        column.setSortable(false);
        column.setResizable(false);
        column.setPrefWidth(46);
        column.setMinWidth(46);
        column.setMaxWidth(46);
        column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
        column.setCellFactory(ignored -> new TableCell<>() {
            private final CheckBox box = new CheckBox();

            {
                box.getStyleClass().add("check");
                box.setOnAction(event -> {
                    T item = getItem();
                    if (item == null) {
                        return;
                    }
                    if (box.isSelected()) {
                        selection.add(item);
                    } else {
                        selection.remove(item);
                    }
                });
                setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    box.setSelected(selection.contains(item));
                    setGraphic(box);
                }
            }
        });

        table.getColumns().add(0, column);
        return this;
    }

    // ------------------------------------------------------------- wiring

    /** Runs when a row is double-clicked. */
    public DataTable<T> onRowAction(Consumer<T> action) {
        this.rowAction = action;
        return this;
    }

    /** A title/subtitle header on the table card, like x-ui.card. */
    public DataTable<T> header(String title, String subtitle, Node... actions) {
        VBox titles = new VBox(2, Components.label(title, "section-title"));
        if (subtitle != null) {
            titles.getChildren().add(Components.label(subtitle, "muted-sm"));
        }

        HBox header = new HBox(12, titles, Components.hGrow());
        if (actions != null && actions.length > 0) {
            HBox bar = new HBox(8, actions);
            bar.setAlignment(Pos.CENTER_RIGHT);
            header.getChildren().add(bar);
        }
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 20, 14, 20));
        header.getStyleClass().add("card-divider");

        card.getChildren().add(0, header);
        return this;
    }

    /** What the table shows when nothing matches. */
    public DataTable<T> emptyState(String title, String description, Node action) {
        table.setPlaceholder(EmptyState.of(title, description, action));
        return this;
    }

    public DataTable<T> pageSize(int rows) {
        this.pageSize = Math.max(1, rows);
        renderPage();
        return this;
    }

    /** The live backing list — mutate it and the table, counts and paging follow. */
    public ObservableList<T> rows() {
        return source;
    }

    /** The ticked rows, in the order they were ticked. */
    public ObservableSet<T> selection() {
        return selection;
    }

    public void clearSelection() {
        selection.clear();
        table.refresh();
    }

    public TableView<T> tableView() {
        return table;
    }

    // -------------------------------------------------------------- build

    private void buildFilterBar() {
        filterBar.setHgap(12);
        filterBar.setVgap(12);
        filterBar.setPadding(new Insets(16));
        filterBar.getStyleClass().addAll("card", "filter-bar");
        filterBar.setVisible(false);
        filterBar.setManaged(false);
    }

    private void addToFilterBar(String label, Region control) {
        if (!filterBar.isManaged()) {
            filterBar.setVisible(true);
            filterBar.setManaged(true);
            getChildren().add(0, filterBar);
        }

        var column = new javafx.scene.layout.ColumnConstraints();
        column.setPercentWidth(0);
        column.setHgrow(Priority.ALWAYS);
        filterBar.getColumnConstraints().add(column);
        for (var existing : filterBar.getColumnConstraints()) {
            existing.setPercentWidth(100d / filterBar.getColumnConstraints().size());
        }

        Field field = new Field(label, control);
        GridPane.setConstraints(field, filterColumn++, 0);
        GridPane.setHgrow(field, Priority.ALWAYS);
        filterBar.getChildren().add(field);
    }

    private void buildTable() {
        table.getStyleClass().add("data-table");
        table.setFixedCellSize(ROW_HEIGHT);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(EmptyState.of("Nothing to show", "No rows match the current filters.", null));
        table.setSelectionModel(null);

        // The TableView only ever holds the current page, so sorting is applied upstream.
        table.setSortPolicy(ignored -> true);
        sorted.comparatorProperty().bind(table.comparatorProperty());

        table.setRowFactory(ignored -> {
            TableRow<T> row = new TableRow<>();
            row.getStyleClass().add("data-row");
            row.setOnMouseClicked(event -> {
                if (rowAction != null && event.getClickCount() == 2 && !row.isEmpty()) {
                    rowAction.accept(row.getItem());
                }
            });
            return row;
        });
    }

    private void buildCard() {
        card.getStyleClass().add("card");

        bulkBar.setAlignment(Pos.CENTER_LEFT);
        bulkBar.setPadding(new Insets(10, 20, 10, 20));
        bulkBar.getStyleClass().add("bulk-bar");
        bulkBar.setVisible(false);
        bulkBar.setManaged(false);
        bulkLabel.getStyleClass().add("font-medium");

        range.getStyleClass().add("muted-xs");
        pageLabel.getStyleClass().add("muted-xs");

        previous.setOnAction(e -> goTo(pageIndex - 1));
        next.setOnAction(e -> goTo(pageIndex + 1));

        ComboBox<String> rowsPerPage = Components.select(
                PAGE_SIZES.stream().map(String::valueOf).toList(), String.valueOf(pageSize));
        rowsPerPage.getStyleClass().add("form-select-sm");
        rowsPerPage.setMaxWidth(84);
        rowsPerPage.valueProperty().addListener((obs, was, is) -> {
            if (is != null) {
                pageSize = Integer.parseInt(is);
                goTo(0);
            }
        });

        HBox pager = new HBox(8, Components.label("Rows", "muted-xs"), rowsPerPage, previous, pageLabel, next);
        pager.setAlignment(Pos.CENTER_RIGHT);

        HBox footer = new HBox(12, range, Components.hGrow(), pager);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(10, 16, 10, 20));
        footer.getStyleClass().add("card-footer");

        card.getChildren().addAll(bulkBar, table, footer);
    }

    // ------------------------------------------------------------- render

    private void applyFilters() {
        String query = search == null || search.getText() == null
                ? "" : search.getText().trim().toLowerCase(Locale.ROOT);
        List<Predicate<T>> active = filters.stream().map(Supplier::get).toList();

        filtered.setPredicate(item -> matches(item, query) && active.stream().allMatch(p -> p.test(item)));
        goTo(0);
    }

    private boolean matches(T item, String query) {
        if (query.isEmpty()) {
            return true;
        }
        for (Column<T> column : columns) {
            if (column.searchText == null) {
                continue;
            }
            String value = column.searchText.apply(item);
            if (value != null && value.toLowerCase(Locale.ROOT).contains(query)) {
                return true;
            }
        }
        return false;
    }

    private void goTo(int index) {
        this.pageIndex = Math.max(0, index);
        renderPage();
    }

    private void renderPage() {
        int total = sorted.size();
        int pages = Math.max(1, (int) Math.ceil(total / (double) pageSize));
        pageIndex = Math.min(Math.max(pageIndex, 0), pages - 1);

        int from = Math.min(pageIndex * pageSize, total);
        int to = Math.min(from + pageSize, total);
        page.setAll(new ArrayList<>(sorted.subList(from, to)));

        range.setText(total == 0
                ? "No results"
                : "Showing " + (from + 1) + "–" + to + " of " + total + (total == 1 ? " row" : " rows"));
        pageLabel.setText(pageIndex + 1 + " / " + pages);
        previous.setDisable(pageIndex == 0);
        next.setDisable(pageIndex >= pages - 1);

        // Size to the rows on screen so the card never carries an inner scrollbar.
        table.setPrefHeight(page.isEmpty() ? HEADER_HEIGHT + 200 : HEADER_HEIGHT + page.size() * ROW_HEIGHT + 2);
        table.setMinHeight(Region.USE_PREF_SIZE);

        renderSelection();
    }

    private void renderSelection() {
        boolean any = !selection.isEmpty();
        boolean hasBulkActions = !bulkBar.getChildren().isEmpty();
        bulkBar.setVisible(any && hasBulkActions);
        bulkBar.setManaged(any && hasBulkActions);
        bulkLabel.setText(selection.size() + (selection.size() == 1 ? " row selected" : " rows selected"));

        if (selectAll != null) {
            // The header box reflects the rows currently in view, not the whole selection.
            boolean all = !sorted.isEmpty() && selection.containsAll(sorted);
            boolean some = sorted.stream().anyMatch(selection::contains);
            selectAll.setSelected(all);
            selectAll.setIndeterminate(some && !all);
        }
    }

    // ------------------------------------------------------------- column

    /** Declarative column configuration; every setter returns {@code this}. */
    public static final class Column<T> {

        private final TableColumn<T, T> column;
        private final Function<T, String> text;

        private Function<T, Node> cell;
        private Function<T, String> searchText;
        private Pos alignment = Pos.CENTER_LEFT;
        private final List<String> cellClasses = new ArrayList<>();
        private DataTable<T> owner;

        private Column(String title, Function<T, String> text) {
            this.text = text;
            this.column = new TableColumn<>(title == null ? "" : title.toUpperCase(Locale.ROOT));
            this.column.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue()));
            this.column.setComparator(Comparator.comparing(
                    item -> text.apply(item) == null ? "" : text.apply(item).toLowerCase(Locale.ROOT)));
            this.column.setCellFactory(ignored -> new TableCell<>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setAlignment(alignment);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    if (cell != null) {
                        setText(null);
                        setGraphic(cell.apply(item));
                    } else {
                        setGraphic(null);
                        setText(text.apply(item));
                        getStyleClass().removeAll(cellClasses);
                        getStyleClass().addAll(cellClasses);
                    }
                }
            });
        }

        /** Include this column's text in what the search box looks at. */
        public Column<T> searchable() {
            this.searchText = text;
            return this;
        }

        /** Search this column on something other than what it displays. */
        public Column<T> searchBy(Function<T, String> value) {
            this.searchText = value;
            return this;
        }

        public Column<T> sortBy(Comparator<T> comparator) {
            column.setComparator(comparator);
            return this;
        }

        public Column<T> unsortable() {
            column.setSortable(false);
            return this;
        }

        public Column<T> width(double width) {
            column.setPrefWidth(width);
            column.setMinWidth(Math.min(width, 72));
            return this;
        }

        /** A column that keeps its width when the table is resized. */
        public Column<T> fixed(double width) {
            column.setPrefWidth(width);
            column.setMinWidth(width);
            column.setMaxWidth(width);
            column.setResizable(false);
            return this;
        }

        public Column<T> right() {
            alignment = Pos.CENTER_RIGHT;
            column.getStyleClass().add("column-right");
            return this;
        }

        public Column<T> center() {
            alignment = Pos.CENTER;
            return this;
        }

        /** Renders the value in medium weight, the way the web tables emphasise the first column. */
        public Column<T> medium() {
            return styleClass("font-medium");
        }

        public Column<T> styleClass(String... classes) {
            cellClasses.addAll(List.of(classes));
            return this;
        }

        /** Sort the table by this column on first paint. */
        public Column<T> sortedFirst() {
            if (owner != null) {
                owner.table.getSortOrder().setAll(column);
            }
            return this;
        }
    }
}
