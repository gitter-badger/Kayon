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

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.impl.*;
import cf.kayon.core.sql.StaticConnectionHolder;
import com.google.common.collect.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class NounViewController
{
    public static List<NounDeclension> nounDeclensions = Lists.newArrayList();

    static
    {
        nounDeclensions.add(ANounDeclension.getInstance());
        nounDeclensions.add(ONounDeclension.getInstance());
        nounDeclensions.add(ORNounDeclension.getInstance());
        nounDeclensions.add(ConsonantNounDeclension.getInstance());
        nounDeclensions.add(INounDeclension.getInstance());
        nounDeclensions.add(MixedNounDeclension.getInstance());
        nounDeclensions.add(UNounDeclension.getInstance());
        nounDeclensions.add(ENounDeclension.getInstance());
        nounDeclensions.add(DummyNounDeclension.getInstance());
    }

    @FXML
    ResourceBundle resources;

    Table<Case, Count, Triple<Text, TextField, CheckBox>> tableElements;

    @FXML
    CheckBox nomSgCheckBox, genSgCheckBox, datSgCheckBox, accSgCheckBox, ablSgCheckBox, vocSgCheckBox, nomPlCheckBox, genPlCheckBox, datPlCheckBox, accPlCheckBox,
            ablPlCheckBox, vocPlCheckBox;

    @FXML
    Text nomSgText, genSgText, datSgText, accSgText, ablSgText, vocSgText, nomPlText, genPlText, datPlText, accPlText, ablPlText, vocPlText;

    @FXML
    TextField nomSgTextField, genSgTextField, datSgTextField, accSgTextField, ablSgTextField, vocSgTextField, nomPlTextField, genPlTextField, datPlTextField,
            accPlTextField, ablPlTextField, vocPlTextField;

    @FXML
    Button resetButton, saveButton;

    @FXML
    TextField rootWordTextBox;

    @FXML
    ComboBox<Gender> genderComboBox;

    @FXML
    ComboBox<NounDeclension> declensionComboBox;

    @Nullable
    Noun initialBackingNoun;

    @Nullable
    Noun currentBackingNoun;

    /**
     * @see javafx.fxml.Initializable
     */
    public void initialize()
    {
        /*
         * ComboBoxes
         */
        genderComboBox.getItems().addAll(Gender.values());
        genderComboBox.setConverter(new StringConverter<Gender>()
        {
            BiMap<Gender, String> biMap = EnumHashBiMap.create(Gender.class);

            {
                for (Gender gender : Gender.values())
                    biMap.put(gender, NounViewController.this.resources.getString("Gender." + gender.toString()));
            }

            @Override
            public String toString(Gender object)
            {
                return biMap.get(object);
            }

            @Override
            public Gender fromString(String string)
            {
                return biMap.inverse().get(string);
            }
        });
        declensionComboBox.getItems().setAll(nounDeclensions);
        declensionComboBox.setConverter(new StringConverter<NounDeclension>()
        {
            BiMap<NounDeclension, String> biMap = HashBiMap.create();

            {
                for (NounDeclension current : NounViewController.nounDeclensions)
                {
                    String classSimpleName = current instanceof DummyNounDeclension ? "null" : current.getClass().getSimpleName();
                    biMap.put(current, NounViewController.this.resources.getString("NounDeclension." + classSimpleName));
                }
            }

            @Override
            public String toString(NounDeclension object)
            {
                return biMap.get(object);
            }

            @Override
            public NounDeclension fromString(String string)
            {
                return biMap.inverse().get(string);
            }
        });

        /*
         * Table elements
         */
        Table<Case, Count, Triple<Text, TextField, CheckBox>> temporaryTable = HashBasedTable.create(6, 2);
        temporaryTable.put(Case.NOMINATIVE, Count.SINGULAR, new ImmutableTriple<>(nomSgText, nomSgTextField, nomSgCheckBox));
        temporaryTable.put(Case.GENITIVE, Count.SINGULAR, new ImmutableTriple<>(genSgText, genSgTextField, genSgCheckBox));
        temporaryTable.put(Case.DATIVE, Count.SINGULAR, new ImmutableTriple<>(datSgText, datSgTextField, datSgCheckBox));
        temporaryTable.put(Case.ACCUSATIVE, Count.SINGULAR, new ImmutableTriple<>(accSgText, accSgTextField, accSgCheckBox));
        temporaryTable.put(Case.ABLATIVE, Count.SINGULAR, new ImmutableTriple<>(ablSgText, ablSgTextField, ablSgCheckBox));
        temporaryTable.put(Case.VOCATIVE, Count.SINGULAR, new ImmutableTriple<>(vocSgText, vocSgTextField, vocSgCheckBox));

        temporaryTable.put(Case.NOMINATIVE, Count.PLURAL, new ImmutableTriple<>(nomPlText, nomPlTextField, nomPlCheckBox));
        temporaryTable.put(Case.GENITIVE, Count.PLURAL, new ImmutableTriple<>(genPlText, genPlTextField, genPlCheckBox));
        temporaryTable.put(Case.DATIVE, Count.PLURAL, new ImmutableTriple<>(datPlText, datPlTextField, datPlCheckBox));
        temporaryTable.put(Case.ACCUSATIVE, Count.PLURAL, new ImmutableTriple<>(accPlText, accPlTextField, accPlCheckBox));
        temporaryTable.put(Case.ABLATIVE, Count.PLURAL, new ImmutableTriple<>(ablPlText, ablPlTextField, ablPlCheckBox));
        temporaryTable.put(Case.VOCATIVE, Count.PLURAL, new ImmutableTriple<>(vocPlText, vocPlTextField, vocPlCheckBox));
        this.tableElements = Tables.unmodifiableTable(temporaryTable); // Unmodifiable view

        /*
         * Listeners
         */
        this.rootWordTextBox.textProperty().addListener((observable, oldValue, newValue) -> rootWordChange(newValue));
        this.genderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> genderChange(newValue));
        this.declensionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> declensionChange(newValue));
    }

    /*
     * <a href="http://stackoverflow.com/a/32384404/4464702">http://stackoverflow.com/a/32384404/4464702</a>
     * <a href="https://bugs.openjdk.java.net/browse/JDK-8132897">https://bugs.openjdk.java.net/browse/JDK-8132897</a>
     * <p>
     * Not having this here causes an application-wide freeze
     * on Windows 10 devices with touch (my computer is one of those)
     */
    @FXML
    protected void requestFocus(MouseEvent event)
    {
        ((Node) event.getSource()).requestFocus();
    }

    private boolean init = false;

    /*
     * Call on JavaFX Application Thread
     *
     * ONLY CALL ONCE!!!
     */
    public void initializeWithNoun(Noun noun)
    {
        if (init)
            throw new IllegalStateException();
        init = true;
        this.initialBackingNoun = noun;
        bindNoun(noun, true, true);
    }

    Set<PropertyChangeListener> listeners = Sets.newHashSet();

    private PropertyChangeListener register(PropertyChangeListener listener)
    {
        listeners.add(listener);
        return listener;
    }

    private void unregisterAll(Noun noun)
    {
        listeners.forEach(noun::removePropertyChangeListener);
        listeners.clear();
    }

    boolean initializedWithNoun = false;

    /*
     * Call on JavaFX Application Thread
     */
    public void bindNoun(Noun noun, boolean isReset, boolean doInit)
    {
        boolean reRegisterListeners = noun != this.currentBackingNoun && noun != null; // Reference check
        if (reRegisterListeners) // If the whole instance changed
        {
            unregisterAll(noun);
        }

        this.currentBackingNoun = noun;
        if (doInit) this.initialBackingNoun = noun;

        if (reRegisterListeners)
        {
            rootWordTextBox.setText(noun.getRootWord());
            genderComboBox.setValue(noun.getGender());
            declensionComboBox.setValue(noun.getNounDeclension());
            noun.addPropertyChangeListener(register(FxUtil.bind(rootWordTextBox.textProperty(), "rootWord")));
            noun.addPropertyChangeListener(register(FxUtil.bind(genderComboBox.valueProperty(), "gender")));
            noun.addPropertyChangeListener(register(FxUtil.bind(declensionComboBox.valueProperty(), "nounDeclension")));
        }

        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                Triple<Text, TextField, CheckBox> currentTriple = tableElements.get(caze, count);
                Text currentText = currentTriple.getLeft();
                TextField currentTextField = currentTriple.getMiddle();
                CheckBox currentCheckBox = currentTriple.getRight();

                if (!initializedWithNoun) // For readability, place this here instead of .initialize() (saves iterations and map-gets)
                {
                    FxUtil.bindInverse(currentText.visibleProperty(), currentCheckBox.selectedProperty());
                    currentTextField.visibleProperty().bind(currentCheckBox.selectedProperty());
                    currentCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> checkBoxChanged(caze, count, newValue));
                }

                // Text
                String declinedForm = noun != null ? noun.getDeclinedForm(caze, count) : null;
                currentText.setText(declinedForm != null ? declinedForm : resources.getString("Text.DeclinedForm.NoDeclinedForm"));
                // TextField
                String definedForm = noun != null ? noun.getDefinedForm(caze, count) : null;
                currentTextField.setText(definedForm != null ? definedForm : "");
                // CheckBox
                if (isReset && noun != null)
                    currentCheckBox.setSelected(noun.getDefinedForm(caze, count) != null);

                if (reRegisterListeners)
                {
                    noun.addPropertyChangeListener(register(FxUtil.bind(currentText.textProperty(), caze + "_" + count + "_declined")));
                    noun.addPropertyChangeListener(register(FxUtil.bind(currentTextField.textProperty(), caze + "_" + count + "_defined")));

                    currentTextField.textProperty().addListener((observable, oldValue, newValue) -> definedFormChange(caze, count, newValue));
                }
            }
        initializedWithNoun = true;
    }

    private void definedFormChange(Case caze, Count count, String newValue)
    {
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setDefinedForm(caze, count, newValue);
            } catch (PropertyVetoException e)
            {
                throw new RuntimeException(e);
            }
    }

    private void checkBoxChanged(Case caze, Count count, boolean newValue)
    {

        if (currentBackingNoun != null)
            if (!newValue)
                currentBackingNoun.removeDefinedForm(caze, count);
            else
                try
                {
                    currentBackingNoun.setDefinedForm(caze, count, tableElements.get(caze, count).getMiddle().getText());
                } catch (PropertyVetoException e)
                {
                    throw new RuntimeException(e);
                }
    }

    private void rootWordChange(String newValue)
    {
        tryBackingNoun();
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setRootWord(newValue);
            } catch (PropertyVetoException ignored) {}
    }

    private void declensionChange(NounDeclension newValue)
    {
        tryBackingNoun();
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setNounDeclension(newValue);
            } catch (PropertyVetoException ignored) {}
    }

    private void genderChange(Gender newValue)
    {
        tryBackingNoun();
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setGender(newValue);
            } catch (PropertyVetoException ignored) {}
    }

    private void tryBackingNoun()
    {
        if (currentBackingNoun == null && !rootWordTextBox.getText().isEmpty() && genderComboBox.getValue() != null)
        {
            bindNoun(new Noun(declensionComboBox.getValue(), genderComboBox.getValue(), rootWordTextBox.getText()), true, true);
        }
    }

    @FXML
    private void save(ActionEvent event)
    {
        bindNoun(this.currentBackingNoun, false, true);
        Task<Void> nounSaveTask = new NounSaveTask(currentBackingNoun, StaticConnectionHolder.connectionForId("main"));
        nounSaveTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue)
            {
                case FAILED:
                case CANCELLED:
                case SUCCEEDED:
                    saveButton.setDisable(false);
                    resetButton.setDisable(false);
                    break;
                default:
                    saveButton.setDisable(true);
                    resetButton.setDisable(true);
                    break;
            }
        });
        new Thread(nounSaveTask).start();
    }

    @FXML
    private void reset(ActionEvent event)
    {
        bindNoun(this.initialBackingNoun, true, false);
    }
}
