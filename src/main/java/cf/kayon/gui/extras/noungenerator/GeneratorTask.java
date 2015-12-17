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

package cf.kayon.gui.extras.noungenerator;

import cf.kayon.core.Gender;
import cf.kayon.core.KayonContext;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.gui.vocabview.noun.DummyNounDeclension;
import cf.kayon.gui.vocabview.noun.NounViewController;
import javafx.concurrent.Task;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A task for generating a batch of nouns.
 *
 * @author Ruben Anders
 * @since 0.2.3
 */
public class GeneratorTask extends Task<Void>
{

    /**
     * The logger for this class.
     *
     * @since 0.2.3
     */
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorTask.class);

    /**
     * The {@link KayonContext} of this class (used to instantiate newly generated
     * nouns and to communicate to the database).
     *
     * @since 0.2.3
     */
    private final KayonContext context;

    /**
     * Whether to generate numeric characters in the root word.
     *
     * @since 0.2.3
     */
    private final boolean numeric;

    /**
     * The length of the generated root words.
     *
     * @since 0.2.3
     */
    private final int length;

    /**
     * The count of Nouns to generate.
     *
     * @since 0.2.3
     */
    private final int count;
    /**
     * The {@link Random} instance to use to generate the random values.
     *
     * @since 0.2.3
     */
    @NotNull
    private final Random random;

    /**
     * Constructs a new GeneratorTask with a string seed.
     *
     * @param context The {@link KayonContext} of this class (used to instantiate newly generated
     *                nouns and to communicate to the database).
     * @param numeric Whether to generate numeric characters in the root word.
     * @param length  The length of the generated root words.
     * @param count   The count of nouns to generate.
     * @param seed    The seed for the {@link Random} instance. If this value is {@code null} or {@link String#isEmpty() empty}, the
     *                {@link Random} will be instantiated without a seed - a random one will be chosen. Otherwise, the random is
     *                instantiated with the {@link String#hashCode()} of the string.
     * @throws NullPointerException If {@code context} is {@code null}.
     * @since 0.2.3
     */
    public GeneratorTask(@NotNull KayonContext context, boolean numeric, int length, int count, @Nullable @NonNls String seed)
    {
        this(context, numeric, length, count, (seed == null || seed.isEmpty()) ? new Random() : new Random(seed.hashCode()));
    }

    /**
     * Constructs a new GeneratorTask with a string seed.
     *
     * @param numeric Whether to generate numeric characters in the root word.
     * @param length  The length of the generated root words.
     * @param count   The count of nouns to generate.
     * @param random  The {@link Random} instance to use to generate random values.
     * @throws NullPointerException If {@code context} or {@code random} is {@code null}.
     * @since 0.2.3
     */
    public GeneratorTask(@NotNull KayonContext context, boolean numeric, int length, int count, @NotNull Random random)
    {
        checkNotNull(random);
        checkNotNull(context);
        this.context = context;
        this.numeric = numeric;
        this.length = length;
        this.count = count;
        this.random = random;
    }

    /**
     * @return Always {@code null}.
     * @throws Exception Can be thrown if the execution of the SQL fails.
     * @since 0.2.3
     */
    @Override
    @Nullable
    protected Void call() throws Exception
    {
        final int nounDeclensions = NounViewController.nounDeclensions.size();
        final int genders = Gender.values().length;
        final int batchSize = context.getConfig().getInt("gui.extras.noungenerator.batchSize");
        final boolean log = context.getConfig().getBoolean("gui.extras.noungenerator.log");

        LOGGER.info("GeneratorTask running");
        LOGGER.info("    nounDeclensions = " + nounDeclensions);
        LOGGER.info("    genders = " + genders);
        LOGGER.info("    batchSize = " + batchSize);
        LOGGER.info("    numeric = " + numeric);
        LOGGER.info("    length = " + length);
        LOGGER.info("    count = " + count);

        for (int c = 0; c < count; )
        {
            if (log) LOGGER.info("Iteration " + c);
            /*
             * Generate noun
             */
            @NotNull final String rootWord = RandomStringUtils.random(length, 0, 0, true, numeric, null, random).toLowerCase();
            @Nullable NounDeclension nounDeclension = NounViewController.nounDeclensions.get(random.nextInt(nounDeclensions));
            nounDeclension = nounDeclension instanceof DummyNounDeclension ? null : nounDeclension;
            @NotNull final Gender gender = Gender.values()[random.nextInt(genders)];

            @NotNull final Noun noun = new Noun(context, nounDeclension, gender, rootWord);

            if (log) LOGGER.info("Generated noun " + noun);
            /*
             * Execute SQL (or add to batch)
             */
            final boolean cancelled = isCancelled() || Thread.interrupted();
            final boolean doExecute = c % batchSize == 0 || c + 1 == count || cancelled;
            System.out.print("\rc = " + (doExecute ? c + "\n": c));
            context.getNounSQLFactory().saveNounToDatabase(noun, !doExecute);

            updateProgress(++c, count);

            if (cancelled)
                return null;
        }
        return null;
    }
}
