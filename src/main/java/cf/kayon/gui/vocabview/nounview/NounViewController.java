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

package cf.kayon.gui.vocabview.nounview;

import cf.kayon.core.*;
import cf.kayon.core.Count;
import cf.kayon.core.noun.Noun;
import cf.kayon.core.noun.NounDeclension;
import cf.kayon.core.noun.NounForm;
import cf.kayon.core.noun.impl.*;
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
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static cf.kayon.core.util.StringUtil.checkNotEmpty;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Controls the noun view.
 *
 * @author Ruben Anders
 * @see NounView
 * @since 0.0.1
 */
public class NounViewController extends Contexed
{
    /**
     * A static immutable buffer of all noun declensions.
     * <p>
     * Safe to be used and accessed amongst arbitrary threads.
     *
     * @since 0.0.1
     */
    @NotNull
    public static final List<NounDeclension> nounDeclensions = ImmutableList.<NounDeclension>builder()
            .add(ANounDeclension.getInstance())
            .add(ONounDeclension.getInstance())
            .add(ORNounDeclension.getInstance())
            .add(ConsonantNounDeclension.getInstance())
            .add(INounDeclension.getInstance())
            .add(MixedNounDeclension.getInstance())
            .add(UNounDeclension.getInstance())
            .add(ENounDeclension.getInstance())
            .add(DummyNounDeclension.getInstance())
            .build();

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(NounViewController.class);

    /**
     * The listeners bound to the current backing noun by this class, for later unregistering purposes.
     *
     * @since 0.0.1
     */
    private final Map<String, PropertyChangeListener> listeners = new HashMap<>();

    private Map<NounForm, Triple<Text, TextField, CheckBox>> tableElements;

    /* package-private */
    @FXML
    ResourceBundle resources;

    @FXML
    private CheckBox nomSgCheckBox, genSgCheckBox, datSgCheckBox, accSgCheckBox, ablSgCheckBox, vocSgCheckBox, nomPlCheckBox, genPlCheckBox, datPlCheckBox, accPlCheckBox,
            ablPlCheckBox, vocPlCheckBox;

    @FXML
    private Text nomSgText, genSgText, datSgText, accSgText, ablSgText, vocSgText, nomPlText, genPlText, datPlText, accPlText, ablPlText, vocPlText;

    @FXML
    private TextField nomSgTextField, genSgTextField, datSgTextField, accSgTextField, ablSgTextField, vocSgTextField, nomPlTextField, genPlTextField, datPlTextField,
            accPlTextField, ablPlTextField, vocPlTextField;

    @FXML
    private Button resetButton, saveButton;

    @FXML
    private TextField rootWordTextField;

    @FXML
    private ComboBox<Gender> genderComboBox;

    @FXML
    private ComboBox<NounDeclension> declensionComboBox;

    @FXML
    private GridPane rootPane;

    @FXML
    private Text uuidValueText;

    @Nullable
    private Noun initialBackingNoun;

    @Nullable
    private Noun currentBackingNoun;

    /**
     * Whether this NounView is being displayed on its own window ( {@code true} ) or
     * if it is just displayed in a pane ( {@code false} ).
     *
     * @since 0.0.1
     */
    private boolean isWindowed = false;

    protected NounViewController(@NotNull KayonContext context)
    {
        super(context);
    }

    public NounViewController()
    {
        this(FxUtil.context);
    }

