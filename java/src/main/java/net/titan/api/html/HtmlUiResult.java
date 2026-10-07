package net.titan.api.html;

/** Result of an HTML surface write; failed writes never evict another message. */
public enum HtmlUiResult {
    OK, INVALID, INACTIVE, OVERFLOW, STALE, UNAVAILABLE;

    public boolean accepted() { return this == OK; }
}
