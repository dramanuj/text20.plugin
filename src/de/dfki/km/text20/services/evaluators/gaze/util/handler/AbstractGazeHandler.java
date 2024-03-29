/*
 * AbstractGazeHandler.java
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
package de.dfki.km.text20.services.evaluators.gaze.util.handler;

import static net.jcores.jre.CoreKeeper.$;

import java.util.Collection;

import net.xeoh.plugins.base.PluginManager;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluationEvent;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluationListener;
import de.dfki.km.text20.services.evaluators.gaze.GazeEvaluator;
import de.dfki.km.text20.services.evaluators.gaze.GazeHandler;
import de.dfki.km.text20.services.evaluators.gaze.GazeHandlerFlags;
import de.dfki.km.text20.services.evaluators.gaze.options.AddGazeEvaluationListenerOption;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingEvent;
import de.dfki.km.text20.services.trackingdevices.eyes.EyeTrackingListener;

/**
 * An abstract {@link GazeHandler}, only used internally.
 * 
 * @author Ralf Biedert
 * @param <E>
 * @param <L>
 * @since 1.3
 */
public abstract class AbstractGazeHandler<E extends GazeEvaluationEvent, L extends GazeEvaluationListener<E>>
        implements EyeTrackingListener, GazeHandler {

    /** Related listener */
    protected L attachedListener;
    
    /** Options */
    protected AddGazeEvaluationListenerOption options[];

    /** The related gaze evaluator */
    protected GazeEvaluator gazeEvaluator;

    /** Plugin manager */
    protected PluginManager pluginManager;

    /** */
    public AbstractGazeHandler() {}

    /**
     * Calls the attached listener with an event.
     *
     * @param event
     */
    public void callListener(final E event) {
        this.attachedListener.newEvaluationEvent(event);
    }

    /**
     * Called after all fields have been set and gives the handler a chance to use these
     * fields to set itself up.
     */
    public void init() {
    }

    /* (non-Javadoc)
     * @see de.dfki.km.augmentedtext.services.trackingdevices.TrackingListener#newTrackingEvent(de.dfki.km.augmentedtext.services.trackingdevices.TrackingEvent)
     */
    @Override
    public void newTrackingEvent(final EyeTrackingEvent filteredEvent) {
        // Nothing to see here, move on.
    }

    /* (non-Javadoc)
     * @see de.dfki.km.text20.services.gazeevaluator.GazeHandler#getFlags()
     */
    @Override
    public Collection<GazeHandlerFlags> getFlags() {
        return $.list();
    }
}
