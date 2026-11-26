package net.titan.runtime.spi;

/** Titan's own SDK, offered to one provider plugin as {@link GameApi} offers a provider's game API to a Titan
 * plugin. It is for that plugin's own classes. A provider does not use it for itself. */
public interface TitanApi {
    /** Finds the classes of {@code net.titan.api}, which the host defines, and no others. */
    ClassLoader loader();
    /** The client a plugin may inject, and its type. Calls belong to the plugin whose callback makes them. */
    Class<?> clientType();
    Object client();
}
