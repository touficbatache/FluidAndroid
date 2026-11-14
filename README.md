# Fluid Android

A collection of Android UI motion experiments: physics-based gestures and spring animations that try to make interactions feel fluid and natural. Each experiment opens from the home screen.

Written in Kotlin with Android's `DynamicAnimation` springs and flings, plus a small vendored spring helper (`libs/springx`).

## Experiments

- **Squircle:** continuous-corner ("squircle") shapes, as used in iOS icons, drawn on Android.
- **Home screen animation:** an app-opening transition that grows the icon into a full-screen window with rounded corners.
- **FaceTime PiP:** a draggable picture-in-picture window that you can fling to any corner, with velocity projection deciding where it lands.
- **Insta 3D touch:** a press-and-hold photo preview that springs up over a blurred background.
- **Synced lyrics:** a lyrics view where the current line springs into focus and the list follows it.
- **Bubbles:** a menu of categories laid out as bubbles.
- **Jumping:** a view you fling that settles with fling and spring physics.
- **Player:** a music player screen with blurred artwork.

The `old/` package keeps earlier iterations (fling and chart experiments, versions one to five) for reference.

## Status

A personal playground from 2021, not a maintained library. It builds with the toolchain of that time (Android Gradle Plugin 4.2, Gradle 7.0, Kotlin synthetic views), so open it in an Android Studio version that supports that setup, with JDK 11.

The artwork in this repository (screenshots, album covers) and the synced lyrics are placeholders. The originals were personal screenshots and copyrighted material, so they are not published.

## Third-party code

`libs/springx` is vendored from [lcdsmao/springx](https://github.com/lcdsmao/springx) under the Apache License 2.0 (see `libs/springx/LICENSE`).

## License

MIT, see [LICENSE](LICENSE), except `libs/springx` (Apache 2.0).
