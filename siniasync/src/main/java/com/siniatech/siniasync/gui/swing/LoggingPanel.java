/*******************************************************************************
 * SiniaSync
 * Copyright (c) 2011-2 Siniatech Ltd  
 * http://www.siniatech.com/products/siniasync
 *
 * All rights reserved. This project and the accompanying materials are made 
 * available under the terms of the MIT License which can be found in the root  
 * of the project, and at http://www.opensource.org/licenses/mit-license.php
 *
 ******************************************************************************/
package com.siniatech.siniasync.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.joda.time.DateTime;

public class LoggingPanel extends JPanel {

    private JTextPane loggingPane;
    private Style errorStyle;
    private Style infoStyle;

    public LoggingPanel() {
        setLayout( new BorderLayout() );
        loggingPane = new JTextPane();
        loggingPane.setEditable( false );
        JPanel noWrapPanel = new JPanel( new BorderLayout() );
        noWrapPanel.add( loggingPane, BorderLayout.CENTER );
        add( new JScrollPane( noWrapPanel ), BorderLayout.CENTER );

        errorStyle = loggingPane.addStyle( "ERROR_STYLE", null );
        errorStyle.addAttribute( StyleConstants.Bold, Boolean.TRUE );
        errorStyle.addAttribute( StyleConstants.Foreground, Color.red );
        errorStyle.addAttribute( StyleConstants.FontSize, 10 );

        infoStyle = loggingPane.addStyle( "INFO_STYLE", null );
        infoStyle.addAttribute( StyleConstants.Bold, Boolean.FALSE );
        infoStyle.addAttribute( StyleConstants.Foreground, Color.black );
        infoStyle.addAttribute( StyleConstants.FontSize, 10 );
    }

    public void reportInfo( String msg ) {
        report( msg, infoStyle );
    }

    public void reportError( String msg ) {
        report( msg, errorStyle );

    }

    private void report( final String msg, final Style style ) {
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                StyledDocument doc = loggingPane.getStyledDocument();
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append( new DateTime() );
                    sb.append( " :: " );
                    sb.append( msg );
                    sb.append( "\n" );
                    doc.insertString( doc.getLength(), sb.toString(), style );
                    loggingPane.setCaretPosition( doc.getLength() );
                } catch ( BadLocationException e ) {
                    e.printStackTrace();
                }

            }
        } );
    }
}
