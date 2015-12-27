/*
 * Kayon
 * Copyright (C) 2015 Ruben Anders
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cf.kayon.core;

import net.jcip.annotations.ThreadSafe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Describes a latin word.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@ThreadSafe
public interface Vocab extends Translatable
{
    /**
     * Returns the UUID of this Vocab.
     *
     * @return The UUID. {@code null} if no UUID has been {@link #initializeUuid(UUID) initialized} yet.
     * @since 0.0.1
     */
    @Nullable UUID getUuid();

    /**
     * Initializes the Vocab with a UUID.
     *
     * @param uuid The UUID to set.
     * @throws IllegalStateException If the UUID has already been initialized.
     * @throws NullPointerException  If {@code uuid} is {@code null}.
     * @since 0.0.1
     */
    void initializeUuid(@NotNull UUID uuid);
}
