# ⚡ Ultimate Performance Mod

<p align="center">
  <em>A comprehensive, all-in-one client performance suite for Minecraft.</em>
</p>

<p align="center">
  <img alt="Version" src="https://img.shields.io/badge/Version-1.0.0--alpha.1-7b68ee?style=flat-square">
  <img alt="Stage" src="https://img.shields.io/badge/Stage-Alpha-orange?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/License-GPL--3.0-red?style=flat-square">
  <img alt="Environment" src="https://img.shields.io/badge/Side-Client--Only-0078d4?style=flat-square">
  <a href="https://discord.gg/gNajXYku5z"><img alt="Discord" src="https://img.shields.io/badge/Discord-Join-5865f2?style=flat-square&logo=discord&logoColor=white"></a>
</p>

---

**Ultimate Performance Mod (UPM)** is a client-side mod built with one goal: squeeze every frame out of Minecraft without compromising the experience. Rather than targeting a single subsystem, UPM is engineered as an **expanding suite of optimizations** — each release adding new improvements across rendering, memory, entity processing, and beyond.

No visual changes. No server required. Just performance.

---

## Philosophy

Most performance mods pick a lane — chunk rendering, server logic, memory management. UPM is different.

Every part of the Minecraft client that can be made faster **will** be addressed. The roadmap covers the full stack: render pipeline batching, entity tick optimization, particle system efficiency, memory allocation patterns, JVM-level tuning hints, and more. Alpha releases focus on the rendering pipeline, but this is the foundation of something far broader.

If it costs CPU time or GPU time without adding to your experience, it's a target.

---

## Current Optimizations (v1.0.0-alpha.1)

This release establishes the rendering pipeline foundation:

### 🎨 Render Batching
The core of Minecraft's GPU draw call system — `BufferSource.getBuffer()` — is intercepted to defer and group compatible buffer flushes. Rather than submitting each type switch to the GPU immediately, UPM batches compatible calls together, reducing total GPU submissions per frame.

### 🔁 Consolidation Expansion
Minecraft's built-in `canConsolidateConsecutiveGeometry` hint is extended to cover the most common real-world case: the same render type queried consecutively. When this fires, the active buffer is reused entirely — no flush, no state change, no wasted frame budget.

### 🧍 Entity Sorting
Before entities enter the per-frame render state extraction phase, UPM sorts them by type. Entities of the same type share a model, texture set, and shader pipeline — processing them consecutively eliminates redundant GPU pipeline state switches.

### 📊 Performance Overlay
An optional compact HUD overlay provides live insight into what your client is doing: frames per second, frame time, memory usage, visible entity count, and active particle count — all updated in real time.

---

## Installation

1. Place the `.jar` file into your `mods/` folder
2. Launch the game — UPM activates automatically

---

## Configuration

Settings are stored at `.minecraft/config/ultimateperformancemod.json` and generated automatically on first launch. New options added in future versions appear automatically with their defaults — no manual migration needed.

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

> UPM targets render-thread and CPU bottlenecks. As more optimization modules ship, gains will broaden across all hardware profiles.

---

## Compatibility

UPM is **client-side only** — compatible with any server, vanilla or modded, without any server-side installation.

UPM operates at the entity and buffer-level rendering layer and does not interfere with chunk rendering, shader injection, or game logic systems. It is designed to layer cleanly alongside any other performance-oriented mods.

---

## Frequently Asked Questions

**Does this change how the game looks?**
No. UPM only modifies the timing and ordering of internal GPU buffer submissions. All rendered output is visually identical.

**Will this cause issues on anti-cheat servers?**
UPM does not modify any game logic, movement, or network behavior — only the rendering pipeline. It is safe universally, but always verify with individual server policies.

**When will new optimization modules ship?**
New modules are added as they are tested and stable. Follow the [releases page](https://github.com/yigit-guven/Ultimate-Performance-Mod/releases) or the [Discord](https://discord.gg/gNajXYku5z) for updates.

---

## Building From Source

```bash
git clone https://github.com/yigit-guven/Ultimate-Performance-Mod.git
cd Ultimate-Performance-Mod
./gradlew build
# Output: build/libs/upm-<version>.jar
```

Requires Java 21 or later. All dependencies are resolved automatically by Gradle.

---

## Contributing

- **Bug reports:** Open an [issue](https://github.com/yigit-guven/Ultimate-Performance-Mod/issues) with version info, a description, and any crash reports from `.minecraft/crash-reports/`
- **Optimization ideas:** Open an [issue](https://github.com/yigit-guven/Ultimate-Performance-Mod/issues) or post in [Discord](https://discord.gg/gNajXYku5z) — if it can be measured and improved, it's worth exploring

---

## License

Released under the [GNU General Public License v3.0](LICENSE).

You are free to use, modify, and redistribute this project. Derivative works must carry the same license with appropriate attribution.

---

<p align="center">
  Built by <a href="https://github.com/yigit-guven">Yigit Guven</a>
  &nbsp;·&nbsp;
  <a href="https://discord.gg/gNajXYku5z">Discord</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/yigit-guven/Ultimate-Performance-Mod/issues">Issues</a>
  &nbsp;·&nbsp;
  <a href="https://github.com/yigit-guven/Ultimate-Performance-Mod/wiki">Wiki</a>
</p>
