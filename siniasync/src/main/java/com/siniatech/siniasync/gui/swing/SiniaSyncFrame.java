package com.siniatech.siniasync.gui.swing;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.siniatech.siniasync.change.IChange;
import com.siniatech.siniasync.manager.SyncManager;
import com.siniatech.siniasync.monitor.SysoutProgressMonitor;

public class SiniaSyncFrame extends JFrame {

    private Path source;
    private Path target;
    private SyncManager syncManager;

    public SiniaSyncFrame() {
        getContentPane().setLayout( new GridLayout( 1, 3 ) );

        syncManager = new SyncManager();

        JButton setSource = new JButton( "Set Source" );
        setSource.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                JFileChooser sourceChooser = new JFileChooser();
                sourceChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                int res = sourceChooser.showOpenDialog( SiniaSyncFrame.this );
                if ( res == JFileChooser.APPROVE_OPTION ) {
                    source = sourceChooser.getSelectedFile().toPath();
                    System.out.println( source );
                }
            }
        } );
        getContentPane().add( setSource );

        JButton setTarget = new JButton( "Set Target" );
        setTarget.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                JFileChooser targetChooser = new JFileChooser();
                targetChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
                int res = targetChooser.showOpenDialog( SiniaSyncFrame.this );
                if ( res == JFileChooser.APPROVE_OPTION ) {
                    target = targetChooser.getSelectedFile().toPath();
                    System.out.println( target );
                }
            }
        } );
        getContentPane().add( setTarget );

        JButton synchButton = new JButton( "Synch" );
        synchButton.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    List<IChange> changes = syncManager.determineChanges( source, target );
                    System.out.println( changes );
                    for ( IChange change : changes ) {
                        change.apply( new SysoutProgressMonitor() );
                    }

                } catch ( Exception e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        getContentPane().add( synchButton );
        setSize( 400, 300 );
        setVisible( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
