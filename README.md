# HydrogenDLC

HydrogenDLC is a modular Minecraft 1.21.4 client with a renderer-owned control center. The dashboard does **not** use Minecraft's `DrawContext`, `BufferBuilder`, GUI shaders, texture manager, or font renderer. Minecraft supplies only the window and active OpenGL context.

## Rendering architecture

```text
GUIScreen (window/input adapter)
  └─ DashboardController + DashboardLayout
      └─ DashboardRenderer
          └─ RenderEngine / Renderer2D
              ├─ ShapeBatch (instanced SDF surfaces)
              └─ TextBatch (streamed MTSDF glyph vertices)
```

- **One static quad:** rounded surfaces, gradients, independent corner radii, borders, softness, and shadows are packed into a 72-byte instance stream.
- **Ordered batching:** adjacent primitives of one pipeline are submitted together while explicit pipeline and scissor boundaries preserve authoring order.
- **No frame geometry garbage:** power-of-two native buffers are retained and resized only when capacity is exceeded.
- **Low synchronization overhead:** dynamic GPU buffers are orphaned before one contiguous upload, avoiding stalls on storage still consumed by an earlier frame.
- **Renderer-owned text:** MTSDF metadata and atlas pixels are decoded and uploaded directly through LWJGL; glyphs use a compact 20-byte vertex format.
- **Host interoperability:** touched OpenGL bindings, blend state, capabilities, texture state, pixel-store alignment, viewport, color-write mask, and scissor are captured and restored around every frame.
- **Instrumentation:** draw calls, instances, glyphs, upload bytes, and smoothed CPU frame time are published only after all batches flush; the dashboard therefore reports the previous completed frame instead of partial current-frame counters.

The implementation targets OpenGL 3.2 plus instanced arrays (core in 3.3 or `ARB_instanced_arrays`) and GLSL 150 core.

## Application architecture

All project-owned client code lives in the canonical `hydrogen` namespace:

- `core`, `module`, `setting`, `event`, and `config` contain the gameplay runtime and feature model.
- `ui/model` defines stable renderer-facing module and setting contracts.
- `integration/ClientModuleRepository` isolates the gameplay model from the dashboard.
- `ui/dashboard` contains responsive geometry, hit testing, filtering, scrolling, spring animation, and visual composition.
- `render` owns GPU resources and all custom drawing; it does not call Minecraft drawing APIs.
- `Hydrogen` is the lifecycle facade, while `core/HydrogenClient` owns the gameplay runtime.
- `platform` is the Fabric entrypoint and mixin boundary. Embedded third-party API surfaces remain separated from Hydrogen-owned code.

These boundaries keep gameplay modules operational while allowing presentation and rendering systems to evolve independently.

## Build

Java 21 is required.

```bash
./gradlew build
```

Open the dashboard with **Right Shift**. Use **Ctrl+F** to focus module search, the mouse wheel to scroll, and click a module card to inspect its settings. On narrower windows the inspector becomes a foreground drawer; close it with its **X** action or **Escape** to return to the full module list.
