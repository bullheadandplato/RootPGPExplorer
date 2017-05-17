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

package com.slownet5.RootTools.containers;

public class Permissions
{
    String type;
    String user;
    String group;
    String other;
    String symlink;
    int permissions;

    public String getSymlink()
    {
        return this.symlink;
    }

    public String getType()
    {
        return type;
    }

    public int getPermissions()
    {
        return this.permissions;
    }

    public String getUserPermissions()
    {
        return this.user;
    }

    public String getGroupPermissions()
    {
        return this.group;
    }

    public String getOtherPermissions()
    {
        return this.other;
    }

    public void setSymlink(String symlink)
    {
        this.symlink = symlink;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setPermissions(int permissions)
    {
        this.permissions = permissions;
    }

    public void setUserPermissions(String user)
    {
        this.user = user;
    }

    public void setGroupPermissions(String group)
    {
        this.group = group;
    }

    public void setOtherPermissions(String other)
    {
        this.other = other;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getOther()
    {
        return other;
    }

    public void setOther(String other)
    {
        this.other = other;
    }



}
