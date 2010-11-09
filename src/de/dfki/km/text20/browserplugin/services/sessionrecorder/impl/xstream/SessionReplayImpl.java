package de.dfki.km.text20.browserplugin.services.sessionrecorder.impl.xstream;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;

import net.xeoh.plugins.base.util.OptionUtils;

import com.thoughtworks.xstream.XStream;

import de.dfki.km.text20.browserplugin.services.sessionrecorder.ReplayListener;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.SessionReplay;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.AbstractSessionEvent;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.ImageEvent;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.PropertyEvent;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.ScreenSizeEvent;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.events.pseudo.PseudoImageEvent;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.impl.xstream.loader.AbstractLoader;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.impl.xstream.loader.ZIPLoader;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.ReplayOption;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.replay.OptionGetMetaInfo;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.replay.OptionLoadImages;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.replay.OptionRealtime;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.replay.OptionSlowMotion;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.options.replay.OptionWaitForFinish;
import de.dfki.km.text20.browserplugin.services.sessionrecorder.util.metadata.DisplacementRegion;

/**
 * @author rb
 *
 */
public class SessionReplayImpl implements SessionReplay {

    /** Logger */
    final Logger logger = Logger.getLogger(this.getClass().getName());

    /** Xstream (de)serializer */
    final XStream xstream = new XStream();

    /** Input stream to read the content from */
    ObjectInputStream in;

    /** List of events to filter */
    final List<Class<? extends AbstractSessionEvent>> toFilter = new ArrayList<Class<? extends AbstractSessionEvent>>();

    /** List of properties stored in the replay */
    final Map<String, String> propertyMap = new HashMap<String, String>();

    /** The recorded screen size */
    Dimension screenSize;

    /** The file to replay. This can either be a zip file (with an internal .xstream) or an xstream directly. */
    private File file;

    /** The loader to access elements */
    AbstractLoader loader = null;

    /** Displacement regions to apply */
    private List<DisplacementRegion> fixationDisplacementRegions;


    /**
     * @param file
     */
    public SessionReplayImpl(final File file) {
        if (!file.exists()) {
            throw new IllegalArgumentException("file: " + file.getAbsolutePath() + " does not exist");
        }

        this.file = file;

        SessionStreamer.setAlias(this.xstream);
        SessionStreamer.registerConverters(this.xstream);

        // Parse file to get properties and screen size
        this.getMetaInfo();
    }

    @Override
    public List<DisplacementRegion> getDisplacements() {
        return new ArrayList<DisplacementRegion>();
    }

    @Override
    public Map<String, String> getProperties(String... properties) {
        // waitForFinish();
        return this.propertyMap;
    }

    @Override
    public Dimension getScreenSize() {
        // waitForFinish();
        return this.screenSize;
    }

