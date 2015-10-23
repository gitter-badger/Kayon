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

package cf.kayon.cli;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.Vocab;
import cf.kayon.core.adjective.Adjective;
import cf.kayon.core.adjective.AdjectiveDeclension;
import cf.kayon.core.adjective.ComparisonDegree;
import cf.kayon.core.adjective.impl.*;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.impl.*;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows some I/O method to perform command-like actions.
 *
 * @author Ruben Anders
 * @see cf.kayon.cli.fx.FxInterface
 * @since 0.0.1
 * @deprecated Use the graphical JavaFX interface instead.
 */
@Deprecated
public class CommandLineAccessor
{
    /**
     * All command line actions.
     *
     * @since 0.0.1
     */
    @Deprecated
    @NotNull
    private static Map<Pattern, TriFunction<Pair<String, Boolean>, String, Matcher, List<Vocab>>> actions = Maps.newHashMap();

    /**
     * All {@link AdjectiveDeclension}s.
     *
     * @since 0.0.1
     */
    @Deprecated
    @NotNull
    private static Map<Integer, AdjectiveDeclension> adjectiveDeclensionMap = new HashMap<>(6);

    /**
     * All {@link NounDeclension}s.
     *
     * @since 0.0.1
     */
    @Deprecated
    @NotNull
    private static Map<Integer, NounDeclension> nounDeclensionMap = new HashMap<>(9);

