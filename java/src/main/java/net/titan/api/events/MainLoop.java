package net.titan.api.events;

/**
 * Fired once per outer client MAIN_LOOP iteration, including title/login
 * screens and gameplay.
 *
 * <p>Static definition-cache reads are available in every client state. Check
 * the login facade's world-ready state before live client, entity, widget,
 * scene, or projection queries. Added in Titan Java SDK 0.1.43.</p>
 */
public final class MainLoop {
}
