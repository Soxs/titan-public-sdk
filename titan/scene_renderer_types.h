#pragma once

#include <cstdint>
#include <type_traits>

namespace TitanPluginSdk {

// New optional interface records. These records belong to SceneRenderer v1;
// extending an existing record requires a new interface major.
inline constexpr uint32_t kSceneRendererVersion = 1;
enum SceneRendererCapability : uint32_t {
    SceneCapture = 1u << 0, SceneModels = 1u << 1, SceneTerrain = 1u << 2,
    SceneTextures = 1u << 3, SceneComposite = 1u << 4,
    SceneNativeSuppression = 1u << 5
};
enum class SceneRenderResult : uint32_t { Skipped = 0, Composited = 1, Failed = 2 };
enum SceneMeshFlag : uint32_t { SceneMeshTerrain = 1u, SceneMeshDynamic = 2u };

struct SceneRenderVertex {
    float position[3]{};
    float normal[3]{};
    float uv[2]{};
    uint32_t colourRgba = 0xffffffffu; // R in bits 0..7, alpha in bits 24..31.
    int32_t textureId = -1;            // Native texture identity; not an atlas layer.
    float opacity = 1.0f;
    float depthBias = 0.0f; // Native signed units: +1 subtracts 5 camera-Z units before projection.
    int32_t priority = 0; // Native face priority, including special 10/11 ordering.
    uint32_t flags = 0;
};
struct SceneRenderMesh {
    uint64_t id = 0;
    uint64_t revision = 0;
    const SceneRenderVertex* vertices = nullptr;
    const uint32_t* indices = nullptr;
    uint32_t vertexCount = 0;
    uint32_t indexCount = 0;
    uint32_t flags = 0;
    uint32_t reserved = 0;
};
struct SceneRenderInstance {
    uint64_t meshId = 0;
    uint64_t selectionKey = 0;
    // Column-major local-to-camera matrix. Includes the nested WorldView
    // transform. Native coordinates are converted by the host, once.
    float modelView[16]{};
    int32_t worldViewId = -1;
    uint32_t flags = 0;
};
struct SceneRenderTexture {
    int32_t id = -1;
    uint32_t width = 0;
    uint32_t height = 0;
    uint32_t reserved = 0;
    uint64_t revision = 0;
    const uint8_t* rgba = nullptr; // Tightly packed RGBA8; width * height * 4.
    float scrollU = 0.0f; // Optional UV velocity per second; zero for already-scrolled decoded pixels.
    float scrollV = 0.0f;
};
struct SceneRenderFrame {
    uint32_t structSize = sizeof(SceneRenderFrame);
    uint32_t version = kSceneRendererVersion;
    uint64_t frameId = 0;
    uint64_t sceneGeneration = 0;
    uint64_t contextKey = 0;
    float projection[16]{}; // Column-major camera-to-OpenGL clip matrix.
    int32_t viewportX = 0, viewportY = 0, viewportWidth = 0, viewportHeight = 0;
    int32_t framebufferWidth = 0, framebufferHeight = 0;
    uint32_t destinationFramebuffer = 0;
    uint32_t flags = 0; // Reserved; native suppression is not implied by composition.
    const SceneRenderMesh* meshes = nullptr;
    const SceneRenderInstance* instances = nullptr;
    const SceneRenderTexture* textures = nullptr;
    uint32_t meshCount = 0, instanceCount = 0, textureCount = 0;
    uint32_t reserved = 0;
};
// Separate optional record: SceneRenderFrame v1 remains byte-for-byte unchanged.
struct SceneFrameEnvironment {
    uint32_t structSize = sizeof(SceneFrameEnvironment);
    uint32_t version = 1;
    uint64_t frameId = 0, contextKey = 0;
    float cameraToScene[16]{}; // Column-major; root scene-local coordinates, +Y up.
    float cameraPosition[3]{}; // Translation of cameraToScene, in native scene units.
    uint32_t flags = 0; // Bit 0: verified root scene transform.
    int32_t rootWorldViewId = -1;
    uint32_t reserved = 0;
};
// Optional SDK 165 query. These views share the exact callback lifetime; they
// do not extend or reinterpret any SceneRenderer v1 payload.
struct SceneTerrainFace {
    int32_t worldX=0, worldY=0, plane=0;
    int32_t underlayId=0, overlayId=0;
    uint32_t flags=0; // 1: overlay face, 2: valid world coordinates, 4: underground.
    int32_t originalHsl[3]{};
};
struct SceneTerrainMetadata {
    uint64_t meshId=0, revision=0;
    const SceneTerrainFace* faces=nullptr; // In emitted index-triplet order.
    uint32_t faceCount=0, reserved=0;
};
struct SceneEntityMetadata {
    uint64_t selectionKey=0;
    int32_t worldViewId=-1, kind=0; // 1: NPC, 2: object; zero means unavailable.
    int32_t definitionId=-1, animationId=-1, plane=0, orientation=0;
    int32_t sizeX=1, sizeY=1;
};
struct SceneWorldVariable {
    int32_t kind=0, id=0, value=0; // kind 1: varbit, 2: varp; -1 value unavailable.
};
struct SceneEffectEmitter {
    uint64_t identity=0;
    int32_t kind=0, definitionId=-1; // 3: projectile, 4: graphics object.
    int32_t plane=0, animationId=-1;
    float position[3]{}; // Root scene units, +Y up.
    int32_t orientation=0;
};
struct SceneFrameWorldData {
    uint32_t structSize=sizeof(SceneFrameWorldData), version=1;
    uint64_t frameId=0, contextKey=0;
    int32_t baseX=0, baseY=0, plane=0, focusX=0, focusY=0;
    uint32_t flags=0; // 1: root origin valid, 2: focus valid, 4: instanced root.
    const SceneTerrainMetadata* terrain=nullptr;
    const SceneEntityMetadata* entities=nullptr;
    uint32_t terrainCount=0, entityCount=0;
    const SceneEffectEmitter* effects=nullptr;
    const SceneWorldVariable* variables=nullptr;
    uint32_t effectCount=0, variableCount=0;
};
struct SceneRendererCallbacks {
    uint32_t structSize = sizeof(SceneRendererCallbacks);
    uint32_t version = kSceneRendererVersion;
    void* userData = nullptr;
    // Current GL context, before native UI. All views are immutable and valid
    // only for this call. Do not call game/lifecycle APIs from this callback.
    // Render to a private FBO first; Failed/Skipped must leave native colour intact.
    uint32_t (*render)(void*, const SceneRenderFrame*) = nullptr;
};
struct SceneRendererStatus {
    uint32_t structSize = sizeof(SceneRendererStatus);
    uint32_t capabilities = 0;
    uint64_t frameId = 0;
    uint64_t sceneGeneration = 0;
    uint64_t capturedModels = 0, capturedTriangles = 0, droppedFrames = 0;
    uint32_t registered = 0, enabled = 0, lastResult = 0, reserved = 0;
    char diagnostic[256]{};
};
enum class SceneGlResourceKind : uint32_t {
    Buffer = 1, VertexArray, Texture, Framebuffer, Renderbuffer, Program
};
struct SceneGlResource { uint32_t kind = 0, id = 0; };

static_assert(std::is_standard_layout_v<SceneRenderFrame>);
static_assert(std::is_trivially_copyable_v<SceneRenderVertex>);
static_assert(sizeof(SceneRenderVertex) == 56);
static_assert(sizeof(SceneRenderMesh) == 48);
static_assert(sizeof(SceneRenderInstance) == 88);
static_assert(sizeof(SceneRenderTexture) == 40);
static_assert(sizeof(SceneRenderFrame) == 168);
static_assert(sizeof(SceneFrameEnvironment) == 112);
static_assert(sizeof(SceneTerrainFace) == 36);
static_assert(sizeof(SceneTerrainMetadata) == 32);
static_assert(sizeof(SceneEntityMetadata) == 40);
static_assert(sizeof(SceneEffectEmitter) == 40);
static_assert(sizeof(SceneWorldVariable) == 12);
static_assert(sizeof(SceneFrameWorldData) == 96);
static_assert(std::is_trivially_copyable_v<SceneFrameWorldData>);
static_assert(sizeof(SceneRendererCallbacks) == 24);
static_assert(sizeof(SceneRendererStatus) == 320);
static_assert(sizeof(SceneGlResource) == 8);

} // namespace TitanPluginSdk
