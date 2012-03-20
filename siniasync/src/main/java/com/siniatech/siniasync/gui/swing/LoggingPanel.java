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
        add( new JScrollPane( loggingPane ), BorderLayout.CENTER );

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
                } catch ( BadLocationException e ) {
                    e.printStackTrace();
                }

            }
        } );
    }
}
