package com.vintorr.javadesktoptemplate.presentation.view;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.vintorr.javadesktoptemplate.common.util.Money;
import com.vintorr.javadesktoptemplate.domain.model.Person;
import com.vintorr.javadesktoptemplate.presentation.Route;
import com.vintorr.javadesktoptemplate.presentation.Router;
import com.vintorr.javadesktoptemplate.presentation.component.AppShell;
import com.vintorr.javadesktoptemplate.presentation.component.Components;
import com.vintorr.javadesktoptemplate.presentation.component.DataTable;
import com.vintorr.javadesktoptemplate.service.PersonService;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The worked example for {@link DataTable}: search, two selects, sortable columns,
 * tick-box selection with bulk actions, row actions and pagination over 34 demo rows.
 * Copy this file, swap the service and the column list, and you have your own list screen.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PeopleView implements AppView {

    private static final DateTimeFormatter JOINED = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final PersonService people;
    private final Router router;

    public PeopleView(PersonService people, Router router) {
        this.people = people;
        this.router = router;
    }

    @Override
    public String title(String applicationTitle) {
        return "People · " + applicationTitle;
    }

    @Override
    public Parent view() {
        Button add = Components.primary("Add person", "user-plus", false);
        add.setOnAction(e -> router.placeholder("Add person"));

        Button export = Components.secondary("Export", "download");
        export.setOnAction(e -> router.placeholder("Export"));

        VBox page = new VBox(20,
                Components.pageHeader("People", "Everyone with access to this workspace.",
                        List.of("Workspace", "People"), add, export),
                table());

        return AppShell.wrap(router, Route.PEOPLE, page);
    }

    private DataTable<Person> table() {
        DataTable<Person> table = new DataTable<>(people.all());

        table.searchable("Name, email or role…");
        table.filter("Status", "All statuses",
                Arrays.stream(Person.Status.values()).map(Person.Status::label).toList(),
                person -> person.status().label());
        // A filter can key off a field the table never shows — team has no column here.
        table.filter("Team", "All teams", people.teams(), Person::team);

        table.node("Name", this::identityCell)
                .searchBy(Person::name)
                .sortBy(Comparator.comparing(Person::name))
                .width(270);
        table.column("Role", Person::role).searchable().width(160);
        table.badge("Status", person -> person.status().label(), person -> person.status().color()).width(124);
        table.column("Joined", person -> person.joined().format(JOINED))
                .sortBy(Comparator.comparing(Person::joined))
                .width(118);
        table.number("Monthly", person -> Money.compact(person.monthlySpend()), Person::monthlySpend).width(100);
        table.actions(this::rowActions);

        table.bulkActions(
                bulkButton("Email", "document"),
                bulkButton("Archive", "inbox"));

        table.header("Directory", "Sortable, searchable and paginated by the DataTable component.");
        table.emptyState("No people match", "Try a different search term or clear the filters.", null);
        table.onRowAction(person -> router.placeholder(person.name()));
        return table;
    }

    /** Avatar + name over the email address — the two-line cell most list screens want. */
    private Node identityCell(Person person) {
        VBox text = new VBox(1,
                Components.label(person.name(), "font-medium"),
                Components.label(person.email(), "muted-xs"));

        HBox cell = new HBox(10, Components.avatar(person.name(), 32), text);
        cell.setAlignment(Pos.CENTER_LEFT);
        return cell;
    }

    private Node rowActions(Person person) {
        Button edit = Components.iconButton("pencil");
        edit.setOnAction(e -> router.placeholder("Edit " + person.name()));

        Button remove = Components.iconButton("trash");
        remove.setOnAction(e -> router.placeholder("Remove " + person.name()));

        HBox actions = new HBox(2, edit, remove);
        actions.setAlignment(Pos.CENTER_RIGHT);
        return actions;
    }

    private Button bulkButton(String label, String icon) {
        Button button = Components.small(label, icon, "secondary");
        button.setOnAction(e -> router.placeholder(label));
        return button;
    }
}
