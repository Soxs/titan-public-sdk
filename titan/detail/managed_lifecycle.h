#pragma once
#include <memory>
namespace titan::detail {
// Plugin-owned lifetimes, pumped by SDK thunks rather than author overrides.
class ManagedLifecycle {
public:
    virtual ~ManagedLifecycle() = default;
    virtual void pump() = 0;
    virtual void tick() {}
    virtual void stop() = 0;
    virtual void close() { stop(); }
};
}
