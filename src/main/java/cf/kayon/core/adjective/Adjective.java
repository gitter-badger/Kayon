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

package cf.kayon.core.adjective;

import cf.kayon.core.*;
import cf.kayon.core.Count;
import cf.kayon.core.util.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Adjective implements Vocab
{

    //region Fields
    @NotNull
    private transient ArrayTable<ComparisonDegree, Case, ArrayTable<Count, Gender, String>> declinedForms =
            ArrayTable.create(() -> new ObjectArrayIterator<>(ComparisonDegree.values(), 0), () -> new ObjectArrayIterator<>(Case.values(), 0));

    @NotNull
    private HashBasedTable<ComparisonDegree, Case, Table<Count, Gender, String>> definedForms = HashBasedTable.create(3, 6);

    @NotNull
    private String rootWord;

    @Nullable
    private AdjectiveDeclension adjectiveDeclension;

    @NotNull
    private HashMap<String, String> translations = Maps.newHashMap();

    private boolean allowsPositive = true;
    private boolean allowsComparative = true;
    private boolean allowsSuperlative = true;
    //endregion

    //region Constructors
    public Adjective(@Nullable AdjectiveDeclension adjectiveDeclension, @NotNull String rootWord)
    {
        this.adjectiveDeclension = adjectiveDeclension;
        this.rootWord = StringUtil.requireNonEmpty(rootWord);
        _declineIntoBuffer();
    }

    public Adjective(@NotNull String rootWord)
    {
        this(null, rootWord);
    }
    //endregion

    //region Getters and Setters
    @NotNull
    public String getRootWord()
    {
        return rootWord;
    }

    public void setRootWord(@NotNull String rootWord)
    {
        this.rootWord = rootWord;
        _declineIntoBuffer();
    }

    @NotNull
    public AdjectiveDeclension getAdjectiveDeclension() throws NoDeclensionException
    {
        if (this.adjectiveDeclension == null)
            throw new NoDeclensionException();
        return adjectiveDeclension;
    }

    public void setAdjectiveDeclension(@Nullable AdjectiveDeclension adjectiveDeclension)
    {
        this.adjectiveDeclension = adjectiveDeclension;
        _declineIntoBuffer();
    }

    public boolean allowsPositive()
    {
        return allowsPositive;
    }

    public void setAllowsPositive(boolean allowsPositive)
    {
        this.allowsPositive = allowsPositive;
        _declineIntoBuffer();
    }

    public boolean allowsComparative()
    {
        return allowsComparative;
    }

    public void setAllowsComparative(boolean allowsComparative)
    {
        this.allowsComparative = allowsComparative;
        _declineIntoBuffer();
    }

    public boolean allowsSuperlative()
    {
        return allowsSuperlative;
    }

    public void setAllowsSuperlative(boolean allowsSuperlative)
    {
        this.allowsSuperlative = allowsSuperlative;
        _declineIntoBuffer();
    }

    public boolean allows(@NotNull ComparisonDegree comparisonDegree)
    {
        checkNotNull(comparisonDegree);
        switch (comparisonDegree)
        {
            case POSITIVE:
                return this.allowsPositive();
            case COMPARATIVE:
                return this.allowsComparative();
            case SUPERLATIVE:
                return this.allowsSuperlative();
            default:
                return false;
        }
    }

    public void setAllows(@NotNull ComparisonDegree comparisonDegree, boolean allows)
    {
        checkNotNull(comparisonDegree);
        switch (comparisonDegree)
        {
            case POSITIVE:
                this.setAllowsPositive(allows);
                break;
            case COMPARATIVE:
                this.setAllowsComparative(allows);
                break;
            case SUPERLATIVE:
                this.setAllowsSuperlative(allows);
                break;
        }
    }
    //endregion

    //region Defining Forms
    public void defineForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String form)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        StringUtil.requireNonEmpty(form);

        Table<Count, Gender, String> subTable = this.definedForms.get(comparisonDegree, caze);
        if (subTable == null)
        {
            subTable = HashBasedTable.create(2, 3);
            subTable.put(count, gender, form);
            this.definedForms.put(comparisonDegree, caze, subTable);
        } else
        {
            subTable.put(count, gender, form);
        }
        _declineIntoBuffer(); // Equaling forms
    }

    public boolean isFormDefined(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        Table<Count, Gender, String> subTable = definedForms.get(comparisonDegree, caze);
        return subTable != null && subTable.contains(count, gender);
    }

    public int formsDefined()
    {
        int counter = 0;
        for (Table.Cell<ComparisonDegree, Case, Table<Count, Gender, String>> currentCell : this.definedForms.cellSet())
        {
            if (allows(currentCell.getRowKey()))
                counter += currentCell.getValue().size();
        }
        return counter;
    }

    @NotNull
    public String getDefinedFormOrThrow(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
            throws NoSuchElementException
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        @Nullable
        Table<Count, Gender, String> subTable = definedForms.get(comparisonDegree, caze);
        if (subTable == null)
            throw new NoSuchElementException();

        @Nullable
        String definedFormOrNull = subTable.get(count, gender);
        if (definedFormOrNull == null)
            throw new NoSuchElementException();
        return definedFormOrNull;
    }

    @Nullable
    public String getDefinedFormOrNull(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        @Nullable
        Table<Count, Gender, String> subTable = definedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return null;

        // Nullable
        return subTable.get(count, gender);
    }

    // TODO Add to javadoc: Even though a comparison degree is disallowed, stale values still may reside in this returned table
    @NotNull
    public Table<ComparisonDegree, Case, Table<Count, Gender, String>> getDefinedFormsView()
    {
        return Tables.unmodifiableTable(this.definedForms);
    }

    public void deleteAllDefinedForms()
    {
        this.definedForms.clear();
        _declineIntoBuffer();
    }

    public void removeDefinedForm(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);

        @Nullable
        Table<Count, Gender, String> subTable = this.definedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return;
        subTable.remove(count, gender);
        _declineIntoBuffer();
    }
    //endregion

    //region Declension-based operations
    public void removeAdjectiveDeclension()
    {
        this.setAdjectiveDeclension(null); // Delegates declining into buffer
    }

    public boolean hasAdjectiveDeclension()
    {
        return this.adjectiveDeclension != null;
    }

    @NotNull
    public String getDeclinedFormOrThrow(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
            throws FormingException
    {
        // NotNull is delegated to getDeclinedFormOrNull()
        String declinedFormOrNull = getDeclinedFormOrNull(comparisonDegree, caze, count, gender);
        if (declinedFormOrNull == null)
            throw new FormingException(
                    "Could not determine form for Comparison Level " + comparisonDegree.toString() + ", Case " + caze.toString() + ", Count " + count.toString() +
                    " and Gender " + gender.toString());
        return declinedFormOrNull;
    }

    @Nullable
    public String getDeclinedFormOrNull(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        requireAllowedComparisonDegree(comparisonDegree);
        checkNotNull(caze);
        checkNotNull(count);
        checkNotNull(gender);
        if (!this.hasAdjectiveDeclension())
            return null;
        @Nullable
        ArrayTable<Count, Gender, String> subTable = this.declinedForms.get(comparisonDegree, caze);
        if (subTable == null)
            return null;
        return subTable.get(count, gender);
    }

    public boolean hasDeclinationFailed(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        // NotNull is delegated to getDeclinedFormOrNull()
        return getDeclinedFormOrNull(comparisonDegree, caze, count, gender) == null;
    }

    private void putDeclinedFormIfAbsent(
            @NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String form)
    {
        @Nullable
        ArrayTable<Count, Gender, String> subTable = this.declinedForms.get(comparisonDegree, caze);
        if (subTable == null) // First time only or after declension had been cleared
        {
            subTable = ArrayTable.create(() -> new ObjectArrayIterator<>(Count.values()), () -> new ObjectArrayIterator<>(Gender.values()));
            this.declinedForms.put(comparisonDegree, caze, subTable);
        }
        if (subTable.get(count, gender) == null)
            subTable.put(count, gender, form);
    }

    private void _declineIntoBuffer()
    {
        this.declinedForms.eraseAll();
        if (!this.hasAdjectiveDeclension()) { return; }

        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
            if (this.allows(comparisonDegree))
                // Just let the old values reside, if a comparison degree gets enabled later on,
                // this will run again and fill the relevant spaces with correct data
                for (Case caze : Case.values())
                    for (Count count : Count.values())
                        for (Gender gender : Gender.values())
                            try
                            {
                                if (this.isFormDefined(comparisonDegree, caze, count, gender))
                                {
                                    Set<AdjectiveForm> equalForms = this.getAdjectiveDeclension().getEqualForms(comparisonDegree, caze, count, gender);
                                    if (equalForms != null)
                                        for (AdjectiveForm form : equalForms)
                                            if (!form.equals(new AdjectiveForm(comparisonDegree, caze, count, gender)))
                                                try
                                                {
                                                    String definedFormOrNull = getDefinedFormOrThrow(comparisonDegree, caze, count, gender);
                                                    putDeclinedFormIfAbsent(form.getComparisonDegree(), form.getCase(), form.getCount(), form.getGender(),
                                                                            definedFormOrNull);
                                                } catch (NoSuchElementException e)
                                                {
                                                    throw new ConcurrentModificationException("Concurrent modification of defined forms on Noun is not allowed", e);
                                                }
                                }
                            } catch (NoDeclensionException e)
                            {
                                throw new ConcurrentModificationException("Concurrent access to Adjective is not allowed", e);
                            }

        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
            if (this.allows(comparisonDegree))
                // Just let the old values reside, if a comparison degree gets enabled later on,
                // this will run again and fill the relevant spaces with correct data
                for (Case caze : Case.values())
                    for (Count count : Count.values())
                        for (Gender gender : Gender.values())
                            try
                            {
                                putDeclinedFormIfAbsent(comparisonDegree, caze, count, gender,
                                                        this.getAdjectiveDeclension().decline(comparisonDegree, caze, count, gender, getRootWord()));
                            } catch (NoDeclensionException e)
                            {
                                throw new ConcurrentModificationException("Concurrent access to Adjective is not allowed", e);
                            } catch (FormingException ignored) {} // null will reside
    }
    //endregion

    protected ComparisonDegree requireAllowedComparisonDegree(@NotNull ComparisonDegree comparisonDegree)
    {
        if (!allows(comparisonDegree)) // Delegates null check
            throw new IllegalArgumentException("Unallowed Comparison degree " + comparisonDegree.toString());
        return comparisonDegree;
    }

    @NotNull
    public String getFormOrThrow(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender) throws FormingException
    {
        @Nullable
        String defined = this.getDefinedFormOrNull(comparisonDegree, caze, count, gender); // Delegates not null checks
        if (defined != null)
            return defined;
        return this.getDeclinedFormOrThrow(comparisonDegree, caze, count, gender);
    }

    @Nullable
    public String getFormOrNull(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender)
    {
        @Nullable
        String defined = this.getDefinedFormOrNull(comparisonDegree, caze, count, gender); // Delegates not null checks
        if (defined != null)
            return defined;
        return this.getDeclinedFormOrNull(comparisonDegree, caze, count, gender);
    }

    @NotNull
    @Override
    public List<String> commandLineRepresentation()
    {
        List<String> buffer = new ArrayList<>(53);

        for (ComparisonDegree comparisonDegree : ComparisonDegree.values())
        {
            if (this.allows(comparisonDegree))
            {
                buffer.add("+----------+---------------+---------------+---------------+");
                buffer.add("| " + comparisonDegree.toString().substring(0, 8) + " |   MASCULINE   |    FEMININE   |     NEUTER    |");
                buffer.add("+----------+---------------+---------------+---------------+");
                for (Count count : Count.values())
                {
                    for (Case caze : Case.values())
                    {
                        StringBuilder builder = new StringBuilder(60);

                        builder.append("|");
                        builder.append(Strings.padStart(caze.toString(), 10, ' '));
                        builder.append("|");

                        for (Gender gender : Gender.values())
                        {
                            @Nullable
                            String formOrNull = getFormOrNull(comparisonDegree, caze, count, gender);
                            formOrNull = formOrNull == null ? "???????????????" : formOrNull;
                            builder.append(isFormDefined(comparisonDegree, caze, count, gender) ? "$" : " ");
                            builder.append(Strings.padEnd(formOrNull, 14, ' '));
                            builder.append("|");
                        }

                        buffer.add(builder.toString());
                    }
                    buffer.add("+----------+---------------+---------------+---------------+");
                }

            } else
            {
                buffer.add(comparisonDegree.toString() + " is disallowed");
            }
            buffer.add("");
        }

        return buffer;
    }

    private UUID uuid;

    @Nullable
    @Override
    public UUID getUuid()
    {
        return uuid;
    }

    @Override
    public void initializeUuid(@NotNull UUID uuid)
    {
        if (this.uuid != null)
            throw new IllegalStateException("UUID has already been initialized");
        this.uuid = uuid;
    }

    @Nullable
    @Override
    public Map<String, String> getTranslations()
    {
        return translations;
    }
}