    /**
     * To be called on the JavaFX Application Thread.
     *
     * @see javafx.fxml.Initializable
     * @since 0.0.1
     */
    public void initialize()
    {
        LOGGER.info("Initializing NounViewController");

        /*
         * ComboBoxes
         */
        genderComboBox.setConverter(new StringConverter<Gender>()
        {
            private final BiMap<Gender, String> biMap = EnumHashBiMap.create(Gender.class);

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
        genderComboBox.getItems().addAll(Gender.values());
        declensionComboBox.setConverter(new StringConverter<NounDeclension>()
        {
            private final BiMap<NounDeclension, String> biMap = HashBiMap.create();

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
        declensionComboBox.getItems().addAll(nounDeclensions);

        /*
         * Table elements
         */
        this.tableElements = ImmutableMap.<NounForm, Triple<Text, TextField, CheckBox>>builder()
                .put(NounForm.of(Case.NOMINATIVE, Count.SINGULAR), new ImmutableTriple<>(nomSgText, nomSgTextField, nomSgCheckBox))
                .put(NounForm.of(Case.GENITIVE, Count.SINGULAR), new ImmutableTriple<>(genSgText, genSgTextField, genSgCheckBox))
                .put(NounForm.of(Case.DATIVE, Count.SINGULAR), new ImmutableTriple<>(datSgText, datSgTextField, datSgCheckBox))
                .put(NounForm.of(Case.ACCUSATIVE, Count.SINGULAR), new ImmutableTriple<>(accSgText, accSgTextField, accSgCheckBox))
                .put(NounForm.of(Case.ABLATIVE, Count.SINGULAR), new ImmutableTriple<>(ablSgText, ablSgTextField, ablSgCheckBox))
                .put(NounForm.of(Case.VOCATIVE, Count.SINGULAR), new ImmutableTriple<>(vocSgText, vocSgTextField, vocSgCheckBox))
                .put(NounForm.of(Case.NOMINATIVE, Count.PLURAL), new ImmutableTriple<>(nomPlText, nomPlTextField, nomPlCheckBox))
                .put(NounForm.of(Case.GENITIVE, Count.PLURAL), new ImmutableTriple<>(genPlText, genPlTextField, genPlCheckBox))
                .put(NounForm.of(Case.DATIVE, Count.PLURAL), new ImmutableTriple<>(datPlText, datPlTextField, datPlCheckBox))
                .put(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), new ImmutableTriple<>(accPlText, accPlTextField, accPlCheckBox))
                .put(NounForm.of(Case.ABLATIVE, Count.PLURAL), new ImmutableTriple<>(ablPlText, ablPlTextField, ablPlCheckBox))
                .put(NounForm.of(Case.VOCATIVE, Count.PLURAL), new ImmutableTriple<>(vocPlText, vocPlTextField, vocPlCheckBox))
                .build();

        /*
         * Listeners
         */
        this.rootWordTextField.textProperty().addListener((observable, oldValue, newValue) -> rootWordChange(newValue));
        this.genderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> genderChange(newValue));
        this.declensionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> declensionChange(newValue));

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

        for (NounForm current : NounForm.values())
        {
            Triple<Text, TextField, CheckBox> currentTriple = tableElements.get(current);
            Text currentText = currentTriple.getLeft();
            TextField currentTextField = currentTriple.getMiddle();
            CheckBox currentCheckBox = currentTriple.getRight();

            currentText.visibleProperty().bind(currentCheckBox.selectedProperty().not());
            currentTextField.visibleProperty().bind(currentCheckBox.selectedProperty());
            currentCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> checkBoxChanged(current, newValue));
            currentTextField.textProperty().addListener((observable, oldValue, newValue) -> definedFormChange(current, newValue));
        }
    }

