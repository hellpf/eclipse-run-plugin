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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import cz.hell.eclipse.run.RunPlugin;

/**
 * @author <a href="mailto:jan@loose.cz">Jan Loose (hell)</a>
 * @version $Id: AbstractDialog.java 8 2011-03-27 18:49:07Z hell $
 */
public abstract class AbstractDialog extends SelectionStatusDialog {

    protected String DIALOG_SETTINGS = AbstractDialog.class.getName();

    private static final String KEY_WIDTH = "width"; //$NON-NLS-1$

    private static final String KEY_HEIGHT = "height"; //$NON-NLS-1$

    private static final String KEY_X = "x"; //$NON-NLS-1$

    private static final String KEY_Y = "y"; //$NON-NLS-1$

    protected int DEFAULT_KEY_WIDTH = 480;

    protected int DEFAULT_KEY_HEIGHT = 450;

    private IDialogSettings settings;

    private Point location;

    private Point size;

    public AbstractDialog(Shell parent, Class<?> childClass, int width, int height) {
        super(parent);

        DEFAULT_KEY_WIDTH = width;
        DEFAULT_KEY_HEIGHT = height;

        DIALOG_SETTINGS = childClass.getName();

        initSettings();
    }

    public AbstractDialog(Shell parent, Class<? extends AbstractDialog> childClass) {
        super(parent);

        DIALOG_SETTINGS = childClass.getName();

        initSettings();
    }

    public void initSettings() {
        final IDialogSettings pluginSettings = RunPlugin.getDefault().getDialogSettings();
        IDialogSettings settings = pluginSettings.getSection(DIALOG_SETTINGS);
        if (settings == null) {
            settings = new DialogSettings(DIALOG_SETTINGS);
            settings.put(KEY_WIDTH, DEFAULT_KEY_WIDTH);
            settings.put(KEY_HEIGHT, DEFAULT_KEY_HEIGHT);
            pluginSettings.addSection(settings);
        }
        this.settings = settings;
    }

    @Override
    protected Point getInitialSize() {
        final Point result = super.getInitialSize();
        if (size != null) {
            result.x = Math.max(result.x, size.x);
            result.y = Math.max(result.y, size.y);
            final Rectangle display = getShell().getDisplay().getClientArea();
            result.x = Math.min(result.x, display.width);
            result.y = Math.min(result.y, display.height);
        }
        return result;
    }

    @Override
    protected Point getInitialLocation(Point initialSize) {
        final Point result = super.getInitialLocation(initialSize);
        if (location != null) {
            result.x = location.x;
            result.y = location.y;
            final Rectangle display = getShell().getDisplay().getClientArea();
            final int xe = result.x + initialSize.x;
            if (xe > display.width) {
                result.x -= xe - display.width;
            }
            final int ye = result.y + initialSize.y;
            if (ye > display.height) {
                result.y -= ye - display.height;
            }
        }
        return result;
    }

    @Override
    public boolean close() {
        writeSettings();
        return super.close();
    }

    /**
     * Initializes itself from the dialog settings with the same state as at the previous invocation.
     */
    protected void readSettings() {
        try {
            final int x = settings.getInt(KEY_X);
            final int y = settings.getInt(KEY_Y);
            location = new Point(x, y);
        } catch (final NumberFormatException e) {
            location = null;
        }
        try {
            final int width = settings.getInt(KEY_WIDTH);
            final int height = settings.getInt(KEY_HEIGHT);
            size = new Point(width, height);

        } catch (final NumberFormatException e) {
            size = null;
        }
    }

    /**
     * Stores it current configuration in the dialog store.
     */
    private void writeSettings() {
        final Point location = getShell().getLocation();
        settings.put(KEY_X, location.x);
        settings.put(KEY_Y, location.y);

        final Point size = getShell().getSize();
        settings.put(KEY_WIDTH, size.x);
        settings.put(KEY_HEIGHT, size.y);
    }

    @Override
    public void updateStatus(IStatus status) {
        super.updateStatus(status);
    }

    public IDialogSettings getSettings() {
        return this.settings;
    }
}
