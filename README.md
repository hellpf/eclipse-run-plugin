# eclipse-run-plugin

Run/Debug eclipse plugin is a tool for launching run/debug configurations more simply.

# Features

## Run/Debug View

- displays list or tree (with types) of launch configurations
- ability to display only launch configurations of opened projects
- ability to execute run configurations by double click
- ability to open a launch configuration dialog from context menu using Run or Debug

## Run and Debug Dialog

- dialog can be displayed by global key shortcut (default alt+r / alt+d)
- ability to select launch configuration by pressing keys up/down and confirm by enter.
- dialog is integrated to the main menu

# Version
 - 1.0.4 - 2008-07-29 - first version

# Instalations

Eclipse 3.1-3.3 Update Manager: you can use special Eclipse update site for plugin presented here. Go to "Help -> Software Updates -> Find and Install... -> Search for new features to install -> Next -> New Remote Site..." and use the http://web.loose.cz/plugins/eclipse/ as url.

Eclipse 3.4 Update Manager: you can use special Eclipse update site for plugin presented here. Go to "Help -> Software Updates -> Available Software -> Add Site..." and use the http://web.loose.cz/plugins/eclipse/ as url.

# Notes

This plugin has been tested only on Eclipse 3.4 - GNU/Linux 32bit.

# Screenshots

Run/Debug View - simple

![Run/Debug View - simple](https://raw.githubusercontent.com/hellpf/eclipse-run-plugin/master/screenshots/run-view.png)

Run/Debug View - full

![Run/Debug View - full](https://raw.githubusercontent.com/hellpf/eclipse-run-plugin/master/screenshots/run-view2.png)

Run/Debug Dialog

![Run/Debug Dialog](https://raw.githubusercontent.com/hellpf/eclipse-run-plugin/master/screenshots/run-dialog.png)


# TODO

This is a TODO list of useful features that are not implemented yet.

- allow to define custom hot keys
- add a filter (like the Open Resource/Class dialogs) - filter by the name/type
- add various sorting options - Alphabetical, Most recently used, group by type of launcher etc
- add support for placeholders ${workspace_loc} and ${project_loc}
- add to context menu a "new configuration"
- add support for batch launching (may be new run type which will be able to run other run configuration in a sequence)
- add an integration with mylyn
- Can you please make the debug dialog in a way that can be docked like the run dialog together with other views?
- Can you please make the popup which shows the progress to be able to send the progress in background like the eclipse debug.
