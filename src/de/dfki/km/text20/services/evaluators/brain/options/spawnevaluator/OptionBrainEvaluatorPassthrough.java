/*
 * OptionGazeEvaluatorPassthrough.java
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
package de.dfki.km.text20.services.evaluators.brain.options.spawnevaluator;

import de.dfki.km.text20.services.evaluators.brain.BrainEvaluationListener;
import de.dfki.km.text20.services.evaluators.brain.options.AddBrainEvaluationListenerOption;
import de.dfki.km.text20.services.evaluators.common.options.SpawnEvaluatorOption;

/**
 * Passes options to the {@link BrainEvaluationListener}.
 * 
 * @author Ralf Biedert
 * @since 1.4
 */
public class OptionBrainEvaluatorPassthrough implements SpawnEvaluatorOption {

    /**  */
    private static final long serialVersionUID = 358315534220091144L;

    /** */
    private final AddBrainEvaluationListenerOption[] options;

    /**
     * Construct a new options object with the parametes to pass through.
     * 
     * @param options The parameters to pass through.
     */
    public OptionBrainEvaluatorPassthrough(AddBrainEvaluationListenerOption... options) {
        this.options = options;
    }

    /**
     * Returns the passed through parameters.
     * 
     * @return the options The parameters.
     */
    public AddBrainEvaluationListenerOption[] getOptions() {
        return this.options;
    }
}