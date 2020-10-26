/*
 * Magma Server
 * Copyright (C) 2019-2020.
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

package org.magmafoundation.magma.remapper.proxy;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.reader.ReaderException;

/**
 * ProxyYamlConfiguration
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 21/11/2019 - 08:03 pm
 */
public class ProxyYamlConfiguration {

    private static final Charset GBK = Charset.forName("GBK");
    private static final Charset defaultCharset = Charset.defaultCharset();
    private static final Charset otherCharse = defaultCharset.name().equalsIgnoreCase("GBK") ? StandardCharsets.UTF_8 : GBK;

    public static YamlConfiguration loadConfiguration(InputStream inputStream) {
        ReaderException readerException = null;
        try {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, defaultCharset));
        } catch (ReaderException e) {
            readerException = e;
            try {
                return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, otherCharse));
            } catch (ReaderException e2) {
                throw readerException;
            }
        }
    }
}
