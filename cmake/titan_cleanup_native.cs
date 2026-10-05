// Windows-only conservative deletion of SDK-owned staging generations.
// Loaded on demand by titan_cleanup_native.ps1; no build toolchain is needed.
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;
using System.Text.RegularExpressions;
using Microsoft.Win32.SafeHandles;

namespace Titan.NativeDevelopment
{
    public static class GenerationCleanup
    {
        const uint DeleteAccess = 0x00010000;
        const uint ReadAttributes = 0x00000080;
        const uint GenericRead = 0x80000000;
        const uint GenericWrite = 0x40000000;
        const uint ShareReadWrite = 0x3;
        const uint OpenExisting = 3;
        const uint BackupSemantics = 0x02000000;
        const uint OpenReparsePoint = 0x00200000;
        const uint DirectoryAttribute = 0x10;
        const uint ReparseAttribute = 0x400;
        static readonly Regex OwnedName = new Regex(@"^(gen-[1-9][0-9]*|\.pending-[a-fA-F0-9]{32})$");

        [StructLayout(LayoutKind.Sequential)]
        struct AttributeInfo { public uint Attributes; public uint ReparseTag; }
        [StructLayout(LayoutKind.Sequential)]
        struct DispositionInfo { public byte DeleteFile; }
        [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
        static extern SafeFileHandle CreateFileW(string path, uint access, uint share,
            IntPtr security, uint creation, uint flags, IntPtr template);
        [DllImport("kernel32.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool GetFileInformationByHandleEx(SafeFileHandle handle, int informationClass,
            out AttributeInfo information, uint size);
        [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
        static extern uint GetFinalPathNameByHandleW(SafeFileHandle handle, StringBuilder path,
            uint capacity, uint flags);
        [DllImport("kernel32.dll", SetLastError = true)]
        [return: MarshalAs(UnmanagedType.Bool)]
        static extern bool SetFileInformationByHandle(SafeFileHandle handle, int informationClass,
            ref DispositionInfo information, uint size);

        sealed class Entry : IDisposable
        {
            internal string Path;
            internal SafeFileHandle Handle;
            internal bool Pending;
            public void Dispose() { if (Handle != null) { Handle.Dispose(); Handle = null; } }
        }

        static string Absolute(string path)
        {
            if (!System.IO.Path.IsPathRooted(path)) throw new IOException("Cleanup paths must be absolute");
            return System.IO.Path.GetFullPath(path).TrimEnd('\\', '/');
        }

        static void RejectReparseAncestors(string path)
        {
            for (var directory = new DirectoryInfo(path); directory != null; directory = directory.Parent)
                if ((directory.Attributes & FileAttributes.ReparsePoint) != 0)
                    throw new IOException("Cleanup does not traverse reparse links: " + directory.FullName);
        }

        static string FinalPath(SafeFileHandle handle)
        {
            var result = new StringBuilder(32768);
            uint length = GetFinalPathNameByHandleW(handle, result, (uint)result.Capacity, 0);
            if (length == 0 || length >= result.Capacity)
                throw new Win32Exception(Marshal.GetLastWin32Error(), "Cannot resolve cleanup handle path");
            string path = result.ToString();
            if (path.StartsWith(@"\\?\UNC\", StringComparison.OrdinalIgnoreCase)) path = @"\\" + path.Substring(8);
            else if (path.StartsWith(@"\\?\", StringComparison.Ordinal)) path = path.Substring(4);
            return Absolute(path);
        }

        static Entry Open(string path, string root, bool directory, bool rootHandle)
        {
            string absolute = Absolute(path);
            if (!absolute.Equals(root, StringComparison.OrdinalIgnoreCase) &&
                !absolute.StartsWith(root + System.IO.Path.DirectorySeparatorChar, StringComparison.OrdinalIgnoreCase))
                throw new IOException("Cleanup entry is outside its configuration directory");
            uint access = rootHandle ? ReadAttributes : DeleteAccess | ReadAttributes;
            if (!directory) access |= GenericRead | GenericWrite;
            // Exclusive file handles reject active readers and mapped images.
            // Directory handles allow enumeration but prevent path replacement.
            var handle = CreateFileW(absolute, access, directory ? ShareReadWrite : 0,
                IntPtr.Zero, OpenExisting, BackupSemantics | OpenReparsePoint, IntPtr.Zero);
            if (handle.IsInvalid) {
                int error = Marshal.GetLastWin32Error();
                handle.Dispose();
                throw new Win32Exception(error, "File or directory is locked or inaccessible: " + absolute);
            }
            try {
                AttributeInfo info;
                if (!GetFileInformationByHandleEx(handle, 9, out info, 8))
                    throw new Win32Exception(Marshal.GetLastWin32Error());
                if ((info.Attributes & ReparseAttribute) != 0 ||
                    ((info.Attributes & DirectoryAttribute) != 0) != directory)
                    throw new IOException("Cleanup entry changed or is a reparse link: " + absolute);
                if (!FinalPath(handle).Equals(absolute, StringComparison.OrdinalIgnoreCase))
                    throw new IOException("Cleanup resolved outside its expected path: " + absolute);
                return new Entry { Path = absolute, Handle = handle };
            } catch { handle.Dispose(); throw; }
        }

        static void Collect(string path, string root, List<Entry> directories, List<Entry> files, int depth)
        {
            if (depth > 128) throw new IOException("Cleanup directory nesting is too deep");
            var entry = Open(path, root, true, false);
            directories.Add(entry);
            foreach (string child in Directory.GetFileSystemEntries(path)) {
                FileAttributes attributes = File.GetAttributes(child);
                if ((attributes & FileAttributes.ReparsePoint) != 0)
                    throw new IOException("Cleanup does not traverse reparse links: " + child);
                if ((attributes & FileAttributes.Directory) != 0) Collect(child, root, directories, files, depth + 1);
                else files.Add(Open(child, root, false, false));
            }
        }

        static void SetPending(Entry entry, bool pending)
        {
            var info = new DispositionInfo { DeleteFile = pending ? (byte)1 : (byte)0 };
            if (!SetFileInformationByHandle(entry.Handle, 4, ref info, 1))
                throw new Win32Exception(Marshal.GetLastWin32Error(), "Cannot remove locked entry: " + entry.Path);
            entry.Pending = pending;
        }

        // Return a skip reason rather than failing a successfully published build.
        // No file handle is closed after marking delete-pending until every file
        // accepts the operation. A locked late entry therefore cannot cause its
        // unlocked siblings to disappear during preflight.
        public static bool TryRemove(string loadRoot, string candidate, string keepGeneration, out string reason)
        {
            reason = "";
            var directories = new List<Entry>();
            var files = new List<Entry>();
            Entry rootHandle = null;
            bool committed = false;
            try {
                string root = Absolute(loadRoot);
                string target = Absolute(candidate);
                string keep = Absolute(keepGeneration);
                if (!System.IO.Path.GetDirectoryName(target).Equals(root, StringComparison.OrdinalIgnoreCase) ||
                    !System.IO.Path.GetDirectoryName(keep).Equals(root, StringComparison.OrdinalIgnoreCase) ||
                    !OwnedName.IsMatch(System.IO.Path.GetFileName(target)) ||
                    !Regex.IsMatch(System.IO.Path.GetFileName(keep), @"^gen-[1-9][0-9]*$") ||
                    target.Equals(keep, StringComparison.OrdinalIgnoreCase))
                    throw new IOException("Cleanup target is not an old owned generation");
                RejectReparseAncestors(root);
                rootHandle = Open(root, root, true, true);
                Collect(target, root, directories, files, 0);
                // Fully held tree, with resolved paths validated, before deletion.
                foreach (Entry entry in files) SetPending(entry, true);
                committed = true;
                foreach (Entry entry in files) entry.Dispose();
                for (int i = directories.Count - 1; i >= 0; --i) {
                    SetPending(directories[i], true);
                    directories[i].Dispose();
                }
                return true;
            } catch (Exception error) {
                reason = error.Message;
                return false;
            } finally {
                try {
                    if (!committed) {
                        foreach (Entry entry in files) {
                            if (entry.Pending) {
                                // With the original DELETE handle still held,
                                // clear each mark before releasing any handle.
                                try { SetPending(entry, false); }
                                catch (Exception error) {
                                    reason += "; could not cancel deletion: " + error.Message;
                                }
                            }
                        }
                    }
                } finally {
                    foreach (Entry entry in files) entry.Dispose();
                    foreach (Entry entry in directories) entry.Dispose();
                    if (rootHandle != null) rootHandle.Dispose();
                }
            }
        }
    }
}
