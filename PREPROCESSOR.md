

Versions:

| mcversion   | version code      |
|-------------|-------------------|
| 1.8.9       | 10809 -- DEFAULT  |
| 1.9.4       | 10904             |
| 1.10.2      | 11002             |
| 1.11.2      | 11102             |
| 1.12.2      | 11202             |

To change version:
`./gradlew changeMcVersion -PminecraftVersion=<version code>`
`./gradlew setupDecompWorkspace`

The preprocessor works like so:
```
//#if <condition>
<code>
(optionally) {
//#else (or #elseif <condition>)
//$$ alternative code if condition is true
}
//#endif
```

A condition compares against the MC version, like so:
`//#if MC==10809`
or
`//#if MC>10809`
etc. supporting all of <, <=, >, >=, ==

After running a Gradle build, this version will automatically be reset back to 1.8.9, just so you don't accidentally
push a non-1.8.9 version of the source code. However, that is not fool-proof, as you may not build before pushing,
so if you've changed the mc version, DONT FORGET TO CHANGE IT BACK!
