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

package cf.kayon.gui;

import cf.kayon.core.Vocab;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.util.concurrent.CountDownLatch;

public class NodeTask extends Task<Void>
{
    private final Vocab v;

    private final FlowPane p;

    private final CountDownLatch l;

    public NodeTask(final Vocab v, final FlowPane p, final CountDownLatch l)
    {
        this.v = v;
        this.p = p;
        this.l = l;
    }

    @Override
    protected Void call() throws Exception
    {
        Node n = VocabNodeFactory.getNode(v); // Static class initialization is synchronized by JLS
        Platform.runLater(() -> p.getChildren().add(n));
        l.countDown();
        return null;
    }
}