    @Override
    public synchronized void replay(final ReplayListener listener, final ReplayOption... options) {
        createInputStream();

        // Sanity check
        if (this.in == null) {
            this.logger.warning("Unable to load replay for file " + this.file);
            return;
        }

        // Options
        final AtomicBoolean gettingMetaInfo = new AtomicBoolean(false);
        final AtomicBoolean realtimeReplay = new AtomicBoolean(false);
        final AtomicBoolean loadImages = new AtomicBoolean(false);

        // Some variables
        final AtomicLong slowdownFactor = new AtomicLong(1);
        final AtomicLong currentEvenTime = new AtomicLong();
        final AtomicLong firstEventTime = new AtomicLong();
//        final AtomicLong realtimeDuration = new AtomicLong();

        // Process options
        final OptionUtils<ReplayOption> ou = new OptionUtils<ReplayOption>(options);
        if (ou.contains(OptionWaitForFinish.class)) throw new IllegalArgumentException();
        if (ou.contains(OptionGetMetaInfo.class)) gettingMetaInfo.set(true);
        if (ou.contains(OptionRealtime.class)) realtimeReplay.set(true);
        if (ou.contains(OptionLoadImages.class)) loadImages.set(true);
        if (ou.contains(OptionSlowMotion.class)) {
            realtimeReplay.set(true);
            slowdownFactor.set(ou.get(OptionSlowMotion.class).getFactor());
        }

        // (Fixed Issue #30)
        // Create the actual replay thread
        final Thread t = new Thread(new Runnable() {

            private boolean hasMore = true;

            @Override
            public void run() {

                try {
                    AbstractSessionEvent previousEvent = null;

                    // As long as we have more events
                    while (this.hasMore) {
                        AbstractSessionEvent event = null;

                        try {
                            // Load the next event
                            event = (AbstractSessionEvent) SessionReplayImpl.this.in.readObject();

                            // In case we have no previous event, save the first event time
                            if (previousEvent == null) {
                                firstEventTime.set(event.originalEventTime);
                                previousEvent = event;
                            }

                            // Store current event time
                            currentEvenTime.set(event.originalEventTime);

                            // Dont process if filtered
                            if (SessionReplayImpl.this.toFilter.contains(event.getClass())) {
                                continue;
                            }

                            // Can be switched off, to make replay as fast as possible.
                            if (realtimeReplay.get()) {
                                long delta = event.originalEventTime - previousEvent.originalEventTime;

                                // TODO: When does this happen?
                                if (delta < 0) {
                                    SessionReplayImpl.this.logger.fine("Event times are mixed up " + event.originalEventTime + " < " + previousEvent.originalEventTime);
                                    delta = 0;
                                }

                                // And now wait for the given time
                                try {
                                    Thread.sleep(delta * slowdownFactor.get());
                                } catch (final InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            // Check what kind of event it is and if we have some special rules
                            if (loadImages.get() && event instanceof ImageEvent) {
                                final ImageEvent e = (ImageEvent) event;

                                // TODO: Image loading should be fixed... it's still using a special loader only used with zip files...
                                if (SessionReplayImpl.this.loader == null) {
                                    continue;
                                }

                                final InputStream is = SessionReplayImpl.this.loader.getFile(e.associatedFilename);
                                final BufferedImage read = ImageIO.read(is);

                                event = new PseudoImageEvent(e, read);
                            }

                            // Now we are permitted to fire the event.
                            try {
                                listener.nextEvent(event);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            previousEvent = event;
                        } catch (EOFException e) {
                            this.hasMore = false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    try {
                        SessionReplayImpl.this.in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.setDaemon(true);
        t.start();
    }

    /** */
    private void createInputStream() {
        try {
            InputStream input = null;

            // First check if we have a .zip ...
            if (this.file.getAbsolutePath().endsWith(".zip")) {
                this.loader = new ZIPLoader(this.file);
                input = this.loader.getSessionInputStream();
            }

            // ... and check if we have .xstream file
            if (this.file.getAbsolutePath().endsWith(".xstream")) {
                input = new FileInputStream(this.file);
            }

            // ... and it might be a gz file
            // (Fixed Issue #16)
            if (this.file.getAbsolutePath().endsWith(".gz")) {
                input = new GZIPInputStream(new FileInputStream(this.file));
            }

            // (Fixed Issue #26)
            this.in = this.xstream.createObjectInputStream(new BufferedReader(new InputStreamReader(input, "UTF-8")));

        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /** */
    private void getMetaInfo() {
        this.createInputStream();

        boolean isFinishedReading = false;

        while (!isFinishedReading) {
            try {
                final AbstractSessionEvent event = (AbstractSessionEvent) this.in.readObject();

                if (event instanceof ScreenSizeEvent) {
                    this.screenSize = ((ScreenSizeEvent) event).screenSize;
                    continue;
                }

                if (event instanceof PropertyEvent) {
                    this.propertyMap.put(((PropertyEvent) event).key, ((PropertyEvent) event).value);
                    continue;
                }

            } catch (EOFException e) {
                isFinishedReading = true;

                // TODO: Is this neccessary and if yes, how to implement it correctly?
//                realtimeDuration.set(currentEvenTime.get() - firstEventTime.get());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            this.in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return .
     */
    public synchronized List<DisplacementRegion> getFixationDisplacementRegions() {
        return this.fixationDisplacementRegions;
    }

    /**
     * @param fixationDisplacementRegions
     */
    public synchronized void setFixationDisplacementRegions(final List<DisplacementRegion> fixationDisplacementRegions) {
        this.fixationDisplacementRegions = fixationDisplacementRegions;
    }

    /**
     * Events of that class wont be passed.
     *
     * @param filter
     */
    @SuppressWarnings("unused")
    private void addFilter(final Class<? extends AbstractSessionEvent> filter) {
        this.toFilter.add(filter);
    }
}
