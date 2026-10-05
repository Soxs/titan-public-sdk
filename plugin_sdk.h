/// @file plugin_sdk.h
/// @brief Deprecated: included for host-side compatibility only.
///
/// The native plugin SDK has moved to the fluent layer under `shared/titan/*.h`.
/// Plugin authors must NOT include this header directly; include
/// `<titan/plugin.h>` instead. This shim only exists so the injected client DLL
/// (which implements the low-level ABI) can still reach the private structs.

#pragma once

#include "titan/detail/abi.h"
