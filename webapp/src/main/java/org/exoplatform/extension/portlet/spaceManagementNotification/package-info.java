/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

@Portlet
@Application(name = "SpaceManagementNotification")
@Scripts({
        @Script(id = "spaceManagementNotification", value = "js/spacemanagementnotification.js")
})
@Stylesheets ({
                @Stylesheet(value = "/org/exoplatform/extension/portlet/spaceManagementNotification/assets/skin/spaceManagementNotification.css")
})
@Less("skin/spaceManagementNotification.less")
@Assets("*")

package org.exoplatform.extension.portlet.spaceManagementNotification;

import juzu.Application;
import juzu.plugin.asset.Assets;
import juzu.plugin.asset.Script;
import juzu.plugin.asset.Stylesheet;
import juzu.plugin.asset.Stylesheets;
import juzu.plugin.asset.Scripts;


import juzu.plugin.less.Less;
import juzu.plugin.portlet.Portlet;


