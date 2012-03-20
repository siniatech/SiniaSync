package com.siniatech.siniasync.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.siniatech.dokz.DokzContainer;
import com.siniatech.siniasync.change.ChangeOrchestrator;
import com.siniatech.siniasync.manager.SyncManager;
import com.siniatech.siniautils.fn.IResponse0;

public class SiniaSyncFrame extends JFrame {

    private Path source;
    private Path target;
    private SyncManager syncManager;

    public SiniaSyncFrame() {
        final ExecutorService threadPool = Executors.newCachedThreadPool();

        getContentPane().setLayout( new BorderLayout() );

        DokzContainer dokzContainer = new DokzContainer();
        getContentPane().add( dokzContainer.asJComponent() );

        JPanel panel = new JPanel();
        panel.setLayout( new GridLayout( 2, 3 ) );
        dokzContainer.add( panel, "Synch" );

        JPanel loggingPanel = new JPanel();
        dokzContainer.add( loggingPanel, "Log" );

        syncManager = new SyncManager();

        JButton setSource = new JButton( "Set Source" );
        setSource.addActionListener( new AbstractAction() {
            @Override
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
        panel.add( setSource );

        JButton setTarget = new JButton( "Set Target" );
        setTarget.addActionListener( new AbstractAction() {
            @Override
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
        panel.add( setTarget );

        final SynchReportPanel synchReportPanel = new SynchReportPanel();
        final JButton synchButton = new JButton( "Synch" );
        synchButton.addActionListener( new AbstractAction() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                threadPool.submit( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SwingUtilities.invokeLater( new Runnable() {
                                @Override
                                public void run() {
                                    synchButton.setEnabled( false );
                                    synchReportPanel.startSynch();
                                }
                            } );
                            IResponse0 atEnd = new IResponse0() {
                                @Override
                                public void respond() {
                                    SwingUtilities.invokeLater( new Runnable() {
                                        @Override
                                        public void run() {
                                            synchButton.setEnabled( true );

                                        }
                                    } );
                                }
                            };
                            final ChangeOrchestrator changeOrchestrator = new ChangeOrchestrator( threadPool, synchReportPanel.getChangeContext() );
                            syncManager.determineChanges( source, target, changeOrchestrator, synchReportPanel.getSynchMonitor() );
                            changeOrchestrator.setSynchComplete( atEnd );

                        } catch ( Exception e1 ) {
                            e1.printStackTrace();
                        }
                    }
                } );
            }
        } );
        panel.add( synchButton );
        panel.add( synchReportPanel );
        setSize( 800, 600 );
        setVisible( true );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }
}
