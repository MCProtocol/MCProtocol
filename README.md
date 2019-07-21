# MCProtocol (W.I.P)
A Minecraft session library supporting multiple Minecraft versions with Kotlin DSL.
This library is inspired from [Steveice10's MCProtocolLib](https://github.com/Steveice10/MCProtocolLib) and this library uses some of it's code.
MCProtocolLib license can be found [here](https://github.com/Steveice10/MCProtocolLib/blob/master/LICENSE.txt).
 
# Example
> **NOTE:** This may change at anytime as this library is still in development stage

**Kotlin:**
```kotlin
client("mc.hypixel.net") {
    // TODO: Authentication
    bind().subscribe { println("Connected to Hypixel!") }
}
```

**Java:**
```java
Client client = new Client("mc.hypixel.net")
client.bind()
    .subscribe((c) -> System.out.println("Connected to Hypixel!"))
```