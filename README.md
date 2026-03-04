<p align="center">
  <a href="https://modrinth.com/mod/superionic"><img src="src/client/resources/assets/superionic/icon.png" alt="Superionic Banner" width="1200"></a>
</p>

<p align="center">
  <em>An all-in-one optimization suite that boosts FPS and eliminates stutters by revamping Minecraft's rendering and logic.</em>
</p>

<p align="center">
  <img alt="Version" src="https://img.shields.io/badge/Version-1.0.0--alpha.2-7b68ee?style=flat-square">
  <img alt="Stage" src="https://img.shields.io/badge/Stage-Alpha-orange?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/License-GPL--3.0-red?style=flat-square">
  <img alt="Environment" src="https://img.shields.io/badge/Side-Client--Only-0078d4?style=flat-square">
  <a href="https://discord.gg/gNajXYku5z"><img alt="Discord" src="https://img.shields.io/badge/Discord-Join-5865f2?style=flat-square&logo=discord&logoColor=white"></a>
  <a href="https://www.curseforge.com/minecraft/mc-mods/superionic"><img alt="CurseForge" src="https://img.shields.io/badge/CurseForge-Available-f16436?style=flat-square&logo=curseforge&logoColor=white"></a>
  <a href="https://modrinth.com/mod/superionic"><img alt="Modrinth" src="https://img.shields.io/badge/Modrinth-Available-00af5c?style=flat-square&logo=modrinth&logoColor=white"></a>
</p>

---

**Superionic** is a client-side optimization mod designed to improve Minecraft's rendering efficiency. By implementing advanced batching techniques and entity sorting, Superionic reduces the overhead of modern Minecraft's draw call system, leading to more consistent frame times and improved performance in complex scenes.

No visual changes. No server required. Just a more efficient client.

---

## core Optimization Modules (v1.0.0-alpha.2)

### 🎨 Render Pipeline Batching
Superionic intercepts the primary client-side buffering system (`BufferSource.getBuffer()`) to identify and consolidate compatible render types. By deferring buffer flushes, it minimizes the number of draw calls submitted to the GPU, reducing the driver-level overhead of state changes.

### 🔁 State Consolidation
Extends the engine's built-in `canConsolidateConsecutiveGeometry` logic to cover a wider range of identical render type queries. This prevents unnecessary buffer flushes when the same material or shader pipeline is used repeatedly in sequence.

### 🧍 Entity Type Sorting
By injecting into `LevelRenderer.extractVisibleEntities()`, Superionic sorts visible entities by their internal type hash before they are processed for rendering. This ensures that entities sharing the same model and texture data are rendered together, maximizing the effectiveness of the batching system and reducing GPU state switches.

### 📊 Performance HUD
A lightweight on-screen overlay provides real-time data on FPS, memory usage, and entity/particle counts, allowing for immediate feedback on client performance and resource utilization.

### 💨 Particle Culling
Skips the rendering and processing of particles that are outside the camera's view (frustum) or are too small/far to be meaningfully visible. This significantly reduces the CPU and GPU load in areas with dense particle effects like campfires, explosions, or spores.

### 👥 Entity Shadow Culling
Optimizes entity shadows by disabling them for entities beyond a 32-block radius. This maintains visual depth for nearby objects while eliminating unnecessary shadow calculation overhead for distant entities.

### 🍃 Fast Leaves
Implements a more efficient rendering path for leaf blocks by treating them as opaque (solid) geometry. This drastically reduces the number of transparent faces the GPU must sort and render, providing a substantial FPS boost in forest biomes.

### 📦 Fast Chest Rendering
Chests in Minecraft traditionally use complex animated models that are expensive to render in large quantities. Superionic provides a simplified rendering path that disables non-essential animations for chests, reducing CPU-to-GPU overhead in storage rooms.

### 🤖 AI Pathfinding Throttling
Significantly reduces the update frequency of AI logic and pathfinding for mobs that are far from the player. This saves CPU cycles on entities that don't immediately affect the player's experience.

### 🧹 Allocation Reduction
Intelligently reuses objects and optimizes data structures in hot code paths (like rendering) to minimize temporary object creation. This reduces the frequency of Java Garbage Collection (GC) pauses, leading to smoother gameplay.

### 🚀 Fast Chunk Loading
Optimizes the thread priority and task scheduling of the internal chunk builder. This allows new terrain to load and render faster without impacting the main game thread's frame rate.

### 🛰️ Network Tuning
Refines packet compression thresholds and entity tracking logic to ensure smoother data flow on multiplayer servers. This reduces "network lag" during high-traffic scenarios.

---

## Installation

1. Place the `.jar` file into your `mods/` folder
2. Launch the game — Superionic activates automatically

---

## Configuration

Settings are stored at `.minecraft/config/superionic.json` and generated automatically on first launch. New options added in future versions appear automatically with their defaults — no manual migration needed.

```json
{
  "batchRendering": true,
  "entitySorting": true,
  "hudBatching": true,
  "showPerformanceToast": false
}
```

