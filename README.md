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

## Acknowledgements

This project exists because of **[Chan Karunamuni](https://x.com/chan_k)**, the legend who started the fluid interfaces movement at Apple. His WWDC 2018 talk *Designing Fluid Interfaces* ([Apple Developer](https://developer.apple.com/videos/play/wwdc2018/803/) · [YouTube](https://www.youtube.com/watch?v=gttSJA-kDmQ)) started all of this for me, and it still hasn't stopped. Since then he has been behind some of Apple's most delightful motion: the [Dynamic Island](https://developer.apple.com/videos/play/wwdc2023/10194/), the little shockwave moment of the new AirDrop and NameDrop sharing in iOS 17, the flashlight UI in iOS 18, Liquid Glass itself, and the open and close transition of the iPhone Duo, among much more. Thank you, Chan!

I am also very grateful to **Nathan Gitter**, whose article [Building Fluid Interfaces](https://medium.com/@nathangitter/building-fluid-interfaces-ios-swift-9732bb934bf5) (Medium, August 2018) took the talk apart in depth and turned its pseudo-code into working code. He rebuilt eight interfaces from the presentation (the calculator and flashlight buttons, spring animations, rubberbanding, acceleration pausing, rewarding momentum, the FaceTime picture-in-picture and rotation), each with the design theory behind it and the code to build it ([demo app](https://github.com/nathangitter/fluid-interfaces)). It inspired these experiments: FaceTime PiP and acceleration pausing (in `old/`) are my Android takes on those interfaces.

And thanks to **[Christian Schnorr](https://medium.com/@jenoxx)**, whose article [Demystifying UIKit Spring Animations](https://medium.com/ios-os-x-development/demystifying-uikit-spring-animations-2bb868446773) (Medium, June 2018) made springs click for me, as it did for Nathan. He starts from the physics of a damped mass on a spring (underdamped, critically damped, overdamped), shows why the talk's design-friendly parameters, damping ratio and response, beat picking a duration, and explains how to hand a gesture's velocity over to a spring as a relative initial velocity ([demo app](https://github.com/jenox/UIKit-Playground/tree/master/01-Demystifying-UIKit-Spring-Animations/)). Android's `SpringForce` takes a damping ratio and a stiffness, so his explanation maps straight onto every spring in this project.

## Third-party code

`libs/springx` is vendored from [lcdsmao/springx](https://github.com/lcdsmao/springx) under the Apache License 2.0 (see `libs/springx/LICENSE`).

## License

MIT, see [LICENSE](LICENSE), except `libs/springx` (Apache 2.0).
