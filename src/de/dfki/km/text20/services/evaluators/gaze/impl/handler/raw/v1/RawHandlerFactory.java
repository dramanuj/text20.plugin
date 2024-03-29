/*
 * RawHandlerFactory.java
 * 
 * Copyright (c) 2010, Ralf Biedert, DFKI. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA 02110-1301  USA
 *
 */
package de.dfki.km.text20.services.evaluators.gaze.impl.handler.raw.v1;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.meta.Author;
import net.xeoh.plugins.base.annotations.meta.Version;
import de.dfki.km.text20.services.evaluators.gaze.listenertypes.raw.RawGazeListener;
import de.dfki.km.text20.services.evaluators.gaze.util.handler.AbstractGazeHandlerFactory;

/**
 * @author Ralf Biedert
 */
@PluginImplementation
@Version(version = 10000)
@Author(name = "Ralf Biedert")
public class RawHandlerFactory extends AbstractGazeHandlerFactory {
    public RawHandlerFactory() {
        super(RawGazeListener.class, RawHandlerImpl.class);
    }
}
