package net.titan.dev

import java.security.MessageDigest
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/** Relocates only the bundled authorization SDK and references to it. */
final class AuthorizationRelocator {
    static byte[] relocateClass(byte[] bytes, String replacement) {
        def input = new DataInputStream(new ByteArrayInputStream(bytes))
        def buffer = new ByteArrayOutputStream()
        def output = new DataOutputStream(buffer)
        int magic = input.readInt()
        if (magic != (int) 0xCAFEBABE) throw new IOException('Invalid class file')
        output.writeInt(magic)
        output.writeShort(input.readUnsignedShort()); output.writeShort(input.readUnsignedShort())
        int count = input.readUnsignedShort(); output.writeShort(count)
        for (int i = 1; i < count; i++) {
            int tag = input.readUnsignedByte(); output.writeByte(tag)
            if (tag == 1) {
                String value = input.readUTF()
                output.writeUTF(value.replace('net/titan/authorization/', replacement.replace('.', '/') + '/')
                    .replace('net.titan.authorization.', replacement + '.'))
                continue
            }
            int size
            switch (tag) {
                case 3: case 4: case 9: case 10: case 11: case 12: case 17: case 18: size = 4; break
                case 5: case 6: size = 8; i++; break
                case 7: case 8: case 16: case 19: case 20: size = 2; break
                case 15: size = 3; break
                default: throw new IOException('Unsupported class constant tag: ' + tag)
            }
            byte[] data = new byte[size]; input.readFully(data); output.write(data)
        }
        input.transferTo(output); output.flush(); return buffer.toByteArray()
    }

    static void relocate(File jarFile) {
        byte[] digest = MessageDigest.getInstance('SHA-256').digest(jarFile.bytes)
        String suffix = digest.encodeHex().toString().substring(0, 24)
        String replacement = 'net.titan.authorization.b_' + suffix
        File temporary = new File(jarFile.parentFile, jarFile.name + '.authorization.tmp')
        try {
            new JarFile(jarFile).withCloseable { source ->
                new JarOutputStream(new FileOutputStream(temporary)).withCloseable { target ->
                    def seen = new HashSet<String>()
                    source.entries().each { entry ->
                        if (entry.name ==~ /(?i)META-INF\/.*\.(SF|RSA|DSA|EC)/) {
                            throw new IOException('Bundle authorization before signing the plugin JAR')
                        }
                        String name = entry.name.replace('net/titan/authorization/', replacement.replace('.', '/') + '/')
                        if (!seen.add(name)) throw new IOException('Duplicate JAR entry: ' + name)
                        byte[] content = source.getInputStream(entry).withCloseable { it.readAllBytes() }
                        if (name.endsWith('.class')) content = relocateClass(content, replacement)
                        def renamed = new ZipEntry(name); renamed.time = 0
                        target.putNextEntry(renamed); target.write(content); target.closeEntry()
                    }
                }
            }
            java.nio.file.Files.move(temporary.toPath(), jarFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
        } finally { java.nio.file.Files.deleteIfExists(temporary.toPath()) }
    }
}
