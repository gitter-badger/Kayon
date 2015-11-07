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

import java.util.concurrent.CountDownLatch;

public class NodeTask extends Task<Void>
{
    private final Vocab vocab;

    private final VBox vBox;

    private final CountDownLatch latch;

    public NodeTask(final Vocab vocab, final VBox vBox, final CountDownLatch latch)
    {
        this.vocab = vocab;
        this.vBox = vBox;
        this.latch = latch;
    }

    // snychronized for visibility to other threads
    @Override
    protected synchronized Void call() throws Exception
    {
        Node node;

        if (vocab.getClass() == Noun.class)
            node = NounView.createNewParent((Noun) vocab).getLeft();
        else
            throw new IllegalArgumentException();
        synchronized (latch) // Race condition, thread visibility
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
