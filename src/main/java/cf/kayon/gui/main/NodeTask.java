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

import cf.kayon.core.Vocab;
import cf.kayon.core.noun.Noun;
import cf.kayon.gui.vocabview.noun.NounView;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class NodeTask extends Task<Void>
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeTask.class);

    private final Vocab vocab;

    private final VBox vBox;

    private final CountDownLatch latch;

    public NodeTask(final Vocab vocab, final VBox vBox, final CountDownLatch latch)
    {
        // final fields guarantee visibility
        this.vocab = vocab;
        this.vBox = vBox;
        this.latch = latch;
    }

    // synchronized for visibility to other threads
    @Override
    protected synchronized Void call() throws Exception
    {
        LOGGER.info("Making node for vocab " + vocab);
        Node node;

        if (vocab instanceof Noun)
            node = NounView.createNewParent((Noun) vocab).getLeft();
        else
            throw new IllegalArgumentException("Unknown vocab class!");

        synchronized (latch) // race condition
        {
            if (latch.getCount() == 1)
                Platform.runLater(() -> vBox.getChildren().add(node));
            else
                Platform.runLater(() -> {
                    vBox.getChildren().add(node);
                    vBox.getChildren().add(new Separator());
                });
            latch.countDown();
        }
        return null;
    }
}
