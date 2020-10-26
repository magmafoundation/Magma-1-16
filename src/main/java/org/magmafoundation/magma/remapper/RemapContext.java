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

package org.magmafoundation.magma.remapper;

import java.util.LinkedList;
import org.bukkit.plugin.PluginDescriptionFile;
import org.objectweb.asm.tree.ClassNode;

/**
 * RemapContext
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 11/11/2019 - 07:57 am
 */
public class RemapContext {

    private static final LinkedList<RemapContext> remapStack = new LinkedList<>();

    private ClassNode classNode;
    private PluginDescriptionFile description;

    public static LinkedList<RemapContext> getRemapStack() {
        return remapStack;
    }

    public static void push(RemapContext context) {
        remapStack.push(context);
    }

    public static RemapContext peek() {
        return remapStack.peek();
    }

    public static RemapContext pop() {
        return remapStack.pop();
    }

    public PluginDescriptionFile getDescription() {
        return description;
    }

    public RemapContext setDescription(PluginDescriptionFile description) {
        this.description = description;
        return this;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public RemapContext setClassNode(ClassNode classNode) {
        this.classNode = classNode;
        return this;
    }
}
