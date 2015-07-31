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

import org.eclipse.core.commands.IHandler;
import org.eclipse.debug.core.ILaunchManager;

/**
 * @author <a href="mailto:jan@loose.cz">Jan Loose (hell)</a>
 * @version $Id: DebugDialogAction.java 8 2011-03-27 18:49:07Z hell $
 */
public class DebugDialogAction extends RunDialogAction implements IHandler {

    @Override
    protected String getMode() {
        return ILaunchManager.DEBUG_MODE;
    }
}
