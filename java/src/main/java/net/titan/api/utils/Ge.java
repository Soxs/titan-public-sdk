package net.titan.api.utils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.titan.api.*;

/** Thin binding to the C++ GE utility. Java owns no queue, worker, or state machine. */
public final class Ge {
    private Ge() {}
    /** Returns 0 if invalid, logged out, unsupported, or the host queue is full. */
    public static long addBuyToQueue(GeBuyOptions options) { return Titan.client().geSubmitBuy(options); }
    public static long addBuyToQueue(int id, int quantity) { return addBuyToQueue(new GeBuyOptions(id, quantity)); }
    public static long addBuyToQueue(int id, int quantity, boolean autoOpen) {
        return addBuyToQueue(id, quantity, true, autoOpen, true, 3, 10000);
    }
    public static long addBuyToQueue(int id, int quantity, boolean toInventory, boolean autoOpen) {
        return addBuyToQueue(id, quantity, toInventory, autoOpen, true, 3, 10000);
    }
    public static long addBuyToQueue(int id, int quantity, boolean toInventory, boolean autoOpen, boolean noted) {
        return addBuyToQueue(id, quantity, toInventory, autoOpen, noted, 3, 10000);
    }
    public static long addBuyToQueue(int id, int quantity, boolean toInventory, boolean autoOpen, boolean noted, int attempts, int waitMs) {
        return addBuyToQueue(new GeBuyOptions(id, quantity, toInventory, autoOpen, noted, attempts, waitMs, 180000, 0));
    }
    /** Selling is an explicit unsupported stub and never enqueues an operation. */
    public static long addSellToQueue(int id, int quantity) { return 0; }
    public static Optional<GeRequest> request(long id) {
        if (id <= 0) return Optional.empty();
        return Titan.client().geRequests(id).stream().findFirst();
    }
    public static List<GeRequest> getRequests() { return Titan.client().geRequests(0); }
    public static List<GeRequest> getExchangeQueue() {
        return getRequests().stream().filter(r -> !r.isComplete()).collect(Collectors.toUnmodifiableList());
    }
    public static int getQueueSize() { return getExchangeQueue().size(); }
    public static boolean isExchanging() { return getQueueSize() != 0; }
    public static String getStatus() { return getExchangeQueue().stream().map(GeRequest::message).findFirst().orElse(""); }
    public static boolean abortRequest(long id) { return Titan.client().geCancelRequest(id); }
    public static boolean release(long id) { return Titan.client().geReleaseRequest(id); }
    public static void clearExchangeQueue() { for (GeRequest r : getExchangeQueue()) abortRequest(r.requestId()); }
}
