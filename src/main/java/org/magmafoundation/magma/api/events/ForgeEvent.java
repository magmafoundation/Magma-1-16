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

package org.magmafoundation.magma.api.events;

import net.minecraftforge.eventbus.api.Event;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

/**
 * ForgeEvents
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 06/03/2020 - 12:32 am
 */
public class ForgeEvent extends org.bukkit.event.Event {

    private static final HandlerList handlers = new HandlerList();
    private final Event forgeEvent;

    public ForgeEvent(Event event) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.forgeEvent = event;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Event getForgeEvent() {
        return forgeEvent;
    }

    public String getEventName() { return forgeEvent.getClass().getSimpleName(); }
}