    static
    {
        /*
         * exit
         */
        actions.put(Pattern.compile("^exit", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> new ImmutablePair<>(null, false)); // Once database exists, change this

        actions.put(Pattern.compile("^help", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> new ImmutablePair<>(
                "help                                                          - Show this text\n" +
                "show <id>                                                     - Display all forms and details about a word.\n" +
                "exit                                                          - Exit the application\n" + "adjective\n" +
                "  create <declension id> <root word>                          - Create a new Adjective. Shows you the ID of the new Adjective.\n" +
                "  def <id> <comparison degree> <gender> <count> <case> <form> - Define a form of an Adjective.\n" +
                "  comp <id> <comparison degree> <allow/disallow>              - Enable or disable a Comparison Degree of the Adjective.\n" +
                "  declensions                                                 - Shows you all AdjectiveDeclensions and their IDs.\n" + "noun\n" +
                "  create <declension id> <gender> <root word>                 - Create a new Noun. Shows you the ID of the new Noun.\n" +
                "  def <id> <case> <count> <form>                              - Define a form of a Noun.\n" +
                "  declensions                                                 - Shows you all NounDeclensions and theird IDs.", true));

        /*
         * show <id>
         */
        actions.put(Pattern.compile("^show ([0-9]*)", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            StringBuilder sB = new StringBuilder();
            try
            {
                Vocab voc = vocab.get(Integer.parseInt(matcher.group(1)));
                voc.commandLineRepresentation().forEach(str -> {
                    sB.append(str);
                    sB.append("\n");
                });
            } catch (NumberFormatException | IndexOutOfBoundsException e)
            {
                sB.append("Specified number does not exist!");
            }
            return new ImmutablePair<>(sB.toString(), true);
        });

        //region Adjective actions
        adjectiveDeclensionMap.put(-1, null);
        adjectiveDeclensionMap.put(0, OAAdjectiveDeclension.getInstance());
        adjectiveDeclensionMap.put(1, ORAAdjectiveDeclension.getInstance());
        adjectiveDeclensionMap.put(2, IOneEndAdjectiveDeclension.getInstance());
        adjectiveDeclensionMap.put(3, ITwoEndAdjectiveDeclension.getInstance());
        adjectiveDeclensionMap.put(4, IThreeEndAdjectiveDeclension.getInstance());

        /*
         * adjective create <declension> <root word>
         */
        actions.put(Pattern.compile("^adjective create (-?[0-9]) (.*)", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            int declension = Integer.parseInt(matcher.group(1));
            String rootWord = matcher.group(2).toLowerCase();
            Adjective adjective = new Adjective(adjectiveDeclensionMap.get(declension), rootWord);
            vocab.add(adjective);
            int index = vocab.indexOf(adjective);
            return new ImmutablePair<>("Successfully registered adjective with ID " + index, true);
        });

        /*
         * adjective def <id> <comp degree> <gender> <count> <case> <form>
         */
        actions.put(Pattern.compile(
                "^adjective def ([0-9]*) (POSITIVE|COMPARATIVE|SUPERLATIVE) (MASCULINE|FEMININE|NEUTER) (SINGULAR|PLURAL) (NOMINATIVE|GENITIVE|DATIVE|ACCUSATIVE|ABLATIVE|VOCATIVE) (.*)",
                Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            try
            {
                int id = Integer.parseInt(matcher.group(1));

                Adjective a = (Adjective) vocab.get(id);
                ComparisonDegree comparisonDegree = ComparisonDegree.valueOf(matcher.group(2).toUpperCase());
                Gender gender = Gender.valueOf(matcher.group(3).toUpperCase());
                Count count = Count.valueOf(matcher.group(4).toUpperCase());
                Case caze = Case.valueOf(matcher.group(5).toUpperCase());
                String form = matcher.group(6).toLowerCase();

                a.defineForm(comparisonDegree, caze, count, gender, form);
            } catch (ClassCastException e)
            {
                return new ImmutablePair<>("ID supplied does not point to a Adjective!", true);
            } catch (IllegalArgumentException e) // Also NumberFormatException
            {
                return new ImmutablePair<>("One argument was invalid!", true);
            } catch (IndexOutOfBoundsException e)
            {
                return new ImmutablePair<>("ID supplied does not exist!", true);
            }
            return new ImmutablePair<>("Success", true);
        });

        /*
         * adjective comp <comp degree> <allow|disallow>
         */
        actions.put(Pattern.compile("^adjective comp ([0-9]*) (POSITIVE|COMPARATIVE|SUPERLATIVE) (disallow|allow)", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            try
            {
                int id = Integer.parseInt(matcher.group(1));
                Adjective a = (Adjective) vocab.get(id);
                ComparisonDegree comparisonDegree = ComparisonDegree.valueOf(matcher.group(2));
                boolean allow = matcher.group(3).equalsIgnoreCase("allow");


                a.setAllows(comparisonDegree, allow);
                return new ImmutablePair<>("Success: Set to " + (allow ? "allow " : "disallow ") + comparisonDegree.toString(), true);
            } catch (ClassCastException e)
            {
                return new ImmutablePair<>("ID supplied does not point to a Adjective!", true);
            } catch (IllegalArgumentException e)
            {
                return new ImmutablePair<>("Invalid comparison degree!", true);
            } catch (IndexOutOfBoundsException e)
            {
                return new ImmutablePair<>("ID supplied does not exist!", true);
            }
        });

        actions.put(Pattern.compile("^adjective declensions", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            StringBuilder sB = new StringBuilder();
            adjectiveDeclensionMap.forEach((i, aD) -> {
                if (aD == null)
                {
                    sB.append(i).append(": No Declension (all forms have to be defined!)");
                } else
                {
                    sB.append(i).append(": ").append(aD.getClass().getSimpleName());
                }
            });
            return new ImmutablePair<>(sB.toString(), true);
        });
        //endregion

        //region Noun actions
        nounDeclensionMap.put(-1, null);
        nounDeclensionMap.put(0, ONounDeclension.getInstance());
        nounDeclensionMap.put(1, ORNounDeclension.getInstance());
        nounDeclensionMap.put(2, ANounDeclension.getInstance());
        nounDeclensionMap.put(3, ConsonantNounDeclension.getInstance());
        nounDeclensionMap.put(4, ENounDeclension.getInstance());
        nounDeclensionMap.put(5, UNounDeclension.getInstance());
        nounDeclensionMap.put(6, INounDeclension.getInstance());
        nounDeclensionMap.put(7, MixedNounDeclension.getInstance());

        actions.put(Pattern.compile("^noun create (-?[0-9]) (MASCULINE|FEMININE|NEUTER) (.*)", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            try
            {
                int decl = Integer.parseInt(matcher.group(1));
                String gend = matcher.group(2);
                String rootWord = matcher.group(3);
                @Nullable
                NounDeclension nounDeclension = nounDeclensionMap.get(decl);
                Gender gender = Gender.valueOf(gend.toUpperCase());

                Noun noun = new Noun(nounDeclension, gender, rootWord);
                vocab.add(noun);
                int index = vocab.indexOf(noun);
                return new ImmutablePair<>("Successfully registered noun with ID " + Integer.toString(index), true);
            } catch (IllegalArgumentException e)
            {
                return new ImmutablePair<>("Invalid gender or declension ID! (" + e.getClass().getSimpleName() + ")", true);
            }
        });

        actions.put(Pattern.compile("^noun def ([0-9]*) (NOMINATIVE|GENITIVE|DATIVE|ACCUSATIVE|ABLATIVE|VOCATIVE) (SINGULAR|PLURAL) (.*)", Pattern.CASE_INSENSITIVE),
                    (s, matcher, vocab) -> {
                        try
                        {
                            Noun noun = (Noun) vocab.get(Integer.parseInt(matcher.group(1)));
                            Case caze = Case.valueOf(matcher.group(2).toUpperCase());
                            Count count = Count.valueOf(matcher.group(3).toUpperCase());
                            String form = matcher.group(4).toLowerCase();
                            noun.setDefinedForm(caze, count, form);
                            return new ImmutablePair<>("Successfully defined form.", true);
                        } catch (NumberFormatException e)
                        {
                            return new ImmutablePair<>("Specified ID is in invalid format!", true);
                        } catch (ClassCastException e)
                        {
                            return new ImmutablePair<>("Specified ID does not point to a Noun!", true);
                        } catch (IndexOutOfBoundsException e)
                        {
                            return new ImmutablePair<>("Unknown ID!", true);
                        } catch (PropertyVetoException e)
                        {
                            return new ImmutablePair<>("Specified form is invalid!", true);
                        }
                    });

        actions.put(Pattern.compile("noun declensions", Pattern.CASE_INSENSITIVE), (s, matcher, vocab) -> {
            StringBuilder sB = new StringBuilder();
            nounDeclensionMap.forEach((i, nD) -> {
                if (nD == null)
                    sB.append(i).append(": No Declension (all forms have to be defined!)\n");
                else
                    sB.append(i).append(": ").append(nD.getClass().getSimpleName()).append("\n");
            });
            return new ImmutablePair<>(sB.toString(), true);
        });
    }

    /**
     * The list of vocab this CommandLineAccessor manages.
     *
     * @since 0.0.1
     */
    // TODO remove this later once DB is implemented
    @NotNull
    @Deprecated
    private List<Vocab> vocab = new ArrayList<>();

    /**
     * Processes a command string.
     * <p>
     * If multiple command match the command (should not happen), they both get executed in the order they have been defined in the static initializer block.
     *
     * @param commandLine The command line string the user entered.
     * @return A pair: A response string from this {@link CommandLineAccessor} that is to be printed to some sort of console and
     * a boolean whether the application should continue running.
     * @since 0.0.1
     */
    @NotNull
    @Deprecated
    public Pair<String, Boolean> processCommand(String commandLine)
    {
        if (commandLine != null)
            for (Map.Entry<Pattern, TriFunction<Pair<String, Boolean>, String, Matcher, List<Vocab>>> entry : actions.entrySet())
            {
                Matcher matcher = entry.getKey().matcher(commandLine);
                if (matcher.matches())
                {
                    return entry.getValue().accept(commandLine, matcher, this.vocab);
                }
            }
        return new ImmutablePair<>("Command not recognized!", true);
    }

    /**
     * A function with three parameters and a return value.
     * <p>
     * Everything in this function is supposed to be {@link NotNull}.
     *
     * @param <R> The result.
     * @param <F> The first argument.
     * @param <S> The second argument.
     * @param <T> The third argument.
     * @author Ruben Anders
     * @since 0.0.1
     */
    @Deprecated
    @FunctionalInterface
    public interface TriFunction<R, F, S, T>
    {
        /**
         * Applies three parameters to this function.
         *
         * @param first  The first argument.
         * @param second The second argument.
         * @param third  The third argument.
         * @return A return value.
         * @throws NullPointerException If any of the arguments is {@code null}.
         * @since 0.0.1
         */
        @Deprecated
        @NotNull
        R accept(@NotNull F first, @NotNull S second, @NotNull T third);
    }
}
