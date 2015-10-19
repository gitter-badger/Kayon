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
import cf.kayon.core.noun.Noun;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkNotNull;

public class VocabNodeFactory
{

    @Nullable
    public static Node getNode(Vocab v)
    {
        checkNotNull(v);
        if (v instanceof Noun)
            return getNode((Noun) v);
        return null;
    }

    @Nullable
    public static Node forType(Class<? extends Vocab> clazz)
    {
        if (clazz.equals(Noun.class))
            return getNode(null);
        return null;
    }

    private static final byte[] nounFxml;

    static
    {
        try (InputStream nounStream = VocabNodeFactory.class.getResourceAsStream("/cf/kayon/gui/NounView.fxml")) // try-with-resources auto-closes InputStream
        {
            nounFxml = IOUtils.toByteArray(nounStream); // cache bytes
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static Node getNode(Noun noun)
    {
        // URL is for context only (resolving images, css files, etc...), there is no actual File IO for the FXML
        FXMLLoader loader = new FXMLLoader(VocabNodeFactory.class.getResource("/cf/kayon/gui/NounView.fxml"), ResourceBundle.getBundle("cf.kayon.gui.bundles.NounView"));
        try
        {
            Parent root = loader.load(new ByteArrayInputStream(nounFxml)); // use cached bytes
            ((NounViewController) loader.getController()).initializeWithNoun(noun);
            return root;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