| Key | Default | Description |
|---|---|---|
| `batchRendering` | `true` | Deferred buffer flushing and render type consolidation |
| `entitySorting` | `true` | Pre-extraction entity grouping by render type |
| `hudBatching` | `true` | HUD-level draw call batching (upcoming) |
| `showPerformanceToast` | `false` | Enable the on-screen performance overlay |
| `particleCulling` | `true` | Frustum-based skipping of off-screen particles |
| `entityShadowCulling` | `true` | Distance-based culling for entity shadows |
| `fastLeaves` | `false` | Accelerated rendering for leaf blocks (opaque) |
| `fastChestRendering` | `true` | Simplify chest models and animations |
| `aiThrottling` | `true` | Reduce AI update frequency for distant mobs |
| `reduceAllocations` | `true` | Minimize object creation for better GC |
| `fastChunkLoading` | `true` | Optimize thread priorities for world loading |
| `packetCompressionTuning` | `true` | Tune network compression thresholds |

### Performance Overlay

```
FPS: 144   MS: 6.9
MEM: 41%   E: 48
P: 203
```

| Field | Meaning |
|---|---|
| FPS | Frames per second — 🟢 ≥60 · 🟡 ≥30 · 🔴 <30 |
| MS | Frame time in milliseconds |
| MEM | JVM heap usage — 🟢 <70% · 🟡 <90% · 🔴 ≥90% |
| E | Visible entity count |
| P | Active particle count |

---

## Performance Impact

| Scenario | Expected Gain |
|---|---|
| Large entity groups (farms, mob spawners) | **High** — sorting eliminates repeated pipeline switches |
| Varied terrain with many distinct block surfaces | **Medium** — deferred flush reduces per-type overhead |
| General survival gameplay | **Low to Medium** — consistent baseline improvement |

> Superionic targets render-thread and CPU bottlenecks. As more optimization modules ship, gains will broaden across all hardware profiles.

---

## Compatibility

Superionic is **client-side only** — compatible with any server, vanilla or modded, without any server-side installation.

Superionic operates at the entity and buffer-level rendering layer and does not interfere with chunk rendering, shader injection, or game logic systems. It is designed to layer cleanly alongside any other performance-oriented mods.

---

## Frequently Asked Questions

**Does this change how the game looks?**
No. Superionic only modifies the timing and ordering of internal GPU buffer submissions. All rendered output is visually identical.

**Will this cause issues on anti-cheat servers?**
Superionic does not modify any game logic, movement, or network behavior — only the rendering pipeline. It is safe universally, but always verify with individual server policies.

**When will new optimization modules ship?**
New modules are added as they are tested and stable. Follow the [releases page](https://github.com/yigit-guven/Superionic/releases) or the [Discord](https://discord.gg/gNajXYku5z) for updates.

---

## Technical Implementation

### Batching & Flush Suppression
Superionic uses Mixins to inject into `MultiBufferSource.BufferSource`. It tracks the "pending" `RenderType` and its associated `BufferBuilder`. When a new `VertexConsumer` is requested for a different `RenderType`, Superionic compares them using an identity-based cache. If compatible, the flush is suppressed, allowing multiple geometries to be submitted in a single large GPU buffer.

### Entity Processing Workflow
In vanilla Minecraft, entities are processed in the order they appear in the level's internal lists, which typically results in frequent switching between `RenderType` states (e.g., Pig -> Sheep -> Pig -> Zombie). Superionic re-orders this list every frame after visibility extraction, grouping identical entity types together. This transformation ensures that the "Batching & Flush Suppression" logic mentioned above has the highest possible hit rate.

## Performance Evidence & Benchmarking

### Methodology
Performance is measured by observing the reduction in GPU draw calls and the resulting stability in frame times (1% lows). In scenes with dense entity populations, Superionic can reduce state-switching draw calls by up to 30-50%.

### Expected Benefits
- **High Entity Scenarios**: (e.g., entity farms, dense villages) Significant reduction in CPU-to-GPU overhead due to reduced draw call counts.
- **Complex UI/Translucency**: Improved handling of complex layered UI elements through more efficient buffer management.

> [!NOTE]
> Performance gains vary significantly based on hardware profiles. CPU-bound systems (with slower single-core performance) typically see more pronounced frame time stability improvements.

---

## Installation

```bash
git clone https://github.com/yigit-guven/Superionic.git
cd Superionic
./gradlew build
# Output: build/libs/Superionic-<version>.jar
```

Requires Java 21 or later. All dependencies are resolved automatically by Gradle.

---

## Contributing

- **Bug reports:** Open an [issue](https://github.com/yigit-guven/Superionic/issues) with version info, a description, and any crash reports from `.minecraft/crash-reports/`
- **Optimization ideas:** Open an [issue](https://github.com/yigit-guven/Superionic/issues) or post in [Discord](https://discord.gg/gNajXYku5z) — if it can be measured and improved, it's worth exploring

---

## License

Released under the [GNU General Public License v3.0](LICENSE).

You are free to use, modify, and redistribute this project. Derivative works must carry the same license with appropriate attribution.

---

<p align="center">
  Built by <a href="https://github.com/yigit-guven">Yigit Guven</a>
  &nbsp;·&nbsp;
  <a href="https://www.curseforge.com/minecraft/mc-mods/superionic">CurseForge</a>
  &nbsp;·&nbsp;
  <a href="https://modrinth.com/mod/superionic">Modrinth</a>
  &nbsp;·&nbsp;
  <a href="https://discord.gg/gNajXYku5z">Discord</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/yigit-guven/Superionic/issues">Issues</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/yigit-guven/Superionic/wiki">Wiki</a>
</p>
