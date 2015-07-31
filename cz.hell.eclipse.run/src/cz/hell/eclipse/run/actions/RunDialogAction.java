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

package cz.hell.eclipse.run.actions;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import cz.hell.eclipse.run.RunPlugin;
import cz.hell.eclipse.run.dialog.RunDialog;

/**
 * @author <a href="mailto:jan@loose.cz">Jan Loose (hell)</a>
 * @version $Id: RunDialogAction.java 8 2011-03-27 18:49:07Z hell $
 */
@SuppressWarnings("restriction")
public class RunDialogAction extends Action implements IHandler, IWorkbenchWindowActionDelegate {

    /**
     * A collection of objects listening to changes to this manager. This collection is <code>null</code> if there are no listeners.
     */
    private transient ListenerList listenerList = null;

    public void addHandlerListener(IHandlerListener listener) {
        if (listenerList == null) {
            listenerList = new ListenerList(ListenerList.IDENTITY);
        }
        listenerList.add(listener);
    }

    public void removeHandlerListener(IHandlerListener listener) {
        if (listenerList != null) {
            listenerList.remove(listener);
            if (listenerList.isEmpty()) {
                listenerList = null;
            }
        }
    }

    public void dispose() {
        listenerList = null;
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        final RunDialog dialog = new RunDialog(RunPlugin.getShell(), getMode());
        if (dialog.open() == Window.OK) {
            if (dialog.getFirstResult() instanceof ILaunchConfiguration) {
                final ILaunchConfiguration configuration = (ILaunchConfiguration) dialog.getFirstResult();
                DebugUIPlugin.launchInForeground(configuration, getMode());
            }
        }

        return null;
    }

    public void run(IAction action) {
        try {
            execute(null);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    protected String getMode() {
        return ILaunchManager.RUN_MODE;
    }

    public void selectionChanged(IAction action, ISelection selection) {
    }

    public void init(IWorkbenchWindow window) {
    }
}
