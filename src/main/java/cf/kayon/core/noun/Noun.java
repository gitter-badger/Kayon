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
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cf.kayon.core.util.StringUtil.requireNonEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

public class Noun implements Vocab
{
    //region Fields
    @NotNull
    private ArrayTable<Case, Count, String> declinedForms = ArrayTable.create(
            () -> new ObjectArrayIterator<>(Case.values()),
            () -> new ObjectArrayIterator<>(Count.values()));

    @NotNull
    private Table<Case, Count, String> definedForms = HashBasedTable.create(6, 2);

    @NotNull
    private Map<String, String> translations = Maps.newHashMap();

    @NotNull
    private Gender gender;

    @NotNull
    private String rootWord;

    @Nullable
    private NounDeclension nounDeclension;

    @NotNull
    private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    @NotNull
    private VetoableChangeSupport vetoSupport = new VetoableChangeSupport(this);
    //endregion

    //region Constructors
    public Noun(@Nullable NounDeclension nounDeclension, @NotNull Gender gender, @NotNull String rootWord)
    {
        this.nounDeclension = nounDeclension;
        this.gender = checkNotNull(gender);
        this.rootWord = requireNonEmpty(rootWord);
        _declineIntoBuffer();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Noun noun = (Noun) o;
        return com.google.common.base.Objects.equal(declinedForms, noun.declinedForms) &&
               Objects.equal(definedForms, noun.definedForms) &&
               Objects.equal(translations, noun.translations) &&
               Objects.equal(gender, noun.gender) &&
               Objects.equal(rootWord, noun.rootWord) &&
               Objects.equal(nounDeclension, noun.nounDeclension);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(declinedForms, definedForms, translations, gender, rootWord, nounDeclension);
    }

    public Noun(@NotNull Gender gender, @NotNull String rootWord)
    {
        this(null, gender, rootWord);
    }
    //endregion

    //region Defining forms
    public void setDefinedForm(@NotNull Case caze, @NotNull Count count, @NotNull String form) throws PropertyVetoException
    {
        String oldForm = getDefinedForm(caze, count);
        vetoSupport.fireVetoableChange(count + "_" + caze + "_defined", oldForm, form);
        definedForms.put(caze, count, form);
        changeSupport.firePropertyChange(count + "_" + caze + "_defined", oldForm, form);
    }

    @Nullable
    public String getDefinedForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return definedForms.get(caze, count);
    }

    public void removeDefinedForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        this.definedForms.remove(caze, count);
    }

    //endregion

    //region Declining forms
    @Nullable
    public String getDeclinedForm(@NotNull Case caze, @NotNull Count count)
    {
        checkNotNull(caze);
        checkNotNull(count);
        return this.declinedForms.get(caze, count);
    }

    private void _declineIntoBuffer()
    {
        for (Case caze : Case.values())
            for (Count count : Count.values())
            {
                String oldValue = this.declinedForms.get(caze, count);
                String newValue;
                if (this.getNounDeclension() == null)
                    newValue = null;
                else
                    try
                    {
                        newValue = getNounDeclension().decline(caze, count, this.gender, this.rootWord);
                    } catch (FormingException ignored)
                    {
                        newValue = null;
                    }
                this.declinedForms.put(caze, count, newValue);
                changeSupport.firePropertyChange(caze + "_" + count + "_declined", oldValue, newValue);
            }
    }
    //endregion

    //region Setters and Getters
    @NotNull
    public Gender getGender()
    {
        return gender;
    }

    public void setGender(@NotNull Gender gender) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("gender", this.gender, gender);
        Gender oldGender = this.gender;
        this.gender = gender;
        changeSupport.firePropertyChange("gender", oldGender, gender);
    }

    @NotNull
    public String getRootWord()
    {
        return rootWord;
    }

    public void setRootWord(@NotNull String rootWord) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("rootWord", this.rootWord, rootWord);
        String oldRootWord = this.rootWord;
        this.rootWord = rootWord;
        changeSupport.firePropertyChange("rootWord", oldRootWord, rootWord);
    }

    @Nullable
    public NounDeclension getNounDeclension()
    {
        return this.nounDeclension;
    }

    public void setNounDeclension(@Nullable NounDeclension nounDeclension) throws PropertyVetoException
    {
        vetoSupport.fireVetoableChange("nounDeclension", this.nounDeclension, nounDeclension);
        NounDeclension oldNounDeclension = this.nounDeclension;
        this.nounDeclension = nounDeclension;
        changeSupport.firePropertyChange("nounDeclension", oldNounDeclension, nounDeclension);
    }
    //endregion

    //region Translation
    @NotNull
    public Map<String, String> getTranslations()
    {
        return translations;
    }

    public void setTranslations(@NotNull Map<String, String> translations)
    {
        checkNotNull(translations);
        this.translations = translations;
    }

    @Nullable
    public String getForm(@NotNull Case caze, @NotNull Count count)
    {
        @Nullable
        String definedFormOrNull = getDefinedForm(caze, count); // Delegates NotNull
        if (definedFormOrNull != null)
            return definedFormOrNull;
        return getDeclinedForm(caze, count);
    }
    //endregion

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

            sB.append(getDefinedForms().contains(caze, Count.SINGULAR) ? '$' : ' ');
            @Nullable
            String singularFormOrNull = getForm(caze, Count.SINGULAR);
            singularFormOrNull = singularFormOrNull == null ? "???????????????" : Strings.padEnd(singularFormOrNull, 15, ' ');
            sB.append(singularFormOrNull);

            sB.append("|");

            sB.append(getDefinedForms().contains(caze, Count.PLURAL) ? '$' : ' ');
            @Nullable
            String pluralFormOrNull = getForm(caze, Count.PLURAL);
            pluralFormOrNull = pluralFormOrNull == null ? "???????????????" : Strings.padEnd(pluralFormOrNull, 15, ' ');
            sB.append(pluralFormOrNull);

            sB.append("|");

            buffer.add(sB.toString());
        }

        buffer.add("+----------+----------------+----------------+");

        return buffer;
    }

    //region Bean support
    {
        addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("gender") || evt.getPropertyName().equals("rootWord") || evt.getPropertyName().equals("nounDeclension"))
                _declineIntoBuffer();
        });

        addVetoableChangeListener(evt -> {
            if (evt.getNewValue() == null)
                throw new PropertyVetoException("New value may not be null!", evt);
            if (evt.getNewValue() instanceof CharSequence && ((CharSequence) evt.getNewValue()).length() == 0)
                throw new PropertyVetoException("New CharSequence value may not be empty!", evt);
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
        changeSupport.removePropertyChangeListener(listener);
    }

    public void addVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.addVetoableChangeListener(listener);
    }

    public void removeVetoableChangeListener(VetoableChangeListener listener)
    {
        vetoSupport.removeVetoableChangeListener(listener);
    }

    @NotNull
    public Table<Case, Count, String> getDefinedForms()
    {
        return definedForms;
    }
    //endregion
}
