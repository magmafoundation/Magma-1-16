/*
 * Magma Server
 * Copyright (C) 2019-2021.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.remapper.v2.proxy;

import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Manifest;

import cpw.mods.modlauncher.TransformingClassLoader;
import net.md_5.specialsource.JarMapping;
import net.md_5.specialsource.provider.ClassLoaderProvider;
import net.md_5.specialsource.provider.JointProvider;
import net.md_5.specialsource.repo.RuntimeRepo;
import org.magmafoundation.magma.remapper.v2.ClassInheritanceProvider;
import org.magmafoundation.magma.remapper.v2.MagmaRemapper;
import org.magmafoundation.magma.remapper.v2.MappingLoader;
import org.magmafoundation.magma.remapper.v2.ReflectionMapping;
import org.magmafoundation.magma.remapper.v2.ReflectionTransformer;

import net.minecraft.server.MinecraftServer;

/**
 * ProxyURLClassLoader
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 13/07/2021 - 08:38 pm
 */
public class ProxyURLClassLoader extends URLClassLoader {

	private final Map<String, Class<?>> classes = new HashMap<>();
	private final JarMapping jarMapping;
	private final MagmaRemapper remapper;
	private final TransformingClassLoader launchClassloader;


	{
		this.launchClassloader = (TransformingClassLoader) MinecraftServer.getServer().getClass().getClassLoader();
		this.jarMapping = MappingLoader.loadMapping();
		final JointProvider provider = new JointProvider();
		provider.add(new ClassInheritanceProvider());
		provider.add(new ClassLoaderProvider(this));
		this.jarMapping.setFallbackInheritanceProvider(provider);
		this.remapper = new MagmaRemapper(this.jarMapping);
	}

	public ProxyURLClassLoader(final URL[] urls, final ClassLoader parent) {
		super(urls, parent);
	}

	public ProxyURLClassLoader(final URL[] urls) {
		super(urls);
	}

	public ProxyURLClassLoader(final URL[] urls, final ClassLoader parent, final URLStreamHandlerFactory factory) {
		super(urls, parent, factory);
	}

	protected Class<?> findClass(final String name) throws ClassNotFoundException {
		if (ReflectionMapping.isNMSPackage(name)) {
			final String remappedClass = this.jarMapping.classes.get(name.replaceAll("\\.", "\\/"));
			return launchClassloader.loadClass(remappedClass);
		}

		Class<?> result = this.classes.get(name);
		synchronized (name.intern()) {
			if (result == null) {
				result = this.remappedFindClass(name);

				if (result == null) {
					try {
						result = super.findClass(name);
					} catch (ClassNotFoundException ignored) {
					}
				}

				if (result == null) {
					try {
						result = launchClassloader.loadClass(name);
					} catch (ClassNotFoundException ignored) {
					}
				}

				if (result == null) {
					try {
						result = launchClassloader.getClass().getClassLoader().loadClass(name);
					} catch (Throwable throwable) {
						throw new ClassNotFoundException(name, throwable);
					}
				}

				if (result == null) throw new ClassNotFoundException(name);
				this.classes.put(name, result);
			}
		}
		return result;
	}

	private Class<?> remappedFindClass(final String name) throws ClassNotFoundException {
		Class<?> result = null;
		try {
			final String path = name.replace('.', '/').concat(".class");
			final URL url = this.findResource(path);
			if (url != null) {
				final InputStream stream = url.openStream();
				if (stream != null) {
					final JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
					final URL jarURL = jarURLConnection.getJarFileURL();
					final Manifest manifest = jarURLConnection.getManifest();

					byte[] bytecode = this.remapper.remapClassFile(stream, RuntimeRepo.getInstance());
					bytecode = ReflectionTransformer.transform(bytecode);

					int dot = name.lastIndexOf('.');
					if (dot != -1) {
						String pkgName = name.substring(0, dot);
						if (getPackage(pkgName) == null) {
							try {
								if (manifest != null) {
									definePackage(pkgName, manifest, url);
								} else {
									definePackage(pkgName, null, null, null, null, null, null, null);
								}
							} catch (IllegalArgumentException ignored) {
							}
						}
					}

					final CodeSource codeSource = new CodeSource(jarURL, new CodeSigner[0]);
					result = this.defineClass(name, bytecode, 0, bytecode.length, codeSource);
					if (result != null) {
						this.resolveClass(result);
					}
				}
			}
		} catch (Throwable t) {
			throw new ClassNotFoundException("Failed to remap class " + name, t);
		}
		return result;
	}
}
