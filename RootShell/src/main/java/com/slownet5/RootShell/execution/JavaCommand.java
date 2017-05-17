/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.RootShell.execution;

import android.content.Context;

public class JavaCommand extends Command
{
    /**
     * Constructor for executing Java commands rather than binaries
     *
     * @param context     needed to execute java command.
     */
    public JavaCommand(int id, Context context, String... command) {
        super(id, command);
        this.context = context;
        this.javaCommand = true;
    }

    /**
     * Constructor for executing Java commands rather than binaries
     *
     * @param context     needed to execute java command.
     */
    public JavaCommand(int id, boolean handlerEnabled, Context context, String... command) {
        super(id, handlerEnabled, command);
        this.context = context;
        this.javaCommand = true;
    }

    /**
     * Constructor for executing Java commands rather than binaries
     *
     * @param context     needed to execute java command.
     */
    public JavaCommand(int id, int timeout, Context context, String... command) {
        super(id, timeout, command);
        this.context = context;
        this.javaCommand = true;
    }


    @Override
    public void commandOutput(int id, String line)
    {
        super.commandOutput(id, line);
    }

    @Override
    public void commandTerminated(int id, String reason)
    {
        // pass
    }

    @Override
    public void commandCompleted(int id, int exitCode)
    {
        // pass
    }
}
