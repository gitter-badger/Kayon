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

package cf.kayon.gui.vocabview.noun;

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import cf.kayon.core.Gender;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounForm;
import cf.kayon.core.noun.impl.*;
import cf.kayon.core.sql.ConnectionHolder;
import cf.kayon.gui.FxUtil;
import com.google.common.collect.*;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

/**
 * Controls the noun view.
 *
 * @author Ruben Anders
 * @see NounView
 * @since 0.0.1
 */
public class NounViewController
{
    /**
     * A static buffer of all noun declensions.
     *
     * @since 0.0.1
     */
    private static final List<NounDeclension> nounDeclensions = Lists.newArrayList();

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

    @FXML
    GridPane rootPane;

    @FXML
    Text uuidText, uuidValueText;

    @Nullable
    Noun initialBackingNoun;

    @Nullable
    Noun currentBackingNoun;

    /**
     * @see javafx.fxml.Initializable
     * @since 0.0.1
     */
    public void initialize()
    {
        /*
         * ComboBoxes
         */
        genderComboBox.getItems().addAll(Gender.values());
        genderComboBox.setConverter(new StringConverter<Gender>()
        {
            final BiMap<Gender, String> biMap = EnumHashBiMap.create(Gender.class);

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
            final BiMap<NounDeclension, String> biMap = HashBiMap.create();

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
    //    @FXML
    //    protected void requestFocus(MouseEvent event)
    //    {
    //        ((Node) event.getSource()).requestFocus();
    //    }

    /**
     * Whether this NounView has already been initialized for the first time.
     * This is required to register the between refreshes persisting listeners.
     * <p>
     * Difference to {@link #initializedWithNoun}: This also captures whether the stage property listener has been initialized.
     *
     * @since 0.0.1
     */
    private boolean init = false;

    /**
     * Whether this NounView is being displayed on its own window ( {@code true} ) or
     * if it is just displayed in a pane ( {@code false} ).
     *
     * @since 0.0.1
     */
    private boolean isWindowed = false;

    /**
     * To be called to initialize the NounView initially.
     * <p>
     * Only call this method ever once. If the noun reference is to be changed, call {@link #bindNoun(Noun, boolean, boolean)}.
     * <p>
     * This method is to be called on the JavaFX application thread.
     *
     * @param noun The noun to initialize.
     * @throws IllegalStateException If this NounView has already been initialized.
     * @since 0.0.1
     */
    public void initializeWithNoun(Noun noun)
    {
        if (init)
            throw new IllegalStateException();
        init = true;
        bindNoun(noun, true, true);
        // Because the scene property of this node will be set later in the initialization chain
        rootPane.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                newValue.windowProperty().addListener((observable1, oldValue1, newValue1) -> {
                    if (newValue1 != null)
                    {
                        saveButton.setText(resources.getString("Button.SaveAndExit"));
                        isWindowed = true;
                    }
                });
        });
    }

    /**
     * The listeners bound to the current backing noun by this class, for later unregistering purposes.
     *
     * @since 0.0.1
     */
    final Set<PropertyChangeListener> listeners = Sets.newHashSet();

    /**
     * Registers a listener into the listeners set for later unbinding.
     *
     * @param listener The listener to register.
     * @return The listener itself. Useful for inlining.
     * @since 0.0.1
     */
    private PropertyChangeListener register(PropertyChangeListener listener)
    {
        listeners.add(listener);
        return listener;
    }

    /**
     * Unregisters all currently known listeners from the provided noun.
     * <p>
     * Will clear the set of current listeners.
     *
     * @param noun The noun to unregister from.
     * @since 0.0.1
     */
    private void unregisterAll(Noun noun)
    {
        listeners.forEach(noun::removePropertyChangeListener);
        listeners.clear();
    }

    /**
     * Whether this NounView has already been initialized with a noun.
     *
     * @since 0.0.1
     */
    boolean initializedWithNoun = false;


    /**
     * To be called on the JavaFX application thread.
     *
     * @param noun    The noun to bind.
     * @param isReset Whether this is a reset: Setting this to true will override all checkbox settings.
     * @param doInit  Whether this is a initialization: This is to be called e.g. on a save operation. Setting this parameter to true will make
     *                this method set the {@link #initialBackingNoun} field.
     * @since 0.0.1
     */
    public void bindNoun(@Nullable Noun noun, boolean isReset, boolean doInit)
    {
        boolean reRegisterListeners = noun != this.currentBackingNoun && noun != null; // Reference check
        if (reRegisterListeners) // If the whole instance changed
            unregisterAll(noun);

        this.currentBackingNoun = noun;
        if (doInit) this.initialBackingNoun = noun;

        if (reRegisterListeners)
        {
            rootWordTextBox.setText(noun.getRootWord());
            genderComboBox.setValue(noun.getGender());
            declensionComboBox.setValue(noun.getNounDeclension());
            uuidValueText.setText(noun.getUuid() != null ? noun.getUuid().toString() : resources.getString("Text.UUID.NoneSet"));
            noun.addPropertyChangeListener(register(FxUtil.bind(rootWordTextBox.textProperty(), "rootWord")));
            noun.addPropertyChangeListener(register(FxUtil.bind(genderComboBox.valueProperty(), "gender")));
            noun.addPropertyChangeListener(register(FxUtil.bind(declensionComboBox.valueProperty(), "nounDeclension")));
            noun.addPropertyChangeListener(register(
                    FxUtil.bind(uuidValueText.textProperty(), "uuid", (UUID uuid) -> uuid != null ? uuid.toString() : resources.getString("Text.UUID.NoneSet"))));
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
                    currentTextField.textProperty().addListener((observable, oldValue, newValue) -> definedFormChange(caze, count, newValue));
                }

                // Text
                String declinedForm = noun != null ? noun.getDeclinedForm(NounForm.of(caze, count)) : null;
                currentText.setText(declinedForm != null ? declinedForm : resources.getString("Text.DeclinedForm.NoDeclinedForm"));
                // TextField
                String definedForm = noun != null ? noun.getDefinedForm(NounForm.of(caze, count)) : null;
                currentTextField.setText(definedForm != null ? definedForm : resources.getString("Text.NoSuchForm"));
                // CheckBox
                if (isReset && noun != null)
                    currentCheckBox.setSelected(noun.getDefinedForm(NounForm.of(caze, count)) != null);

                if (reRegisterListeners)
                {
                    noun.addPropertyChangeListener(register(FxUtil.bind(currentText.textProperty(), caze + "_" + count + "_declined")));
                    noun.addPropertyChangeListener(register(FxUtil.bind(currentTextField.textProperty(), caze + "_" + count + "_defined")));
                }
            }
        initializedWithNoun = true;
    }