    /**
     * Registers a listener into the listeners set for later unbinding.
     *
     * @param propertyName The property name the listener is bound to.
     * @param listener     The listener to register.
     * @return The listener itself. Useful for inlining.
     * @since 0.0.1
     */
    @NotNull
    private PropertyChangeListener register(@NotNull @NonNls String propertyName, @NotNull PropertyChangeListener listener)
    {
        checkNotEmpty(propertyName);
        checkNotNull(listener);
        listeners.put(propertyName, listener);
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
    private void unregisterAll(@NotNull Noun noun)
    {
        listeners.forEach(noun::removePropertyChangeListener);
        listeners.clear();
    }

    /**
     * To be called on the JavaFX application thread.
     *
     * @param newNoun The noun to bind.
     * @param isReset Whether this is a reset: Setting this to true will override all checkbox settings.
     * @param isInit  Whether this is a initialization: This is to be called e.g. on a save operation. Setting this parameter to true will make
     *                this method set the {@link #initialBackingNoun} field.
     * @since 0.0.1
     */
    /* package-local */
    void bindNoun(@Nullable Noun newNoun, boolean isReset, boolean isInit)
    {
        LOGGER.info(String.format("Binding noun:%nnewNoun: %s%nisReset: %b%nisInit: %b%ncurrentBackingNoun: %s%ninitialBackingNoun: %s", newNoun, isReset, isInit,
                                  currentBackingNoun,
                                  initialBackingNoun));

        @Nullable Noun oldBackingNoun = currentBackingNoun;
        //        @Nullable Noun oldInitialBackingNoun = initialBackingNoun;
        currentBackingNoun = newNoun;
        if (isInit) initialBackingNoun = newNoun != null ? newNoun.copyDeep() : null;

        if (currentBackingNoun != oldBackingNoun)
        {
            // Unregister listeners from noun
            // Yes: Noun@123abc -> Noun@345def
            // Yes: Noun@123abc -> null
            // No:  null -> Noun@123abc
            if (oldBackingNoun != null)
                unregisterAll(oldBackingNoun);

            // Register new listeners
            // Yes: Noun@123abc -> Noun@345def
            // No: Noun@123abc -> null
            // Yes:  null -> Noun@123abc
            if (currentBackingNoun != null)
            {
                rootWordTextField.setText(currentBackingNoun.getRootWord());
                genderComboBox.setValue(currentBackingNoun.getGender());
                declensionComboBox.setValue(currentBackingNoun.getNounDeclension());
                uuidValueText.setText(currentBackingNoun.getUuid() != null ? currentBackingNoun.getUuid().toString() : resources.getString("Text.UUID.NoneSet"));
                register("rootWord", FxUtil.bindTo(currentBackingNoun, rootWordTextField.textProperty(), "rootWord", null, null));
                register("gender", FxUtil.bindTo(currentBackingNoun, genderComboBox.valueProperty(), "gender", null, null));
                register("nounDeclension", FxUtil.bindTo(currentBackingNoun, declensionComboBox.valueProperty(), "nounDeclension",
                                                         (NounDeclension n) -> n == null ? DummyNounDeclension.getInstance() : n));
                register("uuid", FxUtil.bindTo(currentBackingNoun, uuidValueText.textProperty(), "uuid", uuid -> {
                    if (uuid == null)
                        return resources.getString("Text.UUID.NoneSet");
                    return uuid.toString();
                }));
            }
        }

        if (currentBackingNoun == null && isReset)
        {
            rootWordTextField.clear();
            genderComboBox.setValue(null);
            declensionComboBox.setValue(null);
            uuidValueText.setText(resources.getString("Text.UUID.NoneSet"));
        }


        for (@NotNull NounForm current : NounForm.values())
        {
            @NotNull Triple<Text, TextField, CheckBox> currentTriple = tableElements.get(current);
            @NotNull Text currentText = currentTriple.getLeft();
            @NotNull TextField currentTextField = currentTriple.getMiddle();
            @NotNull CheckBox currentCheckBox = currentTriple.getRight();

            if (currentBackingNoun == null)
            {
                currentText.setText(resources.getString("Text.DeclinedForm.NoDeclinedForm"));
                if (isReset)
                {
                    currentTextField.setText(resources.getString("Text.NoSuchForm"));
                    currentCheckBox.setSelected(false);
                }
            } else // currentBackingNoun != null
            {
                // Text
                String declinedForm = currentBackingNoun.getDeclinedForm(current);
                currentText.setText(declinedForm != null ? declinedForm : resources.getString("Text.DeclinedForm.NoDeclinedForm"));

                // TextField
                String definedForm = currentBackingNoun.getDefinedForm(current);
                currentTextField.setText(definedForm != null ? definedForm : resources.getString("Text.NoSuchForm"));

                // CheckBox
                if (isReset)
                    currentCheckBox.setSelected(currentBackingNoun.getDefinedForm(current) != null);

                // Yes: Noun@123abc -> Noun@345def
                // No: Noun@123abc -> null
                // Yes:  null -> Noun@123abc
                if (currentBackingNoun != oldBackingNoun)
                {
                    register(current.getPropertyName("declined"),
                             FxUtil.bindTo(currentBackingNoun, currentText.textProperty(), current.getPropertyName("declined"), resources,
                                           "Text.DeclinedForm.NoDeclinedForm"));
                    register(current.getPropertyName("defined"),
                             FxUtil.bindTo(currentBackingNoun, currentTextField.textProperty(), current.getPropertyName("defined"), resources,
                                           "Text.NoSuchForm"));
                }
            }
        }
    }

    /**
     * Called when the user changes a defined form.
     *
     * @param nounForm The form that was changed.
     * @param newValue The new value.
     * @since 0.0.1
     */
    private void definedFormChange(@NotNull NounForm nounForm, @Nullable String newValue)
    {
        if (newValue != null)
        {
            String lowerCase = newValue.toLowerCase();
            if (!newValue.equals(lowerCase))
            {
                tableElements.get(nounForm).getMiddle().setText(lowerCase);
                return;
            }
            if (currentBackingNoun != null)
            {
                currentBackingNoun.setDefinedForm(nounForm, newValue);
            }
        }
    }

    /**
     * Called when the user checks or unchecks a checkbox.
     *
     * @param nounForm The form that was changed.
     * @param newValue The new value of the checkbox.
     * @since 0.0.1
     */
    private void checkBoxChanged(@NotNull NounForm nounForm, boolean newValue)
    {
        if (currentBackingNoun != null)
            if (!newValue)
            {
                currentBackingNoun.removeDefinedForm(nounForm);
            } else
            {
                currentBackingNoun.setDefinedForm(nounForm, tableElements.get(nounForm).getMiddle().getText());
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
                rootWordTextField.setText(lowerCase);
                return;
            }
            tryBackingNoun();
            if (currentBackingNoun != null)
            {
                currentBackingNoun.setRootWord(newValue);
            }
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
        {
            currentBackingNoun.setNounDeclension(newValue instanceof DummyNounDeclension ? null : newValue);
        }
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
        {
            currentBackingNoun.setGender(newValue);
        }
    }

    /**
     * Tries to establish a Noun instance out of the properties entered by the user.
     *
     * @since 0.0.1
     */
    private void tryBackingNoun()
    {
        if (currentBackingNoun == null && !rootWordTextField.getText().isEmpty() && genderComboBox.getValue() != null)
        {
            NounDeclension declension = declensionComboBox.getValue();
            if (declension instanceof DummyNounDeclension)
                declension = null;
            bindNoun(new Noun(getContext(), declension, genderComboBox.getValue(), rootWordTextField.getText()), false, false);
        } else
        {
            tryDestroyBackingNoun();
        }
    }

    /**
     * Possibly eliminates the current backing noun, e.g. if the user set the root word to an empty string.
     *
     * @since 0.2.0
     */
    private void tryDestroyBackingNoun()
    {
        if (rootWordTextField.getText().isEmpty() && currentBackingNoun != null)
            bindNoun(null, false, false); // do not reset checkboxes, do not override
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
        Task<Void> nounSaveTask = new NounSaveTask(currentBackingNoun); // just uses context of noun
        nounSaveTask.stateProperty().addListener((observable, oldValue, newValue) -> {
            // Debugging shows that these event listeners get executed in the JavaFX application thread.
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
        FxUtil.executor.execute(nounSaveTask);
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
        bindNoun(initialBackingNoun, true, false);
    }
}
