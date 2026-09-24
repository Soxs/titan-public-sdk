package net.titan.api.crosstab;

/** Options for {@link CrossTab#put} and {@link CrossTab#putIf} (SDK 141). */
public enum CrossTabOption {
    /**
     * Handle the value as a secret: never logged or persisted, sent only to
     * tabs this controller launched (other tabs see it redacted), and read
     * back only through {@link CrossTab#getSecret}. Refused in a tab this
     * controller did not launch. It is not access control: other plugins in
     * the same tab share the process.
     */
    SECRET
}