    /**
     * Called when the user changes a defined form.
     *
     * @param caze     The case of the changed form.
     * @param count    The count of the changed form.
     * @param newValue The new value.
     * @since 0.0.1
     */
    private void definedFormChange(@NotNull Case caze, @NotNull Count count, @Nullable String newValue)
    {
        if (newValue != null)
        {
            String lowerCase = newValue.toLowerCase();
            if (!newValue.equals(lowerCase))
            {
                tableElements.get(caze, count).getMiddle().setText(lowerCase);
                return;
            }
            if (currentBackingNoun != null)
                try
                {
                    currentBackingNoun.setDefinedForm(NounForm.of(caze, count), newValue);
                } catch (PropertyVetoException e)
                {
                    throw new RuntimeException(e);
                }
        }
    }

    /**
     * Called when the user checks or unchecks a checkbox.
     *
     * @param caze     The case of the form the checkbox was changed on.
     * @param count    The count of the form the checkbox was changed on.
     * @param newValue The new value of the checkbox.
     * @since 0.0.1
     */
    private void checkBoxChanged(@NotNull Case caze, @NotNull Count count, boolean newValue)
    {
        if (currentBackingNoun != null)
            if (!newValue)
                try
                {
                    currentBackingNoun.removeDefinedForm(NounForm.of(caze, count));
                } catch (PropertyVetoException e)
                {
                    throw new RuntimeException(e);
                }
            else
                try
                {
                    currentBackingNoun.setDefinedForm(NounForm.of(caze, count), tableElements.get(caze, count).getMiddle().getText());
                } catch (PropertyVetoException e)
                {
                    throw new RuntimeException(e);
                }
    }

    /**
     * Called when the user changes the root word.
     *
     * @param newValue The new value.
     * @since 0.0.1
     */
    private void rootWordChange(@Nullable String newValue)
    {
        if (newValue != null)
        {
            String lowerCase = newValue.toLowerCase();
            if (!newValue.equals(lowerCase))
            {
                rootWordTextBox.setText(lowerCase);
                return;
            }
            tryBackingNoun();
            if (currentBackingNoun != null)
                try
                {
                    currentBackingNoun.setRootWord(newValue);
                } catch (PropertyVetoException ignored) {}
        }
    }

    /**
     * Called when the user changes the noun declension.
     *
     * @param newValue The new value.
     * @since 0.0.1
     */
    private void declensionChange(@NotNull NounDeclension newValue) // null is represented by DummyNounDeclension
    {
        tryBackingNoun();
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setNounDeclension(newValue instanceof DummyNounDeclension ? null : newValue);
            } catch (PropertyVetoException ignored) {}
    }

    /**
     * Called when the user changes the gender.
     *
     * @param newValue The new value.
     * @since 0.0.1
     */
    private void genderChange(@NotNull Gender newValue)
    {
        tryBackingNoun();
        if (currentBackingNoun != null)
            try
            {
                currentBackingNoun.setGender(newValue);
            } catch (PropertyVetoException ignored) {}
    }

    /**
     * Tries to establish a Noun instance out of the properties entered by the user.
     *
     * @since 0.0.1
     */
    private void tryBackingNoun()
    {
        if (currentBackingNoun == null && !rootWordTextBox.getText().isEmpty() && genderComboBox.getValue() != null)
        {
            bindNoun(new Noun(declensionComboBox.getValue(), genderComboBox.getValue(), rootWordTextBox.getText()), true, true);
        }
    }

    /**
     * Called when the "Save" or "Save and exit" button is clicked.
     *
     * @param event The event.
     * @since 0.0.1
     */
    @FXML
    private void save(@Nullable ActionEvent event)
    {
        bindNoun(this.currentBackingNoun, false, true);
        Task<Void> nounSaveTask = new NounSaveTask(currentBackingNoun, ConnectionHolder.getConnection());
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
        if (isWindowed)
            rootPane.getScene().getWindow().hide(); // Equivalent to Stage.close(), prevent unnecessary casts
    }

    /**
     * Called when the user clicks the "Reset" button.
     *
     * @param event The event.
     * @since 0.0.1
     */
    @FXML
    private void reset(@Nullable ActionEvent event)
    {
        bindNoun(this.initialBackingNoun, true, false);
    }
}
