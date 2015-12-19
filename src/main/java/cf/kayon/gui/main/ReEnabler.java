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

package cf.kayon.gui.main;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

public class ReEnabler implements Runnable
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(ReEnabler.class);

    @NotNull
    private final CountDownLatch latch;

    @NotNull
    private final MainController controller;

    public ReEnabler(@NotNull CountDownLatch latch, @NotNull MainController controller)
    {
        checkNotNull(latch);
        checkNotNull(controller);
        this.latch = latch;
        this.controller = controller;
    }

    @Override
    public void run()
    {
        LOGGER.info("ReEnabler started: " + Thread.currentThread());
        try
        {
            latch.await();
        } catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        } finally {
            controller.resetUI();
            LOGGER.info("ReEnabler terminated normally: "+ Thread.currentThread());
        }
    }
}
