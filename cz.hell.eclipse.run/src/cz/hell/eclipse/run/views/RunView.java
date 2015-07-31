/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.hell.eclipse.run.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.core.LaunchManager;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.eclipse.ui.part.ViewPart;

import cz.hell.eclipse.run.RunPlugin;

/**
 * The view with run/debug configurations.
 *
 * @author <a href="mailto:jan@loose.cz">Jan Loose (hell)</a>
 * @version $Id: RunView.java 8 2011-03-27 18:49:07Z hell $
 */
@SuppressWarnings("restriction")
public class RunView extends ViewPart {

    public static final ImageDescriptor IMG_RUN = DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_OBJS_LAUNCH_RUN);

    public static final ImageDescriptor IMG_DEBUG = DebugPluginImages.getImageDescriptor(IDebugUIConstants.IMG_OBJS_LAUNCH_DEBUG);

    public static final String SHOW_HIDE_TOOLTIP = "Show/Hide configuration types.";

    public static final String OPENED_TOOLTIP = "Show only configuration types from opened projects.";

    public static final String CONFIGURE_RUN = "Configure r&un...";

    public static final String CONFIGURE_DEBUG = "Configure debu&g...";

    public static final String DEBUG = "&Debug";

    public static final String RUN = "&Run";

    public static final String HIDE_TYPES = "Hide &Types";

    public static final String SHOW_TYPES = "Show &Types";

    public static final String OPENED = "&Opened Project";

    public static final String KEY_OPENED = "opened";

    public static final String KEY_TYPES = "types";

    private TreeViewer viewer;

    private Action typesAction;

    private Action openedAction;

    private Action runAction;

    private Action debugAction;

    private Action configureRunAction;

    private Action configureDebugAction;

    private ViewContentProvider provider;

    private IResourceChangeListener listener = new IResourceChangeListener() {

        public void resourceChanged(IResourceChangeEvent event) {
            if (provider.isOpened()) {
                if (event.getSource() instanceof IWorkspace) {
                    if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
                        RunPlugin.getStandardDisplay().asyncExec(new Runnable() {

                            public void run() {
                                viewer.refresh(true);
                            }
                        });
                    }
                }
            }
        }
    };

    /**
     * The Class ViewContentProvider.
     */
    public static class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        private boolean types = RunPlugin.getDefault().getPreferenceStore().getBoolean(KEY_TYPES);

        private boolean opened = RunPlugin.getDefault().getPreferenceStore().getBoolean(KEY_OPENED);

        private Object root;

        private String mode;

        public ViewContentProvider(Object root) {
            this.root = root;
        }

        public ViewContentProvider(Object root, String mode) {
            this.root = root;
            this.mode = mode;
        }

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            if (parent.equals(root)) {
                if (!types) {
                    try {
                        return getConfigurations().toArray();
                    } catch (CoreException e) {
                    }
                }
                return getTypes().toArray();
            }
            return getChildren(parent);
        }

        public Object getParent(Object child) {
            return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof ILaunchConfigurationType) {
                try {
                    return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations((ILaunchConfigurationType) parent);
                } catch (CoreException e) {
                }
            }
            return new Object[0];
        }

        public boolean hasChildren(Object parent) {
            return getChildren(parent).length > 0;
        }

        private List<ILaunchConfigurationType> getTypes() {
            final ILaunchConfigurationType[] types = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationTypes();
            final List<ILaunchConfigurationType> list = new ArrayList<ILaunchConfigurationType>();
            for (ILaunchConfigurationType type : types) {
                if (isTypeSupported(type)) {
                    list.add(type);
                }
            }
            return list;
        }

        private List<ILaunchConfiguration> getConfigurations() throws CoreException {
            final ILaunchConfiguration[] configurations = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurations();
            final List<ILaunchConfiguration> list = new ArrayList<ILaunchConfiguration>();
            for (ILaunchConfiguration c : configurations) {
                if (isTypeSupported(c.getType())) {
                    if (isOpened() && !c.getType().getPluginIdentifier().equals("org.eclipse.pde.ui")) {
                        try {
                            for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
                                if (project.isAccessible()) {
                                    if (c.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "").equals("loose")) {
                                        list.add(c);
                                        break;
                                    } else if (c.getAttribute("org.eclipse.jdt.launching.WORKING_DIRECTORY", "").endsWith("loose" + "}")) {
                                        list.add(c);
                                        break;
                                    }
                                }
                            }
                        } catch (CoreException ex) {
                            // nothing to do
                        }
                    } else {
                        list.add(c);
                    }
                }
            }
            return list;
        }

        private boolean isTypeSupported(final ILaunchConfigurationType type) {
            if (mode == null) {
                return (type.supportsMode(ILaunchManager.RUN_MODE) || type.supportsMode(ILaunchManager.DEBUG_MODE))
                                && !IExternalToolConstants.ID_EXTERNAL_TOOLS_BUILDER_LAUNCH_CATEGORY.equals(type.getCategory());
            } else {
                return (type.supportsMode(mode)) && !IExternalToolConstants.ID_EXTERNAL_TOOLS_BUILDER_LAUNCH_CATEGORY.equals(type.getCategory());
            }
        }

        public void invertTypes() {
            types = !types;
            RunPlugin.getDefault().getPreferenceStore().setValue(KEY_TYPES, types);
        }

        public boolean isTypes() {
            return types;
        }

        public void invertOpened() {
            opened = !opened;
            RunPlugin.getDefault().getPreferenceStore().setValue(KEY_OPENED, opened);
        }

        public boolean isOpened() {
            return opened;
        }
    }

    /**
     * The Class NameSorter.
     */
    public static class NameSorter extends ViewerSorter {
    }

    /**
     * The constructor.
     */
    public RunView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize it.
     */
    public void createPartControl(Composite parent) {
        viewer = new TreeViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(provider = new ViewContentProvider(getViewSite()));
        viewer.setLabelProvider(new DefaultLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(getViewSite());

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                final ILaunchConfiguration configuration = getLaunchConfiguration();
                if (configuration != null) {
                    try {
                        final ILaunchConfigurationType type = configuration.getType();
                        if (type.supportsMode(ILaunchManager.RUN_MODE)) {
                            runAction.setEnabled(event != null);
                            configureRunAction.setEnabled(event != null);
                        } else {
                            runAction.setEnabled(false);
                            configureRunAction.setEnabled(false);
                        }
                        if (type.supportsMode(ILaunchManager.DEBUG_MODE)) {
                            debugAction.setEnabled(event != null);
                            configureDebugAction.setEnabled(event != null);
                        } else {
                            debugAction.setEnabled(false);
                            configureDebugAction.setEnabled(false);
                        }
                    } catch (CoreException e) {
                    }
                } else {
                    runAction.setEnabled(false);
                    debugAction.setEnabled(false);
                    configureRunAction.setEnabled(false);
                    configureDebugAction.setEnabled(false);
                }
            }
        });

        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();

        DebugPlugin.getDefault().getLaunchManager().addLaunchConfigurationListener(new ILaunchConfigurationListener() {

            public void launchConfigurationAdded(ILaunchConfiguration configuration) {
                refresh();
            }

            public void launchConfigurationChanged(ILaunchConfiguration configuration) {
                refresh();
            }

            public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
                refresh();
            }

            private void refresh() {
                RunPlugin.getStandardDisplay().asyncExec(new Runnable() {

                    public void run() {
                        viewer.refresh(true);
                    }
                });
            }
        });

        ResourcesPlugin.getWorkspace().addResourceChangeListener(listener, IResourceChangeEvent.POST_CHANGE);
    }

    private void hookContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                RunView.this.fillContextMenu(manager);
            }
        });

        final Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, viewer);
    }

    private void contributeToActionBars() {
        final IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(runAction);
        manager.add(debugAction);
        manager.add(new Separator());
        manager.add(typesAction);
        manager.add(openedAction);
    }

    private void fillContextMenu(IMenuManager manager) {
        manager.add(runAction);
        manager.add(debugAction);
        manager.add(new Separator());
        manager.add(typesAction);
        manager.add(openedAction);
        manager.add(new Separator());
        manager.add(configureRunAction);
        manager.add(configureDebugAction);
        // other plug-ins can contribute there actions here
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(runAction);
        manager.add(debugAction);
    }

    private void makeActions() {

        // show/hide configurations types actions
        typesAction = new Action(SHOW_TYPES) {

            public void run() {
                provider.invertTypes();
                viewer.refresh(true);
                if (provider.isTypes()) {
                    setText(HIDE_TYPES);
                } else {
                    setText(SHOW_TYPES);
                }
            }
        };
        typesAction.setToolTipText(SHOW_HIDE_TOOLTIP);

        // show only configuration types from opened projects action
        openedAction = new Action(OPENED, SWT.CHECK) {

            public void run() {
                provider.invertOpened();
                viewer.refresh(true);
            }
        };
        openedAction.setChecked(provider.isOpened());
        openedAction.setToolTipText(OPENED_TOOLTIP);

        // run action
        runAction = new Action() {

            public void run() {
                launch(LaunchManager.RUN_MODE);
            }
        };
        runAction.setText(RUN);
        runAction.setToolTipText(RUN);
        runAction.setImageDescriptor(IMG_RUN);
        runAction.setEnabled(false);

        // run action
        debugAction = new Action() {

            public void run() {
                launch(LaunchManager.DEBUG_MODE);
            }
        };
        debugAction.setText(DEBUG);
        debugAction.setToolTipText(DEBUG);
        debugAction.setImageDescriptor(IMG_DEBUG);
        debugAction.setEnabled(false);

        // configure run action
        configureRunAction = new Action() {

            public void run() {
                final ILaunchConfiguration configuration = getLaunchConfiguration();
                if (configuration != null) {
                    final ILaunchGroup group = DebugUITools.getLaunchGroup(getLaunchConfiguration(), LaunchManager.RUN_MODE);
                    if (group != null) {
                        DebugUIPlugin.openLaunchConfigurationEditDialog(RunPlugin.getShell(), configuration, group.getIdentifier(), null, true);
                    }
                }
            }
        };
        configureRunAction.setText(CONFIGURE_RUN);

        // configure debug action
        configureDebugAction = new Action() {

            public void run() {
                final ILaunchConfiguration configuration = getLaunchConfiguration();
                if (configuration != null) {
                    final ILaunchGroup group = DebugUITools.getLaunchGroup(getLaunchConfiguration(), LaunchManager.DEBUG_MODE);
                    if (group != null) {
                        DebugUIPlugin.openLaunchConfigurationEditDialog(RunPlugin.getShell(), configuration, group.getIdentifier(), null, true);
                    }
                }
            }
        };
        configureDebugAction.setText(CONFIGURE_DEBUG);
    }

    private void launch(String mode) {
        final ILaunchConfiguration configuration = getLaunchConfiguration();
        if (configuration != null) {
            DebugUIPlugin.launchInForeground(configuration, mode);
        }
    }

    private ILaunchConfiguration getLaunchConfiguration() {
        final ISelection selection = viewer.getSelection();
        final Object obj = ((IStructuredSelection) selection).getFirstElement();
        if (obj instanceof ILaunchConfiguration) {
            return (ILaunchConfiguration) obj;
        }
        return null;
    }

    private void hookDoubleClickAction() {
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                launch(LaunchManager.RUN_MODE);
            }
        });
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        viewer.getControl().setFocus();
    }

    @Override
    public void dispose() {
        super.dispose();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(listener);
    }
}