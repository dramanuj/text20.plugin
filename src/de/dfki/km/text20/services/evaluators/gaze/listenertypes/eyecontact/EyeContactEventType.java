/*
 * FixationEventType.java
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
package de.dfki.km.text20.services.evaluators.gaze.listenertypes.eyecontact;

/**
 * The type of eye contact.
 * 
 * @author Ralf Biedert
 * @sine 1.4
 */
public enum EyeContactEventType {
    /**
     * If the tracking devices did not see both eyes anymore.
     */
    EYECONTACT_LOST,

    /**
     * If the eye tracking device has just seen at leaset one of the eyes again.
     */
    EYECONTACT_FOUND,

    /**
     * If the last LOST-FOUND-cycle was probably due to a blink. Might be fired late,
     * (e.g., 500ms *after* the eyes have been re-opened again).
     */
    BLINK_SUCCEEDED,
}
