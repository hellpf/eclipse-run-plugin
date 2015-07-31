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

package cz.hell.eclipse.run.dialog;

import java.util.Arrays;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import cz.hell.eclipse.run.views.RunView.NameSorter;
import cz.hell.eclipse.run.views.RunView.ViewContentProvider;

/**
 * @author <a href="mailto:jan@loose.cz">Jan Loose (hell)</a>
 * @version $Id: RunDialog.java 8 2011-03-27 18:49:07Z hell $
 */
@SuppressWarnings("restriction")
public class RunDialog extends AbstractDialog {

    private TreeViewer viewer;

    private final String mode;

    /**
     * The Constructor.
     *
     * @param parent the shell
     * @param mode the mode
     */
    public RunDialog(Shell parent, String mode) {
        super(parent, RunDialog.class);
        this.mode = mode;

        setShellStyle(getShellStyle() | SWT.RESIZE);
        setStatusLineAboveButtons(true);
        setTitle(mode.equals(ILaunchManager.RUN_MODE) ? "Run" : "Debug");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        readSettings();

        final GridLayout layout = new GridLayout();
        final Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(layout);

        final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

        viewer = new TreeViewer(container, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setContentProvider(new ViewContentProvider(this, mode));
        viewer.setLabelProvider(new DefaultLabelProvider());
        viewer.setSorter(new NameSorter());
        viewer.setInput(this);

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                setResult(Arrays.asList(((IStructuredSelection) viewer.getSelection()).getFirstElement()));
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            public void doubleClick(DoubleClickEvent event) {
                computeResult();
                close();
            }
        });

        viewer.getControl().setLayoutData(gridData);
        viewer.getControl().setFocus();

        return container;
    }

    @Override
    protected void computeResult() {
        setResult(Arrays.asList(((IStructuredSelection) viewer.getSelection()).getFirstElement()));
    }
}
