# MCProtocol (W.I.P)
A Minecraft session library supporting multiple Minecraft versions with Kotlin DSL.
This library is inspired from [Steveice10's MCProtocolLib](https://github.com/Steveice10/MCProtocolLib) and this library uses some of it's code.
MCProtocolLib license can be found [here](https://github.com/Steveice10/MCProtocolLib/blob/master/LICENSE.txt).
 
# Example
> **NOTE:** This may change at anytime as this library is still in development stage

**Kotlin:**
```kotlin
     server()
            .sessionFactory { ch ->
                println("New session: ${ch.remoteAddress()}")
                buildProtocol(SERVER, ch) {
                    applyDefaults()
                    wiretap()
                }
            }
            .bind()
            .doOnSuccess { println("Bound to: ${it.host()}:${it.port()}") }
            .block()!!
            .onDispose()
            .block() // Block until the server shuts down
```

**Java:**
```java
// TODO
```