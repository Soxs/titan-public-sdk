package net.titan.api;

/**
 * Internal marker for SDK snapshots that can dispatch actions back through
 * the injected Titan client.
 */
public interface ClientBacked {
    void bindClient(Client client);
    Client client();

    default Client requireClient() {
        Client value = client();
        if (value == null) {
            throw new IllegalStateException(
                "This SDK object is not attached to a Titan Client instance.");
        }
        return value;
    }
}
