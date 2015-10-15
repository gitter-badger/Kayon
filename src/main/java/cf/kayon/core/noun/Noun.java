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

package cf.kayon.core.noun;

import cf.kayon.core.*;
import cf.kayon.core.Count;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static cf.kayon.core.util.StringUtil.requireNonEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class Noun implements Translatable, Vocab
{
    //region Fields
    @NotNull
    private transient ArrayTable<Case, Count, String> declinedForms = ArrayTable.create(
            () -> new ObjectArrayIterator<>(Case.values()),
            () -> new ObjectArrayIterator<>(Count.values()));

    @NotNull
    private HashBasedTable<Case, Count, String> definedForms = HashBasedTable.create(6, 2);

    @NotNull
    private Map<String, String> translations = Maps.newHashMap();

    @NotNull
    private Gender gender;

    @NotNull
    private String rootWord;

    @Nullable
    private NounDeclension nounDeclension;

//    @Nullable
//    private HashBasedTable<Case, Count, List<String>> alternateDeclinedForms = null; //Lazy
//
//    @Nullable
//    private HashBasedTable<Case, Count, List<String>> alternateDefinedForms = null; //Lazy

//    @Nullable
//    private HashBasedTable<Case, Count, Boolean> alternateOverrides = null; //Lazy
    //endregion

    //region Constructors
    public Noun(@Nullable NounDeclension nounDeclension, @NotNull Gender gender, @NotNull String rootWord)
    {
        this.nounDeclension = nounDeclension;
        this.gender = checkNotNull(gender);
        this.rootWord = requireNonEmpty(rootWord);
        _declineIntoBuffer();
    }

    public Noun(@NotNull Gender gender, @NotNull String rootWord)
    {
        this(null, gender, rootWord);
    }
    //endregion

    //region Defining forms
    public void defineForm(@NotNull Case caze, @NotNull Count count, @NotNull String form)
    {
        checkNotNull(caze);
        checkNotNull(count);
        requireNonEmpty(form);
        definedForms.put(caze, count, form);
    }

    public boolean isFormDefined(@NotNull Case caze, @NotNull Count count)
    {
        return definedForms.contains(checkNotNull(caze), checkNotNull(count));
    }

    public int formsDefined()
    {
        return definedForms.size();
    }

    @Nullable
    public String getDefinedFormOrNull(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return definedForms.get(caze, count);
    }

    @NotNull
    public String getDefinedFormOrThrow(@NotNull Case caze, @NotNull Count count) throws NoSuchElementException
    {
        @Nullable
        String definedFormOrNull = getDefinedFormOrNull(caze, count); // Delegates NotNull
        if (definedFormOrNull == null)
            throw new NoSuchElementException("No element for Case " + caze.toString() + " and Count " + count.toString());
        return definedFormOrNull;
    }

    @NotNull
    public Table<Case, Count, String> getDefinedFormsView()
    {
        return Tables.unmodifiableTable(this.definedForms);
    }

    @NotNull
    public Table<Case, Count, String> getDefinedFormsCopy()
    {
        return HashBasedTable.create(this.definedForms);
    }

    public void deleteAllDefinedForms()
    {
        this.definedForms.clear();
    }

    public void removeDefinedForm(@NotNull Case caze, @NotNull Count count)
    {
        this.definedForms.remove(caze, count);
    }

    //endregion

    //region Declension-based operations
    public void removeNounDeclension()
    {
        this.setNounDeclension(null);
    }

    public boolean hasNounDeclension()
    {
        return this.nounDeclension != null;
    }

    @NotNull
    public String getDeclinedFormOrThrow(@NotNull Case caze, @NotNull Count count) throws FormingException
    {
        @Nullable
        String declinedFormOrNull = getDeclinedFormOrNull(caze, count);
        if (declinedFormOrNull == null)
            throw new FormingException("Could not determine form for case " + caze + " and count " + count);
        return declinedFormOrNull;
    }

    @Nullable
    public String getDeclinedFormOrNull(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return this.declinedForms.get(caze, count);
    }

    public boolean hasDeclinationFailed(@NotNull Case caze, @NotNull Count count)
    {
        return this.declinedForms.get(checkNotNull(caze),
                                      checkNotNull(count)) == null;
    }

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
    private void _declineIntoBuffer()
    {
        //You could do that, but since every single cell is visited anyways, everything will be overriden anyways.
        if (!this.hasNounDeclension())
        {
            this.declinedForms.eraseAll();
//            if (this.alternateDeclinedForms != null)
//            {
//                this.alternateDeclinedForms.clear();
//            }
            return;
        }
        for (Case currentCase : Case.values())
            for (Count currentCount : Count.values())
            {
                try
                {
                    try
                    {
                        this.declinedForms.put(currentCase, currentCount, getNounDeclension().decline(currentCase, currentCount, this.gender, this.rootWord));
                    } catch (FormingException ignored)
                    {
                        this.declinedForms.put(currentCase, currentCount, null);
                    }
//                    if (this.getNounDeclension().hasAlternateForms(currentCase, currentCount, getGender(), getRootWord()))
//                    {
//                        _ensureAlternateDeclinedFormsTable();
//                        List<String> altForms = this.getNounDeclension().getAlternateForms(currentCase, currentCount, getGender(), getRootWord());
//                        if (altForms != null)
//                        {
//                            //noinspection ConstantConditions
//                            this.alternateDeclinedForms.put(currentCase, currentCount, altForms);
//                        }
//                    }
                } catch (NoDeclensionException e)
                {
                    //This will/should not happen in a single-threaded environment
                    throw new ConcurrentModificationException("Concurrent access to Noun is not allowed", e);
                }
            }
    }
    //endregion

    //region Basic Setters and Getters
    @NotNull
    public Gender getGender()
    {
        return gender;
    }

    public void setGender(@NotNull Gender gender)
    {
        this.gender = gender;
        _declineIntoBuffer();
    }

    @NotNull
    public String getRootWord()
    {
        return rootWord;
    }

    public void setRootWord(@NotNull String rootWord)
    {
        this.rootWord = requireNonEmpty(rootWord);
        _declineIntoBuffer();
    }

    public NounDeclension getNounDeclension() throws NoDeclensionException
    {
        if (this.nounDeclension == null)
            throw new NoDeclensionException();
        return this.nounDeclension;
    }

    public void setNounDeclension(@Nullable NounDeclension valueToSetTo)
    {
        this.nounDeclension = valueToSetTo;
        _declineIntoBuffer();
    }
    //endregion

    //region Alternate Forms
//    @Nullable
//    public List<String> getAlternateForms(@NotNull Case caze, @NotNull Count count)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//
//        if (this.alternateDeclinedForms == null && this.alternateDefinedForms == null)
//            return null;
//
//        List<String> merge = new ArrayList<>();
//        if (this.alternateDeclinedForms != null)
//        {
//            if (this.alternateOverrides == null)
//            {
//                //No overrides (table object is null)
//                List<String> gottenList = this.alternateDeclinedForms.get(caze, count);
//                if (gottenList != null)
//                    merge.addAll(gottenList);
//            } else
//            {
//                //There are overrides, get the boolean value out
//                Boolean gotten = this.alternateOverrides.get(caze, count);
//                if (gotten != null) //There will most likely be no mapping for these two values
//                {
//                    if (!gotten) //There is a mapping, check for it to be false
//                    {
//                        List<String> gottenList = this.alternateDeclinedForms.get(caze, count);
//                        if (gottenList != null)
//                            merge.addAll(gottenList);
//                    }
//                } else
//                { //No override (gotten boolean is null)
//                    List<String> gottenList = this.alternateDeclinedForms.get(caze, count);
//                    if (gottenList != null)
//                        merge.addAll(gottenList);
//                }
//            }
//        }
//
//        if (this.alternateDefinedForms != null)
//        {
//            List<String> gottenList = this.alternateDefinedForms.get(caze, count);
//            if (gottenList != null)
//                merge.addAll(gottenList);
//        }
//
//        return merge;
//    }

//    @Nullable
//    public List<String> getAlternateDeclinedForms(@NotNull Case caze, @NotNull Count count)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        if (this.alternateDeclinedForms == null)
//            return null;
//        List<String> gottenList = this.alternateDeclinedForms.get(caze, count);
//        if (gottenList != null)
//            return Collections.unmodifiableList(gottenList);
//        return null;
//    }
//
//    @Nullable
//    public List<String> getAlternateDefinedForms(@NotNull Case caze, @NotNull Count count)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        if (this.alternateDefinedForms == null)
//            return null;
//        List<String> gottenList = this.alternateDefinedForms.get(caze, count);
//        if (gottenList != null)
//            return Collections.unmodifiableList(gottenList);
//        return null;
//    }
//
//    @Nullable
//    public Table<Case, Count, List<String>> getAlternateDeclinedForms()
//    {
//        if (this.alternateDeclinedForms == null)
//            return null;
//        return Tables.unmodifiableTable(this.alternateDeclinedForms);
//    }
//
//    @Nullable
//    public Table<Case, Count, List<String>> getAlternateDefinedForms()
//    {
//        if (this.alternateDefinedForms == null)
//            return null;
//        return Tables.unmodifiableTable(this.alternateDefinedForms);
//    }
//
//    public boolean isOverrideAlternateDeclinedForms(@NotNull Case caze, @NotNull Count count)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        if (this.alternateOverrides == null)
//            return false;
//        @Nullable
//        Boolean gottenBoolean = this.alternateOverrides.get(caze, count);
//        if (gottenBoolean == null)
//            return false;
//        return gottenBoolean;
//    }
//
//    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
//    public void setOverrideAlternateDeclinedForms(@NotNull Case caze, @NotNull Count count, boolean newValue)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        _ensureAlternateOverridesTable();
//        //noinspection ConstantConditions
//        this.alternateOverrides.put(caze, count, newValue);
//    }
//
//    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH")
//    public void addAlternateDefinedForm(@NotNull Case caze, @NotNull Count count, @NotNull String form, @Nullable Integer index)
//    {
//        checkNotNull(caze);
//        checkNotNull(count);
//        requireNonEmpty(form);
//        _ensureAlternateDefinedFormsTable();
//        //noinspection ConstantConditions
//        @Nullable
//        List<String> gottenList = this.alternateDefinedForms.get(caze, count);
//        if (gottenList != null)
//            if (index != null)
//                gottenList.add(index, form);
//            else
//                gottenList.add(form);
//        else
//            this.alternateDefinedForms.put(caze, count, Lists.newArrayList(form));
//    }
//
//    public void addAlternateDefinedForm(@NotNull Case caze, @NotNull Count count, @NotNull String form)
//    {
//        this.addAlternateDefinedForm(caze, count, form, null);
//    }
//
//    private void _ensureAlternateDeclinedFormsTable()
//    {
//        if (this.alternateDeclinedForms == null)
//            this.alternateDeclinedForms = HashBasedTable.create(6, 2);
//    }
//
//    private void _ensureAlternateDefinedFormsTable()
//    {
//        if (this.alternateDefinedForms == null)
//            this.alternateDefinedForms = HashBasedTable.create(6, 2);
//    }
//
//    private void _ensureAlternateOverridesTable()
//    {
//        if (this.alternateOverrides == null)
//            this.alternateOverrides = HashBasedTable.create(6, 2);
//    }

    @Nullable
    @Override
    public String translateTo(String iso639_1)
    {
        return translations.get(iso639_1);
    }

    @Nullable
    public String getFormOrNull(@NotNull Case caze, @NotNull Count count)
    {
        @Nullable
        String definedFormOrNull = getDefinedFormOrNull(caze, count); // Delegates NotNull
        if (definedFormOrNull != null)
            return definedFormOrNull;
        return getDeclinedFormOrNull(caze, count);
    }

    @NotNull
    public String getFormOrThrow(@NotNull Case caze, @NotNull Count count) throws NoSuchElementException
    {
        @Nullable
        String formOrNull = getFormOrNull(caze, count); // Delegates NotNull
        if (formOrNull == null)
            throw new NoSuchElementException("No form for Case " + caze.toString() + " and Count " + count.toString());
        return formOrNull;
    }

    @NotNull
    @Override
    public List<String> commandLineRepresentation()
    {
        List<String> buffer = new ArrayList<>(10);
        buffer.add("+----------+----------------+----------------+");
        buffer.add("|          |    SINGULAR    |      PLURAL    |");
        buffer.add("+----------+----------------+----------------+");
        for (Case caze : Case.values())
        {
            StringBuilder sB = new StringBuilder(46);
            sB.append("|");
            sB.append(Strings.padStart(caze.toString(), 10, ' '));
            sB.append("|");

            sB.append(isFormDefined(caze, Count.SINGULAR) ? '$' : ' ');
            @Nullable
            String singularFormOrNull = getFormOrNull(caze, Count.SINGULAR);
            singularFormOrNull = singularFormOrNull == null ? "???????????????" : Strings.padEnd(singularFormOrNull, 15, ' ');
            sB.append(singularFormOrNull);

            sB.append("|");

            sB.append(isFormDefined(caze, Count.PLURAL) ? '$' : ' ');
            @Nullable
            String pluralFormOrNull = getFormOrNull(caze, Count.PLURAL);
            pluralFormOrNull = pluralFormOrNull == null ? "???????????????" : Strings.padEnd(pluralFormOrNull, 15, ' ');
            sB.append(pluralFormOrNull);

            sB.append("|");

            buffer.add(sB.toString());
        }

        buffer.add("+----------+----------------+----------------+");

        return buffer;
    }
    //endregion

    //region SQL
//    public void dbTest()
//    {
//        try (Connection connect = DriverManager.getConnection("jdbc:hsqldb:file:database.db"))
//        {
//
//            Statement s = connect.createStatement();
//        } catch (SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }
    //endregion
}
