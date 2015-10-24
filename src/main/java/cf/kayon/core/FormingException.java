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

package cf.kayon.core;

/**
 * Describes a failure when forming.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class FormingException extends Exception
{
    /**
     * @since 0.0.1
     */
    public FormingException()
    {
        super();
    }

    /**
     * @since 0.0.1
     */
    public FormingException(String message)
    {
        super(message);
    }

    /**
     * @since 0.0.1
     */
    public FormingException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @since 0.0.1
     */
    public FormingException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @since 0.0.1
     */
    protected FormingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
