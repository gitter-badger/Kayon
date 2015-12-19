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
import cf.kayon.gui.vocabview.nounview.NounView;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import static com.google.common.base.Preconditions.checkNotNull;

@Immutable
public class NodeTask implements Callable<Void>
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeTask.class);

    /**
     * The {@link VBox} this task will append its created node(s) to.
     *
     * @since 0.0.1
     */
    @NotNull
    private final VBox vBox;

    /**
     * The {@link BlockingQueue} this task will take the vocab from.
     *
     * @since 0.2.3
     */
    private final BlockingQueue<Vocab> queue;

    /**
     * The poison object for stopping this consumer task.
     * If this object is found on the BlockingQueue, this consumer will finish its execution (and the posion will be re-inserted into the queue,
     * so other consumers terminate as well)
     *
     * @since 0.2.3
     */
    @NotNull
    private final Vocab poison;

    /**
     * The {@link CountDownLatch} this task decrements once it finished working.
     *
     * @since 0.2.3
     */
    @NotNull
    private final CountDownLatch latch;

    /**
     * Constructs a new NodeTask.
     *
     * @param vBox   The {@link VBox} this task will append its created node(s) to.
     * @param queue  The {@link BlockingQueue} this task will take the vocab from.
     * @param poison The poison object for stopping this consumer task.
     *               If this object is found on the BlockingQueue, this consumer will finish its execution (and the posion will be re-inserted into the queue,
     *               so other consumers terminate as well)
     * @param latch  The {@link CountDownLatch} this task decrements once it finished working.
     * @since 0.2.3
     */
    public NodeTask(@NotNull final VBox vBox, final BlockingQueue<Vocab> queue, @NotNull Vocab poison, @NotNull CountDownLatch latch)
    {
        checkNotNull(vBox);
        checkNotNull(queue);
        checkNotNull(poison);
        checkNotNull(latch);

        // final fields guarantee visibility
        this.vBox = vBox;
        this.queue = queue;
        this.poison = poison;
        this.latch = latch;
    }

    /**
     * @since 0.2.3
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof NodeTask)) return false;
        NodeTask nodeTask = (NodeTask) o;
        return Objects.equal(vBox, nodeTask.vBox) &&
               Objects.equal(queue, nodeTask.queue) &&
               Objects.equal(poison, nodeTask.poison) &&
               Objects.equal(latch, nodeTask.latch);
    }

    /**
     * @since 0.2.3
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(vBox, queue, poison, latch);
    }

    /**
     * @since 0.2.3
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("vBox", vBox)
                          .add("queue", queue)
                          .add("poison", poison)
                          .add("latch", latch)
                          .toString();
    }

    @Nullable
    @Override
    public Void call() throws InterruptedException, IOException
    {
        LOGGER.info("NodeTask started: " + Thread.currentThread());
        try
        {
            while (true)
            {
                Vocab v = queue.take(); // InterruptedException is thrown to caller
                if (v == poison)
                {
                    while (true)
                    {
                        try
                        {
                            queue.put(v);
                            break;
                        } catch (InterruptedException ignored) {} // retry
                    }
                    return null;
                }
                LOGGER.info("Making node for vocab " + v);

                Node node;
                if (v instanceof Noun)
                    node = NounView.createNewParent((Noun) v).getLeft();
                else
                    throw new IllegalArgumentException("Unknown vocab class!");

                Platform.runLater(() -> vBox.getChildren().add(node));
            }
        } finally
        {
            latch.countDown();
            LOGGER.info("NodeTask terminated normally: " + Thread.currentThread() + " (encountered poison)");
        }
    }
}
