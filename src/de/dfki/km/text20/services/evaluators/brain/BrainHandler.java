/*
 * GazeHandler.java
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
package de.dfki.km.text20.services.evaluators.brain;

import java.util.Collection;

import de.dfki.km.text20.services.evaluators.common.Handler;
import de.dfki.km.text20.services.evaluators.gaze.util.handler.AbstractGazeHandler;
import de.dfki.km.text20.services.trackingdevices.brain.BrainTrackingEvent;

/**
 * Used internally, handles gaze events for registered listener. 
 * 
 * @author Ralf Biedert
 * @since 1.3
 * @see AbstractGazeHandler
 */
public interface BrainHandler extends Handler<BrainTrackingEvent> {

    /**
     * Returns the flags for this handler. 
     * 
     * @return All current flags.
     */
    public Collection<BrainHandlerFlags> getFlags();
}
